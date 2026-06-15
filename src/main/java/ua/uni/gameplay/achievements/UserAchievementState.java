package ua.uni.gameplay.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;

public class UserAchievementState {
    private static final String PREFS_NAME = "shadowflight_achievements";
    private static final String KEY_PREFIX = "ach_";
    private static final String KEY_TOTAL_SCORE = "total_score";
    private static final String KEY_TOTAL_WINS = "total_wins";
    private static final String KEY_TOTAL_DEATHS = "total_deaths";
    private static final String KEY_TOTAL_LOSSES = "total_losses";
    private static final String KEY_TOTAL_PLAY_SECONDS = "total_play_seconds";
    private static final String KEY_COOP_SESSIONS = "coop_sessions";
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

    public void setUnlocked(String code, boolean value) {
        String normalized = normalizeCode(code);
        unlocked.put(normalized, value);
        preferences.putBoolean(KEY_PREFIX + normalized, value);
        preferences.flush();
    }

    public int getTotalScore() {
        return preferences.getInteger(KEY_TOTAL_SCORE, 0);
    }

    public int addToTotalScore(int delta) {
        int total = Math.max(0, preferences.getInteger(KEY_TOTAL_SCORE, 0) + delta);
        preferences.putInteger(KEY_TOTAL_SCORE, total);
        preferences.flush();
        return total;
    }

    public void setTotalScore(int totalScore) {
        preferences.putInteger(KEY_TOTAL_SCORE, Math.max(0, totalScore));
        preferences.flush();
    }

    public int getTotalWins() {
        return preferences.getInteger(KEY_TOTAL_WINS, 0);
    }

    public int incrementTotalWins() {
        int total = preferences.getInteger(KEY_TOTAL_WINS, 0) + 1;
        preferences.putInteger(KEY_TOTAL_WINS, total);
        preferences.flush();
        return total;
    }

    public void setTotalWins(int totalWins) {
        preferences.putInteger(KEY_TOTAL_WINS, Math.max(0, totalWins));
        preferences.flush();
    }

    public int getTotalDeaths() {
        return preferences.getInteger(KEY_TOTAL_DEATHS, 0);
    }

    public int incrementTotalDeaths() {
        int total = preferences.getInteger(KEY_TOTAL_DEATHS, 0) + 1;
        preferences.putInteger(KEY_TOTAL_DEATHS, total);
        preferences.flush();
        return total;
    }

    public void setTotalDeaths(int totalDeaths) {
        preferences.putInteger(KEY_TOTAL_DEATHS, Math.max(0, totalDeaths));
        preferences.flush();
    }

    public int getTotalLosses() {
        return preferences.getInteger(KEY_TOTAL_LOSSES, 0);
    }

    public int incrementTotalLosses() {
        int total = preferences.getInteger(KEY_TOTAL_LOSSES, 0) + 1;
        preferences.putInteger(KEY_TOTAL_LOSSES, total);
        preferences.flush();
        return total;
    }

    public void setTotalLosses(int totalLosses) {
        preferences.putInteger(KEY_TOTAL_LOSSES, Math.max(0, totalLosses));
        preferences.flush();
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

    public void setLevelAttempts(int level, int attempts) {
        preferences.putInteger(KEY_LEVEL_ATTEMPTS_PREFIX + level, Math.max(0, attempts));
        preferences.flush();
    }

    public boolean isLevelCompleted(int level) {
        return preferences.getBoolean(KEY_LEVEL_COMPLETED_PREFIX + level, false);
    }

    public int getCompletedLevelsCount(int totalLevels) {
        int completed = 0;
        for (int level = 1; level <= totalLevels; level++) {
            if (isLevelCompleted(level)) {
                completed++;
            }
        }
        return completed;
    }

    public void markLevelCompleted(int level) {
        preferences.putBoolean(KEY_LEVEL_COMPLETED_PREFIX + level, true);
        preferences.flush();
    }

    public void setLevelCompleted(int level, boolean completed) {
        preferences.putBoolean(KEY_LEVEL_COMPLETED_PREFIX + level, completed);
        preferences.flush();
    }

    public int getTotalPlaySeconds() {
        return preferences.getInteger(KEY_TOTAL_PLAY_SECONDS, 0);
    }

    public void setTotalPlaySeconds(int totalSeconds) {
        preferences.putInteger(KEY_TOTAL_PLAY_SECONDS, Math.max(0, totalSeconds));
        preferences.flush();
    }

    public int incrementCoopSessions() {
        int total = preferences.getInteger(KEY_COOP_SESSIONS, 0) + 1;
        preferences.putInteger(KEY_COOP_SESSIONS, total);
        preferences.flush();
        return total;
    }

    public int getCoopSessionsCount() {
        return preferences.getInteger(KEY_COOP_SESSIONS, 0);
    }

    public void setCoopSessionsCount(int count) {
        preferences.putInteger(KEY_COOP_SESSIONS, Math.max(0, count));
        preferences.flush();
    }

    public void resetAll() {
        unlocked.clear();
        preferences.clear();
        preferences.flush();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Achievement code must not be blank");
        }
        return code.trim().toUpperCase();
    }
}
