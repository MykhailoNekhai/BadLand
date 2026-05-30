package ua.uni.exceptions;

public class AppException extends RuntimeException {
    public enum Severity {
        WARN,
        ERROR
    }

    private final Severity severity;

    protected AppException(String userMessage, Severity severity) {
        super(userMessage);
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("Exception message must not be blank");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Exception severity must not be null");
        }
        this.severity = severity;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String toUiMessage() {
        return getMessage();
    }
}
