package ua.uni.game;

public final class RuntimeProfile {
    private static final String PROFILE_PROPERTY = "badland.profile";

    private RuntimeProfile() {}

    public static String get() {
        String profile = System.getProperty(PROFILE_PROPERTY, "").trim();
        return profile.isEmpty() ? "default" : profile;
    }

    public static boolean isDefault() {
        return "default".equals(get());
    }

    public static String suffix() {
        return isDefault() ? "" : "_" + sanitize(get());
    }

    public static String prefsName(String baseName) {
        return baseName + suffix();
    }

    private static String sanitize(String value) {
        String sanitized = value.toLowerCase().replaceAll("[^a-z0-9_\\-]", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        return sanitized.isEmpty() ? "default" : sanitized;
    }
}
