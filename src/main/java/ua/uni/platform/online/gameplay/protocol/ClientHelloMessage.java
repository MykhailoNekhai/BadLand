package ua.uni.platform.online.gameplay.protocol;

public class ClientHelloMessage {
    private final String type = "client_hello";
    private String matchId;
    private String playerId;
    private String gameplayToken;
    private int levelId;
    private int expectedPlayers;
    private int protocolVersion = 1;

    public String getType() {
        return type;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getGameplayToken() {
        return gameplayToken;
    }

    public void setGameplayToken(String gameplayToken) {
        this.gameplayToken = gameplayToken;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getExpectedPlayers() {
        return expectedPlayers;
    }

    public void setExpectedPlayers(int expectedPlayers) {
        this.expectedPlayers = expectedPlayers;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
