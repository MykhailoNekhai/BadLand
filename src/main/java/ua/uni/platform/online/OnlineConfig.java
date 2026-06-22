package ua.uni.platform.online;

import java.io.IOException;
import java.util.Properties;
import ua.uni.utility.config.ConfigProperties;

public class OnlineConfig {
    private static final String RESOURCE_PATH = "game-resourses/config/nakama.properties";
    private static final String LOCAL_RESOURCE_PATH = "src/main/resources/" + RESOURCE_PATH;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_API_PORT = 7349;
    private static final int DEFAULT_SOCKET_PORT = 7350;
    private static final int DEFAULT_GAMEPLAY_UDP_PORT = 7360;
    private static final int DEFAULT_GAMEPLAY_HEALTH_PORT = 8081;

    private final boolean enabled;
    private final String host;
    private final int apiPort;
    private final int socketPort;
    private final String serverKey;
    private final boolean ssl;
    private final boolean trace;
    private final boolean createAccountsByDefault;
    private final String gameplayHost;
    private final int gameplayUdpPort;
    private final String gameplayTokenSecret;
    private final int gameplayTickRate;
    private final int gameplaySnapshotRate;
    private final int gameplayHealthPort;

    public OnlineConfig(boolean enabled, String host, int apiPort, int socketPort,
                        String serverKey, boolean ssl, boolean trace, boolean createAccountsByDefault,
                        String gameplayHost, int gameplayUdpPort, String gameplayTokenSecret,
                        int gameplayTickRate, int gameplaySnapshotRate, int gameplayHealthPort) {
        this.enabled = enabled;
        this.host = host;
        this.apiPort = apiPort;
        this.socketPort = socketPort;
        this.serverKey = serverKey;
        this.ssl = ssl;
        this.trace = trace;
        this.createAccountsByDefault = createAccountsByDefault;
        this.gameplayHost = gameplayHost;
        this.gameplayUdpPort = gameplayUdpPort;
        this.gameplayTokenSecret = gameplayTokenSecret;
        this.gameplayTickRate = gameplayTickRate;
        this.gameplaySnapshotRate = gameplaySnapshotRate;
        this.gameplayHealthPort = gameplayHealthPort;
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

    public String getGameplayHost() {
        return gameplayHost;
    }

    public int getGameplayUdpPort() {
        return gameplayUdpPort;
    }

    public String getGameplayTokenSecret() {
        return gameplayTokenSecret;
    }

    public int getGameplayTickRate() {
        return gameplayTickRate;
    }

    public int getGameplaySnapshotRate() {
        return gameplaySnapshotRate;
    }

    public int getGameplayHealthPort() {
        return gameplayHealthPort;
    }

    public static OnlineConfig loadFromResources() {
        try {
            Properties properties = loadProperties();
            boolean enabled = resolveEnabled(properties);
            if (!enabled) {
                return disabled();
            }

            String host = resolveRequired(properties, "host", "NAKAMA_HOST");
            int apiPort = parsePort(properties, "apiPort", "NAKAMA_API_PORT", DEFAULT_API_PORT);
            int socketPort = parsePort(properties, "socketPort", "NAKAMA_SOCKET_PORT", DEFAULT_SOCKET_PORT);
            String serverKey = resolveRequired(properties, "serverKey", "NAKAMA_SERVER_KEY");
            boolean ssl = Boolean.parseBoolean(resolve(properties, "ssl", "NAKAMA_SSL", "false"));
            boolean trace = Boolean.parseBoolean(resolve(properties, "trace", "NAKAMA_TRACE", "false"));
            boolean createAccountsByDefault = Boolean.parseBoolean(
                    resolve(properties, "createAccountsByDefault", "NAKAMA_CREATE_ACCOUNTS_BY_DEFAULT", "true"));
            String gameplayHost = resolveRequired(properties, "gameplayHost", "NAKAMA_GAMEPLAY_HOST");
            int gameplayUdpPort = parsePort(properties, "gameplayUdpPort", "NAKAMA_GAMEPLAY_UDP_PORT",
                    DEFAULT_GAMEPLAY_UDP_PORT);
            String gameplayTokenSecret = resolveRequired(properties, "gameplayTokenSecret", "NAKAMA_GAMEPLAY_TOKEN_SECRET");
            int gameplayTickRate = parseInt(properties, "gameplayTickRate", "NAKAMA_GAMEPLAY_TICK_RATE", 30);
            int gameplaySnapshotRate = parseInt(properties, "gameplaySnapshotRate",
                    "NAKAMA_GAMEPLAY_SNAPSHOT_RATE", 15);
            int gameplayHealthPort = parseInt(properties, "gameplayHealthPort", "NAKAMA_GAMEPLAY_HEALTH_PORT",
                    DEFAULT_GAMEPLAY_HEALTH_PORT);
            return new OnlineConfig(enabled, host, apiPort, socketPort, serverKey, ssl, trace,
                    createAccountsByDefault, gameplayHost, gameplayUdpPort, gameplayTokenSecret,
                    gameplayTickRate, gameplaySnapshotRate, gameplayHealthPort);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load nakama config", e);
        }
    }

    private static Properties loadProperties() throws IOException {
        return ConfigProperties.load(RESOURCE_PATH, LOCAL_RESOURCE_PATH);
    }

    private static OnlineConfig disabled() {
        return new OnlineConfig(false, DEFAULT_HOST, DEFAULT_API_PORT, DEFAULT_SOCKET_PORT,
                "defaultkey", false, false, true, DEFAULT_HOST, DEFAULT_GAMEPLAY_UDP_PORT,
                "", 30, 15, DEFAULT_GAMEPLAY_HEALTH_PORT);
    }

    private static boolean resolveEnabled(Properties properties) {
        String explicit = resolve(properties, "enabled", "NAKAMA_ENABLED");
        if (explicit != null) {
            return Boolean.parseBoolean(explicit);
        }
        return hasRequiredConfig(properties);
    }

    private static boolean hasRequiredConfig(Properties properties) {
        return ConfigProperties.hasResolvedValue(properties, "host", "NAKAMA_HOST")
                && ConfigProperties.hasResolvedValue(properties, "serverKey", "NAKAMA_SERVER_KEY")
                && ConfigProperties.hasResolvedValue(properties, "gameplayHost", "NAKAMA_GAMEPLAY_HOST")
                && ConfigProperties.hasResolvedValue(properties, "gameplayTokenSecret", "NAKAMA_GAMEPLAY_TOKEN_SECRET");
    }

    // Priority: environment variable, then .env, then ignored local properties file.
    private static String resolveRequired(Properties props, String propKey, String envKey) {
        String value = resolve(props, propKey, envKey);
        if (value == null) {
            throw new IllegalStateException(
                "No value for '" + propKey + "': set env var '" + envKey + "', .env, or check nakama.properties");
        }
        return value;
    }

    private static int parsePort(Properties properties, String key, String envKey, int defaultValue) {
        return parseInt(properties, key, envKey, defaultValue);
    }

    private static int parseInt(Properties properties, String key, String envKey, int defaultValue) {
        String value = resolve(properties, key, envKey);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer for " + key + " / " + envKey, e);
        }
    }

    private static String resolve(Properties properties, String propKey, String envKey) {
        return ConfigProperties.resolve(properties, propKey, envKey);
    }

    private static String resolve(Properties properties, String propKey, String envKey, String defaultValue) {
        return ConfigProperties.resolve(properties, propKey, envKey, defaultValue);
    }
}
