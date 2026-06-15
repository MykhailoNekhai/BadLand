package ua.uni.online.gameplay;

import ua.uni.online.CoopMatchState;
import ua.uni.online.OnlineConfig;

public class StaticGameplayReservationService implements GameplayReservationService {
    private static final long TOKEN_TTL_MS = 5 * 60 * 1000L;

    private final OnlineConfig config;

    public StaticGameplayReservationService(OnlineConfig config) {
        this.config = config;
    }

    @Override
    public GameplayServerReservation reserve(CoopMatchState matchState) {
        String token = GameplayConnectTokenCodec.mint(config.getGameplayTokenSecret(),
                matchState.getMatchId(), matchState.getLocalUserId(), matchState.getSelectedLevel(),
                matchState.getExpectedPlayers(), TOKEN_TTL_MS);
        return new GameplayServerReservation(matchState.getMatchId(), matchState.getSelectedLevel(),
                config.getGameplayHost(), config.getGameplayUdpPort(), matchState.getExpectedPlayers(),
                matchState.getLocalUserId(), token);
    }
}
