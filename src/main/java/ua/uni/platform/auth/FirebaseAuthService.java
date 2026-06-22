package ua.uni.platform.auth;

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
    private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static final String ACTION_HANDLER_PATH = "/web/action/";
    private final OkHttpClient client = FirebaseHttpClient.INSTANCE;
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
        payload.addProperty("continueUrl", buildActionHandlerUrl());
        post("accounts:sendOobCode", payload);
    }

    public AuthResult updateEmail(String idToken, String newEmail) {
        JsonObject payload = new JsonObject();
        payload.addProperty("idToken", idToken);
        payload.addProperty("email", newEmail);
        payload.addProperty("returnSecureToken", true);
        JsonObject json = requireResponse(post("accounts:update", payload), "accounts:update");
        return new AuthResult(
                getString(json, "idToken"),
                getString(json, "localId"),
                getString(json, "refreshToken"),
                getString(json, "email")
        );
    }

    public void sendPasswordResetEmail(String email) {
        JsonObject payload = new JsonObject();
        payload.addProperty("requestType", "PASSWORD_RESET");
        payload.addProperty("email", email);
        payload.addProperty("continueUrl", buildActionHandlerUrl());
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

    public AccountMetadata getAccountMetadata(String idToken) {
        JsonObject payload = new JsonObject();
        payload.addProperty("idToken", idToken);
        JsonObject response = post("accounts:lookup", payload);
        JsonArray users = response.getAsJsonArray("users");
        if (users == null || users.isEmpty()) {
            return new AccountMetadata("", "", false);
        }
        JsonObject user = users.get(0).getAsJsonObject();
        String createdAt = user.has("createdAt") ? user.get("createdAt").getAsString() : "";
        String lastLoginAt = user.has("lastLoginAt") ? user.get("lastLoginAt").getAsString() : "";
        boolean emailVerified = user.has("emailVerified") && user.get("emailVerified").getAsBoolean();
        return new AccountMetadata(createdAt, lastLoginAt, emailVerified);
    }

    public AuthResult refreshIdToken(String refreshToken, String fallbackEmail) {
        config.requireConfigured();
        String url = "https://securetoken.googleapis.com/v1/token?key=" + config.getApiKey();
        String body = "grant_type=refresh_token&refresh_token=" + refreshToken;
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, FORM))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rawBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
            JsonObject json = gson.fromJson(rawBody, JsonObject.class);
            if (json == null || !json.has("id_token") || !json.has("refresh_token") || !json.has("user_id")) {
                throw FirebaseErrorMapper.toException(response.code(), rawBody, null);
            }
            return new AuthResult(
                    json.get("id_token").getAsString(),
                    json.get("user_id").getAsString(),
                    json.get("refresh_token").getAsString(),
                    fallbackEmail == null ? "" : fallbackEmail
            );
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw FirebaseErrorMapper.toException(0, null, e);
        }
    }

    private AuthResult authenticate(String endpoint, String email, String password) {
        JsonObject payload = new JsonObject();
        payload.addProperty("email", email);
        payload.addProperty("password", password);
        payload.addProperty("returnSecureToken", true);
        JsonObject json = requireResponse(post(endpoint, payload), endpoint);
        return new AuthResult(
                getString(json, "idToken"),
                getString(json, "localId"),
                getString(json, "refreshToken"),
                getString(json, "email")
        );
    }

    private static JsonObject requireResponse(JsonObject json, String endpoint) {
        if (json == null) {
            throw new ua.uni.core.exceptions.firebase.FirebaseServerException(
                "Empty response from Firebase endpoint: " + endpoint);
        }
        return json;
    }

    private static String getString(JsonObject json, String field) {
        if (!json.has(field) || json.get(field).isJsonNull()) {
            throw new ua.uni.core.exceptions.firebase.FirebaseServerException(
                "Missing field '" + field + "' in Firebase response");
        }
        return json.get(field).getAsString();
    }

    private JsonObject post(String endpoint, JsonObject payload) {
        config.requireConfigured();
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

    private String buildActionHandlerUrl() {
        return "https://" + config.getHostingDomain() + ACTION_HANDLER_PATH;
    }

    public record AuthResult(String idToken, String uid, String refreshToken, String email) {
    }

    public record AccountMetadata(String createdAt, String lastLoginAt, boolean emailVerified) {
    }
}
