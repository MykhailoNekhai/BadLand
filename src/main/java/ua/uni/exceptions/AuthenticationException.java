package ua.uni.exceptions;

public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(message, Severity.ERROR);
    }

    public static AuthenticationException authentication(String message) {
        return new AuthenticationException(message);
    }
}
