package ua.uni.online.gameplay;

import ua.uni.online.CoopMatchState;

public interface GameplayReservationService {
    GameplayServerReservation reserve(CoopMatchState matchState);
}
