package ua.uni.platform.online.lobby;

import java.util.Map;

public sealed interface CoopLobbyState
        permits CoopLobbyState.Idle,
                CoopLobbyState.Hosting,
                CoopLobbyState.Joined,
                CoopLobbyState.Starting {

    record Idle() implements CoopLobbyState {}

    record Hosting(
            String matchId,
            String selfId,
            int selectedLevel,
            Map<String, LobbyPlayer> players
    ) implements CoopLobbyState {}

    record Joined(
            String matchId,
            String selfId,
            String hostId,
            boolean isReady,
            Map<String, LobbyPlayer> players
    ) implements CoopLobbyState {}

    record Starting(
            String matchId,
            int level,
            boolean keepConnection
    ) implements CoopLobbyState {}
}
