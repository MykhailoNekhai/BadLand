package ua.uni.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SessionManager {
    private static final String PREFS_NAME = "shadowflight_session";
    private static final String KEY_ID_TOKEN = "id_token";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";

    private final Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);

    public void save(FirebaseAuthService.AuthResult result) {
        preferences.putString(KEY_ID_TOKEN, result.idToken());
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
                && !preferences.getString(KEY_UID, "").isBlank();
    }

    public String getIdToken() {
        return preferences.getString(KEY_ID_TOKEN, "");
    }

    public String getUid() {
        return preferences.getString(KEY_UID, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }
}
