package ua.uni.exceptions.app;

public class AuthorizationException extends AppException {
    public AuthorizationException(String message) {
        super(message, Severity.ERROR);
    }

    public static AuthorizationException authorization(String message) {
        return new AuthorizationException(message);
    }
}
