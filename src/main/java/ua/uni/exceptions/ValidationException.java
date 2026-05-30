package ua.uni.exceptions;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message, Severity.WARN);
    }

    public static ValidationException validation(String message) {
        return new ValidationException(message);
    }
}
