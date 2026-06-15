package ua.uni.core.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import ua.uni.core.dto.PlayerSettingsDto;

public final class GameSettings {
    public interface SettingsChangeListener {
        void onSettingsChanged(String key);
    }

    private static final String PREFS_NAME = "badland-settings";

    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_MOVE_LEFT = "moveLeft";
    private static final String KEY_MOVE_RIGHT = "moveRight";
    private static final String KEY_JUMP = "moveUp";
    private static final String KEY_INTERACT = "moveDown";

    public static final float DEFAULT_MUSIC_VOLUME = 0.65f;

    private static Preferences prefs;
    private static float musicVolume = DEFAULT_MUSIC_VOLUME;
    private static String language = "EN";
    private static int moveLeft = Input.Keys.A;
    private static int moveRight = Input.Keys.D;
    private static int moveUp = Input.Keys.SPACE;
    private static int moveDown = Input.Keys.E;
    private static boolean loaded;
    private static SettingsChangeListener settingsChangeListener;
    private static boolean notificationsSuppressed;

    private GameSettings() {}

    public static void load() {
        if (loaded) return;
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        musicVolume = clamp(prefs.getFloat(KEY_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME));
        language = prefs.getString(KEY_LANGUAGE, "EN");
        moveLeft = prefs.getInteger(KEY_MOVE_LEFT, Input.Keys.A);
        moveRight = prefs.getInteger(KEY_MOVE_RIGHT, Input.Keys.D);
        moveUp = prefs.getInteger(KEY_JUMP, Input.Keys.SPACE);
        moveDown = prefs.getInteger(KEY_INTERACT, Input.Keys.E);
        loaded = true;
    }

    public static float getMusicVolume() { return musicVolume; }
    public static boolean isSoundOn() { return musicVolume > 0f; }
    public static String getLanguage() { return language; }
    public static int getMoveLeft() { return moveLeft; }
    public static int getMoveRight() { return moveRight; }
    public static int getMoveUp() { return moveUp; }
    public static int getMoveDown() { return moveDown; }

    public static void setMusicVolume(float value) {
        musicVolume = clamp(value);
        prefs.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        prefs.flush();
        notifyChanged(KEY_MUSIC_VOLUME);
    }

    private static float clamp(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    public static void setLanguage(String value) {
        language = value;
        prefs.putString(KEY_LANGUAGE, value);
        prefs.flush();
        notifyChanged(KEY_LANGUAGE);
    }

    public static void setMoveLeft(int key) {
        moveLeft = key;
        prefs.putInteger(KEY_MOVE_LEFT, key);
        prefs.flush();
        notifyChanged(KEY_MOVE_LEFT);
    }

    public static void setMoveRight(int key) {
        moveRight = key;
        prefs.putInteger(KEY_MOVE_RIGHT, key);
        prefs.flush();
        notifyChanged(KEY_MOVE_RIGHT);
    }

    public static void setMoveUp(int key) {
        moveUp = key;
        prefs.putInteger(KEY_JUMP, key);
        prefs.flush();
        notifyChanged(KEY_JUMP);
    }

    public static void setMoveDown(int key) {
        moveDown = key;
        prefs.putInteger(KEY_INTERACT, key);
        prefs.flush();
        notifyChanged(KEY_INTERACT);
    }

    public static void setSettingsChangeListener(SettingsChangeListener listener) {
        settingsChangeListener = listener;
    }

    public static void apply(PlayerSettingsDto dto) {
        if (dto == null) {
            return;
        }
        notificationsSuppressed = true;
        try {
            musicVolume = clamp(dto.getMusicVolume());
            language = safeString(dto.getLanguage(), language);
            moveLeft = dto.getMoveLeft();
            moveRight = dto.getMoveRight();
            moveUp = dto.getMoveUp();
            moveDown = dto.getMoveDown();

            prefs.putFloat(KEY_MUSIC_VOLUME, musicVolume);
            prefs.putString(KEY_LANGUAGE, language);
            prefs.putInteger(KEY_MOVE_LEFT, moveLeft);
            prefs.putInteger(KEY_MOVE_RIGHT, moveRight);
            prefs.putInteger(KEY_JUMP, moveUp);
            prefs.putInteger(KEY_INTERACT, moveDown);
            prefs.flush();
        } finally {
            notificationsSuppressed = false;
        }
    }

    private static void notifyChanged(String key) {
        if (!notificationsSuppressed && settingsChangeListener != null) {
            settingsChangeListener.onSettingsChanged(key);
        }
    }

    private static String safeString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
