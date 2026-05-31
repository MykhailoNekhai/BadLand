package ua.uni.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseAuthService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final FirebaseConfig config;

    public FirebaseAuthService(FirebaseConfig config) {
        this.config = config;
    }

    public AuthResult signUp(String email, String password) {
        return authenticate("accounts:signUp", email, password);
    }

    public AuthResult signIn(String email, String password) {
        return authenticate("accounts:signInWithPassword", email, password);
    }

    public void sendEmailVerification(String idToken) {
        JsonObject payload = new JsonObject();
        payload.addProperty("requestType", "VERIFY_EMAIL");
        payload.addProperty("idToken", idToken);
        post("accounts:sendOobCode", payload);
    }

    public boolean isEmailVerified(String idToken) {
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

    private AuthResult authenticate(String endpoint, String email, String password) {
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

    private JsonObject post(String endpoint, JsonObject payload) {
        String url = "https://identitytoolkit.googleapis.com/v1/" + endpoint + "?key=" + config.getApiKey();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(payload.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rawBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
            JsonObject json = gson.fromJson(rawBody, JsonObject.class);
            if (json != null && json.has("error")) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
            return json;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw FirebaseErrorMapper.toException(0, null, e);
        }
    }

    public record AuthResult(String idToken, String uid, String refreshToken, String email) {
    }
}
