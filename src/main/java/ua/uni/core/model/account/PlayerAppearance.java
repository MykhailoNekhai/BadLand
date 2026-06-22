package ua.uni.core.model.account;

public class PlayerAppearance {
    private String skinId;
    private String colorId;
    private String eyeStyleId;
    private String eyeColorId;

    public PlayerAppearance() {
        this("classic", "shadow", "shadow", "gray");
    }

    public PlayerAppearance(String skinId, String colorId) {
        this(skinId, colorId, "shadow", "gray");
    }

    public PlayerAppearance(String skinId, String colorId, String eyeStyleId, String eyeColorId) {
        this.skinId = normalize(skinId, "classic");
        this.colorId = normalize(colorId, "shadow");
        this.eyeStyleId = normalize(eyeStyleId, "shadow");
        this.eyeColorId = normalize(eyeColorId, "gray");
    }

    public String getSkinId() {
        return skinId;
    }

    public void setSkinId(String skinId) {
        this.skinId = normalize(skinId, "classic");
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = normalize(colorId, "shadow");
    }

    public String getEyeStyleId() {
        return eyeStyleId;
    }

    public void setEyeStyleId(String eyeStyleId) {
        this.eyeStyleId = normalize(eyeStyleId, "shadow");
    }

    public String getEyeColorId() {
        return eyeColorId;
    }

    public void setEyeColorId(String eyeColorId) {
        this.eyeColorId = normalize(eyeColorId, "gray");
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
