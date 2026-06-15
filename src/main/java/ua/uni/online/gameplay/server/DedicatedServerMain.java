package ua.uni.online.gameplay.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ua.uni.online.OnlineConfig;
import ua.uni.online.Serialization;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DedicatedServerMain {
    private DedicatedServerMain() {
    }

    public static void main(String[] args) {
        OnlineConfig config = OnlineConfig.loadFromResources();
        DedicatedMatchUdpServer server = new DedicatedMatchUdpServer(config);
        startHealthEndpoint(config, server);
        server.start();
    }

    private static void startHealthEndpoint(OnlineConfig config, DedicatedMatchUdpServer server) {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(config.getGameplayHealthPort()), 0);
            httpServer.createContext("/health", exchange -> respondHealth(exchange, server));
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start gameplay health endpoint", e);
        }
    }

    private static void respondHealth(HttpExchange exchange, DedicatedMatchUdpServer server) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("status", server.isHealthy() ? "ok" : "degraded");
            payload.put("healthy", server.isHealthy());
            payload.put("udpServerRunning", server.isRunning());
            payload.put("activeMatches", server.getActiveMatchCount());
            payload.put("totalPlayers", server.getConnectedPlayerCount());
            payload.put("lastPacketAtMs", server.getLastPacketAtMs());

            byte[] body = Serialization.toJsonObject(payload).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(server.isHealthy() ? 200 : 503, body.length);
            exchange.getResponseBody().write(body);
        } catch (Exception e) {
            try {
                exchange.sendResponseHeaders(500, -1);
            } catch (Exception ignored) {
            }
        } finally {
            exchange.close();
        }
    }
}
