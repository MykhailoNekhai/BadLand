package ua.uni.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import ua.uni.game.RuntimeProfile;

public class SessionManager {
    private static final String PREFS_NAME = "shadowflight_session";
    private static final String KEY_ID_TOKEN = "id_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";

    private final Preferences preferences = Gdx.app.getPreferences(RuntimeProfile.prefsName(PREFS_NAME));

    public void save(FirebaseAuthService.AuthResult result) {
        preferences.putString(KEY_ID_TOKEN, result.idToken());
        preferences.putString(KEY_REFRESH_TOKEN, result.refreshToken());
        preferences.putString(KEY_UID, result.uid());
        preferences.putString(KEY_EMAIL, result.email());
        preferences.flush();
    }

    public void clear() {
        preferences.clear();
        preferences.flush();
    }

    public boolean hasSession() {
        return !preferences.getString(KEY_ID_TOKEN, "").isBlank()
                && !preferences.getString(KEY_REFRESH_TOKEN, "").isBlank()
                && !preferences.getString(KEY_UID, "").isBlank();
    }

    public String getIdToken() {
        return preferences.getString(KEY_ID_TOKEN, "");
    }

    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, "");
    }

    public String getUid() {
        return preferences.getString(KEY_UID, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getValidIdToken(FirebaseAuthService authService) {
        String refreshToken = getRefreshToken();
        if (refreshToken.isBlank()) {
            clear();
            throw new IllegalStateException("Firebase session is missing refresh token. Please log in again.");
        }
        FirebaseAuthService.AuthResult refreshed = authService.refreshIdToken(refreshToken, getEmail());
        save(refreshed);
        return refreshed.idToken();
    }
}
