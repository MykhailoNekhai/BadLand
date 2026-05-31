package ua.uni.objects;


import ua.uni.exceptions.app.ValidationException;
import org.mindrot.jbcrypt.BCrypt;

public final class Password {
    private final String hash;

    private Password(String hash) {
        if (hash == null || hash.isBlank()) {
            throw ValidationException.validation("encrypted password must not be blank");
        }
        this.hash = hash;
    }

    public static Password fromRaw(String rawPassword) {
        return new Password(passwordEncryption(rawPassword));
    }

    public static Password fromEncrypted(String encryptedPassword) {
        return new Password(encryptedPassword);
    }

    public String getHash() {
        return hash;
    }

    public boolean matches(String rawPassword) {
        return checkPassword(rawPassword, hash);
    }

    public static String validate(String password) {
        if (password == null || password.isBlank()) {
            throw ValidationException.validation("password must not be blank");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,64}$")) {
            throw ValidationException.validation("password must be 8-64 chars, no spaces, contain upper, lower, digit, and special char");
        }
        return password;
    }

    public static String passwordEncryption(String password) {
        String validPassword = validate(password);
        return BCrypt.hashpw(validPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String rawPassword, String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw ValidationException.validation("encrypted password must not be blank");
        }
        String validPassword = validate(rawPassword);
        return BCrypt.checkpw(validPassword, encryptedPassword);
    }
}
