package ua.uni.utility.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigProperties {
    private static final String DOT_ENV_PATH = ".env";

    private ConfigProperties() {
    }

    public static Properties load(String resourcePath, String localResourcePath) throws IOException {
        Properties properties = new Properties();
        loadClasspath(properties, resourcePath);
        loadFile(properties, Path.of(localResourcePath));
        loadFile(properties, Path.of(DOT_ENV_PATH));
        return properties;
    }

    public static String resolve(Properties properties, String propKey, String envKey) {
        String envValue = clean(System.getenv(envKey));
        if (envValue != null) {
            return envValue;
        }

        String dotenvValue = clean(properties.getProperty(envKey));
        if (dotenvValue != null) {
            return dotenvValue;
        }

        return clean(properties.getProperty(propKey));
    }

    public static String resolve(Properties properties, String propKey, String envKey, String defaultValue) {
        String value = resolve(properties, propKey, envKey);
        return value == null ? defaultValue : value;
    }

    public static boolean hasResolvedValue(Properties properties, String propKey, String envKey) {
        return resolve(properties, propKey, envKey) != null;
    }

    private static void loadClasspath(Properties properties, String resourcePath) throws IOException {
        try (InputStream input = ConfigProperties.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input != null) {
                properties.load(input);
            }
        }
    }

    private static void loadFile(Properties properties, Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        }
    }

    private static String clean(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        if (cleaned.isBlank() || cleaned.startsWith("REPLACE_WITH_")) {
            return null;
        }
        if ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }
        return cleaned.isBlank() || cleaned.startsWith("REPLACE_WITH_") ? null : cleaned;
    }
}
