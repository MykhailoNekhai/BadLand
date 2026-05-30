package ua.uni.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FirebaseConfig {
    private final String apiKey;
    private final String projectId;

    public FirebaseConfig(String apiKey, String projectId) {
        this.apiKey = apiKey;
        this.projectId = projectId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProjectId() {
        return projectId;
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
            if (apiKey == null || apiKey.isBlank() || projectId == null || projectId.isBlank()) {
                throw new IllegalStateException("firebase.properties must contain apiKey and projectId");
            }
            return new FirebaseConfig(apiKey.trim(), projectId.trim());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load firebase config", e);
        }
    }
}
