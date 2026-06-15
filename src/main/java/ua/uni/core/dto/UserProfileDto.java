package ua.uni.core.dto;

public class UserProfileDto {
    private String uid;
    private String nickname;
    private String email;
    private String language;
    private long createdAt;
    private long lastSeenAt;
    private boolean emailVerified;
    private String avatarUrl;

    public UserProfileDto() {
    }

    public UserProfileDto(String uid, String nickname, String email, String language,
                          long createdAt, long lastSeenAt, boolean emailVerified) {
        this.uid = uid;
        this.nickname = nickname;
        this.email = email;
        this.language = language;
        this.createdAt = createdAt;
        this.lastSeenAt = lastSeenAt;
        this.emailVerified = emailVerified;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(long lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
