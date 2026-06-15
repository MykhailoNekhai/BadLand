package ua.uni.platform.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FirebaseConfig {
    private final String apiKey;
    private final String projectId;
    private final String hostingDomain;

    public FirebaseConfig(String apiKey, String projectId, String hostingDomain) {
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.hostingDomain = hostingDomain;
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

    public static FirebaseConfig loadFromResources() {
        Properties properties = new Properties();
        try (InputStream input = FirebaseConfig.class.getClassLoader()
                .getResourceAsStream("game-resourses/config/firebase.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing resource: resourses/config/firebase.properties");
            }
            properties.load(input);
            String apiKey = properties.getProperty("apiKey");
            String projectId = properties.getProperty("projectId");
            String hostingDomain = properties.getProperty("hostingDomain");
            if (apiKey == null || apiKey.isBlank() || projectId == null || projectId.isBlank()) {
                throw new IllegalStateException("firebase.properties must contain apiKey and projectId");
            }
            String resolvedHostingDomain = hostingDomain == null || hostingDomain.isBlank()
                    ? projectId.trim() + ".web.app"
                    : hostingDomain.trim();
            return new FirebaseConfig(apiKey.trim(), projectId.trim(), resolvedHostingDomain);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load firebase config", e);
        }
    }
}
