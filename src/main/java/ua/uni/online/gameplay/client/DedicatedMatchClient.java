package ua.uni.online.gameplay.client;

import ua.uni.online.Serialization;
import ua.uni.online.gameplay.GameplayServerReservation;
import ua.uni.online.gameplay.protocol.ClientHelloMessage;
import ua.uni.online.gameplay.protocol.InputFrameMessage;
import ua.uni.online.gameplay.protocol.MatchEndMessage;
import ua.uni.online.gameplay.protocol.ServerHelloMessage;
import ua.uni.online.gameplay.protocol.WorldSnapshotMessage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class DedicatedMatchClient {
    private final GameplayServerReservation reservation;
    private final DedicatedMatchClientListener listener;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private Thread receiverThread;
    private volatile boolean running;

    public DedicatedMatchClient(GameplayServerReservation reservation, DedicatedMatchClientListener listener) {
        this.reservation = reservation;
        this.listener = listener;
    }

    public void connect() {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(reservation.getServerHost());
            running = true;
            startReceiver();
            sendHello();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to dedicated match server", e);
        }
    }

    public void sendInput(int frameNumber, float moveX, float moveY) {
        if (!running) {
            return;
        }
        InputFrameMessage message = new InputFrameMessage();
        message.setFrameNumber(frameNumber);
        message.setPlayerId(reservation.getPlayerId());
        message.setMoveX(moveX);
        message.setMoveY(moveY);
        send(message);
    }

    public void close() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private void sendHello() {
        ClientHelloMessage hello = new ClientHelloMessage();
        hello.setMatchId(reservation.getMatchId());
        hello.setPlayerId(reservation.getPlayerId());
        hello.setGameplayToken(reservation.getGameplayToken());
        hello.setLevelId(reservation.getLevelId());
        hello.setExpectedPlayers(reservation.getExpectedPlayers());
        send(hello);
    }

    private void startReceiver() {
        receiverThread = new Thread(() -> {
            byte[] buffer = new byte[8192];
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String json = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    dispatch(json);
                } catch (Exception e) {
                    if (running) {
                        listener.onError("Gameplay socket closed: " + e.getMessage());
                    }
                    return;
                }
            }
        }, "dedicated-match-client");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void dispatch(String json) {
        String type = Serialization.getStringField(json, "type");
        if ("server_hello".equals(type)) {
            listener.onServerHello(Serialization.fromJson(json, ServerHelloMessage.class));
            return;
        }
        if ("world_snapshot".equals(type)) {
            listener.onWorldSnapshot(Serialization.fromJson(json, WorldSnapshotMessage.class));
            return;
        }
        if ("match_end".equals(type)) {
            listener.onMatchEnded(Serialization.fromJson(json, MatchEndMessage.class));
            return;
        }
        listener.onError("Unknown gameplay packet type: " + type);
    }

    private void send(Object payload) {
        try {
            byte[] bytes = Serialization.toJsonObject(payload).getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, reservation.getServerUdpPort());
            socket.send(packet);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send gameplay packet", e);
        }
    }
}
