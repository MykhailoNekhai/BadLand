package ua.uni.platform.auth;

import ua.uni.core.security.RuntimeSecrets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FirebaseConfig {
    private final String apiKey;
    private final String projectId;
    private final String hostingDomain;
    private final String storageBucket;

    public FirebaseConfig(String apiKey, String projectId, String hostingDomain, String storageBucket) {
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.hostingDomain = hostingDomain;
        this.storageBucket = storageBucket;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getHostingDomain() {
        return hostingDomain;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public static FirebaseConfig loadFromResources() {
        Properties properties = new Properties();
        try (InputStream input = FirebaseConfig.class.getClassLoader()
                .getResourceAsStream("game-resourses/config/firebase.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing resource: resourses/config/firebase.properties");
            }
            properties.load(input);
            String apiKey = resolve(properties, "apiKey", "FIREBASE_API_KEY");
            String projectId = resolve(properties, "projectId", "FIREBASE_PROJECT_ID");
            String hostingDomain = resolve(properties, "hostingDomain", "FIREBASE_HOSTING_DOMAIN");
            String storageBucket = resolve(properties, "storageBucket", "FIREBASE_STORAGE_BUCKET");
            if (apiKey == null || apiKey.isBlank() || projectId == null || projectId.isBlank()) {
                throw new IllegalStateException(
                    "Firebase API key and project ID must be set via firebase.properties or env vars FIREBASE_API_KEY / FIREBASE_PROJECT_ID");
            }
            String resolvedHostingDomain = hostingDomain == null || hostingDomain.isBlank()
                    ? projectId.trim() + ".web.app"
                    : hostingDomain.trim();
            String resolvedStorageBucket = storageBucket == null || storageBucket.isBlank()
                    ? projectId.trim() + ".firebasestorage.app"
                    : storageBucket.trim();
            return new FirebaseConfig(apiKey.trim(), projectId.trim(), resolvedHostingDomain, resolvedStorageBucket);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load firebase config", e);
        }
    }

    // Priority: env var → RuntimeSecrets (encrypted in code) → properties file.
    private static String resolve(Properties props, String propKey, String envKey) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) return envVal.trim();
        String built = builtInSecret(envKey);
        if (built != null) return built;
        String propVal = props.getProperty(propKey);
        if (propVal != null && propVal.startsWith("REPLACE_WITH_")) return null;
        return propVal;
    }

    private static String builtInSecret(String envKey) {
        if ("FIREBASE_API_KEY".equals(envKey)) return RuntimeSecrets.firebaseApiKey();
        return null;
    }
}
