package ua.uni.platform.auth;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class TokenRefreshService {
    // Firebase ID tokens expire after 1 hour; refresh 5 minutes before to be safe.
    private static final long TOKEN_TTL_MS = 55 * 60 * 1_000L;

    private final SessionManager sessionManager;
    private final FirebaseAuthService authService;

    private final AtomicReference<String> cachedToken = new AtomicReference<>(null);
    private final AtomicLong cachedAtMs = new AtomicLong(0);

    public TokenRefreshService(SessionManager sessionManager, FirebaseAuthService authService) {
        this.sessionManager = sessionManager;
        this.authService = authService;
        // Seed cache from the persisted session so first call after startup is cheap.
        String stored = sessionManager.getIdToken();
        if (stored != null && !stored.isBlank()) {
            cachedToken.set(stored);
            // Treat as fresh; it will be refreshed once the TTL window passes.
            cachedAtMs.set(System.currentTimeMillis());
        }
    }

    /**
     * Returns the cached token if it is still within the TTL window; otherwise
     * refreshes via Firebase and updates the cache.
     * May block on first call after startup or after TTL expiry — call from a background thread.
     */
    public String getValidToken() {
        if (isCacheValid()) {
            return cachedToken.get();
        }
        return getFreshToken();
    }

    /**
     * Always performs an HTTP call to Firebase to get a new id token.
     * Call only from a background thread.
     */
    public String getFreshToken() {
        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken.isBlank()) {
            sessionManager.clear();
            throw new IllegalStateException("Firebase session is missing refresh token. Please log in again.");
        }
        FirebaseAuthService.AuthResult refreshed = authService.refreshIdToken(refreshToken, sessionManager.getEmail());
        sessionManager.save(refreshed);
        cachedToken.set(refreshed.idToken());
        cachedAtMs.set(System.currentTimeMillis());
        return refreshed.idToken();
    }

    private boolean isCacheValid() {
        String token = cachedToken.get();
        return token != null
                && !token.isBlank()
                && (System.currentTimeMillis() - cachedAtMs.get()) < TOKEN_TTL_MS;
    }
}
