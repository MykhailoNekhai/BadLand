package ua.uni.core.exceptions.app;

public class AuthorizationException extends AppException {
    public AuthorizationException(String message) {
        super(message, Severity.ERROR);
    }

    public static AuthorizationException authorization(String message) {
        return new AuthorizationException(message);
    }
}
