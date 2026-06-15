package ua.uni.gameplay.score;

import ua.uni.gameplay.achievements.Achievements;

public class Score {
    private int totalScore;
    private int COMMON_SCORE = 500;
    private int RARE_SCORE = 1000;
    private int EPIC_SCORE = 5000;
    private int LEGENDARY_SCORE = 10000;
    private int LEVEL_SCORE = 1000;

    public Score() {
        this.totalScore = 0;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addLevelScore() {
        totalScore += LEVEL_SCORE;
    }

    public void addAchievementScore(Achievements achievement) {
        switch (achievement.getRarity()) {
            case Common -> totalScore += COMMON_SCORE;
            case Rare -> totalScore += RARE_SCORE;
            case Epic -> totalScore += EPIC_SCORE;
            case Legendary -> totalScore += LEGENDARY_SCORE;
        }
    }
}
