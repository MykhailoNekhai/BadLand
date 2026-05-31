package ua.uni.exceptions.app;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message, Severity.ERROR);
    }

    public static NotFoundException notFound(String message) {
        return new NotFoundException(message);
    }
}
