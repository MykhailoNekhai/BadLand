package ua.uni.platform.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ua.uni.core.dto.*;
import ua.uni.gameplay.achievements.AchievementManager;
import ua.uni.gameplay.achievements.Achievements;
import ua.uni.bootstrap.MainGame;
import ua.uni.core.exceptions.firebase.FirebaseNotFoundException;
import ua.uni.core.logging.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class PlayerDataSyncService implements AchievementManager.Listener {
    private static final String TAG = "PlayerSync";

    private final MainGame game;

    public PlayerDataSyncService(MainGame game) {
        this.game = game;
    }

    public void syncProfileHeartbeat() {
        if (!hasSession()) {
            return;
        }
        runAsync("profile_heartbeat", () -> {
            String uid = game.getSessionManager().getUid();
            String token = game.getValidIdToken();
            UserProfileDto current = game.getFirestoreService().getUserProfileDto(token, uid);
            String email = blankTo(game.getSessionManager().getEmail(), current != null ? current.getEmail() : "");
            String nickname = current != null && current.getNickname() != null && !current.getNickname().isBlank()
                    ? current.getNickname()
                    : nicknameFromEmail(email);
            String language = current != null && current.getLanguage() != null && !current.getLanguage().isBlank()
                    ? current.getLanguage()
                    : ua.uni.core.config.GameSettings.getLanguage();
            long now = System.currentTimeMillis();
            long createdAt = current != null && current.getCreatedAt() > 0 ? current.getCreatedAt() : now;
            boolean verified = current != null && current.isEmailVerified();

            game.getFirestoreService().saveUserProfile(token, uid,
                    new UserProfileDto(uid, nickname, email, language, createdAt, now, verified));
            game.getFirestoreService().appendPlayerEvent(token, uid,
                    new PlayerEventDto("PROFILE_HEARTBEAT", now, "session-active"));
        });
    }

    public void bootstrapFromCloud() {
        if (!hasSession()) {
            return;
        }
        runAsync("bootstrap", () -> {
            String uid = game.getSessionManager().getUid();
            String token = game.getValidIdToken();

            try {
                UserProfileDto profile = game.getFirestoreService().getUserProfileDto(token, uid);
                PlayerSettingsDto settings = getOrNull(() -> game.getFirestoreService().getPlayerSettings(token, uid));
                PlayerProgressDto progress = getOrNull(() -> game.getFirestoreService().getPlayerProgress(token, uid));
                PlayerStatsDto stats = getOrNull(() -> game.getFirestoreService().getPlayerStats(token, uid));
                PlayerAchievementsDto achievements = getOrNull(() -> game.getFirestoreService().getPlayerAchievements(token, uid));

                Gdx.app.postRunnable(() -> {
                    if (settings != null) {
                        ua.uni.core.config.GameSettings.apply(settings);
                    } else if (profile != null && profile.getLanguage() != null && !profile.getLanguage().isBlank()) {
                        ua.uni.core.config.GameSettings.setLanguage(profile.getLanguage());
                    }
                    game.getAchievementManager().applyCloudState(progress, stats, achievements);
                });

                if (settings == null) {
                    game.getFirestoreService().savePlayerSettings(token, uid, buildSettingsDto());
                }
                if (progress == null) {
                    game.getFirestoreService().savePlayerProgress(token, uid, buildProgressDto());
                }
                if (stats == null) {
                    game.getFirestoreService().savePlayerStats(token, uid, buildStatsDto());
                }
                if (achievements == null) {
                    game.getFirestoreService().savePlayerAchievements(token, uid, buildAchievementsDto());
                }

                game.getFirestoreService().appendPlayerEvent(token, uid,
                        new PlayerEventDto("CLOUD_BOOTSTRAP", System.currentTimeMillis(), "cloud-first"));

                if (profile != null) {
                    cacheAvatarIfMissing(token, profile);
                }
            } catch (FirebaseNotFoundException missingProfile) {
                seedCloudFromLocal("seed-missing-profile");
            } catch (Exception e) {
                AppLogger.error(TAG, "Cloud bootstrap failed", e);
            }
        });
    }

    private void cacheAvatarIfMissing(String token, UserProfileDto profile) {
        String avatarUrl = profile.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return;
        }
        FileHandle cachedFile = Gdx.files.local("avatar_cached.bin");
        if (cachedFile.exists()) {
            return;
        }
        try {
            byte[] bytes = game.getStorageService().downloadBytes(avatarUrl, token);
            cachedFile.writeBytes(bytes, false);
            AppLogger.info(TAG, "Avatar cached from cloud");
        } catch (Exception e) {
            AppLogger.error(TAG, "Avatar cache download failed", e);
        }
    }

    public void syncSettings(String reason) {
        if (!hasSession()) {
            return;
        }
        runAsync("settings_" + reason, () -> {
            String uid = game.getSessionManager().getUid();
            String token = game.getValidIdToken();
            game.getFirestoreService().savePlayerSettings(token, uid, buildSettingsDto());
            game.getFirestoreService().appendPlayerEvent(token, uid,
                    new PlayerEventDto("SETTINGS_CHANGED", System.currentTimeMillis(), reason));
        });
    }

    public void syncProgressSnapshot(String eventType, String details) {
        if (!hasSession()) {
            return;
        }
        runAsync("progress_" + eventType, () -> {
            String uid = game.getSessionManager().getUid();
            String token = game.getValidIdToken();
            game.getFirestoreService().savePlayerProgress(token, uid, buildProgressDto());
            game.getFirestoreService().savePlayerStats(token, uid, buildStatsDto());
            game.getFirestoreService().savePlayerAchievements(token, uid, buildAchievementsDto());
            game.getFirestoreService().appendPlayerEvent(token, uid,
                    new PlayerEventDto(eventType, System.currentTimeMillis(), details));
        });
    }

    @Override
    public void onLevelStarted(int level) {
        syncProgressSnapshot("LEVEL_STARTED", "level=" + level);
    }

    @Override
    public void onLevelCompleted(int level) {
        syncProgressSnapshot("LEVEL_COMPLETED", "level=" + level);
    }

    @Override
    public void onLevelFailed(int totalLosses) {
        syncProgressSnapshot("LEVEL_FAILED", "totalLosses=" + totalLosses);
    }

    @Override
    public void onDeathRecorded(int totalDeaths) {
        syncProgressSnapshot("PLAYER_DIED", "totalDeaths=" + totalDeaths);
    }

    @Override
    public void onCoopSessionStarted() {
        syncProgressSnapshot("COOP_SESSION_STARTED", "coop");
    }

    @Override
    public void onAchievementUnlocked(Achievements achievement) {
        if (achievement != null) {
            syncProgressSnapshot("ACHIEVEMENT_UNLOCKED", achievement.getCode());
        }
    }

    private PlayerSettingsDto buildSettingsDto() {
        return new PlayerSettingsDto(
                ua.uni.core.config.GameSettings.getLanguage(),
                ua.uni.core.config.GameSettings.getMusicVolume(),
                ua.uni.core.config.GameSettings.getMoveLeft(),
                ua.uni.core.config.GameSettings.getMoveRight(),
                ua.uni.core.config.GameSettings.getMoveUp(),
                ua.uni.core.config.GameSettings.getMoveDown()
        );
    }

    private PlayerProgressDto buildProgressDto() {
        AchievementManager achievements = game.getAchievementManager();
        List<LevelProgressDto> levels = new ArrayList<>();
        int totalLevels = achievements.getTotalLevels();
        int highestUnlocked = 1;
        for (int level = 1; level <= totalLevels; level++) {
            boolean completed = achievements.isLevelCompleted(level);
            int attempts = achievements.getLevelAttempts(level);
            int deaths = achievements.getLevelDeaths(level);
            levels.add(new LevelProgressDto(level, completed, 0L, deaths, attempts, completed && attempts <= 1, 0L));
            if (completed) {
                highestUnlocked = Math.max(highestUnlocked, Math.min(totalLevels, level + 1));
            }
        }
        return new PlayerProgressDto(highestUnlocked, achievements.getCompletedLevelsCount(), highestUnlocked, 0, levels);
    }

    private PlayerStatsDto buildStatsDto() {
        AchievementManager achievements = game.getAchievementManager();
        return new PlayerStatsDto(
                achievements.getTotalScore(),
                achievements.getTotalWins(),
                achievements.getTotalDeaths(),
                achievements.getTotalLosses(),
                achievements.getTotalPlaySeconds(),
                achievements.getCompletedLevelsCount(),
                achievements.getUnlockedCount(),
                achievements.getTotalCount(),
                achievements.getCoopSessionsCount(),
                System.currentTimeMillis()
        );
    }

    private PlayerAchievementsDto buildAchievementsDto() {
        AchievementManager achievements = game.getAchievementManager ();
        List<AchievementProgressDto> rows = new ArrayList<>();
        for (Achievements achievement : achievements.getCatalog().getAll()) {
            boolean unlocked = achievements.isUnlocked(achievement.getCode());
            rows.add(new AchievementProgressDto(achievement.getCode(), unlocked, unlocked ? 1 : 0, 1, 0L));
        }
        return new PlayerAchievementsDto(achievements.getUnlockedCount(), achievements.getTotalCount(), rows);
    }

    private void runAsync(String name, Runnable action) {
        Thread thread = new Thread(() -> {
            try {
                action.run();
            } catch (Exception e) {
                AppLogger.error(TAG, "Sync failed for " + name, e);
            }
        }, "player-sync-" + name);
        thread.setDaemon(true);
        thread.start();
    }

    private void seedCloudFromLocal(String reason) {
        String uid = game.getSessionManager().getUid();
        String token = game.getValidIdToken();
        long now = System.currentTimeMillis();
        String email = game.getSessionManager().getEmail();
        game.getFirestoreService().saveUserProfile(token, uid, new UserProfileDto(
                uid,
                nicknameFromEmail(email),
                email,
                ua.uni.core.config.GameSettings.getLanguage(),
                now,
                now,
                false
        ));
        game.getFirestoreService().savePlayerSettings(token, uid, buildSettingsDto());
        game.getFirestoreService().savePlayerProgress(token, uid, buildProgressDto());
        game.getFirestoreService().savePlayerStats(token, uid, buildStatsDto());
        game.getFirestoreService().savePlayerAchievements(token, uid, buildAchievementsDto());
        game.getFirestoreService().appendPlayerEvent(token, uid,
                new PlayerEventDto("CLOUD_SEEDED", now, reason));
    }

    private <T> T getOrNull(Loader<T> loader) {
        try {
            return loader.load();
        } catch (FirebaseNotFoundException notFound) {
            return null;
        }
    }

    private boolean hasSession() {
        return game.getSessionManager() != null && game.getSessionManager().hasSession();
    }

    private String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String nicknameFromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "player";
        }
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    @FunctionalInterface
    private interface Loader<T> {
        T load();
    }
}
