package ua.uni.core.dto;

import java.util.ArrayList;
import java.util.List;

public class PlayerAchievementsDto {
    private int unlockedCount;
    private int totalCount;
    private List<AchievementProgressDto> achievements = new ArrayList<>();

    public PlayerAchievementsDto() {
    }

    public PlayerAchievementsDto(int unlockedCount, int totalCount, List<AchievementProgressDto> achievements) {
        this.unlockedCount = unlockedCount;
        this.totalCount = totalCount;
        this.achievements = achievements != null ? achievements : new ArrayList<>();
    }

    public int getUnlockedCount() {
        return unlockedCount;
    }

    public void setUnlockedCount(int unlockedCount) {
        this.unlockedCount = unlockedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<AchievementProgressDto> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementProgressDto> achievements) {
        this.achievements = achievements != null ? achievements : new ArrayList<>();
    }
}
