package ua.uni.platform.online.gameplay.server;

import ua.uni.core.logging.AppLogger;
import ua.uni.platform.online.OnlineConfig;
import ua.uni.utility.serialization.Serialization;
import ua.uni.platform.online.gameplay.GameplayConnectToken;
import ua.uni.platform.online.gameplay.GameplayConnectTokenCodec;
import ua.uni.platform.online.gameplay.MatchPhase;
import ua.uni.platform.online.gameplay.PrototypeLevelOneLayout;
import ua.uni.platform.online.gameplay.protocol.ClientHelloMessage;
import ua.uni.platform.online.gameplay.protocol.InputFrameMessage;
import ua.uni.platform.online.gameplay.protocol.MatchEndMessage;
import ua.uni.platform.online.gameplay.protocol.PlayerSnapshot;
import ua.uni.platform.online.gameplay.protocol.ServerHelloMessage;
import ua.uni.platform.online.gameplay.protocol.WorldSnapshotMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DedicatedMatchUdpServer {
    private static final String LOG_TAG = "DedicatedMatchServer";

    private final OnlineConfig config;
    private final Map<String, ServerMatch> matches = new ConcurrentHashMap<>();
    private volatile boolean running;
    private volatile long lastPacketAtMs;

    public DedicatedMatchUdpServer(OnlineConfig config) {
        this.config = config;
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(config.getGameplayUdpPort())) {
            running = true;
            lastPacketAtMs = System.currentTimeMillis();
            AppLogger.info(LOG_TAG, "Listening for gameplay UDP on port " + config.getGameplayUdpPort());
            byte[] buffer = new byte[8192];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                lastPacketAtMs = System.currentTimeMillis();
                String json = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                dispatch(socket, packet, json);
            }
        } catch (Exception e) {
            running = false;
            throw new IllegalStateException("Dedicated gameplay server crashed", e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isHealthy() {
        return running;
    }

    public int getActiveMatchCount() {
        return matches.size();
    }

    public int getConnectedPlayerCount() {
        return matches.values().stream().mapToInt(ServerMatch::connectedPlayerCount).sum();
    }

    public long getLastPacketAtMs() {
        return lastPacketAtMs;
    }

    private void dispatch(DatagramSocket socket, DatagramPacket packet, String json) throws Exception {
        String type = Serialization.getStringField(json, "type");
        if ("client_hello".equals(type)) {
            handleHello(socket, packet, Serialization.fromJson(json, ClientHelloMessage.class));
            return;
        }
        if ("input_frame".equals(type)) {
            handleInput(packet, Serialization.fromJson(json, InputFrameMessage.class));
        }
    }

    private void handleHello(DatagramSocket socket, DatagramPacket packet, ClientHelloMessage hello) throws Exception {
        ServerHelloMessage response = new ServerHelloMessage();
        GameplayConnectToken token = null;
        try {
            token = GameplayConnectTokenCodec.verify(config.getGameplayTokenSecret(), hello.getGameplayToken());
            if (!token.getMatchId().equals(hello.getMatchId()) || !token.getPlayerId().equals(hello.getPlayerId())) {
                throw new IllegalArgumentException("Gameplay token does not match hello payload");
            }
            final GameplayConnectToken verifiedToken = token;
            ServerMatch match = matches.computeIfAbsent(verifiedToken.getMatchId(),
                    ignored -> new ServerMatch(socket, verifiedToken.getMatchId(), verifiedToken.getLevelId(),
                            verifiedToken.getExpectedPlayers(), config.getGameplayTickRate(), config.getGameplaySnapshotRate()));
            int slot = match.connectPlayer(token.getPlayerId(),
                    new InetSocketAddress(packet.getAddress(), packet.getPort()));
            response.setAccepted(true);
            response.setPlayerSlot(slot);
            response.setLevelId(token.getLevelId());
            response.setTickRate(config.getGameplayTickRate());
            response.setSnapshotRate(config.getGameplaySnapshotRate());
            response.setPhase(match.getPhase());
            send(socket, packet.getAddress(), packet.getPort(), response);
            AppLogger.info(LOG_TAG, "Accepted player " + token.getPlayerId() + " into match " + token.getMatchId());
            return;
        } catch (Exception e) {
            response.setAccepted(false);
            response.setReason(e.getMessage());
            response.setPhase(MatchPhase.ABORTED);
            send(socket, packet.getAddress(), packet.getPort(), response);
            AppLogger.error(LOG_TAG, "Rejected gameplay client for match " + (token == null ? "unknown" : token.getMatchId()), e);
        }
    }

    private void handleInput(DatagramPacket packet, InputFrameMessage input) {
        ServerMatch match = matches.values().stream()
                .filter(candidate -> candidate.accepts(packet.getAddress(), packet.getPort(), input.getPlayerId()))
                .findFirst()
                .orElse(null);
        if (match != null) {
            match.applyInput(input.getPlayerId(), input.getMoveX(), input.getMoveY());
        }
    }

    private void send(DatagramSocket socket, java.net.InetAddress address, int port, Object payload) throws Exception {
        byte[] bytes = Serialization.toJsonObject(payload).getBytes(StandardCharsets.UTF_8);
        DatagramPacket response = new DatagramPacket(bytes, bytes.length, address, port);
        socket.send(response);
    }

    private static final class ServerMatch {
        private static final float WIDTH = PrototypeLevelOneLayout.WORLD_WIDTH;
        private static final float HEIGHT = PrototypeLevelOneLayout.WORLD_HEIGHT;
        private static final float FINISH_X = PrototypeLevelOneLayout.FINISH_X;
        private static final float PLAYER_SPEED = 7f;
        private static final long DISCONNECT_TIMEOUT_MS = 5000L;

        private final DatagramSocket socket;
        private final String matchId;
        private final int levelId;
        private final int expectedPlayers;
        private final int tickRate;
        private final int snapshotRate;
        private final Map<String, ServerPlayer> players = new ConcurrentHashMap<>();
        private final Thread loopThread;
        private volatile MatchPhase phase = MatchPhase.CONNECTING;
        private volatile long tick;
        private volatile String statusMessage = "Waiting for players...";
        private volatile MatchEndMessage terminalMessage;

        private ServerMatch(DatagramSocket socket, String matchId, int levelId, int expectedPlayers, int tickRate, int snapshotRate) {
            this.socket = socket;
            this.matchId = matchId;
            this.levelId = levelId;
            this.expectedPlayers = expectedPlayers;
            this.tickRate = tickRate;
            this.snapshotRate = snapshotRate;
            this.loopThread = new Thread(this::runLoop, "match-" + matchId);
            this.loopThread.setDaemon(true);
            this.loopThread.start();
        }

        private int connectPlayer(String playerId, InetSocketAddress address) {
            ServerPlayer player = players.computeIfAbsent(playerId, ignored -> {
                ServerPlayer created = new ServerPlayer();
                created.playerId = playerId;
                created.slot = players.size();
                created.x = 2f;
                created.y = 7f + (created.slot * 2.2f);
                created.alive = true;
                return created;
            });
            player.address = address;
            player.lastSeenAtMs = System.currentTimeMillis();
            player.connected = true;
            statusMessage = "Players connected: " + players.size() + "/" + expectedPlayers;
            if (players.size() >= expectedPlayers && phase == MatchPhase.CONNECTING) {
                phase = MatchPhase.RUNNING;
                statusMessage = "Match started. Reach the finish line and avoid the hazard field.";
            }
            return player.slot;
        }

        private boolean accepts(java.net.InetAddress address, int port, String playerId) {
            ServerPlayer player = players.get(playerId);
            return player != null && player.address != null
                    && player.address.getAddress().equals(address) && player.address.getPort() == port;
        }

        private void applyInput(String playerId, float moveX, float moveY) {
            ServerPlayer player = players.get(playerId);
            if (player == null || phase != MatchPhase.RUNNING) {
                return;
            }
            player.inputX = clamp(moveX);
            player.inputY = clamp(moveY);
            player.lastSeenAtMs = System.currentTimeMillis();
        }

        private MatchPhase getPhase() {
            return phase;
        }

        private void runLoop() {
            long tickSleepMs = Math.max(1, 1000L / Math.max(1, tickRate));
            long snapshotEveryTicks = Math.max(1, tickRate / Math.max(1, snapshotRate));
            while (phase != MatchPhase.FINISHED && phase != MatchPhase.ABORTED) {
                long startedAt = System.currentTimeMillis();
                if (phase == MatchPhase.RUNNING) {
                    tick++;
                    stepSimulation(1f / tickRate);
                    if (tick % snapshotEveryTicks == 0) {
                        broadcastSnapshot();
                    }
                } else {
                    broadcastSnapshot();
                }
                enforceTimeouts();
                long elapsed = System.currentTimeMillis() - startedAt;
                long sleepMs = tickSleepMs - elapsed;
                if (sleepMs > 0) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            if (terminalMessage != null) {
                broadcast(terminalMessage);
            }
        }

        private void stepSimulation(float delta) {
            boolean anyAlive = false;
            for (ServerPlayer player : players.values()) {
                if (!player.connected || !player.alive) {
                    continue;
                }
                anyAlive = true;
                player.vx = player.inputX * PLAYER_SPEED;
                player.vy = player.inputY * PLAYER_SPEED;
                player.x = clamp(player.x + (player.vx * delta), 1f, WIDTH - 1f);
                player.y = clamp(player.y + (player.vy * delta), 1f, HEIGHT - 1f);
                if (intersectsDeadlyObstacle(player.x, player.y)) {
                    player.alive = false;
                    player.vx = 0f;
                    player.vy = 0f;
                }
                if (player.alive && player.x >= FINISH_X) {
                    finishMatch(MatchPhase.FINISHED, "Victory", "A player reached the finish line of the prototype level.");
                    return;
                }
            }
            if (!anyAlive || players.values().stream().noneMatch(player -> player.alive)) {
                finishMatch(MatchPhase.FINISHED, "Game Over", "All players died on the prototype level.");
            }
        }

        private boolean intersectsDeadlyObstacle(float x, float y) {
            for (PrototypeLevelOneLayout.Obstacle obstacle : PrototypeLevelOneLayout.obstacles()) {
                if (!obstacle.isDeadly()) {
                    continue;
                }
                float halfWidth = obstacle.getWidth() * 0.5f;
                float halfHeight = obstacle.getHeight() * 0.5f;
                if (x >= obstacle.getX() - halfWidth && x <= obstacle.getX() + halfWidth
                        && y >= obstacle.getY() - halfHeight && y <= obstacle.getY() + halfHeight) {
                    return true;
                }
            }
            return false;
        }

        private void enforceTimeouts() {
            long now = System.currentTimeMillis();
            for (ServerPlayer player : players.values()) {
                if (player.connected && now - player.lastSeenAtMs > DISCONNECT_TIMEOUT_MS) {
                    player.connected = false;
                    finishMatch(MatchPhase.ABORTED, "Connection Error", "A player disconnected from the dedicated match.");
                    return;
                }
            }
        }

        private void finishMatch(MatchPhase targetPhase, String title, String message) {
            if (phase == MatchPhase.FINISHED || phase == MatchPhase.ABORTED) {
                return;
            }
            phase = targetPhase;
            statusMessage = message;
            MatchEndMessage endMessage = new MatchEndMessage();
            endMessage.setPhase(targetPhase);
            endMessage.setTitle(title);
            endMessage.setMessage(message);
            terminalMessage = endMessage;
        }

        private void broadcastSnapshot() {
            WorldSnapshotMessage snapshot = new WorldSnapshotMessage();
            snapshot.setServerTick(tick);
            snapshot.setPhase(phase);
            snapshot.setLevelId(levelId);
            snapshot.setStatusMessage(statusMessage);
            PlayerSnapshot[] payloadPlayers = players.values().stream()
                    .sorted((left, right) -> Integer.compare(left.slot, right.slot))
                    .map(ServerPlayer::toSnapshot)
                    .toArray(PlayerSnapshot[]::new);
            snapshot.setPlayers(payloadPlayers);
            broadcast(snapshot);
        }

        private void broadcast(Object payload) {
            byte[] bytes = Serialization.toJsonObject(payload).getBytes(StandardCharsets.UTF_8);
            Arrays.stream(players.values().toArray(new ServerPlayer[0]))
                    .filter(player -> player.address != null && player.connected)
                    .forEach(player -> {
                        try {
                            DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                                    player.address.getAddress(), player.address.getPort());
                            socket.send(packet);
                        } catch (Exception e) {
                            AppLogger.error(LOG_TAG, "Failed to broadcast to player " + player.playerId, e);
                        }
                    });
        }

        private int connectedPlayerCount() {
            return (int) players.values().stream().filter(player -> player.connected).count();
        }

        private float clamp(float value) {
            return clamp(value, -1f, 1f);
        }

        private float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }

        private static final class ServerPlayer {
            private String playerId;
            private int slot;
            private float x;
            private float y;
            private float vx;
            private float vy;
            private float inputX;
            private float inputY;
            private boolean alive;
            private boolean connected;
            private long lastSeenAtMs;
            private InetSocketAddress address;

            private PlayerSnapshot toSnapshot() {
                PlayerSnapshot snapshot = new PlayerSnapshot();
                snapshot.setPlayerId(playerId);
                snapshot.setSlot(slot);
                snapshot.setX(x);
                snapshot.setY(y);
                snapshot.setVx(vx);
                snapshot.setVy(vy);
                snapshot.setAlive(alive);
                snapshot.setConnected(connected);
                return snapshot;
            }
        }
    }
}
