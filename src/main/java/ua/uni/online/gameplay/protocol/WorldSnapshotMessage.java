package ua.uni.online.gameplay.protocol;

import ua.uni.online.gameplay.MatchPhase;

public class WorldSnapshotMessage {
    private final String type = "world_snapshot";
    private long serverTick;
    private MatchPhase phase;
    private int levelId;
    private PlayerSnapshot[] players;
    private String statusMessage;

    public String getType() {
        return type;
    }

    public long getServerTick() {
        return serverTick;
    }

    public void setServerTick(long serverTick) {
        this.serverTick = serverTick;
    }

    public MatchPhase getPhase() {
        return phase;
    }

    public void setPhase(MatchPhase phase) {
        this.phase = phase;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public PlayerSnapshot[] getPlayers() {
        return players;
    }

    public void setPlayers(PlayerSnapshot[] players) {
        this.players = players;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
