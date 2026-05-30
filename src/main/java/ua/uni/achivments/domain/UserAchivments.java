package ua.uni.achivments.domain;

import ua.uni.objects.ID;

public class UserAchivments {
    private ID userId;
    private ID achievementId;
    private int progress;
    boolean unlocked;
    private long unlockedAt;

    public ID getUserId() {
        return userId;
    }

    public void setUserId(ID userId) {
        this.userId = userId;
    }

    public ID getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(ID achievementId) {
        this.achievementId = achievementId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public long getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(long unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public UserAchivments(ID userId, ID achievementId, int progress, boolean unlocked, long unlockedAt) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.progress = progress;
        this.unlocked = unlocked;
        this.unlockedAt = unlockedAt;
    }
}
