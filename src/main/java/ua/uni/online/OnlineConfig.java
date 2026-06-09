package ua.uni.online;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OnlineConfig {
    private final boolean enabled;
    private final String host;
    private final int apiPort;
    private final int socketPort;
    private final String serverKey;
    private final boolean ssl;
    private final boolean trace;
    private final boolean createAccountsByDefault;

    public OnlineConfig(boolean enabled, String host, int apiPort, int socketPort,
                        String serverKey, boolean ssl, boolean trace, boolean createAccountsByDefault) {
        this.enabled = enabled;
        this.host = host;
        this.apiPort = apiPort;
        this.socketPort = socketPort;
        this.serverKey = serverKey;
        this.ssl = ssl;
        this.trace = trace;
        this.createAccountsByDefault = createAccountsByDefault;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getApiPort() {
        return apiPort;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public String getServerKey() {
        return serverKey;
    }

    public boolean isSsl() {
        return ssl;
    }

    public boolean isTrace() {
        return trace;
    }

    public boolean isCreateAccountsByDefault() {
        return createAccountsByDefault;
    }

    public static OnlineConfig loadFromResources() {
        Properties properties = new Properties();
        try (InputStream input = OnlineConfig.class.getClassLoader()
                .getResourceAsStream("game-resourses/config/nakama.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing resource: game-resourses/config/nakama.properties");
            }
            properties.load(input);
            boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
            String host = required(properties, "host");
            int apiPort = parsePort(properties, "apiPort");
            int socketPort = parsePort(properties, "socketPort");
            String serverKey = required(properties, "serverKey");
            boolean ssl = Boolean.parseBoolean(properties.getProperty("ssl", "false"));
            boolean trace = Boolean.parseBoolean(properties.getProperty("trace", "false"));
            boolean createAccountsByDefault = Boolean.parseBoolean(properties.getProperty("createAccountsByDefault", "true"));
            return new OnlineConfig(enabled, host, apiPort, socketPort, serverKey, ssl, trace, createAccountsByDefault);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load nakama config", e);
        }
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("nakama.properties must contain " + key);
        }
        return value.trim();
    }

    private static int parsePort(Properties properties, String key) {
        try {
            return Integer.parseInt(required(properties, key));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer for " + key, e);
        }
    }
}
