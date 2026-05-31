package ua.uni.exceptions.firebase;

public class FirebaseRateLimitException extends FirebaseException {
    public FirebaseRateLimitException(String message) {
        super(message);
    }
}
