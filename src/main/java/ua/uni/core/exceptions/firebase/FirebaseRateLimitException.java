package ua.uni.core.exceptions.firebase;

public class FirebaseRateLimitException extends FirebaseException {
    public FirebaseRateLimitException(String message) {
        super(message);
    }
}
