package ua.uni.platform.auth;

import java.io.IOException;
import java.util.Properties;
import ua.uni.utility.config.ConfigProperties;

public class FirebaseConfig {
    private static final String RESOURCE_PATH = "game-resourses/config/firebase.properties";
    private static final String LOCAL_RESOURCE_PATH = "src/main/resources/" + RESOURCE_PATH;
    private static final String MISSING_REQUIRED_MESSAGE =
            "Firebase API key and project ID must be set via firebase.properties, .env, "
                    + "or env vars FIREBASE_API_KEY / FIREBASE_PROJECT_ID";
    private static final String NOT_CONFIGURED_MESSAGE =
            "Firebase is not configured. Set FIREBASE_API_KEY and FIREBASE_PROJECT_ID "
                    + "in .env, firebase.properties, or OS environment variables.";

    private final String apiKey;
    private final String projectId;
    private final String hostingDomain;
    private final String storageBucket;
    private final boolean configured;

    public FirebaseConfig(String apiKey, String projectId, String hostingDomain, String storageBucket) {
        this(apiKey, projectId, hostingDomain, storageBucket, true);
    }

    private FirebaseConfig(String apiKey, String projectId, String hostingDomain, String storageBucket,
                           boolean configured) {
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.hostingDomain = hostingDomain;
        this.storageBucket = storageBucket;
        this.configured = configured;
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

    public boolean isConfigured() {
        return configured;
    }

    public void requireConfigured() {
        if (!configured) {
            throw new IllegalStateException(NOT_CONFIGURED_MESSAGE);
        }
    }

    public static FirebaseConfig loadFromResources() {
        try {
            Properties properties = loadProperties();
            String apiKey = resolve(properties, "apiKey", "FIREBASE_API_KEY");
            String projectId = resolve(properties, "projectId", "FIREBASE_PROJECT_ID");
            String hostingDomain = resolve(properties, "hostingDomain", "FIREBASE_HOSTING_DOMAIN");
            String storageBucket = resolve(properties, "storageBucket", "FIREBASE_STORAGE_BUCKET");
            if (apiKey == null || apiKey.isBlank() || projectId == null || projectId.isBlank()) {
                if (!hasPartialFirebaseConfig(properties)) {
                    return disabled();
                }
                throw new IllegalStateException(MISSING_REQUIRED_MESSAGE);
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

    private static Properties loadProperties() throws IOException {
        return ConfigProperties.load(RESOURCE_PATH, LOCAL_RESOURCE_PATH);
    }

    private static FirebaseConfig disabled() {
        return new FirebaseConfig("", "", "", "", false);
    }

    private static boolean hasPartialFirebaseConfig(Properties properties) {
        return ConfigProperties.hasResolvedValue(properties, "apiKey", "FIREBASE_API_KEY")
                || ConfigProperties.hasResolvedValue(properties, "projectId", "FIREBASE_PROJECT_ID")
                || ConfigProperties.hasResolvedValue(properties, "hostingDomain", "FIREBASE_HOSTING_DOMAIN")
                || ConfigProperties.hasResolvedValue(properties, "storageBucket", "FIREBASE_STORAGE_BUCKET");
    }

    // Priority: environment variable, then .env, then ignored local properties file.
    private static String resolve(Properties props, String propKey, String envKey) {
        return ConfigProperties.resolve(props, propKey, envKey);
    }
}
