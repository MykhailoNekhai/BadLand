package ua.uni.presentation.screen.menu.account;

import ua.uni.core.config.GameSettings;
import ua.uni.core.dto.UserProfileDto;
import ua.uni.bootstrap.MainGame;

final class AccountProfileService {
    private static final int MIN_NICKNAME_LENGTH = 3;
    private static final int MAX_NICKNAME_LENGTH = 12;

    private final MainGame game;
    private final LocalAccountProfileStore localStore;

    AccountProfileService(MainGame game, LocalAccountProfileStore localStore) {
        this.game = game;
        this.localStore = localStore;
    }

    UpdateNicknameResult updateNickname(String rawNickname) {
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

        if (!game.getSessionManager().hasSession()) {
            return UpdateNicknameResult.success(nickname, "Saved.");
        }

        String uid = game.getSessionManager().getUid();
        String email = game.getSessionManager().getEmail();
        String token = game.getValidIdToken();
        long now = System.currentTimeMillis();

        UserProfileDto profile = game.getFirestoreService().getUserProfileDto(token, uid);
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
        game.getFirestoreService().saveUserProfile(token, uid, profile);
        return UpdateNicknameResult.success(nickname, "Saved.");
    }

    private String normalizeNickname(String rawNickname) {
        if (rawNickname == null) {
            return "";
        }
        return rawNickname.trim().replaceAll("\\s+", " ");
    }

    record UpdateNicknameResult(boolean success, String nickname, String message) {
        static UpdateNicknameResult success(String nickname, String message) {
            return new UpdateNicknameResult(true, nickname, message);
        }

        static UpdateNicknameResult failure(String message) {
            return new UpdateNicknameResult(false, "", message);
        }
    }
}
