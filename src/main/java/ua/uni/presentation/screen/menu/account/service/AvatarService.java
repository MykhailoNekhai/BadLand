package ua.uni.presentation.screen.menu.account.service;

import com.badlogic.gdx.Gdx;
import ua.uni.bootstrap.GameServices;
import ua.uni.core.config.GameSettings;
import ua.uni.core.dto.UserProfileDto;
import ua.uni.core.logging.AppLogger;
import ua.uni.core.model.account.Avatar;
import ua.uni.platform.account.LocalAccountStore;

public final class AvatarService {
    private final GameServices services;
    private final LocalAccountStore localStore;

    public AvatarService(GameServices services, LocalAccountStore localStore) {
        this.services = services;
        this.localStore = localStore;
    }

    public Avatar loadAvatar() {
        return localStore.loadAvatar();
    }

    public String loadAvatarPath() {
        return localStore.loadAvatarPath();
    }

    public void saveAvatarPath(String localPath) {
        localStore.saveAvatarPath(localPath);
    }

    public void resetAvatar() {
        localStore.saveAvatar(new Avatar());
    }

    public void uploadAvatarToCloudAsync(String localPath) {
        if (!services.session().hasSession()) {
            return;
        }
        final byte[] imageBytes;
        try {
            imageBytes = Gdx.files.absolute(localPath).readBytes();
        } catch (Exception e) {
            AppLogger.error("Account", "Could not read avatar bytes for upload", e);
            return;
        }
        Thread thread = new Thread(() -> uploadAvatarToCloud(imageBytes), "avatar-upload");
        thread.setDaemon(true);
        thread.start();
    }

    private void uploadAvatarToCloud(byte[] imageBytes) {
        try {
            String token = services.getValidIdToken();
            String uid = services.session().getUid();
            String url = services.storage().uploadAvatar(token, uid, imageBytes);
            UserProfileDto profile = services.firestore().getUserProfileDto(token, uid);
            if (profile == null) {
                profile = new UserProfileDto();
                profile.setUid(uid);
                profile.setEmail(services.session().getEmail());
                profile.setLanguage(GameSettings.getLanguage());
                long now = System.currentTimeMillis();
                profile.setCreatedAt(now);
                profile.setLastSeenAt(now);
            }
            profile.setAvatarUrl(url);
            services.firestore().saveUserProfile(token, uid, profile);
            Avatar avatar = localStore.loadAvatar();
            avatar.setRemoteUrl(url);
            localStore.saveAvatar(avatar);
            AppLogger.info("Account", "Avatar uploaded to Firebase Storage");
        } catch (Exception e) {
            AppLogger.error("Account", "Avatar cloud upload failed", e);
        }
    }
}
