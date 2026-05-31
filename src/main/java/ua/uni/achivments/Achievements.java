package ua.uni.achivments;

import ua.uni.objects.ID;

public class Achievements {
    private ID id;
    private String code;
    private String title;
    private AchievementsRarity rarity;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Achievements(ID id, String code, String title, AchievementsRarity rarity, String message) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.rarity = rarity;
        this.message = message;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AchievementsRarity getRarity() {
        return rarity;
    }

    public void setRarity(AchievementsRarity rarity) {
        this.rarity = rarity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
