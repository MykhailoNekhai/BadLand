package ua.uni.platform.online;

import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import ua.uni.core.logging.AppLogger;

import java.util.concurrent.ExecutionException;

public class NakamaSocket {
    private static final String LOG_TAG = "Nakama";

    private final Client client;
    private final String host;
    private final int port;
    private final boolean ssl;
    private SocketClient socket;
    private EventListener eventListener;

    public interface EventListener {
        default void onDisconnect(Throwable throwable) {}
        default void onError(Error error) {}
        default void onMatchData(MatchData matchData) {}
        default void onMatchPresence(MatchPresenceEvent presenceEvent) {}
    }

    public NakamaSocket(Client client, String host, int port, boolean ssl) {
        this.client = client;
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    public void connect(Session session) {
        connect(session, null);
    }

    public void connect(Session session, EventListener eventListener) {
        this.eventListener = eventListener;
        socket = client.createSocket(host, port, ssl);

        SocketListener listener = new AbstractSocketListener() {
            @Override
            public void onDisconnect(Throwable throwable) {
                if (throwable != null) {
                    AppLogger.error(LOG_TAG, "Socket disconnected with error", throwable);
                    if (NakamaSocket.this.eventListener != null) {
                        NakamaSocket.this.eventListener.onDisconnect(throwable);
                    }
                    return;
                }
                AppLogger.info(LOG_TAG, "Socket disconnected.");
                if (NakamaSocket.this.eventListener != null) {
                    NakamaSocket.this.eventListener.onDisconnect(null);
                }
            }

            @Override
            public void onError(Error error) {
                AppLogger.error(LOG_TAG, "Socket error: " + error.getMessage(), null);
                if (NakamaSocket.this.eventListener != null) {
                    NakamaSocket.this.eventListener.onError(error);
                }
            }

            @Override
            public void onMatchData(MatchData matchData) {
                if (NakamaSocket.this.eventListener != null) {
                    NakamaSocket.this.eventListener.onMatchData(matchData);
                }
            }

            @Override
            public void onMatchPresence(MatchPresenceEvent presenceEvent) {
                if (NakamaSocket.this.eventListener != null) {
                    NakamaSocket.this.eventListener.onMatchPresence(presenceEvent);
                }
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

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
