package ua.uni.platform.online.gameplay.protocol;

import ua.uni.platform.online.gameplay.MatchPhase;

public class ServerHelloMessage {
    private final String type = "server_hello";
    private boolean accepted;
    private String reason;
    private int playerSlot;
    private int levelId;
    private int tickRate;
    private int snapshotRate;
    private MatchPhase phase;

    public String getType() {
        return type;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPlayerSlot() {
        return playerSlot;
    }

    public void setPlayerSlot(int playerSlot) {
        this.playerSlot = playerSlot;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getTickRate() {
        return tickRate;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public int getSnapshotRate() {
        return snapshotRate;
    }

    public void setSnapshotRate(int snapshotRate) {
        this.snapshotRate = snapshotRate;
    }

    public MatchPhase getPhase() {
        return phase;
    }

    public void setPhase(MatchPhase phase) {
        this.phase = phase;
    }
}
