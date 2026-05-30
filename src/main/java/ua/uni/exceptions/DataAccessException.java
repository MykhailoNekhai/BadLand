package ua.uni.exceptions;

public class DataAccessException extends AppException {
    public DataAccessException(String message) {
        super(message, Severity.ERROR);
    }

    public static DataAccessException dataAccess(String message) {
        return new DataAccessException(message);
    }
}
