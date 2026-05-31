package ua.uni.objects;

import ua.uni.exceptions.app.ValidationException;

import java.util.Objects;

public final class Email {
    private final String value;

    public Email(String email) {
        this.value = validate(email).toLowerCase();
    }

    // Compatibility helper for existing call sites.
    public static String validate(String email) {
        if (email == null || email.isBlank()) {
            throw ValidationException.validation("email must not be blank");
        }
        String normalized = email.trim();
        if (!normalized.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw ValidationException.validation("Invalid email. Use format: name@domain.com (no spaces)");
        }
        return normalized;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
