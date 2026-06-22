package ua.uni.core.model.auth;

public record UserIdentity(String uid, String email, boolean hasSession) {
    public static final UserIdentity GUEST = new UserIdentity("", "", false);
}
