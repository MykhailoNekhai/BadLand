package ua.uni.platform.online.gameplay.client;

import ua.uni.platform.online.gameplay.protocol.MatchEndMessage;
import ua.uni.platform.online.gameplay.protocol.ServerHelloMessage;
import ua.uni.platform.online.gameplay.protocol.WorldSnapshotMessage;

public interface DedicatedMatchClientListener {
    void onServerHello(ServerHelloMessage helloMessage);

    void onWorldSnapshot(WorldSnapshotMessage snapshotMessage);

    void onMatchEnded(MatchEndMessage endMessage);

    void onError(String message);
}
