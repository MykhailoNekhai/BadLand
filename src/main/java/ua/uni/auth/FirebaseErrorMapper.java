package ua.uni.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ua.uni.exceptions.firebase.*;

public class FirebaseErrorMapper {
    public enum StrongErrorCode {
        NETWORK_ERROR,
        RATE_LIMIT,
        EMAIL_EXISTS,
        EMAIL_NOT_FOUND,
        INVALID_PASSWORD,
        USER_DISABLED,
        INVALID_ID_TOKEN,
        HTTP_401,
        HTTP_403,
        HTTP_404,
        HTTP_5XX,
        HTTP_4XX,
        UNKNOWN_ERROR
    }

    private FirebaseErrorMapper() {
    }

    public static String collectErrorParam(int httpStatus, String responseBody, Exception networkException) {
        return collectStrongErrorCode(httpStatus, responseBody, networkException).name();
    }

    public static StrongErrorCode collectStrongErrorCode(int httpStatus, String responseBody, Exception networkException) {
        if (networkException != null) {
            return StrongErrorCode.NETWORK_ERROR;
        }

        String firebaseCode = null;
        try {
            if (responseBody != null && !responseBody.isBlank()) {
                JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
                if (root.has("error")) {
                    JsonObject error = root.getAsJsonObject("error");
                    if (error.has("message")) {
                        firebaseCode = error.get("message").getAsString();
                    }
                }
            }
        } catch (Exception ignored) {
            // keep firebaseCode null when response is not valid JSON
        }

        if (httpStatus == 429 || "TOO_MANY_ATTEMPTS_TRY_LATER".equals(firebaseCode)) return StrongErrorCode.RATE_LIMIT;
        if ("EMAIL_EXISTS".equals(firebaseCode)) return StrongErrorCode.EMAIL_EXISTS;
        if ("EMAIL_NOT_FOUND".equals(firebaseCode)) return StrongErrorCode.EMAIL_NOT_FOUND;
        if ("INVALID_PASSWORD".equals(firebaseCode)) return StrongErrorCode.INVALID_PASSWORD;
        if ("USER_DISABLED".equals(firebaseCode)) return StrongErrorCode.USER_DISABLED;
        if ("INVALID_ID_TOKEN".equals(firebaseCode)) return StrongErrorCode.INVALID_ID_TOKEN;
        if (httpStatus == 401) return StrongErrorCode.HTTP_401;
        if (httpStatus == 403) return StrongErrorCode.HTTP_403;
        if (httpStatus == 404) return StrongErrorCode.HTTP_404;
        if (httpStatus >= 500) return StrongErrorCode.HTTP_5XX;
        if (httpStatus >= 400) return StrongErrorCode.HTTP_4XX;
        return StrongErrorCode.UNKNOWN_ERROR;
    }

    public static RuntimeException toException(StrongErrorCode code) {
        return switch (code) {
            case NETWORK_ERROR -> new FirebaseNetworkException("Network error while calling Firebase");
            case RATE_LIMIT -> new FirebaseRateLimitException("Too many attempts. Try again later");
            case EMAIL_EXISTS -> new FirebaseAuthException("Email is already in use");
            case EMAIL_NOT_FOUND, INVALID_PASSWORD, HTTP_401 -> new FirebaseAuthException("Invalid email or password");
            case USER_DISABLED -> new FirebaseAuthException("User account is disabled");
            case INVALID_ID_TOKEN, HTTP_403 -> new FirebasePermissionException("Permission denied");
            case HTTP_404 -> new FirebaseNotFoundException("Requested Firebase resource was not found");
            case HTTP_5XX -> new FirebaseServerException("Firebase server error");
            case HTTP_4XX -> new FirebaseClientException("Firebase request failed");
            case UNKNOWN_ERROR -> new FirebaseUnknownException("Unknown Firebase error");
        };
    }

    public static RuntimeException toException(int httpStatus, String responseBody, Exception networkException) {
        StrongErrorCode code = collectStrongErrorCode(httpStatus, responseBody, networkException);
        return toException(code);
    }
}
