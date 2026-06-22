package ua.uni.core.model.account;

public class SkinOption {
    private String id;
    private String translationKey;
    private String previewTexturePath;
    private boolean unlocked;

    public SkinOption() {
        this("", "", "", true);
    }

    public SkinOption(String id, String translationKey, String previewTexturePath, boolean unlocked) {
        this.id = id == null ? "" : id;
        this.translationKey = translationKey == null ? "" : translationKey;
        this.previewTexturePath = previewTexturePath == null ? "" : previewTexturePath;
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

    public String getPreviewTexturePath() {
        return previewTexturePath;
    }

    public void setPreviewTexturePath(String previewTexturePath) {
        this.previewTexturePath = previewTexturePath == null ? "" : previewTexturePath;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
