package ua.uni.online;

import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import ua.uni.logging.AppLogger;

import java.util.concurrent.ExecutionException;

public class NakamaSocket {
    private static final String LOG_TAG = "Nakama";

    private final Client client;
    private final String host;
    private final int port;
    private final boolean ssl;
    private SocketClient socket;

    public NakamaSocket(Client client, String host, int port, boolean ssl) {
        this.client = client;
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    public void connect(Session session) {
        socket = client.createSocket(host, port, ssl);

        SocketListener listener = new AbstractSocketListener() {
            @Override
            public void onDisconnect(Throwable throwable) {
                if (throwable != null) {
                    AppLogger.error(LOG_TAG, "Socket disconnected with error", throwable);
                    return;
                }
                AppLogger.info(LOG_TAG, "Socket disconnected.");
            }
        };

        try {
            socket.connect(session, listener).get();
            AppLogger.info(LOG_TAG, "Socket connected.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while connecting Nakama socket", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to connect Nakama socket", e);
        }
    }

    public SocketClient getSocket() {
        return socket;
    }
}
