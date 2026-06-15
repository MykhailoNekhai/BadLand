package ua.uni.gameplay.achievements;

import ua.uni.core.dto.AchievementProgressDto;
import ua.uni.core.dto.LevelProgressDto;
import ua.uni.core.dto.PlayerAchievementsDto;
import ua.uni.core.dto.PlayerProgressDto;
import ua.uni.core.dto.PlayerStatsDto;
import ua.uni.core.logging.AppLogger;

import java.util.ArrayDeque;
import java.util.Queue;

public class AchievementManager {
    private static final int LEVEL_SCORE = 1000;
    private static final int COMMON_SCORE = 500;
    private static final int RARE_SCORE = 1000;
    private static final int EPIC_SCORE = 5000;
    private static final int LEGENDARY_SCORE = 10000;

    public interface Listener {
        void onLevelStarted(int level);
        void onLevelCompleted(int level);
        void onLevelFailed(int totalLosses);
        void onDeathRecorded(int totalDeaths);
        void onCoopSessionStarted();
        void onAchievementUnlocked(Achievements achievement);
    }

    private static final int TOTAL_LEVELS = 20;

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
        userState.addToTotalScore(LEVEL_SCORE);
        int totalWins = userState.incrementTotalWins();
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
        if (unlockWinMilestones(totalWins)) {
            unlockedAny = true;
        }
        if (unlockScoreMilestones()) {
            unlockedAny = true;
        }
        if (listener != null) {
            listener.onLevelCompleted(level);
        }
        return unlockedAny;
    }

    public boolean onDeath(int level) {
        if (level > 0) {
            userState.incrementLevelDeaths(level);
        }
        return onDeath();
    }

    public boolean onDeath() {
        int totalDeaths = userState.incrementTotalDeaths();
        boolean unlockedAny = false;
        if (totalDeaths >= 1 && unlock("DEATH_1_TOTAL")) unlockedAny = true;
        if (totalDeaths >= 5 && unlock("DEATH_5_TOTAL")) unlockedAny = true;
        if (totalDeaths >= 10 && unlock("DEATH_10_TOTAL")) unlockedAny = true;
        if (unlockScoreMilestones()) unlockedAny = true;
        if (listener != null) {
            listener.onDeathRecorded(totalDeaths);
        }
        return unlockedAny;
    }

    public int onLevelFailed() {
        int totalLosses = userState.incrementTotalLosses();
        unlockLossMilestones(totalLosses);
        unlockScoreMilestones();
        if (listener != null) {
            listener.onLevelFailed(totalLosses);
        }
        return totalLosses;
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
        if (unlockScoreMilestones()) unlockedAny = true;
        return unlockedAny;
    }

    public boolean onCoopSessionStart() {
        boolean unlocked = unlock("COOP_SESSION");
        userState.incrementCoopSessions();
        if (unlockScoreMilestones()) {
            unlocked = true;
        }
        if (listener != null) {
            listener.onCoopSessionStarted();
        }
        return unlocked;
    }

    public boolean unlock(String code) {
        Achievements achievement = catalog.findByCode(code);
        boolean unlockedNow = userState.unlock(achievement.getCode());
        if (unlockedNow) {
            userState.addToTotalScore(scoreForRarity(achievement.getRarity()));
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

    public int getLevelDeaths(int level) {
        return userState.getLevelDeaths(level);
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

    public int getTotalWins() {
        return userState.getTotalWins();
    }

    public int getTotalPlaySeconds() {
        return userState.getTotalPlaySeconds();
    }

    public int getTotalLosses() {
        return userState.getTotalLosses();
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

    public int getTotalScore() {
        return userState.getTotalScore();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void applyCloudState(PlayerProgressDto progress, PlayerStatsDto stats, PlayerAchievementsDto achievements) {
        pendingUnlocked.clear();
        playtimeAccumulator = 0f;

        if (stats != null) {
            userState.setTotalScore(stats.getTotalScore());
            userState.setTotalWins(Math.max(stats.getTotalWins(), stats.getCompletedLevels()));
            userState.setTotalDeaths(stats.getTotalDeaths());
            userState.setTotalLosses(stats.getTotalLosses());
            userState.setTotalPlaySeconds(stats.getTotalPlaySeconds());
            userState.setCoopSessionsCount(stats.getCoopSessions());
        }

        if (progress != null && progress.getLevels() != null) {
            for (LevelProgressDto level : progress.getLevels()) {
                userState.setLevelAttempts(level.getLevelId(), level.getAttemptCount());
                userState.setLevelCompleted(level.getLevelId(), level.isCompleted());
                userState.setLevelDeaths(level.getLevelId(), level.getDeathCount());
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

        unlockWinMilestones(userState.getTotalWins());
        unlockLossMilestones(userState.getTotalLosses());
        unlockScoreMilestones();
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

    private int scoreForRarity(AchievementsRarity rarity) {
        return switch (rarity) {
            case Common -> COMMON_SCORE;
            case Rare -> RARE_SCORE;
            case Epic -> EPIC_SCORE;
            case Legendary -> LEGENDARY_SCORE;
        };
    }

    private boolean unlockWinMilestones(int totalWins) {
        boolean unlockedAny = false;
        if (totalWins >= 1 && unlock("WINS_1_TOTAL")) unlockedAny = true;
        if (totalWins >= 5 && unlock("WINS_5_TOTAL")) unlockedAny = true;
        if (totalWins >= 10 && unlock("WINS_10_TOTAL")) unlockedAny = true;
        if (totalWins >= 15 && unlock("WINS_15_TOTAL")) unlockedAny = true;
        if (totalWins >= 20 && unlock("WINS_20_TOTAL")) unlockedAny = true;
        return unlockedAny;
    }

    private boolean unlockLossMilestones(int totalLosses) {
        boolean unlockedAny = false;
        if (totalLosses >= 1 && unlock("LOSSES_1_TOTAL")) unlockedAny = true;
        if (totalLosses >= 5 && unlock("LOSSES_5_TOTAL")) unlockedAny = true;
        if (totalLosses >= 10 && unlock("LOSSES_10_TOTAL")) unlockedAny = true;
        if (totalLosses >= 15 && unlock("LOSSES_15_TOTAL")) unlockedAny = true;
        if (totalLosses >= 20 && unlock("LOSSES_20_TOTAL")) unlockedAny = true;
        return unlockedAny;
    }

    private boolean unlockScoreMilestones() {
        int totalScore = userState.getTotalScore();
        boolean unlockedAny = false;
        if (totalScore >= 1_000 && unlock("SCORE_1000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 5_000 && unlock("SCORE_5000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 10_000 && unlock("SCORE_10000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 15_000 && unlock("SCORE_15000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 20_000 && unlock("SCORE_20000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 25_000 && unlock("SCORE_25000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 30_000 && unlock("SCORE_30000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 50_000 && unlock("SCORE_50000_TOTAL")) unlockedAny = true;
        if (userState.getTotalScore() >= 100_000 && unlock("SCORE_100000_TOTAL")) unlockedAny = true;
        return unlockedAny;
    }
}
