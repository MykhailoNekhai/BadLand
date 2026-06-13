package ua.uni.dto;

public class PlayerDataDto {
    private UserProfileDto profile;
    private PlayerSettingsDto settings;
    private PlayerProgressDto progress;
    private PlayerStatsDto stats;
    private PlayerAchievementsDto achievements;

    public PlayerDataDto() {
    }

    public PlayerDataDto(UserProfileDto profile, PlayerSettingsDto settings, PlayerProgressDto progress,
                         PlayerStatsDto stats, PlayerAchievementsDto achievements) {
        this.profile = profile;
        this.settings = settings;
        this.progress = progress;
        this.stats = stats;
        this.achievements = achievements;
    }

    public UserProfileDto getProfile() {
        return profile;
    }

    public void setProfile(UserProfileDto profile) {
        this.profile = profile;
    }

    public PlayerSettingsDto getSettings() {
        return settings;
    }

    public void setSettings(PlayerSettingsDto settings) {
        this.settings = settings;
    }

    public PlayerProgressDto getProgress() {
        return progress;
    }

    public void setProgress(PlayerProgressDto progress) {
        this.progress = progress;
    }

    public PlayerStatsDto getStats() {
        return stats;
    }

    public void setStats(PlayerStatsDto stats) {
        this.stats = stats;
    }

    public PlayerAchievementsDto getAchievements() {
        return achievements;
    }

    public void setAchievements(PlayerAchievementsDto achievements) {
        this.achievements = achievements;
    }
}
