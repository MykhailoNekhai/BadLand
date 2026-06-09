package ua.uni.online;

import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import ua.uni.logging.AppLogger;

import java.util.concurrent.ExecutionException;

public class NakamaMatchService {
    private static final String LOG_TAG = "NakamaMatch";

    private final NakamaSocket nakamaSocket;
    private SocketClient socket;

    public NakamaMatchService(NakamaSocket nakamaSocket) {
        this.nakamaSocket = nakamaSocket;
    }

    public void connect(Session session) {
        nakamaSocket.connect(session);
        socket = nakamaSocket.getSocket();
    }

    public boolean isConnected() {
        return socket != null;
    }

    public Match createMatch() {
        requireConnected();
        try {
            Match match = socket.createMatch().get();
            AppLogger.info(LOG_TAG, "Created Nakama match id=" + match.getMatchId());
            return match;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while creating Nakama match", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to create Nakama match", e);
        }
    }

    public Match joinMatch(String matchId) {
        requireConnected();
        try {
            Match match = socket.joinMatch(matchId).get();
            AppLogger.info(LOG_TAG, "Joined Nakama match id=" + match.getMatchId());
            return match;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while joining Nakama match", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to join Nakama match", e);
        }
    }

    public void leaveMatch(String matchId) {
        requireConnected();
        try {
            socket.leaveMatch(matchId).get();
            AppLogger.info(LOG_TAG, "Left Nakama match id=" + matchId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while leaving Nakama match", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to leave Nakama match", e);
        }
    }

    public void sendMatchData(String matchId, long opCode, byte[] payload) {
        requireConnected();
        socket.sendMatchData(matchId, opCode, payload);
    }

    public void disconnect() {
        if (socket == null) {
            return;
        }
        try {
            socket.disconnect().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while disconnecting Nakama socket", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to disconnect Nakama socket", e);
        } finally {
            socket = null;
        }
    }

    private void requireConnected() {
        if (socket == null) {
            throw new IllegalStateException("Nakama socket is not connected");
        }
    }
}
