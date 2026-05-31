package ua.uni.achivments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;

public class UserAchievementState {
    private static final String PREFS_NAME = "shadowflight_achievements";
    private static final String KEY_PREFIX = "ach_";
    private static final String KEY_TOTAL_DEATHS = "total_deaths";
    private static final String KEY_LEVEL_ATTEMPTS_PREFIX = "level_attempts_";
    private static final String KEY_LEVEL_COMPLETED_PREFIX = "level_completed_";

    private final Preferences preferences = Gdx.app.getPreferences(PREFS_NAME);
    private final Map<String, Boolean> unlocked = new HashMap<>();

    public boolean isUnlocked(String code) {
        String normalized = normalizeCode(code);
        if (unlocked.containsKey(normalized)) {
            return Boolean.TRUE.equals(unlocked.get(normalized));
        }
        boolean value = preferences.getBoolean(KEY_PREFIX + normalized, false);
        unlocked.put(normalized, value);
        return value;
    }

    public boolean unlock(String code) {
        String normalized = normalizeCode(code);
        if (isUnlocked(normalized)) {
            return false;
        }
        unlocked.put(normalized, true);
        preferences.putBoolean(KEY_PREFIX + normalized, true);
        preferences.flush();
        return true;
    }

    public int incrementTotalDeaths() {
        int total = preferences.getInteger(KEY_TOTAL_DEATHS, 0) + 1;
        preferences.putInteger(KEY_TOTAL_DEATHS, total);
        preferences.flush();
        return total;
    }

    public int incrementLevelAttemptIfNotCompleted(int level) {
        String completedKey = KEY_LEVEL_COMPLETED_PREFIX + level;
        if (preferences.getBoolean(completedKey, false)) {
            return preferences.getInteger(KEY_LEVEL_ATTEMPTS_PREFIX + level, 0);
        }
        String attemptsKey = KEY_LEVEL_ATTEMPTS_PREFIX + level;
        int attempts = preferences.getInteger(attemptsKey, 0) + 1;
        preferences.putInteger(attemptsKey, attempts);
        preferences.flush();
        return attempts;
    }

    public int getLevelAttempts(int level) {
        return preferences.getInteger(KEY_LEVEL_ATTEMPTS_PREFIX + level, 0);
    }

    public boolean isLevelCompleted(int level) {
        return preferences.getBoolean(KEY_LEVEL_COMPLETED_PREFIX + level, false);
    }

    public void markLevelCompleted(int level) {
        preferences.putBoolean(KEY_LEVEL_COMPLETED_PREFIX + level, true);
        preferences.flush();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Achievement code must not be blank");
        }
        return code.trim().toUpperCase();
    }
}
