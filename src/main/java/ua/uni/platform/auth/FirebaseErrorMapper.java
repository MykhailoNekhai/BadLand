package ua.uni.platform.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ua.uni.core.exceptions.firebase.*;

public class FirebaseErrorMapper {
    public enum StrongErrorCode {
        NETWORK_ERROR,
        RATE_LIMIT,
        EMAIL_EXISTS,
        EMAIL_NOT_FOUND,
        INVALID_PASSWORD,
        INVALID_LOGIN_CREDENTIALS,
        INVALID_EMAIL,
        USER_DISABLED,
        OPERATION_NOT_ALLOWED,
        INVALID_API_KEY,
        PROJECT_NOT_FOUND,
        CONFIGURATION_NOT_FOUND,
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

        String firebaseCode = extractFirebaseCode(responseBody);

        if (httpStatus == 429 || "TOO_MANY_ATTEMPTS_TRY_LATER".equals(firebaseCode)) return StrongErrorCode.RATE_LIMIT;
        if ("EMAIL_EXISTS".equals(firebaseCode)) return StrongErrorCode.EMAIL_EXISTS;
        if ("EMAIL_NOT_FOUND".equals(firebaseCode)) return StrongErrorCode.EMAIL_NOT_FOUND;
        if ("INVALID_PASSWORD".equals(firebaseCode)) return StrongErrorCode.INVALID_PASSWORD;
        if ("INVALID_LOGIN_CREDENTIALS".equals(firebaseCode)) return StrongErrorCode.INVALID_LOGIN_CREDENTIALS;
        if ("INVALID_EMAIL".equals(firebaseCode)) return StrongErrorCode.INVALID_EMAIL;
        if ("USER_DISABLED".equals(firebaseCode)) return StrongErrorCode.USER_DISABLED;
        if ("OPERATION_NOT_ALLOWED".equals(firebaseCode)) return StrongErrorCode.OPERATION_NOT_ALLOWED;
        if ("INVALID_API_KEY".equals(firebaseCode) || "API_KEY_INVALID".equals(firebaseCode)) {
            return StrongErrorCode.INVALID_API_KEY;
        }
        if ("PROJECT_NOT_FOUND".equals(firebaseCode)) return StrongErrorCode.PROJECT_NOT_FOUND;
        if ("CONFIGURATION_NOT_FOUND".equals(firebaseCode)) return StrongErrorCode.CONFIGURATION_NOT_FOUND;
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
            case EMAIL_NOT_FOUND, INVALID_PASSWORD, INVALID_LOGIN_CREDENTIALS, HTTP_401 ->
                    new FirebaseAuthException("Invalid email or password");
            case INVALID_EMAIL -> new FirebaseAuthException("Email address format is invalid");
            case USER_DISABLED -> new FirebaseAuthException("User account is disabled");
            case OPERATION_NOT_ALLOWED ->
                    new FirebaseClientException("Email/password sign-in is not enabled in Firebase");
            case INVALID_API_KEY ->
                    new FirebaseClientException("Firebase API key is invalid or rejected for this app");
            case PROJECT_NOT_FOUND ->
                    new FirebaseClientException("Firebase project was not found");
            case CONFIGURATION_NOT_FOUND ->
                    new FirebaseClientException("Firebase auth configuration is missing for this project");
            case INVALID_ID_TOKEN, HTTP_403 -> new FirebasePermissionException("Permission denied");
            case HTTP_404 -> new FirebaseNotFoundException("Requested Firebase resource was not found");
            case HTTP_5XX -> new FirebaseServerException("Firebase server error");
            case HTTP_4XX -> new FirebaseClientException("Firebase request failed");
            case UNKNOWN_ERROR -> new FirebaseUnknownException("Unknown Firebase error");
        };
    }

    public static RuntimeException toException(int httpStatus, String responseBody, Exception networkException) {
        StrongErrorCode code = collectStrongErrorCode(httpStatus, responseBody, networkException);
        RuntimeException exception = toException(code);
        String firebaseCode = extractFirebaseCode(responseBody);
        if (firebaseCode == null || isFriendlyHandled(code)) {
            return exception;
        }
        return new FirebaseClientException(exception.getMessage() + " (" + firebaseCode + ")");
    }

    private static boolean isFriendlyHandled(StrongErrorCode code) {
        return switch (code) {
            case NETWORK_ERROR,
                 RATE_LIMIT,
                 EMAIL_EXISTS,
                 EMAIL_NOT_FOUND,
                 INVALID_PASSWORD,
                 INVALID_LOGIN_CREDENTIALS,
                 INVALID_EMAIL,
                 USER_DISABLED,
                 OPERATION_NOT_ALLOWED,
                 INVALID_API_KEY,
                 PROJECT_NOT_FOUND,
                 CONFIGURATION_NOT_FOUND,
                 INVALID_ID_TOKEN,
                 HTTP_401,
                 HTTP_403,
                 HTTP_404,
                 HTTP_5XX -> true;
            case HTTP_4XX, UNKNOWN_ERROR -> false;
        };
    }

    private static String extractFirebaseCode(String responseBody) {
        try {
            if (responseBody == null || responseBody.isBlank()) {
                return null;
            }
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            if (!root.has("error")) {
                return null;
            }
            JsonObject error = root.getAsJsonObject("error");
            if (!error.has("message")) {
                return null;
            }
            return error.get("message").getAsString();
        } catch (Exception ignored) {
            return null;
        }
    }
}
