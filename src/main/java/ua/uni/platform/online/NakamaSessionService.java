package ua.uni.platform.online;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import ua.uni.core.logging.AppLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NakamaSessionService {
    private static final String LOG_TAG = "NakamaAuth";

    private final Client client;
    private final OnlineConfig config;
    private final OnlineSessionStore sessionStore;

    public NakamaSessionService(Client client, OnlineConfig config, OnlineSessionStore sessionStore) {
        this.client = client;
        this.config = config;
        this.sessionStore = sessionStore;
    }

    public Session authenticateFirebaseUser(String firebaseUid, String username) {
        return authenticateCustom(firebaseUid, sanitizeUsername(username, firebaseUid), buildVars("provider", "firebase"));
    }

    public Session authenticateDevice(String deviceId, String username) {
        try {
            Session session = client.authenticateDevice(deviceId, config.isCreateAccountsByDefault(),
                    sanitizeUsername(username, deviceId)).get();
            sessionStore.save(session);
            AppLogger.info(LOG_TAG, "Authenticated Nakama device session for userId=" + session.getUserId());
            return session;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while authenticating Nakama device session", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to authenticate Nakama device session", e);
        }
    }

    public Session authenticateCustom(String customId, String username) {
        return authenticateCustom(customId, sanitizeUsername(username, customId), Collections.emptyMap());
    }

    public Session authenticateCustom(String customId, String username, Map<String, String> vars) {
        try {
            Session session = client.authenticateCustom(customId, config.isCreateAccountsByDefault(), username, vars).get();
            sessionStore.save(session);
            AppLogger.info(LOG_TAG, "Authenticated Nakama custom session for userId=" + session.getUserId());
            return session;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while authenticating Nakama custom session", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to authenticate Nakama custom session", e);
        }
    }

    public Session restoreSession() {
        Session stored = sessionStore.restore();
        if (stored == null) {
            return null;
        }
        if (!stored.IsExpired()) {
            return stored;
        }
        if (stored.isRefreshExpired()) {
            sessionStore.clear();
            AppLogger.info(LOG_TAG, "Stored Nakama session expired permanently; cleared local cache.");
            return null;
        }
        try {
            Session refreshed = client.refreshSession(stored).get();
            sessionStore.save(refreshed);
            AppLogger.info(LOG_TAG, "Refreshed stored Nakama session for userId=" + refreshed.getUserId());
            return refreshed;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while refreshing Nakama session", e);
        } catch (ExecutionException e) {
            if (shouldDiscardStoredSession(e)) {
                sessionStore.clear();
                AppLogger.info(LOG_TAG, "Stored Nakama session is no longer valid on the server; cleared local cache.");
                return null;
            }
            throw new IllegalStateException("Failed to refresh Nakama session", e);
        }
    }

    public void logout() {
        Session session = sessionStore.restore();
        try {
            if (session != null && !session.isRefreshExpired()) {
                client.logoutSession(session).get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while logging out of Nakama", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to log out of Nakama", e);
        } finally {
            sessionStore.clear();
            AppLogger.info(LOG_TAG, "Cleared local Nakama session.");
        }
    }
    public void isSessionExpired() {
        Session session = sessionStore.restore();
        if (session != null && session.isRefreshExpired()) {
            sessionStore.clear();
            AppLogger.info(LOG_TAG, "Stored Nakama session expired permanently; cleared local cache.");
        }
    }
    public void clearSession() {
        sessionStore.clear();
    }
    public void refreshIfNeeded(Session session) {
        if (session.isRefreshExpired()) {
            try {
                Session refreshed = client.refreshSession(session).get();
                sessionStore.save(refreshed);
                AppLogger.info(LOG_TAG, "Refreshed stored Nakama session for userId=" + refreshed.getUserId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while refreshing Nakama session", e);
            } catch (ExecutionException e) {
                if (shouldDiscardStoredSession(e)) {
                    sessionStore.clear();
                    AppLogger.info(LOG_TAG, "Refresh failed because the Nakama account no longer exists; cleared local cache.");
                    throw new IllegalStateException("Stored Nakama session expired and the account no longer exists. Retry to create a fresh session.", e);
                }
                throw new IllegalStateException("Failed to refresh Nakama session", e);
            }
        }
    }

    private boolean shouldDiscardStoredSession(ExecutionException exception) {
        Throwable cause = exception.getCause();
        if (cause == null || cause.getMessage() == null) {
            return false;
        }
        String message = cause.getMessage().toLowerCase();
        return message.contains("not_found") || message.contains("user account not found");
    }

    public boolean hasStoredSession() {
        return sessionStore.hasSession();
    }

    private Map<String, String> buildVars(String key, String value) {
        Map<String, String> vars = new HashMap<>();
        vars.put(key, value);
        return vars;
    }

    private String sanitizeUsername(String username, String fallbackSource) {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        if (fallbackSource != null && !fallbackSource.isBlank()) {
            String compact = fallbackSource.replaceAll("[^A-Za-z0-9_\\-]", "");
            if (!compact.isBlank()) {
                return compact.length() > 24 ? compact.substring(0, 24) : compact;
            }
        }
        return "player";
    }
}
