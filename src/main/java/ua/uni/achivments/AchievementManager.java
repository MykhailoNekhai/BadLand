package ua.uni.achivments;

import ua.uni.dto.AchievementProgressDto;
import ua.uni.dto.LevelProgressDto;
import ua.uni.dto.PlayerAchievementsDto;
import ua.uni.dto.PlayerProgressDto;
import ua.uni.dto.PlayerStatsDto;
import ua.uni.logging.AppLogger;

import java.util.ArrayDeque;
import java.util.Queue;

public class AchievementManager {
    public interface Listener {
        void onLevelStarted(int level);
        void onLevelCompleted(int level);
        void onDeathRecorded(int totalDeaths);
        void onCoopSessionStarted();
        void onAchievementUnlocked(Achievements achievement);
    }

    private static final int TOTAL_LEVELS = 10;

    private final AchievementsList catalog;
    private final UserAchievementState userState;
    private final Queue<Achievements> pendingUnlocked = new ArrayDeque<>();
    private float playtimeAccumulator;
    private Listener listener;

    public AchievementManager() {
        catalog = new AchievementsList();
        userState = new UserAchievementState();
    }

    public void onLevelStart(int level) {
        userState.incrementLevelAttemptIfNotCompleted(level);
        if (listener != null) {
            listener.onLevelStarted(level);
        }
    }

    public boolean onLevelComplete(int level) {
        boolean unlockedAny = false;
        String levelCode = "LEVEL_" + String.format("%02d", level);
        if (unlock(levelCode)) {
            unlockedAny = true;
        }
        if (!userState.isLevelCompleted(level) && userState.getLevelAttempts(level) == 1) {
            if (unlock("FIRST_TRY_ANY_LEVEL")) {
                unlockedAny = true;
            }
        }
        userState.markLevelCompleted(level);
        if (hasCompletedAllLevels() && unlock("ALL_LEVELS_COMPLETE")) {
            unlockedAny = true;
        }
        if (listener != null) {
            listener.onLevelCompleted(level);
        }
        return unlockedAny;
    }

    public boolean onDeath() {
        int totalDeaths = userState.incrementTotalDeaths();
        boolean unlockedAny = false;
        if (totalDeaths >= 1 && unlock("DEATH_1_TOTAL")) unlockedAny = true;
        if (totalDeaths >= 5 && unlock("DEATH_5_TOTAL")) unlockedAny = true;
        if (totalDeaths >= 10 && unlock("DEATH_10_TOTAL")) unlockedAny = true;
        if (listener != null) {
            listener.onDeathRecorded(totalDeaths);
        }
        return unlockedAny;
    }

    public boolean onPlayTime(float deltaSeconds) {
        if (deltaSeconds <= 0f) {
            return false;
        }

        playtimeAccumulator += deltaSeconds;
        if (playtimeAccumulator < 1f) {
            return false;
        }

        int wholeSeconds = (int) playtimeAccumulator;
        playtimeAccumulator -= wholeSeconds;

        int totalSeconds = userState.getTotalPlaySeconds() + wholeSeconds;
        userState.setTotalPlaySeconds(totalSeconds);

        boolean unlockedAny = false;
        if (totalSeconds >= 10 * 60 && unlock("PLAYTIME_10_MIN")) unlockedAny = true;
        if (totalSeconds >= 30 * 60 && unlock("PLAYTIME_30_MIN")) unlockedAny = true;
        if (totalSeconds >= 60 * 60 && unlock("PLAYTIME_60_MIN")) unlockedAny = true;
        return unlockedAny;
    }

    public boolean onCoopSessionStart() {
        boolean unlocked = unlock("COOP_SESSION");
        userState.incrementCoopSessions();
        if (listener != null) {
            listener.onCoopSessionStarted();
        }
        return unlocked;
    }

    public boolean unlock(String code) {
        Achievements achievement = catalog.findByCode(code);
        boolean unlockedNow = userState.unlock(achievement.getCode());
        if (unlockedNow) {
            pendingUnlocked.offer(achievement);
            AppLogger.info("Achievements", "Unlocked: " + achievement.getCode() + " (" + achievement.getTitle() + ")");
            if (listener != null) {
                listener.onAchievementUnlocked(achievement);
            }
        }
        return unlockedNow;
    }

    public Achievements pollUnlockedAchievement() {
        return pendingUnlocked.poll();
    }

    public boolean isUnlocked(String code) {
        return userState.isUnlocked(code);
    }

    public AchievementsList getCatalog() {
        return catalog;
    }

    public int getUnlockedCount() {
        int count = 0;
        for (Achievements a : catalog.getAll()) {
            if (userState.isUnlocked(a.getCode())) count++;
        }
        return count;
    }

    public int getTotalCount() {
        return catalog.getAll().size();
    }

    public int getTotalDeaths() {
        return userState.getTotalDeaths();
    }

    public int getTotalPlaySeconds() {
        return userState.getTotalPlaySeconds();
    }

    public int getCompletedLevelsCount() {
        return userState.getCompletedLevelsCount(TOTAL_LEVELS);
    }

    public int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    public int getLevelAttempts(int level) {
        return userState.getLevelAttempts(level);
    }

    public boolean isLevelCompleted(int level) {
        return userState.isLevelCompleted(level);
    }

    public int getCoopSessionsCount() {
        return userState.getCoopSessionsCount();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void applyCloudState(PlayerProgressDto progress, PlayerStatsDto stats, PlayerAchievementsDto achievements) {
        pendingUnlocked.clear();
        playtimeAccumulator = 0f;

        if (stats != null) {
            userState.setTotalDeaths(stats.getTotalDeaths());
            userState.setTotalPlaySeconds(stats.getTotalPlaySeconds());
            userState.setCoopSessionsCount(stats.getCoopSessions());
        }

        if (progress != null && progress.getLevels() != null) {
            for (LevelProgressDto level : progress.getLevels()) {
                userState.setLevelAttempts(level.getLevelId(), level.getAttemptCount());
                userState.setLevelCompleted(level.getLevelId(), level.isCompleted());
            }
        }

        if (achievements != null && achievements.getAchievements() != null) {
            for (Achievements achievement : catalog.getAll()) {
                userState.setUnlocked(achievement.getCode(), false);
            }
            for (AchievementProgressDto row : achievements.getAchievements()) {
                userState.setUnlocked(row.getCode(), row.isUnlocked());
            }
        }
    }

    public void resetAll() {
        pendingUnlocked.clear();
        playtimeAccumulator = 0f;
        userState.resetAll();
        AppLogger.info("Achievements", "All achievement progress reset");
    }

    private boolean hasCompletedAllLevels() {
        for (int level = 1; level <= TOTAL_LEVELS; level++) {
            if (!userState.isLevelCompleted(level)) {
                return false;
            }
        }
        return true;
    }
}
