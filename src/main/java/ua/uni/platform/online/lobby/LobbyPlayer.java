package ua.uni.platform.online.lobby;

public final class LobbyPlayer {
    public final String userId;
    public String username;
    public boolean ready;

    public LobbyPlayer(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
