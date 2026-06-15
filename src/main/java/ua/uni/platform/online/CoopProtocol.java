package ua.uni.platform.online;

public final class CoopProtocol {
    public static final long OP_LOBBY_SNAPSHOT = 1001L;
    public static final long OP_READY = 1002L;
    public static final long OP_START_GAME = 1003L;
    public static final long OP_PLAYER_STATE = 1101L;
    public static final long OP_LEVEL_ABORT = 1102L;
    public static final long OP_PLAYER_DEATH = 1103L;
    public static final long OP_LEVEL_RESULT = 1104L;

    private CoopProtocol() {
    }
}
