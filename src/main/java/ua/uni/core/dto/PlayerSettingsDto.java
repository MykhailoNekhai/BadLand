package ua.uni.core.dto;

public class PlayerSettingsDto {
    private String language;
    private float musicVolume;
    private int moveLeft;
    private int moveRight;
    private int moveUp;
    private int moveDown;

    public PlayerSettingsDto() {
    }

    public PlayerSettingsDto(String language, float musicVolume, int moveLeft, int moveRight, int moveUp, int moveDown) {
        this.language = language;
        this.musicVolume = musicVolume;
        this.moveLeft = moveLeft;
        this.moveRight = moveRight;
        this.moveUp = moveUp;
        this.moveDown = moveDown;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getMoveLeft() {
        return moveLeft;
    }

    public void setMoveLeft(int moveLeft) {
        this.moveLeft = moveLeft;
    }

    public int getMoveRight() {
        return moveRight;
    }

    public void setMoveRight(int moveRight) {
        this.moveRight = moveRight;
    }

    public int getMoveUp() {
        return moveUp;
    }

    public void setMoveUp(int moveUp) {
        this.moveUp = moveUp;
    }

    public int getMoveDown() {
        return moveDown;
    }

    public void setMoveDown(int moveDown) {
        this.moveDown = moveDown;
    }
}
