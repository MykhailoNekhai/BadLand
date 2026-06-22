package ua.uni.core.model.account;

public class Account {
    private String nickname;
    private String email;
    private String uid;
    private Avatar avatar;
    private PlayerAppearance appearance;

    public Account() {
        this("", "", "", new Avatar(), new PlayerAppearance());
    }

    public Account(String nickname, String email, String uid, Avatar avatar, PlayerAppearance appearance) {
        this.nickname = nickname == null ? "" : nickname;
        this.email = email == null ? "" : email;
        this.uid = uid == null ? "" : uid;
        this.avatar = avatar == null ? new Avatar() : avatar;
        this.appearance = appearance == null ? new PlayerAppearance() : appearance;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? "" : nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? "" : email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid == null ? "" : uid;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar == null ? new Avatar() : avatar;
    }

    public PlayerAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(PlayerAppearance appearance) {
        this.appearance = appearance == null ? new PlayerAppearance() : appearance;
    }
}
