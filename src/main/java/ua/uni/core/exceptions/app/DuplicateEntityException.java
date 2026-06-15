package ua.uni.core.exceptions.app;

public class DuplicateEntityException extends AppException {
    public DuplicateEntityException(String message) {
        super(message, Severity.ERROR);
    }

    public static DuplicateEntityException duplicate(String message) {
        return new DuplicateEntityException(message);
    }
}
