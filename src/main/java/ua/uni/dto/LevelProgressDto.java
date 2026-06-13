package ua.uni.dto;

public class LevelProgressDto {
    private int levelId;
    private boolean completed;
    private long bestTimeMillis;
    private int deathCount;
    private int attemptCount;
    private boolean completedFirstTry;
    private long completedAt;

    public LevelProgressDto() {
    }

    public LevelProgressDto(int levelId, boolean completed, long bestTimeMillis, int deathCount,
                            int attemptCount, boolean completedFirstTry, long completedAt) {
        this.levelId = levelId;
        this.completed = completed;
        this.bestTimeMillis = bestTimeMillis;
        this.deathCount = deathCount;
        this.attemptCount = attemptCount;
        this.completedFirstTry = completedFirstTry;
        this.completedAt = completedAt;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getBestTimeMillis() {
        return bestTimeMillis;
    }

    public void setBestTimeMillis(long bestTimeMillis) {
        this.bestTimeMillis = bestTimeMillis;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public boolean isCompletedFirstTry() {
        return completedFirstTry;
    }

    public void setCompletedFirstTry(boolean completedFirstTry) {
        this.completedFirstTry = completedFirstTry;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }
}
