package ua.uni.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class FirestoreService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final FirebaseConfig config;

    public FirestoreService(FirebaseConfig config) {
        this.config = config;
    }

    public void createUserProfile(String idToken, String uid, String nickname, String email, String language)
            throws IOException {
        JsonObject fields = new JsonObject();
        fields.add("nickname", stringValue(nickname));
        fields.add("email", stringValue(email));
        fields.add("language", stringValue(language));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        String url = baseDocumentsUrl() + "/users?documentId=" + uid;
        executeAuthorizedPost(url, idToken, body.toString());
    }

    public JsonObject getUserProfile(String idToken, String uid) throws IOException {
        String url = baseDocumentsUrl() + "/users/" + uid;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Firestore get profile failed: HTTP " + response.code());
            }
            return gson.fromJson(response.body().string(), JsonObject.class);
        }
    }

    private void executeAuthorizedPost(String url, String idToken, String body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + idToken)
                .post(RequestBody.create(body, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Firestore write failed: HTTP " + response.code());
            }
        }
    }

    private String baseDocumentsUrl() {
        return "https://firestore.googleapis.com/v1/projects/" + config.getProjectId()
                + "/databases/(default)/documents";
    }

    private JsonObject stringValue(String value) {
        JsonObject object = new JsonObject();
        object.addProperty("stringValue", value);
        return object;
    }
}
