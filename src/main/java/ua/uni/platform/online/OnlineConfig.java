package ua.uni.platform.online;

import ua.uni.core.security.RuntimeSecrets;

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
        Properties properties = new Properties();
        try (InputStream input = OnlineConfig.class.getClassLoader()
                .getResourceAsStream("game-resourses/config/nakama.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing resource: game-resourses/config/nakama.properties");
            }
            properties.load(input);
            boolean enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));
            String host = resolveRequired(properties, "host", "NAKAMA_HOST");
            int apiPort = parsePort(properties, "apiPort", "NAKAMA_API_PORT");
            int socketPort = parsePort(properties, "socketPort", "NAKAMA_SOCKET_PORT");
            String serverKey = resolveRequired(properties, "serverKey", "NAKAMA_SERVER_KEY");
            boolean ssl = Boolean.parseBoolean(properties.getProperty("ssl", "false"));
            boolean trace = Boolean.parseBoolean(properties.getProperty("trace", "false"));
            boolean createAccountsByDefault = Boolean.parseBoolean(properties.getProperty("createAccountsByDefault", "true"));
            String gameplayHost = resolveRequired(properties, "gameplayHost", "NAKAMA_GAMEPLAY_HOST");
            int gameplayUdpPort = parsePort(properties, "gameplayUdpPort", "NAKAMA_GAMEPLAY_UDP_PORT");
            String gameplayTokenSecret = resolveRequired(properties, "gameplayTokenSecret", "NAKAMA_GAMEPLAY_TOKEN_SECRET");
            int gameplayTickRate = Integer.parseInt(properties.getProperty("gameplayTickRate", "30"));
            int gameplaySnapshotRate = Integer.parseInt(properties.getProperty("gameplaySnapshotRate", "15"));
            int gameplayHealthPort = Integer.parseInt(properties.getProperty("gameplayHealthPort", "8081"));
            return new OnlineConfig(enabled, host, apiPort, socketPort, serverKey, ssl, trace,
                    createAccountsByDefault, gameplayHost, gameplayUdpPort, gameplayTokenSecret,
                    gameplayTickRate, gameplaySnapshotRate, gameplayHealthPort);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load nakama config", e);
        }
    }

    // Priority: env var → RuntimeSecrets (encrypted in code) → properties file.
    private static String resolveRequired(Properties props, String propKey, String envKey) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) return envVal.trim();
        String built = builtInSecret(envKey);
        if (built != null) return built;
        String propVal = props.getProperty(propKey);
        if (propVal == null || propVal.isBlank() || propVal.startsWith("REPLACE_WITH_")) {
            throw new IllegalStateException(
                "No value for '" + propKey + "': set env var '" + envKey + "' or check nakama.properties");
        }
        return propVal.trim();
    }

    private static int parsePort(Properties properties, String key, String envKey) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) {
            try { return Integer.parseInt(envVal.trim()); }
            catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid integer in env var " + envKey, e);
            }
        }
        try {
            String val = properties.getProperty(key);
            if (val == null || val.isBlank()) {
                throw new IllegalStateException("nakama.properties must contain '" + key + "' or env var '" + envKey + "' must be set");
            }
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid integer for " + key, e);
        }
    }

    private static String builtInSecret(String envKey) {
        switch (envKey) {
            case "NAKAMA_SERVER_KEY":          return RuntimeSecrets.nakamaServerKey();
            case "NAKAMA_GAMEPLAY_TOKEN_SECRET": return RuntimeSecrets.nakamaTokenSecret();
            case "NAKAMA_HOST":                return RuntimeSecrets.nakamaHost();
            case "NAKAMA_GAMEPLAY_HOST":       return RuntimeSecrets.nakamaHost();
            default:                           return null;
        }
    }
}
