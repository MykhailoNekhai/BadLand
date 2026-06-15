package ua.uni.online.gameplay;

import ua.uni.online.Serialization;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class GameplayConnectTokenCodec {
    private static final String HMAC_ALGO = "HmacSHA256";

    private GameplayConnectTokenCodec() {
    }

    public static String mint(String secret, String matchId, String playerId, int levelId, int expectedPlayers, long ttlMs) {
        GameplayConnectToken token = new GameplayConnectToken();
        token.setMatchId(matchId);
        token.setPlayerId(playerId);
        token.setLevelId(levelId);
        token.setExpectedPlayers(expectedPlayers);
        token.setExpiresAtEpochMs(System.currentTimeMillis() + ttlMs);
        token.setSignature(sign(secret, unsignedPayload(token)));
        return base64Url(Serialization.toJsonObject(token));
    }

    public static GameplayConnectToken verify(String secret, String encodedToken) {
        String decoded = new String(Base64.getUrlDecoder().decode(encodedToken), StandardCharsets.UTF_8);
        GameplayConnectToken token = Serialization.fromJson(decoded, GameplayConnectToken.class);
        if (token == null) {
            throw new IllegalArgumentException("Token payload is empty");
        }
        if (token.getExpiresAtEpochMs() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Gameplay token expired");
        }
        String expectedSignature = sign(secret, unsignedPayload(token));
        if (!expectedSignature.equals(token.getSignature())) {
            throw new IllegalArgumentException("Gameplay token signature is invalid");
        }
        return token;
    }

    private static String unsignedPayload(GameplayConnectToken token) {
        return token.getMatchId() + "|" + token.getPlayerId() + "|" + token.getLevelId()
                + "|" + token.getExpectedPlayers() + "|" + token.getExpiresAtEpochMs();
    }

    private static String sign(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            return base64Url(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign gameplay token", e);
        }
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String base64Url(String value) {
        return base64Url(value.getBytes(StandardCharsets.UTF_8));
    }
}
