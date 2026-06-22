package ua.uni.platform.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ua.uni.core.dto.PlayerAchievementsDto;
import ua.uni.core.dto.PlayerDataDto;
import ua.uni.core.dto.PlayerEventDto;
import ua.uni.core.dto.PlayerProgressDto;
import ua.uni.core.dto.PlayerSettingsDto;
import ua.uni.core.dto.PlayerStatsDto;
import ua.uni.core.dto.UserProfileDto;

public class FirestoreService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = FirebaseHttpClient.INSTANCE;
    private final Gson gson = new Gson();
    private final FirebaseConfig config;

    public FirestoreService(FirebaseConfig config) {
        this.config = config;
    }

    public void createUserProfile(String idToken, String uid, String nickname, String email, String language) {
        long now = System.currentTimeMillis();
        saveUserProfile(idToken, uid, new UserProfileDto(uid, nickname, email, language, now, now, false));
    }

    public JsonObject getUserProfile(String idToken, String uid) {
        String url = baseDocumentsUrl() + "/users/" + uid;
        return executeAuthorizedGet(url, idToken);
    }

    public void saveUserProfile(String idToken, String uid, UserProfileDto profile) {
        executeAuthorizedPatch(documentUrl("users/" + uid), idToken, FirestoreDtoMapper.toDocumentBody(profile).toString());
    }

    public UserProfileDto getUserProfileDto(String idToken, String uid) {
        return getDocument(idToken, "users/" + uid, UserProfileDto.class);
    }

    public void savePlayerSettings(String idToken, String uid, PlayerSettingsDto settings) {
        saveDocument(idToken, privateDocumentPath(uid, "settings"), settings);
    }

    public PlayerSettingsDto getPlayerSettings(String idToken, String uid) {
        return getDocument(idToken, privateDocumentPath(uid, "settings"), PlayerSettingsDto.class);
    }

    public void savePlayerProgress(String idToken, String uid, PlayerProgressDto progress) {
        saveDocument(idToken, privateDocumentPath(uid, "progress"), progress);
    }

    public PlayerProgressDto getPlayerProgress(String idToken, String uid) {
        return getDocument(idToken, privateDocumentPath(uid, "progress"), PlayerProgressDto.class);
    }

    public void savePlayerStats(String idToken, String uid, PlayerStatsDto stats) {
        saveDocument(idToken, privateDocumentPath(uid, "stats"), stats);
    }

    public PlayerStatsDto getPlayerStats(String idToken, String uid) {
        return getDocument(idToken, privateDocumentPath(uid, "stats"), PlayerStatsDto.class);
    }

    public void savePlayerAchievements(String idToken, String uid, PlayerAchievementsDto achievements) {
        saveDocument(idToken, privateDocumentPath(uid, "achievements"), achievements);
    }

    public PlayerAchievementsDto getPlayerAchievements(String idToken, String uid) {
        return getDocument(idToken, privateDocumentPath(uid, "achievements"), PlayerAchievementsDto.class);
    }

    public void savePlayerData(String idToken, String uid, PlayerDataDto playerData) {
        if (playerData == null) {
            return;
        }
        if (playerData.getProfile() != null) {
            saveUserProfile(idToken, uid, playerData.getProfile());
        }
        if (playerData.getSettings() != null) {
            savePlayerSettings(idToken, uid, playerData.getSettings());
        }
        if (playerData.getProgress() != null) {
            savePlayerProgress(idToken, uid, playerData.getProgress());
        }
        if (playerData.getStats() != null) {
            savePlayerStats(idToken, uid, playerData.getStats());
        }
        if (playerData.getAchievements() != null) {
            savePlayerAchievements(idToken, uid, playerData.getAchievements());
        }
    }

    public PlayerDataDto getPlayerData(String idToken, String uid) {
        return new PlayerDataDto(
                getUserProfileDto(idToken, uid),
                getPlayerSettings(idToken, uid),
                getPlayerProgress(idToken, uid),
                getPlayerStats(idToken, uid),
                getPlayerAchievements(idToken, uid)
        );
    }

    public void appendPlayerEvent(String idToken, String uid, PlayerEventDto event) {
        executeAuthorizedPost(documentUrl("users/" + uid + "/eventLogs"), idToken,
                FirestoreDtoMapper.toDocumentBody(event).toString());
    }

    private void saveDocument(String idToken, String documentPath, Object dto) {
        executeAuthorizedPatch(documentUrl(documentPath), idToken, FirestoreDtoMapper.toDocumentBody(dto).toString());
    }

    private <T> T getDocument(String idToken, String documentPath, Class<T> type) {
        JsonObject document = executeAuthorizedGet(documentUrl(documentPath), idToken);
        return FirestoreDtoMapper.fromDocument(document, type);
    }

    private JsonObject executeAuthorizedGet(String url, String idToken) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rawBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
            return gson.fromJson(rawBody, JsonObject.class);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw FirebaseErrorMapper.toException(0, null, e);
        }
    }

    private void executeAuthorizedPatch(String url, String idToken, String body) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .patch(RequestBody.create(body, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rawBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw FirebaseErrorMapper.toException(0, null, e);
        }
    }

    private void executeAuthorizedPost(String url, String idToken, String body) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .post(RequestBody.create(body, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rawBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw FirebaseErrorMapper.toException(0, null, e);
        }
    }

    private String baseDocumentsUrl() {
        config.requireConfigured();
        return "https://firestore.googleapis.com/v1/projects/" + config.getProjectId()
                + "/databases/(default)/documents";
    }

    private String documentUrl(String documentPath) {
        return baseDocumentsUrl() + "/" + documentPath;
    }

    private String privateDocumentPath(String uid, String documentName) {
        return "users/" + uid + "/private/" + documentName;
    }
}
