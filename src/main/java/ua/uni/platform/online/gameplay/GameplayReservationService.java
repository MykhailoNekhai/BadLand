package ua.uni.platform.online.gameplay;

import ua.uni.platform.online.CoopMatchState;

public interface GameplayReservationService {
    GameplayServerReservation reserve(CoopMatchState matchState);
}
