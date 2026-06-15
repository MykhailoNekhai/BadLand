package ua.uni.core.dto;

public class PlayerStatsDto {
    private int totalDeaths;
    private int totalPlaySeconds;
    private int completedLevels;
    private int unlockedAchievements;
    private int totalAchievements;
    private int coopSessions;
    private long lastPlayedAt;

    public PlayerStatsDto() {
    }

    public PlayerStatsDto(int totalDeaths, int totalPlaySeconds, int completedLevels,
                          int unlockedAchievements, int totalAchievements, int coopSessions, long lastPlayedAt) {
        this.totalDeaths = totalDeaths;
        this.totalPlaySeconds = totalPlaySeconds;
        this.completedLevels = completedLevels;
        this.unlockedAchievements = unlockedAchievements;
        this.totalAchievements = totalAchievements;
        this.coopSessions = coopSessions;
        this.lastPlayedAt = lastPlayedAt;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public int getTotalPlaySeconds() {
        return totalPlaySeconds;
    }

    public void setTotalPlaySeconds(int totalPlaySeconds) {
        this.totalPlaySeconds = totalPlaySeconds;
    }

    public int getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(int completedLevels) {
        this.completedLevels = completedLevels;
    }

    public int getUnlockedAchievements() {
        return unlockedAchievements;
    }

    public void setUnlockedAchievements(int unlockedAchievements) {
        this.unlockedAchievements = unlockedAchievements;
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(int totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public int getCoopSessions() {
        return coopSessions;
    }

    public void setCoopSessions(int coopSessions) {
        this.coopSessions = coopSessions;
    }

    public long getLastPlayedAt() {
        return lastPlayedAt;
    }

    public void setLastPlayedAt(long lastPlayedAt) {
        this.lastPlayedAt = lastPlayedAt;
    }
}
