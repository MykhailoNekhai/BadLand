package ua.uni.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

public final class GameSettings {
    private static final String PREFS_NAME = "badland-settings";

    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_MOVE_LEFT = "moveLeft";
    private static final String KEY_MOVE_RIGHT = "moveRight";
    private static final String KEY_JUMP = "jump";
    private static final String KEY_INTERACT = "interact";

    public static final float DEFAULT_MUSIC_VOLUME = 0.65f;

    private static Preferences prefs;
    private static float musicVolume = DEFAULT_MUSIC_VOLUME;
    private static String language = "EN";
    private static int moveLeft = Input.Keys.A;
    private static int moveRight = Input.Keys.D;
    private static int jump = Input.Keys.SPACE;
    private static int interact = Input.Keys.E;
    private static boolean loaded;

    private GameSettings() {}

    public static void load() {
        if (loaded) return;
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        musicVolume = clamp(prefs.getFloat(KEY_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME));
        language = prefs.getString(KEY_LANGUAGE, "EN");
        moveLeft = prefs.getInteger(KEY_MOVE_LEFT, Input.Keys.A);
        moveRight = prefs.getInteger(KEY_MOVE_RIGHT, Input.Keys.D);
        jump = prefs.getInteger(KEY_JUMP, Input.Keys.SPACE);
        interact = prefs.getInteger(KEY_INTERACT, Input.Keys.E);
        loaded = true;
    }

    public static float getMusicVolume() { return musicVolume; }
    public static boolean isSoundOn() { return musicVolume > 0f; }
    public static String getLanguage() { return language; }
    public static int getMoveLeft() { return moveLeft; }
    public static int getMoveRight() { return moveRight; }
    public static int getJump() { return jump; }
    public static int getInteract() { return interact; }

    public static void setMusicVolume(float value) {
        musicVolume = clamp(value);
        prefs.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        prefs.flush();
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
    }

    public static void setMoveLeft(int key) {
        moveLeft = key;
        prefs.putInteger(KEY_MOVE_LEFT, key);
        prefs.flush();
    }

    public static void setMoveRight(int key) {
        moveRight = key;
        prefs.putInteger(KEY_MOVE_RIGHT, key);
        prefs.flush();
    }

    public static void setJump(int key) {
        jump = key;
        prefs.putInteger(KEY_JUMP, key);
        prefs.flush();
    }

    public static void setInteract(int key) {
        interact = key;
        prefs.putInteger(KEY_INTERACT, key);
        prefs.flush();
    }
}
