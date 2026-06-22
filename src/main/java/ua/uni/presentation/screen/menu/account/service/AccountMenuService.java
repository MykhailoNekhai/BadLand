package ua.uni.presentation.screen.menu.account.service;

import com.google.gson.JsonObject;
import ua.uni.bootstrap.GameServices;
import ua.uni.core.config.GameSettings;
import ua.uni.core.dto.UserProfileDto;
import ua.uni.core.logging.AppLogger;
import ua.uni.platform.account.LocalAccountStore;
import ua.uni.platform.auth.FirebaseAuthService;

public final class AccountMenuService {
    private static final int MIN_NICKNAME_LENGTH = 3;
    private static final int MAX_NICKNAME_LENGTH = 12;

    private final GameServices services;
    private final LocalAccountStore localStore;

    public AccountMenuService(GameServices services, LocalAccountStore localStore) {
        this.services = services;
        this.localStore = localStore;
    }

    public UpdateNicknameResult updateNickname(String rawNickname) {
        String nickname = normalizeNickname(rawNickname);
        if (nickname.length() < MIN_NICKNAME_LENGTH) {
            return UpdateNicknameResult.failure("Too short.");
        }
        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            return UpdateNicknameResult.failure("Too long.");
        }
        if (!nickname.matches("[A-Za-z]+")) {
            return UpdateNicknameResult.failure("Only letters.");
        }

        localStore.saveNickname(nickname);

        if (!services.session().hasSession()) {
            return UpdateNicknameResult.success(nickname, "Saved.");
        }

        String uid = services.session().getUid();
        String email = services.session().getEmail();
        String token = services.getValidIdToken();
        long now = System.currentTimeMillis();

        UserProfileDto profile = services.firestore().getUserProfileDto(token, uid);
        if (profile == null) {
            profile = new UserProfileDto();
            profile.setUid(uid);
            profile.setEmail(email);
            profile.setLanguage(GameSettings.getLanguage());
            profile.setCreatedAt(now);
        }

        if (profile.getUid() == null || profile.getUid().isBlank()) {
            profile.setUid(uid);
        }
        if (profile.getEmail() == null || profile.getEmail().isBlank()) {
            profile.setEmail(email);
        }
        if (profile.getLanguage() == null || profile.getLanguage().isBlank()) {
            profile.setLanguage(GameSettings.getLanguage());
        }

        profile.setNickname(nickname);
        profile.setLastSeenAt(now);
        services.firestore().saveUserProfile(token, uid, profile);
        return UpdateNicknameResult.success(nickname, "Saved.");
    }

    public UpdateEmailResult updateEmail(String newEmail) {
        String email = newEmail == null ? "" : newEmail.trim();
        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            return UpdateEmailResult.failure("Invalid email.");
        }
        if (!services.session().hasSession()) {
            return UpdateEmailResult.failure("Not logged in.");
        }
        String idToken = services.getValidIdToken();
        FirebaseAuthService.AuthResult result = services.auth().updateEmail(idToken, email);
        services.session().save(result);
        return UpdateEmailResult.success("Email updated!");
    }

    public void logout() {
        services.session().clear();
    }

    public void resetAchievements() {
        services.achievements().resetAll();
        if (services.sync() != null) {
            services.sync().syncProgressSnapshot("ACHIEVEMENTS_RESET", "manual-reset");
        }
    }

    public void sendResetPassword(String email) {
        services.auth().sendPasswordResetEmail(email);
    }

    public void resendVerificationEmail() {
        services.auth().sendEmailVerification(services.getValidIdToken());
    }

    public RemoteProfileData fetchRemoteProfileData() {
        String nickname = "";
        String email = "";
        String accountCreatedAt = "";
        String lastLoginAt = "";
        try {
            String token = services.getValidIdToken();
            String uid = services.session().getUid();
            try {
                JsonObject profile = services.firestore().getUserProfile(token, uid);
                nickname = stringField(profile, "nickname");
                email = stringField(profile, "email");
            } catch (Exception e) {
                AppLogger.error("Account", "Profile fetch failed", e);
            }
            try {
                FirebaseAuthService.AccountMetadata metadata = services.auth().getAccountMetadata(token);
                accountCreatedAt = metadata.createdAt();
                lastLoginAt = metadata.lastLoginAt();
            } catch (Exception e) {
                AppLogger.error("Account", "Account metadata fetch failed", e);
            }
        } catch (Exception e) {
            AppLogger.error("Account", "Remote profile refresh failed", e);
        }
        return new RemoteProfileData(nickname, email, accountCreatedAt, lastLoginAt);
    }

    private String normalizeNickname(String rawNickname) {
        if (rawNickname == null) {
            return "";
        }
        return rawNickname.trim().replaceAll("\\s+", " ");
    }

    private String stringField(JsonObject profile, String key) {
        if (profile == null || !profile.has("fields")) {
            return "";
        }
        JsonObject fields = profile.getAsJsonObject("fields");
        if (!fields.has(key)) {
            return "";
        }
        JsonObject value = fields.getAsJsonObject(key);
        return value.has("stringValue") ? value.get("stringValue").getAsString() : "";
    }

    public record RemoteProfileData(
            String nickname,
            String email,
            String accountCreatedAt,
            String lastLoginAt
    ) {}

    public record UpdateNicknameResult(boolean success, String nickname, String message) {
        public static UpdateNicknameResult success(String nickname, String message) {
            return new UpdateNicknameResult(true, nickname, message);
        }

        public static UpdateNicknameResult failure(String message) {
            return new UpdateNicknameResult(false, "", message);
        }
    }

    public record UpdateEmailResult(boolean success, String message) {
        public static UpdateEmailResult success(String message) {
            return new UpdateEmailResult(true, message);
        }

        public static UpdateEmailResult failure(String message) {
            return new UpdateEmailResult(false, message);
        }
    }
}
