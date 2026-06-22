package ua.uni.core.model.account;

public class ColorOption {
    private String id;
    private String translationKey;
    private float r;
    private float g;
    private float b;
    private float a;
    private boolean unlocked;

    public ColorOption() {
        this("", "", 1f, 1f, 1f, 1f, true);
    }

    public ColorOption(String id, String translationKey, float r, float g, float b, float a, boolean unlocked) {
        this.id = id == null ? "" : id;
        this.translationKey = translationKey == null ? "" : translationKey;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.unlocked = unlocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? "" : id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey == null ? "" : translationKey;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
