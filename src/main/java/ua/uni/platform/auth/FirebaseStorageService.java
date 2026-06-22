package ua.uni.platform.auth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FirebaseStorageService {
    private static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");
    private final OkHttpClient client = FirebaseHttpClient.INSTANCE;
    private final Gson gson = new Gson();
    private final FirebaseConfig config;

    public FirebaseStorageService(FirebaseConfig config) {
        this.config = config;
    }

    public String uploadAvatar(String idToken, String uid, byte[] imageBytes) {
        String objectPath = "avatars/" + uid + "/avatar";
        String encodedPath = URLEncoder.encode(objectPath, StandardCharsets.UTF_8);
        String url = "https://firebasestorage.googleapis.com/v0/b/"
                + config.getStorageBucket() + "/o?uploadType=media&name=" + encodedPath;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .post(RequestBody.create(imageBytes, OCTET_STREAM))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new RuntimeException("Avatar upload failed: " + response.code() + " " + body);
            }
            JsonObject json = gson.fromJson(body, JsonObject.class);
            JsonElement tokenEl = json != null ? json.get("downloadTokens") : null;
            String token = tokenEl != null && !tokenEl.isJsonNull() ? tokenEl.getAsString() : "";
            return buildDownloadUrl(objectPath, token);
        } catch (IOException e) {
            throw new RuntimeException("Avatar upload failed", e);
        }
    }

    public byte[] downloadBytes(String url, String idToken) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Avatar download failed: " + response.code());
            }
            return response.body().bytes();
        } catch (IOException e) {
            throw new RuntimeException("Avatar download failed", e);
        }
    }

    private String buildDownloadUrl(String objectPath, String token) {
        String encoded = URLEncoder.encode(objectPath, StandardCharsets.UTF_8);
        String url = "https://firebasestorage.googleapis.com/v0/b/"
                + config.getStorageBucket() + "/o/" + encoded + "?alt=media";
        if (token != null && !token.isBlank()) {
            url += "&token=" + token;
        }
        return url;
    }
}
