package ua.uni.platform.online.gameplay;

public class GameplayServerReservation {
    private final String matchId;
    private final int levelId;
    private final String serverHost;
    private final int serverUdpPort;
    private final int expectedPlayers;
    private final String playerId;
    private final String gameplayToken;

    public GameplayServerReservation(String matchId, int levelId, String serverHost, int serverUdpPort,
                                     int expectedPlayers, String playerId, String gameplayToken) {
        this.matchId = matchId;
        this.levelId = levelId;
        this.serverHost = serverHost;
        this.serverUdpPort = serverUdpPort;
        this.expectedPlayers = expectedPlayers;
        this.playerId = playerId;
        this.gameplayToken = gameplayToken;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getLevelId() {
        return levelId;
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerUdpPort() {
        return serverUdpPort;
    }

    public int getExpectedPlayers() {
        return expectedPlayers;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getGameplayToken() {
        return gameplayToken;
    }
}
