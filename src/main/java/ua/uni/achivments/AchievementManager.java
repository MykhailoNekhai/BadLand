package ua.uni.achivments;

import ua.uni.logging.AppLogger;

public class AchievementManager {
    private final AchievementsList catalog;
    private final UserAchievementState userState;

    public AchievementManager() {
        catalog = new AchievementsList();
        userState = new UserAchievementState();
    }

    public void onLevelStart(int level) {
        userState.incrementLevelAttemptIfNotCompleted(level);
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
        return unlockedAny;
    }

    public boolean onDeath() {
        int totalDeaths = userState.incrementTotalDeaths();
        boolean unlockedAny = false;
        if (totalDeaths >= 1 && unlock("DEATH_1_TOTAL")) unlockedAny = true;
        if (totalDeaths >= 5 && unlock("DEATH_5_TOTAL")) unlockedAny = true;
        if (totalDeaths >= 10 && unlock("DEATH_10_TOTAL")) unlockedAny = true;
        return unlockedAny;
    }

    public boolean unlock(String code) {
        Achievements achievement = catalog.findByCode(code);
        boolean unlockedNow = userState.unlock(achievement.getCode());
        if (unlockedNow) {
            AppLogger.info("Achievements", "Unlocked: " + achievement.getCode() + " (" + achievement.getTitle() + ")");
        }
        return unlockedNow;
    }

    public boolean isUnlocked(String code) {
        return userState.isUnlocked(code);
    }

    public AchievementsList getCatalog() {
        return catalog;
    }
}
