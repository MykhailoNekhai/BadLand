package ua.uni.web.main_menu.coop_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.game.MainGame;
import ua.uni.online.gameplay.GameplayServerReservation;
import ua.uni.online.gameplay.MatchPhase;
import ua.uni.online.gameplay.PrototypeLevelOneLayout;
import ua.uni.online.gameplay.client.DedicatedMatchClient;
import ua.uni.online.gameplay.client.DedicatedMatchClientListener;
import ua.uni.online.gameplay.protocol.MatchEndMessage;
import ua.uni.online.gameplay.protocol.PlayerSnapshot;
import ua.uni.online.gameplay.protocol.ServerHelloMessage;
import ua.uni.online.gameplay.protocol.WorldSnapshotMessage;
import ua.uni.online.CoopMatchState;
import ua.uni.web.main_menu.settings_menu.LanguageButton;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoopLevelPlayScreen implements Screen, DedicatedMatchClientListener {
    private final MainGame game;
    private final int level;
    private final CoopMatchState matchState;
    private Stage stage;
    private Texture bg;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private Label statusLabel;
    private ShapeRenderer shapeRenderer;
    private final Map<String, Texture> obstacleTextures = new LinkedHashMap<>();
    private DedicatedMatchClient gameplayClient;
    private GameplayServerReservation reservation;
    private PlayerSnapshot[] latestPlayers;
    private int inputFrameNumber;
    private boolean deathReported;
    private boolean localDead;
    private boolean ended;

    public CoopLevelPlayScreen(MainGame game, int level) {
        this.game = game;
        this.level = level;
        this.matchState = game.getCoopMatchState();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().startLevelMusic();
        game.getAchievementManager().onCoopSessionStart();
        bg = solidTexture(2, 2, new Color(0.03f, 0.03f, 0.04f, 1f));
        shapeRenderer = new ShapeRenderer();
        loadPrototypeTextures();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 86;
        titleParams.color = Color.WHITE;
        titleParams.borderWidth = 2f;
        titleParams.borderColor = Color.BLACK;
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter bodyParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bodyParams.size = 34;
        bodyParams.color = new Color(0.92f, 0.92f, 0.88f, 1f);
        bodyParams.characters = LanguageButton.FONT_CHARACTERS;
        bodyFont = generator.generateFont(bodyParams);
        generator.dispose();

        Label titleLabel = new Label(LanguageButton.tf("COOP_LEVEL_TITLE_FMT", String.format("%02d", level)),
                new Label.LabelStyle(titleFont, Color.WHITE));
        statusLabel = new Label("Connecting to dedicated server...",
                new Label.LabelStyle(bodyFont, Color.WHITE));
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.add(titleLabel).padTop(16f).padBottom(14f).row();
        table.add(statusLabel).width(980f).padTop(370f);
        stage.addActor(table);

        connectGameplayClient();
    }

    @Override
    public void render(float delta) {
        if (ended) {
            return;
        }
        AudioManager.get().updateLevelAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            abortMatch(LanguageButton.t("PLAYER_LEFT_MATCH"));
            return;
        }
        sendCurrentInput();

        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0f, 0f, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();
        drawArena();
        stage.act(delta);
        stage.draw();
    }

    private void drawArena() {
        float worldWidth = stage.getViewport().getWorldWidth();
        float worldHeight = stage.getViewport().getWorldHeight();
        float arenaX = 120f;
        float arenaY = 100f;
        float arenaWidth = worldWidth - 240f;
        float arenaHeight = 340f;

        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        for (PrototypeLevelOneLayout.Obstacle obstacle : PrototypeLevelOneLayout.obstacles()) {
            Texture texture = obstacleTextures.get(obstacle.getTextureName());
            if (texture == null) {
                continue;
            }
            float drawX = arenaX + (((obstacle.getX() - (obstacle.getWidth() * 0.5f)) / PrototypeLevelOneLayout.WORLD_WIDTH) * arenaWidth);
            float drawY = arenaY + (((obstacle.getY() - (obstacle.getHeight() * 0.5f)) / PrototypeLevelOneLayout.WORLD_HEIGHT) * arenaHeight);
            float drawW = (obstacle.getWidth() / PrototypeLevelOneLayout.WORLD_WIDTH) * arenaWidth;
            float drawH = (obstacle.getHeight() / PrototypeLevelOneLayout.WORLD_HEIGHT) * arenaHeight;
            batch.setColor(obstacle.isDeadly() ? new Color(1f, 0.92f, 0.92f, 1f) : Color.WHITE);
            batch.draw(new TextureRegion(texture),
                    drawX, drawY,
                    drawW * 0.5f, drawH * 0.5f,
                    drawW, drawH,
                    1f, 1f,
                    obstacle.getAngleDegrees());
        }
        batch.setColor(Color.WHITE);
        batch.end();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.10f, 0.12f, 0.15f, 1f);
        shapeRenderer.rect(arenaX, arenaY, arenaWidth, arenaHeight);

        for (PrototypeLevelOneLayout.Obstacle obstacle : PrototypeLevelOneLayout.obstacles()) {
            if (!obstacle.isDeadly()) {
                continue;
            }
            float drawX = arenaX + (((obstacle.getX() - (obstacle.getWidth() * 0.5f)) / PrototypeLevelOneLayout.WORLD_WIDTH) * arenaWidth);
            float drawY = arenaY + (((obstacle.getY() - (obstacle.getHeight() * 0.5f)) / PrototypeLevelOneLayout.WORLD_HEIGHT) * arenaHeight);
            float drawW = (obstacle.getWidth() / PrototypeLevelOneLayout.WORLD_WIDTH) * arenaWidth;
            float drawH = (obstacle.getHeight() / PrototypeLevelOneLayout.WORLD_HEIGHT) * arenaHeight;
            shapeRenderer.setColor(0.84f, 0.20f, 0.20f, 0.18f);
            shapeRenderer.rect(drawX, drawY, drawW, drawH);
        }

        float finishX = arenaX + (arenaWidth * (PrototypeLevelOneLayout.FINISH_X / PrototypeLevelOneLayout.WORLD_WIDTH));
        shapeRenderer.setColor(0.26f, 0.78f, 0.34f, 1f);
        shapeRenderer.rect(finishX, arenaY, 8f, arenaHeight);

        if (latestPlayers != null) {
            for (PlayerSnapshot player : latestPlayers) {
                float px = arenaX + ((player.getX() / PrototypeLevelOneLayout.WORLD_WIDTH) * arenaWidth);
                float py = arenaY + ((player.getY() / PrototypeLevelOneLayout.WORLD_HEIGHT) * arenaHeight);
                float radius = matchState.getLocalUserId().equals(player.getPlayerId()) ? 15f : 12f;
                if (!player.isConnected()) {
                    shapeRenderer.setColor(0.45f, 0.45f, 0.45f, 1f);
                } else if (!player.isAlive()) {
                    shapeRenderer.setColor(0.82f, 0.20f, 0.20f, 1f);
                } else if (matchState.getLocalUserId().equals(player.getPlayerId())) {
                    shapeRenderer.setColor(0.96f, 0.90f, 0.32f, 1f);
                } else {
                    shapeRenderer.setColor(0.34f, 0.68f, 0.96f, 1f);
                }
                shapeRenderer.circle(px, py, radius, 28);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.84f, 0.88f, 0.91f, 1f);
        shapeRenderer.rect(arenaX, arenaY, arenaWidth, arenaHeight);
        shapeRenderer.end();
    }

    private void loadPrototypeTextures() {
        for (PrototypeLevelOneLayout.Obstacle obstacle : PrototypeLevelOneLayout.obstacles()) {
            obstacleTextures.computeIfAbsent(obstacle.getTextureName(),
                    key -> new Texture(Gdx.files.internal("game-resourses/textures/" + key + ".png")));
        }
    }

    private void connectGameplayClient() {
        if (matchState == null) {
            abortMatch("Missing co-op match state.");
            return;
        }
        try {
            reservation = game.getGameplayReservationService().reserve(matchState);
            gameplayClient = new DedicatedMatchClient(reservation, this);
            gameplayClient.connect();
        } catch (Exception e) {
            abortMatch("Failed to connect to dedicated server: " + e.getMessage());
        }
    }

    private void sendCurrentInput() {
        if (gameplayClient == null) {
            return;
        }
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveX += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1f;
        gameplayClient.sendInput(++inputFrameNumber, moveX, moveY);
    }

    private void abortMatch(String message) {
        if (ended) {
            return;
        }
        ended = true;
        AudioManager.get().playLevelLose(0.95f);
        cleanupMatchConnection();
        game.setScreen(new CoopStatusScreen(game, LanguageButton.t("CONNECTION_ERROR"), message));
    }

    private void finishMatch(String title, String message) {
        if (ended) {
            return;
        }
        ended = true;
        if ("Victory".equalsIgnoreCase(title)) {
            game.getAchievementManager().onLevelComplete(level);
            AudioManager.get().playLevelWin(0.95f);
        } else {
            AudioManager.get().playLevelLose(0.95f);
        }
        cleanupMatchConnection();
        game.setScreen(new CoopStatusScreen(game, title, message));
    }

    private void cleanupMatchConnection() {
        if (gameplayClient != null) {
            gameplayClient.close();
            gameplayClient = null;
        }
        game.clearCoopMatchState();
    }

    @Override
    public void onServerHello(ServerHelloMessage helloMessage) {
        Gdx.app.postRunnable(() -> {
            if (helloMessage == null) {
                abortMatch("Dedicated server returned an empty handshake.");
                return;
            }
            if (!helloMessage.isAccepted()) {
                abortMatch(helloMessage.getReason() == null ? "Dedicated server rejected the join." : helloMessage.getReason());
                return;
            }
            statusLabel.setText("Connected to " + reservation.getServerHost() + ":" + reservation.getServerUdpPort()
                    + "\nSlot " + helloMessage.getPlayerSlot()
                    + "\nWaiting for " + matchState.getExpectedPlayers() + " players...");
        });
    }

    @Override
    public void onWorldSnapshot(WorldSnapshotMessage snapshotMessage) {
        Gdx.app.postRunnable(() -> applySnapshot(snapshotMessage));
    }

    @Override
    public void onMatchEnded(MatchEndMessage endMessage) {
        Gdx.app.postRunnable(() -> finishMatch(
                endMessage == null || endMessage.getTitle() == null ? LanguageButton.t("MATCH_ENDED") : endMessage.getTitle(),
                endMessage == null || endMessage.getMessage() == null ? LanguageButton.t("COOP_MATCH_FINISHED") : endMessage.getMessage()));
    }

    @Override
    public void onError(String message) {
        Gdx.app.postRunnable(() -> abortMatch(message == null ? LanguageButton.t("CONNECTION_LOST_COOP") : message));
    }

    private void applySnapshot(WorldSnapshotMessage snapshotMessage) {
        if (snapshotMessage == null || ended) {
            return;
        }
        latestPlayers = snapshotMessage.getPlayers();
        if (snapshotMessage.getPhase() == MatchPhase.FINISHED || snapshotMessage.getPhase() == MatchPhase.ABORTED) {
            finishMatch(LanguageButton.t("MATCH_ENDED"),
                    snapshotMessage.getStatusMessage() == null ? LanguageButton.t("COOP_MATCH_FINISHED") : snapshotMessage.getStatusMessage());
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Server: ").append(reservation.getServerHost()).append(':').append(reservation.getServerUdpPort()).append("    ");
        builder.append("Room: ").append(matchState.getMatchId()).append("    ");
        builder.append("Phase: ").append(snapshotMessage.getPhase()).append('\n');
        if (snapshotMessage.getStatusMessage() != null) {
            builder.append(snapshotMessage.getStatusMessage()).append('\n');
        }
        PlayerSnapshot[] players = snapshotMessage.getPlayers();
        if (players != null) {
            for (PlayerSnapshot player : players) {
                builder.append(player.getPlayerId()).append("  slot=").append(player.getSlot())
                        .append("  pos=(").append(Math.round(player.getX() * 10f) / 10f)
                        .append(", ").append(Math.round(player.getY() * 10f) / 10f).append(')')
                        .append(player.isAlive() ? "  ALIVE" : "  DEAD")
                        .append(player.isConnected() ? "" : "  DISCONNECTED")
                        .append('\n');
                if (matchState.getLocalUserId().equals(player.getPlayerId())) {
                    localDead = !player.isAlive();
                    if (localDead && !deathReported) {
                        deathReported = true;
                        game.getAchievementManager().onDeath();
                    }
                }
            }
        }
        builder.append('\n').append("Prototype of Level 01 objects loaded from story assets");
        builder.append('\n').append("Yellow = you, Blue = other players, Red = deadly object hitbox, Green = finish");
        builder.append('\n').append("WASD = move    ESC = leave match");
        statusLabel.setText(builder.toString());
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        AudioManager.get().stopLevelMusic();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (bg != null) bg.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        for (Texture texture : obstacleTextures.values()) {
            texture.dispose();
        }
    }

    private Texture solidTexture(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }
}
