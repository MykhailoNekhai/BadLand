package ua.uni.core.dto;

import java.util.ArrayList;
import java.util.List;

public class PlayerProgressDto {
    private int unlockedLevels;
    private int completedLevels;
    private int lastSelectedLevel;
    private int checkpointLevel;
    private List<LevelProgressDto> levels = new ArrayList<>();

    public PlayerProgressDto() {
    }

    public PlayerProgressDto(int unlockedLevels, int completedLevels, int lastSelectedLevel,
                             int checkpointLevel, List<LevelProgressDto> levels) {
        this.unlockedLevels = unlockedLevels;
        this.completedLevels = completedLevels;
        this.lastSelectedLevel = lastSelectedLevel;
        this.checkpointLevel = checkpointLevel;
        this.levels = levels != null ? levels : new ArrayList<>();
    }

    public int getUnlockedLevels() {
        return unlockedLevels;
    }

    public void setUnlockedLevels(int unlockedLevels) {
        this.unlockedLevels = unlockedLevels;
    }

    public int getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(int completedLevels) {
        this.completedLevels = completedLevels;
    }

    public int getLastSelectedLevel() {
        return lastSelectedLevel;
    }

    public void setLastSelectedLevel(int lastSelectedLevel) {
        this.lastSelectedLevel = lastSelectedLevel;
    }

    public int getCheckpointLevel() {
        return checkpointLevel;
    }

    public void setCheckpointLevel(int checkpointLevel) {
        this.checkpointLevel = checkpointLevel;
    }

    public List<LevelProgressDto> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelProgressDto> levels) {
        this.levels = levels != null ? levels : new ArrayList<>();
    }
}
