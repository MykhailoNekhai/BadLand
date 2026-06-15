package ua.uni.core.dto;

public class AchievementProgressDto {
    private String code;
    private boolean unlocked;
    private int progress;
    private int target;
    private long unlockedAt;

    public AchievementProgressDto() {
    }

    public AchievementProgressDto(String code, boolean unlocked, int progress, int target, long unlockedAt) {
        this.code = code;
        this.unlocked = unlocked;
        this.progress = progress;
        this.target = target;
        this.unlockedAt = unlockedAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public long getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(long unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
