package ua.uni.online;

public class CoopMatchState {
    private final String matchId;
    private final String localUserId;
    private final String hostUserId;
    private final int selectedLevel;
    private final int expectedPlayers;

    public CoopMatchState(String matchId, String localUserId, String hostUserId, int selectedLevel, int expectedPlayers) {
        this.matchId = matchId;
        this.localUserId = localUserId;
        this.hostUserId = hostUserId;
        this.selectedLevel = selectedLevel;
        this.expectedPlayers = expectedPlayers;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getLocalUserId() {
        return localUserId;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public int getExpectedPlayers() {
        return expectedPlayers;
    }

    public boolean isHost() {
        return localUserId != null && localUserId.equals(hostUserId);
    }
}
