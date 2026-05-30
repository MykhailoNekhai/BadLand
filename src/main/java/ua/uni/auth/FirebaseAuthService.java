package ua.uni.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class FirebaseAuthService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final FirebaseConfig config;

    public FirebaseAuthService(FirebaseConfig config) {
        this.config = config;
    }

    public AuthResult signUp(String email, String password) throws IOException {
        return authenticate("accounts:signUp", email, password);
    }

    public AuthResult signIn(String email, String password) throws IOException {
        return authenticate("accounts:signInWithPassword", email, password);
    }

    public void sendEmailVerification(String idToken) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("requestType", "VERIFY_EMAIL");
        payload.addProperty("idToken", idToken);
        post("accounts:sendOobCode", payload);
    }

    public boolean isEmailVerified(String idToken) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("idToken", idToken);
        JsonObject response = post("accounts:lookup", payload);
        JsonArray users = response.getAsJsonArray("users");
        if (users == null || users.isEmpty()) {
            return false;
        }
        JsonObject user = users.get(0).getAsJsonObject();
        return user.has("emailVerified") && user.get("emailVerified").getAsBoolean();
    }

    private AuthResult authenticate(String endpoint, String email, String password) throws IOException {
        JsonObject payload = new JsonObject();
        payload.addProperty("email", email);
        payload.addProperty("password", password);
        payload.addProperty("returnSecureToken", true);
        JsonObject json = post(endpoint, payload);
        return new AuthResult(
                json.get("idToken").getAsString(),
                json.get("localId").getAsString(),
                json.get("refreshToken").getAsString(),
                json.get("email").getAsString()
        );
    }

    private JsonObject post(String endpoint, JsonObject payload) throws IOException {
        String url = "https://identitytoolkit.googleapis.com/v1/" + endpoint + "?key=" + config.getApiKey();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(payload.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Auth failed: HTTP " + response.code());
            }
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
            if (json.has("error")) {
                String msg = json.getAsJsonObject("error").get("message").getAsString();
                throw new IOException("Auth failed: " + msg);
            }
            return json;
        }
    }

    public record AuthResult(String idToken, String uid, String refreshToken, String email) {
    }
}
