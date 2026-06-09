package ua.uni.online;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.heroiclabs.nakama.DefaultSession;
import com.heroiclabs.nakama.Session;

public class OnlineSessionStore {
    private static final String PREFS_NAME = "badland_nakama_session";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private final Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);

    public void save(Session session) {
        preferences.putString(KEY_AUTH_TOKEN, session.getAuthToken());
        preferences.putString(KEY_REFRESH_TOKEN, session.getRefreshToken());
        preferences.flush();
    }

    public Session restore() {
        String authToken = preferences.getString(KEY_AUTH_TOKEN, "");
        String refreshToken = preferences.getString(KEY_REFRESH_TOKEN, "");
        if (authToken.isBlank() || refreshToken.isBlank()) {
            return null;
        }
        return DefaultSession.restore(authToken, refreshToken);
    }

    public boolean hasSession() {
        return restore() != null;
    }

    public void clear() {
        preferences.clear();
        preferences.flush();
    }
}
