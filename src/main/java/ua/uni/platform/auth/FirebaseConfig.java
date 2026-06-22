package ua.uni.platform.auth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FirebaseConfig {
    private static final String RESOURCE_PATH = "game-resourses/config/firebase.properties";
    private static final String LOCAL_RESOURCE_PATH = "src/main/resources/" + RESOURCE_PATH;

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
        try {
            Properties properties = loadProperties();
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

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = FirebaseConfig.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
            if (input != null) {
                properties.load(input);
                return properties;
            }
        }

        Path localPath = Path.of(LOCAL_RESOURCE_PATH);
        if (Files.isRegularFile(localPath)) {
            try (InputStream input = Files.newInputStream(localPath)) {
                properties.load(input);
            }
        }
        return properties;
    }

    // Priority: environment variable, then ignored local properties file.
    private static String resolve(Properties props, String propKey, String envKey) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) return envVal.trim();
        String propVal = props.getProperty(propKey);
        if (propVal != null && propVal.startsWith("REPLACE_WITH_")) return null;
        return propVal;
    }
}
