package ua.uni.platform.account;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import ua.uni.bootstrap.RuntimeProfile;
import ua.uni.core.model.account.Avatar;
import ua.uni.core.model.account.PlayerAppearance;

public final class LocalAccountStore {
    private static final Gson GSON = new Gson();
    private static final String[] SKIN_IDS = {"classic", "runner", "thorn", "night"};
    private static final String[] COLOR_IDS = {"shadow", "amber", "moss", "sky"};
    private static final String[] EYE_STYLE_IDS = {"shadow", "spider", "round", "cluster", "swirl"};
    private static final String[] EYE_COLOR_IDS = {"purple", "gray", "green", "cyan", "yellow"};

    private final FileHandle fileHandle = Gdx.files.local("account_profile" + RuntimeProfile.suffix() + ".json");

    public String loadNickname() {
        ProfileData data = read();
        return data.nickname == null ? "" : data.nickname.trim();
    }

    public void saveNickname(String nickname) {
        ProfileData data = read();
        data.nickname = nickname == null ? "" : nickname.trim();
        write(data);
    }

    public Avatar loadAvatar() {
        ProfileData data = read();
        String localPath = data.avatarPath == null ? "" : data.avatarPath.trim();
        if (localPath.isBlank()) {
            FileHandle cached = Gdx.files.local("avatar_cached.bin");
            localPath = cached.exists() ? cached.path() : "";
        }
        return new Avatar(localPath, data.avatarUrl, !localPath.isBlank() || !safe(data.avatarUrl).isBlank());
    }

    public void saveAvatar(Avatar avatar) {
        ProfileData data = read();
        if (avatar == null) {
            data.avatarPath = "";
            data.avatarUrl = "";
        } else {
            data.avatarPath = safe(avatar.getLocalPath());
            data.avatarUrl = safe(avatar.getRemoteUrl());
        }
        write(data);
    }

    public String loadAvatarPath() {
        return loadAvatar().getLocalPath();
    }

    public void saveAvatarPath(String avatarPath) {
        Avatar avatar = loadAvatar();
        avatar.setLocalPath(avatarPath);
        avatar.setCustom(avatarPath != null && !avatarPath.isBlank());
        saveAvatar(avatar);
    }

    public PlayerAppearance loadAppearance() {
        ProfileData data = read();
        String skinId = data.skinId;
        if (safe(skinId).isBlank()) {
            skinId = idAt(SKIN_IDS, data.skinIndex);
        }
        String colorId = data.colorId;
        if (safe(colorId).isBlank()) {
            colorId = idAt(COLOR_IDS, data.colorIndex);
        }
        String eyeStyleId = data.eyeStyleId;
        if (safe(eyeStyleId).isBlank()) {
            eyeStyleId = idAt(EYE_STYLE_IDS, data.eyeStyleIndex);
        }
        String eyeColorId = data.eyeColorId;
        if (safe(eyeColorId).isBlank()) {
            eyeColorId = idAt(EYE_COLOR_IDS, data.eyeColorIndex);
        }
        return new PlayerAppearance(skinId, colorId, eyeStyleId, eyeColorId);
    }

    public void saveAppearance(PlayerAppearance appearance) {
        ProfileData data = read();
        PlayerAppearance safeAppearance = appearance == null ? new PlayerAppearance() : appearance;
        data.skinId = safeAppearance.getSkinId();
        data.colorId = safeAppearance.getColorId();
        data.eyeStyleId = safeAppearance.getEyeStyleId();
        data.eyeColorId = safeAppearance.getEyeColorId();
        data.skinIndex = indexOf(SKIN_IDS, data.skinId);
        data.colorIndex = indexOf(COLOR_IDS, data.colorId);
        data.eyeStyleIndex = indexOf(EYE_STYLE_IDS, data.eyeStyleId);
        data.eyeColorIndex = indexOf(EYE_COLOR_IDS, data.eyeColorId);
        write(data);
    }

    public int loadSkinIndex() {
        return indexOf(SKIN_IDS, loadAppearance().getSkinId());
    }

    public void saveSkinIndex(int skinIndex) {
        PlayerAppearance appearance = loadAppearance();
        appearance.setSkinId(idAt(SKIN_IDS, skinIndex));
        saveAppearance(appearance);
    }

    public int loadColorIndex() {
        return indexOf(COLOR_IDS, loadAppearance().getColorId());
    }

    public void saveColorIndex(int colorIndex) {
        PlayerAppearance appearance = loadAppearance();
        appearance.setColorId(idAt(COLOR_IDS, colorIndex));
        saveAppearance(appearance);
    }

    private ProfileData read() {
        try {
            if (!fileHandle.exists()) {
                return new ProfileData();
            }
            String raw = fileHandle.readString("UTF-8");
            ProfileData data = GSON.fromJson(raw, ProfileData.class);
            return data == null ? new ProfileData() : data;
        } catch (Exception ignored) {
            return new ProfileData();
        }
    }

    private void write(ProfileData data) {
        fileHandle.writeString(GSON.toJson(data), false, "UTF-8");
    }

    private String idAt(String[] ids, int index) {
        if (index < 0 || index >= ids.length) {
            return ids[0];
        }
        return ids[index];
    }

    private int indexOf(String[] ids, String id) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(id)) {
                return i;
            }
        }
        return 0;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static final class ProfileData {
        private String nickname = "";
        private String avatarPath = "";
        private String avatarUrl = "";
        private int skinIndex = 0;
        private int colorIndex = 0;
        private int eyeStyleIndex = 0;
        private int eyeColorIndex = 1;
        private String skinId = "";
        private String colorId = "";
        private String eyeStyleId = "";
        private String eyeColorId = "";
    }
}
