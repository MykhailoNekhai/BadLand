package ua.uni.presentation.screen.menu.account;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import ua.uni.bootstrap.RuntimeProfile;

final class LocalAccountProfileStore {
    private static final Gson GSON = new Gson();
    private final FileHandle fileHandle = Gdx.files.local("account_profile" + RuntimeProfile.suffix() + ".json");

    String loadNickname() {
        ProfileData data = read();
        return data.nickname == null ? "" : data.nickname.trim();
    }

    void saveNickname(String nickname) {
        ProfileData data = read();
        data.nickname = nickname == null ? "" : nickname.trim();
        fileHandle.writeString(GSON.toJson(data), false, "UTF-8");
    }

    String loadAvatarPath() {
        ProfileData data = read();
        return data.avatarPath == null ? "" : data.avatarPath.trim();
    }

    void saveAvatarPath(String avatarPath) {
        ProfileData data = read();
        data.avatarPath = avatarPath == null ? "" : avatarPath.trim();
        fileHandle.writeString(GSON.toJson(data), false, "UTF-8");
    }

    private ProfileData read() {
        try {
            if (!fileHandle.exists()) {
                return new ProfileData();
            }
            String raw = fileHandle.readString("UTF-8");
            ProfileData data = GSON.fromJson(raw, ProfileData.class);
            return data != null ? data : new ProfileData();
        } catch (Exception ignored) {
            return new ProfileData();
        }
    }

    private static final class ProfileData {
        private String nickname = "";
        private String avatarPath = "";
    }
}
