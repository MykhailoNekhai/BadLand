package ua.uni.platform.online.lobby;

import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.UserPresence;
import ua.uni.platform.online.CoopProtocol;
import ua.uni.platform.online.NakamaMatchService;
import ua.uni.presentation.screen.menu.settings.LanguageButton;
import ua.uni.utility.serialization.Serialization;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CoopLobbyController {
    private static final int MAX_LEVELS = 10;

    private final NakamaMatchService nakamaMatch;
    private final Listener listener;

    private final Map<String, LobbyPlayer> players = new LinkedHashMap<>();
    private String currentMatchId;
    private String selfUserId;
    private String hostUserId;
    private boolean isHost;
    private boolean isReady;
    private boolean gameStarting;
    private boolean preserveConnectionOnDispose;
    private int selectedLevel = 1;

    public interface Listener {
        void onStateChanged(String statusMessage);
        void onStartLevel(int level, boolean keepConnection);
        void onHostLeft();
    }

    public CoopLobbyController(NakamaMatchService nakamaMatch, Listener listener) {
        this.nakamaMatch = nakamaMatch;
        this.listener = listener;
    }

    public void onMatchCreated(String selfId, Match match) {
        selfUserId = selfId;
        currentMatchId = match.getMatchId();
        hostUserId = selfId;
        isHost = true;
        isReady = false;
        selectedLevel = 1;
        gameStarting = false;
        preserveConnectionOnDispose = false;
        players.clear();
        hydratePlayersFromMatch(match);
        broadcastLobbySnapshot();
        listener.onStateChanged(LanguageButton.t("MATCH_CREATED_WAITING"));
    }

    public void onMatchJoined(String selfId, Match match) {
        selfUserId = selfId;
        currentMatchId = match.getMatchId();
        isHost = false;
        isReady = false;
        gameStarting = false;
        preserveConnectionOnDispose = false;
        players.clear();
        hydratePlayersFromMatch(match);
        sendReadyState();
        listener.onStateChanged(LanguageButton.t("JOINED_MATCH_WAITING"));
    }

    public void toggleReady() {
        if (currentMatchId == null || selfUserId == null) {
            listener.onStateChanged(LanguageButton.t("JOIN_OR_CREATE_FIRST"));
            return;
        }
        isReady = !isReady;
        LobbyPlayer self = players.get(selfUserId);
        if (self != null) self.ready = isReady;
        sendReadyState();
        if (isHost) {
            broadcastLobbySnapshot();
        }
        listener.onStateChanged(null);
    }

    public void changeLevel(int delta) {
        if (!isHost || currentMatchId == null) {
            listener.onStateChanged(LanguageButton.t("ONLY_HOST_SELECT"));
            return;
        }
        selectedLevel += delta;
        if (selectedLevel < 1) selectedLevel = MAX_LEVELS;
        if (selectedLevel > MAX_LEVELS) selectedLevel = 1;
        broadcastLobbySnapshot();
        listener.onStateChanged(LanguageButton.tf("HOST_SELECTED_LEVEL_FMT", String.format("%02d", selectedLevel)));
    }

    public void startMatch() {
        if (!isHost) { listener.onStateChanged(LanguageButton.t("ONLY_HOST_START")); return; }
        if (players.isEmpty()) { listener.onStateChanged(LanguageButton.t("JOIN_OR_CREATE_FIRST")); return; }
        if (players.size() > 1 && !allPlayersReady()) { listener.onStateChanged(LanguageButton.t("EVERYONE_READY")); return; }
        Map<String, String> msg = new LinkedHashMap<>();
        msg.put("type", "start");
        msg.put("level", String.valueOf(selectedLevel));
        nakamaMatch.sendMatchData(currentMatchId, CoopProtocol.OP_START_GAME,
                Serialization.toJson(msg).getBytes(StandardCharsets.UTF_8));
        triggerStartLevel(selectedLevel);
    }

    public void reset() {
        currentMatchId = null;
        hostUserId = null;
        selfUserId = null;
        isHost = false;
        isReady = false;
        gameStarting = false;
        preserveConnectionOnDispose = false;
        players.clear();
    }

    public void handleMatchData(long opCode, Map<String, String> data) {
        if (opCode == CoopProtocol.OP_LOBBY_SNAPSHOT) {
            applySnapshot(data);
            listener.onStateChanged(LanguageButton.t("LOBBY_UPDATED"));
        } else if (opCode == CoopProtocol.OP_READY && isHost) {
            String userId = data.get("userId");
            boolean ready = Boolean.parseBoolean(data.getOrDefault("ready", "false"));
            LobbyPlayer player = players.get(userId);
            if (player != null) {
                player.ready = ready;
                broadcastLobbySnapshot();
                listener.onStateChanged(LanguageButton.tf("READY_CHANGED_FMT", player.username));
            }
        } else if (opCode == CoopProtocol.OP_START_GAME) {
            int level = selectedLevel;
            String levelValue = data.get("level");
            if (levelValue != null) {
                try { level = Integer.parseInt(levelValue); } catch (NumberFormatException ignored) {}
            }
            triggerStartLevel(level);
        }
    }

    public void handlePresenceJoined(UserPresence presence) {
        addPresence(presence, false);
        if (isHost) broadcastLobbySnapshot();
        listener.onStateChanged(LanguageButton.t("LOBBY_PRESENCE_UPDATED"));
    }

    public boolean handlePresenceLeft(String userId) {
        players.remove(userId);
        boolean hostLeft = userId != null && userId.equals(hostUserId) && !isHost;
        if (hostLeft) {
            listener.onHostLeft();
            return true;
        }
        if (isHost) broadcastLobbySnapshot();
        listener.onStateChanged(LanguageButton.t("LOBBY_PRESENCE_UPDATED"));
        return false;
    }

    // — Getters for CoopMenu to read when rebuilding UI —

    public String getCurrentMatchId() { return currentMatchId; }
    public String getSelfUserId() { return selfUserId; }
    public String getHostUserId() { return hostUserId; }
    public boolean isHost() { return isHost; }
    public boolean isReady() { return isReady; }
    public boolean isGameStarting() { return gameStarting; }
    public boolean isPreserveConnectionOnDispose() { return preserveConnectionOnDispose; }
    public int getSelectedLevel() { return selectedLevel; }
    public Map<String, LobbyPlayer> getPlayers() { return players; }

    public boolean isInMatch() { return currentMatchId != null; }

    public boolean canStartMatch() {
        if (!isHost || currentMatchId == null || players.isEmpty()) return false;
        return players.size() == 1 || allPlayersReady();
    }

    public CoopLobbyState currentState() {
        if (currentMatchId == null) return new CoopLobbyState.Idle();
        if (gameStarting) return new CoopLobbyState.Starting(currentMatchId, selectedLevel, preserveConnectionOnDispose);
        if (isHost) return new CoopLobbyState.Hosting(currentMatchId, selfUserId, selectedLevel, players);
        return new CoopLobbyState.Joined(currentMatchId, selfUserId, hostUserId, isReady, players);
    }

    // — Private helpers —

    private void triggerStartLevel(int level) {
        boolean useLiveCoop = level == 1 || level == 2;
        boolean soloRun = players.size() <= 1;
        boolean keepConnection = useLiveCoop && !soloRun;
        gameStarting = true;
        preserveConnectionOnDispose = keepConnection;
        listener.onStartLevel(level, keepConnection);
    }

    private void sendReadyState() {
        if (currentMatchId == null || selfUserId == null) return;
        Map<String, String> msg = new LinkedHashMap<>();
        msg.put("type", "ready");
        msg.put("userId", selfUserId);
        msg.put("ready", String.valueOf(isReady));
        nakamaMatch.sendMatchData(currentMatchId, CoopProtocol.OP_READY,
                Serialization.toJson(msg).getBytes(StandardCharsets.UTF_8));
    }

    private void broadcastLobbySnapshot() {
        if (!isHost || currentMatchId == null || hostUserId == null) return;
        Map<String, String> msg = new LinkedHashMap<>();
        msg.put("type", "snapshot");
        msg.put("hostUserId", hostUserId);
        msg.put("selectedLevel", String.valueOf(selectedLevel));
        msg.put("players", encodePlayers());
        nakamaMatch.sendMatchData(currentMatchId, CoopProtocol.OP_LOBBY_SNAPSHOT,
                Serialization.toJson(msg).getBytes(StandardCharsets.UTF_8));
    }

    private void applySnapshot(Map<String, String> snapshot) {
        String snapshotHost = snapshot.get("hostUserId");
        if (snapshotHost != null && !snapshotHost.isBlank()) hostUserId = snapshotHost;
        String levelValue = snapshot.get("selectedLevel");
        if (levelValue != null && !levelValue.isBlank()) {
            try { selectedLevel = Integer.parseInt(levelValue); } catch (NumberFormatException ignored) {}
        }
        String playersValue = snapshot.get("players");
        if (playersValue != null) {
            players.clear();
            if (!playersValue.isBlank()) {
                for (String row : playersValue.split(";")) {
                    String[] parts = row.split(",", 3);
                    if (parts.length < 3) continue;
                    LobbyPlayer p = new LobbyPlayer(parts[0], parts[1]);
                    p.ready = Boolean.parseBoolean(parts[2]);
                    players.put(p.userId, p);
                }
            }
        }
        isHost = selfUserId != null && selfUserId.equals(hostUserId);
        LobbyPlayer self = players.get(selfUserId);
        if (self != null) isReady = self.ready;
    }

    private void hydratePlayersFromMatch(Match match) {
        addPresence(match.getSelf(), false);
        if (match.getPresences() != null) {
            for (UserPresence p : match.getPresences()) addPresence(p, false);
        }
    }

    private void addPresence(UserPresence presence, boolean ready) {
        if (presence == null || presence.getUserId() == null) return;
        String username = presence.getUsername();
        if (username == null || username.isBlank()) username = shortId(presence.getUserId());
        LobbyPlayer existing = players.get(presence.getUserId());
        if (existing == null) {
            existing = new LobbyPlayer(presence.getUserId(), username);
            players.put(existing.userId, existing);
        }
        existing.username = username;
        existing.ready = ready;
    }

    private boolean allPlayersReady() {
        if (players.isEmpty()) return false;
        for (LobbyPlayer p : players.values()) { if (!p.ready) return false; }
        return true;
    }

    private String encodePlayers() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (LobbyPlayer p : players.values()) {
            if (!first) sb.append(';');
            sb.append(p.userId).append(',')
              .append(p.username.replace(",", "_").replace(";", "_")).append(',')
              .append(p.ready);
            first = false;
        }
        return sb.toString();
    }

    private String shortId(String value) {
        if (value == null || value.isBlank()) return "unknown";
        return value.length() <= 8 ? value : value.substring(0, 8);
    }
}
