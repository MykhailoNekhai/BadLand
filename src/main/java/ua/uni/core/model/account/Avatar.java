package ua.uni.core.model.account;

public class Avatar {
    private String localPath;
    private String remoteUrl;
    private boolean custom;

    public Avatar() {
        this("", "", false);
    }

    public Avatar(String localPath, String remoteUrl, boolean custom) {
        this.localPath = localPath == null ? "" : localPath;
        this.remoteUrl = remoteUrl == null ? "" : remoteUrl;
        this.custom = custom;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath == null ? "" : localPath;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl == null ? "" : remoteUrl;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
