package ua.uni.platform.online;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;

public class NakamaClient {
    private final String serverKey;
    private final String host;
    private final int port;
    private final boolean ssl;
    private final int deadlineAfterMs;
    private final long keepAliveTimeMs;
    private final long keepAliveTimeoutMs;
    private final boolean trace;

    public NakamaClient() {
        this("defaultkey", "127.0.0.1", 7349, false, 0, Long.MAX_VALUE, 0L, false);
    }

    public NakamaClient(OnlineConfig config) {
        this(config.getServerKey(), config.getHost(), config.getApiPort(), config.isSsl(),
                0, Long.MAX_VALUE, 0L, config.isTrace());
    }

    public NakamaClient(String serverKey, String host, int port, boolean ssl,
                        int deadlineAfterMs, long keepAliveTimeMs, long keepAliveTimeoutMs, boolean trace) {
        this.serverKey = serverKey;
        this.host = host;
        this.port = port;
        this.ssl = ssl;
        this.deadlineAfterMs = deadlineAfterMs;
        this.keepAliveTimeMs = keepAliveTimeMs;
        this.keepAliveTimeoutMs = keepAliveTimeoutMs;
        this.trace = trace;
    }

    public Client createClient() {
        return new DefaultClient(serverKey, host, port, ssl, deadlineAfterMs, keepAliveTimeMs, keepAliveTimeoutMs, trace);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isSsl() {
        return ssl;
    }
}
