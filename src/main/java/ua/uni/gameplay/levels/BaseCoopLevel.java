package ua.uni.gameplay.levels;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import ua.uni.audio.services.AudioManager;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.gameplay.entity.Shadow;
import ua.uni.bootstrap.MainGame;
import ua.uni.platform.online.CoopMatchState;
import ua.uni.platform.online.CoopProtocol;
import ua.uni.platform.online.NakamaSocket;
import ua.uni.utility.serialization.Serialization;
import ua.uni.utility.physics.BodyEditorLoader;
import ua.uni.presentation.screen.menu.coop.CoopStatusScreen;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseCoopLevel extends Plevel implements NakamaSocket.EventListener {
    private static final float STATE_SEND_INTERVAL = 0.05f;
    private static final float REMOTE_TIMEOUT_SECONDS = 2.5f;
    private static final float MAX_EXTRAPOLATION_SECONDS = 0.18f;

    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private final Array<Shadow> remoteClones = new Array<>();
    private final Array<RemoteShadowState> remoteStates = new Array<>();
    private final CoopMatchState matchState;
    private float networkSendElapsed;
    private float remotePacketElapsed;
    private long localStateSequence;
    private long lastRemoteStateSequence = -1L;
    private boolean ended;
    private boolean remoteStarted;
    private boolean remoteAllDead;
    private boolean remoteStateReceived;
    private boolean localAllDeadBroadcast;

    protected BaseCoopLevel(MainGame game) {
        super(game);
        this.matchState = game.getCoopMatchState();
    }

    @Override
    public void show() {
        super.show();
        game.getNakamaMatchService().setEventListener(this);
        spawnRemoteClone(2f, 9f);
        spawnRemoteClone(4f, 9f);
        spawnRemoteClone(6f, 9f);
        spawnRemoteClone(8f, 9f);
    }

    @Override
    public void render(float delta) {
        if (ended) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            abortLevel(LanguageButton.t("PLAYER_LEFT_MATCH"), true);
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(world, camera.combined);

        boolean w = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean s = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean a = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean d = Gdx.input.isKeyPressed(Input.Keys.D);

        if (!isGameStarted && (w || s || a || d || remoteStarted)) {
            isGameStarted = true;
        }

        AudioManager.get().updateLevelAmbience(delta);
        world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        engine.update(delta);
        updateDeadLocalPlayers();
        updateDeadClones(remoteClones);
        interpolateRemoteClones(delta);
        updateCamera(delta);
        camera.position.y = camera.viewportHeight / 2f;
        camera.update();
        syncLocalState(delta);
        checkRemoteTimeout(delta);
        resolveMatchState();
    }

    private void updateDeadClones(Array<Shadow> shadows) {
        float leftCameraEdge = camera.position.x - (camera.viewportWidth / 2f);
        float deathLineX = leftCameraEdge - SHADOW_SIZE;
        for (Shadow shadow : shadows) {
            Body body = shadow.getBody();
            if (body == null || !body.isActive()) {
                continue;
            }
            if (shadow.isDead() || body.getPosition().x < deathLineX) {
                shadow.setDead(true);
                body.setLinearVelocity(0f, 0f);
                body.setAngularVelocity(0f);
                body.setActive(false);
            }
        }
    }

    private void updateCamera(float delta) {
        if (!isGameStarted) {
            return;
        }
        float leaderX = findLeaderX();
        if (leaderX == -Float.MAX_VALUE) {
            return;
        }
        float minCameraSpeed = 3f;
        float heroOffset = camera.viewportWidth * 0.3f;
        float cameraX = leaderX + heroOffset;
        float smoothness = 4.0f;
        float neededSpeed = (cameraX - camera.position.x) * smoothness;
        float actualCameraSpeed = Math.max(minCameraSpeed, neededSpeed);
        camera.position.x = camera.position.x + (actualCameraSpeed * delta);
    }

    private float findLeaderX() {
        float leaderX = -Float.MAX_VALUE;
        for (Entity player : getLocalPlayers()) {
            Body body = physMapper.get(player).body;
            if (body != null && body.isActive()) {
                leaderX = Math.max(leaderX, body.getPosition().x);
            }
        }
        for (Shadow shadow : remoteClones) {
            Body body = shadow.getBody();
            if (body != null && body.isActive()) {
                leaderX = Math.max(leaderX, body.getPosition().x);
            }
        }
        return leaderX;
    }

    private void syncLocalState(float delta) {
        if (matchState == null || !game.getNakamaMatchService().isConnected()) {
            return;
        }
        networkSendElapsed += delta;
        boolean localAllDead = allInactiveLocal();
        boolean shouldBroadcastDeath = localAllDead && !localAllDeadBroadcast;
        if (networkSendElapsed < STATE_SEND_INTERVAL && !shouldBroadcastDeath) {
            return;
        }
        networkSendElapsed = 0f;
        localStateSequence++;
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("userId", matchState.getLocalUserId());
        payload.put("seq", String.valueOf(localStateSequence));
        payload.put("sentAtMs", String.valueOf(System.currentTimeMillis()));
        payload.put("started", String.valueOf(isGameStarted));
        payload.put("allDead", String.valueOf(localAllDead));
        payload.put("clones", encodeLocalPlayers());
        game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_PLAYER_STATE,
                Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
        if (shouldBroadcastDeath) {
            localAllDeadBroadcast = true;
            broadcastLocalDeath();
        }
    }

    private String encodeShadows(Array<Shadow> shadows) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < shadows.size; i++) {
            Shadow shadow = shadows.get(i);
            Body body = shadow.getBody();
            if (i > 0) {
                builder.append('|');
            }
            if (body == null) {
                builder.append("0,0,0,0,0,true");
                continue;
            }
            Vector2 position = body.getPosition();
            Vector2 velocity = body.getLinearVelocity();
            builder.append(position.x).append(',')
                    .append(position.y).append(',')
                    .append(velocity.x).append(',')
                    .append(velocity.y).append(',')
                    .append(body.getAngle()).append(',')
                    .append(shadow.isDead() || !body.isActive());
        }
        return builder.toString();
    }

    private String encodeLocalPlayers() {
        StringBuilder builder = new StringBuilder();
        ImmutableArray<Entity> players = getLocalPlayers();
        for (int i = 0; i < players.size(); i++) {
            Entity player = players.get(i);
            PhysicsComponent physics = physMapper.get(player);
            PlayerComponent playerState = playerMapper.get(player);
            Body body = physics.body;
            if (i > 0) {
                builder.append('|');
            }
            if (body == null) {
                builder.append("0,0,0,0,0,true");
                continue;
            }
            Vector2 position = body.getPosition();
            Vector2 velocity = body.getLinearVelocity();
            builder.append(position.x).append(',')
                    .append(position.y).append(',')
                    .append(velocity.x).append(',')
                    .append(velocity.y).append(',')
                    .append(body.getAngle()).append(',')
                    .append(playerState.isDead || !body.isActive());
        }
        return builder.toString();
    }

    private void applyRemoteState(String encoded, boolean allDead, boolean started, long sentAtMs, long sequence) {
        if (sequence >= 0 && sequence <= lastRemoteStateSequence) {
            return;
        }
        lastRemoteStateSequence = sequence;
        remotePacketElapsed = 0f;
        remoteStateReceived = true;
        remoteAllDead = allDead;
        remoteStarted = started;
        if (encoded == null) {
            return;
        }
        String[] rows = encoded.split("\\|");
        ensureRemoteCloneCount(rows.length);
        for (int i = 0; i < rows.length; i++) {
            String[] parts = rows[i].split(",");
            if (parts.length < 6) {
                continue;
            }
            Shadow shadow = remoteClones.get(i);
            RemoteShadowState state = remoteStates.get(i);
            Body body = shadow.getBody();
            if (body == null) {
                continue;
            }
            boolean dead = Boolean.parseBoolean(parts[5]);
            shadow.setDead(dead);
            if (dead) {
                state.active = false;
                state.hasTarget = false;
                body.setLinearVelocity(0f, 0f);
                body.setAngularVelocity(0f);
                body.setActive(false);
                continue;
            }
            state.targetX = parseFloat(parts[0]);
            state.targetY = parseFloat(parts[1]);
            state.targetVx = parseFloat(parts[2]);
            state.targetVy = parseFloat(parts[3]);
            state.targetAngle = parseFloat(parts[4]);
            float extrapolation = MathUtils.clamp((System.currentTimeMillis() - sentAtMs) / 1000f, 0f, MAX_EXTRAPOLATION_SECONDS);
            state.targetX += state.targetVx * extrapolation;
            state.targetY += state.targetVy * extrapolation;
            state.active = true;
            if (!state.hasTarget || !body.isActive()) {
                body.setActive(true);
                body.setTransform(state.targetX, state.targetY, state.targetAngle);
                body.setLinearVelocity(state.targetVx, state.targetVy);
            }
            state.hasTarget = true;
        }
    }

    private void ensureRemoteCloneCount(int count) {
        while (remoteClones.size < count) {
            spawnRemoteClone(2f + (remoteClones.size * 2f), 9f);
        }
    }

    private void spawnRemoteClone(float x, float y) {
        BodyEditorLoader heroLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/avatar-1.json"));
        Shadow shadow = new Shadow(world, heroLoader, x, y, SHADOW_SIZE);
        Body body = shadow.getBody();
        body.setGravityScale(0f);
        body.setLinearDamping(0f);
        body.setAngularDamping(0f);
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setSensor(true);
        }
        body.setUserData("REMOTE_SHADOW");
        remoteClones.add(shadow);
        remoteStates.add(new RemoteShadowState());
    }

    private void interpolateRemoteClones(float delta) {
        float alpha = Math.min(1f, delta * 9f);
        for (int i = 0; i < remoteClones.size; i++) {
            Shadow shadow = remoteClones.get(i);
            RemoteShadowState state = remoteStates.get(i);
            Body body = shadow.getBody();
            if (body == null || !state.hasTarget) {
                continue;
            }
            if (!state.active) {
                shadow.setDead(true);
                body.setLinearVelocity(0f, 0f);
                body.setAngularVelocity(0f);
                body.setActive(false);
                continue;
            }

            shadow.setDead(false);
            if (!body.isActive()) {
                body.setActive(true);
                body.setTransform(state.targetX, state.targetY, state.targetAngle);
                body.setLinearVelocity(state.targetVx, state.targetVy);
                continue;
            }

            Vector2 currentPosition = body.getPosition();
            float dx = state.targetX - currentPosition.x;
            float dy = state.targetY - currentPosition.y;
            if ((dx * dx) + (dy * dy) > 9f) {
                body.setTransform(state.targetX, state.targetY, state.targetAngle);
                body.setLinearVelocity(state.targetVx, state.targetVy);
                continue;
            }

            float x = MathUtils.lerp(currentPosition.x, state.targetX, alpha);
            float y = MathUtils.lerp(currentPosition.y, state.targetY, alpha);
            float angle = MathUtils.lerpAngle(body.getAngle(), state.targetAngle, alpha);
            float vx = MathUtils.lerp(body.getLinearVelocity().x, state.targetVx, alpha);
            float vy = MathUtils.lerp(body.getLinearVelocity().y, state.targetVy, alpha);
            body.setTransform(x, y, angle);
            body.setLinearVelocity(vx, vy);
            body.setAngularVelocity(0f);
        }
    }

    private void checkRemoteTimeout(float delta) {
        if (!remoteStateReceived || ended) {
            return;
        }
        remotePacketElapsed += delta;
        if (remotePacketElapsed >= REMOTE_TIMEOUT_SECONDS) {
            abortLevel("Connection to the other player timed out.", false);
        }
    }

    private boolean allInactive(Array<Shadow> shadows) {
        for (Shadow shadow : shadows) {
            Body body = shadow.getBody();
            if (body != null && body.isActive() && !shadow.isDead()) {
                return false;
            }
        }
        return true;
    }

    private void resolveMatchState() {
        boolean localAllDead = allInactiveLocal();
        float leaderX = findLeaderX();
        if (leaderX >= finishLineX) {
            finishLevel("Victory", "Co-op level completed.", true);
            return;
        }
        if (localAllDead && remoteAllDead) {
            finishLevel("Game Over", "All players died. Match ended.", true);
        }
    }

    private void finishLevel(String title, String message, boolean broadcast) {
        if (ended) {
            return;
        }
        ended = true;
        if ("Victory".equalsIgnoreCase(title)) {
            AudioManager.get().playLevelWin(0.95f);
        } else {
            AudioManager.get().playLevelLose(0.95f);
        }
        if (broadcast && matchState != null && game.getNakamaMatchService().isConnected()) {
            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("type", "result");
            payload.put("title", title);
            payload.put("message", message);
            game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_LEVEL_RESULT,
                    Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
        }
        cleanupConnection();
        game.setScreen(new CoopStatusScreen(game, title, message));
    }

    private void abortLevel(String message, boolean broadcast) {
        if (ended) {
            return;
        }
        ended = true;
        AudioManager.get().playLevelLose(0.95f);
        if (broadcast && matchState != null && game.getNakamaMatchService().isConnected()) {
            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("type", "abort");
            payload.put("message", message);
            game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_LEVEL_ABORT,
                    Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
        }
        cleanupConnection();
        game.setScreen(new CoopStatusScreen(game, LanguageButton.t("CONNECTION_ERROR"), message));
    }

    private void broadcastLocalDeath() {
        if (matchState == null || !game.getNakamaMatchService().isConnected()) {
            return;
        }
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("type", "death");
        payload.put("userId", matchState.getLocalUserId());
        payload.put("dead", "true");
        game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_PLAYER_DEATH,
                Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
    }

    private void cleanupConnection() {
        game.clearCoopMatchState();
        if (matchState != null && game.getNakamaMatchService().isConnected()) {
            try {
                game.getNakamaMatchService().leaveMatch(matchState.getMatchId());
            } catch (Exception ignored) {
            }
            game.getNakamaMatchService().disconnect();
        }
    }

    @Override
    public void onDisconnect(Throwable throwable) {
        Gdx.app.postRunnable(() -> abortLevel(LanguageButton.t("CONNECTION_LOST_COOP"), false));
    }

    @Override
    public void onError(Error error) {
        if (error != null) {
            Gdx.app.postRunnable(() -> abortLevel(LanguageButton.tf("SOCKET_ERROR_FMT", error.getMessage()), false));
        }
    }

    @Override
    public void onMatchData(MatchData matchData) {
        if (matchState == null || matchData == null || !matchState.getMatchId().equals(matchData.getMatchId())) {
            return;
        }
        Map<String, String> data = Serialization.fromJson(new String(matchData.getData(), StandardCharsets.UTF_8));
        if (data == null) {
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_PLAYER_STATE) {
            String userId = data.get("userId");
            if (userId != null && !userId.equals(matchState.getLocalUserId())) {
                boolean allDead = Boolean.parseBoolean(data.getOrDefault("allDead", "false"));
                boolean started = Boolean.parseBoolean(data.getOrDefault("started", "false"));
                String encoded = data.get("clones");
                long sentAtMs = parseLong(data.get("sentAtMs"), System.currentTimeMillis());
                long sequence = parseLong(data.get("seq"), -1L);
                Gdx.app.postRunnable(() -> applyRemoteState(encoded, allDead, started, sentAtMs, sequence));
            }
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_PLAYER_DEATH) {
            String userId = data.get("userId");
            if (userId != null && !userId.equals(matchState.getLocalUserId())) {
                Gdx.app.postRunnable(() -> remoteAllDead = Boolean.parseBoolean(data.getOrDefault("dead", "false")));
            }
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_LEVEL_ABORT) {
            Gdx.app.postRunnable(() -> abortLevel(data.getOrDefault("message", LanguageButton.t("PLAYER_DISCONNECTED")), false));
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_LEVEL_RESULT) {
            Gdx.app.postRunnable(() -> finishLevel(
                    data.getOrDefault("title", LanguageButton.t("MATCH_ENDED")),
                    data.getOrDefault("message", LanguageButton.t("COOP_MATCH_FINISHED")), false));
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent presenceEvent) {
        if (matchState == null || presenceEvent == null || !matchState.getMatchId().equals(presenceEvent.getMatchId())) {
            return;
        }
        if (presenceEvent.getLeaves() != null && !presenceEvent.getLeaves().isEmpty()) {
            Gdx.app.postRunnable(() -> abortLevel(LanguageButton.t("PLAYER_DISCONNECTED_LEVEL"), false));
        }
    }

    private float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    private long parseLong(String value, long fallback) {
        try {
            return value == null ? fallback : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private ImmutableArray<Entity> getLocalPlayers() {
        return engine.getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
    }

    private void updateDeadLocalPlayers() {
        float leftCameraEdge = camera.position.x - (camera.viewportWidth / 2f);
        float deathLineX = leftCameraEdge - SHADOW_SIZE;
        ImmutableArray<Entity> players = getLocalPlayers();
        for (int i = 0; i < players.size(); i++) {
            Entity player = players.get(i);
            PhysicsComponent physics = physMapper.get(player);
            PlayerComponent playerState = playerMapper.get(player);
            Body body = physics.body;
            if (body == null || !body.isActive()) {
                continue;
            }
            if (playerState.isDead || body.getPosition().x < deathLineX) {
                playerState.isDead = true;
                body.setLinearVelocity(0f, 0f);
                body.setAngularVelocity(0f);
                body.setActive(false);
            }
        }
    }

    private boolean allInactiveLocal() {
        ImmutableArray<Entity> players = getLocalPlayers();
        for (int i = 0; i < players.size(); i++) {
            Entity player = players.get(i);
            PhysicsComponent physics = physMapper.get(player);
            PlayerComponent playerState = playerMapper.get(player);
            Body body = physics.body;
            if (body != null && body.isActive() && !playerState.isDead) {
                return false;
            }
        }
        return true;
    }

    private static final class RemoteShadowState {
        private float targetX;
        private float targetY;
        private float targetVx;
        private float targetVy;
        private float targetAngle;
        private boolean active;
        private boolean hasTarget;
    }
}
