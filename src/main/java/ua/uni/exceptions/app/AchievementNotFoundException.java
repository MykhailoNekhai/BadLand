package ua.uni.exceptions.app;

public class AchievementNotFoundException extends RuntimeException {
    public AchievementNotFoundException(String message) {
        super(message);
    }
}
