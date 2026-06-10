package ua.uni.web.main_menu.coop_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import ua.uni.audio.services.AudioManager;
import ua.uni.game.MainGame;
import ua.uni.online.CoopMatchState;
import ua.uni.online.CoopProtocol;
import ua.uni.online.NakamaSocket;
import ua.uni.online.Serialization;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoopLevelPlayScreen implements Screen, NakamaSocket.EventListener {
    private final MainGame game;
    private final int level;
    private final CoopMatchState matchState;
    private Stage stage;
    private Texture bg;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private Label statusLabel;
    private boolean localDead;
    private boolean remoteDead;
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
        AudioManager.get().stopMenuMusic();
        bg = solidTexture(2, 2, new Color(0.03f, 0.03f, 0.04f, 1f));
        game.getNakamaMatchService().setEventListener(this);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 86;
        titleParams.color = Color.WHITE;
        titleParams.borderWidth = 2f;
        titleParams.borderColor = Color.BLACK;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter bodyParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bodyParams.size = 34;
        bodyParams.color = new Color(0.92f, 0.92f, 0.88f, 1f);
        bodyFont = generator.generateFont(bodyParams);
        generator.dispose();

        Label titleLabel = new Label("CO-OP LEVEL " + String.format("%02d", level),
                new Label.LabelStyle(titleFont, Color.WHITE));
        statusLabel = new Label("Placeholder co-op level.\nENTER = finish, K = local death, ESC = abort match",
                new Label.LabelStyle(bodyFont, Color.WHITE));
        statusLabel.setAlignment(Align.center);
        statusLabel.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titleLabel).padBottom(18).row();
        table.add(statusLabel).width(900f);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (ended) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            abortMatch("A player left the match.", true);
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K) && !localDead) {
            localDead = true;
            sendDeathState();
            statusLabel.setText("You are dead. Waiting for teammate...");
            checkTeamWipe();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            finishMatch("Victory", "Co-op level " + String.format("%02d", level) + " completed.", true);
            return;
        }

        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0f, 0f, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    private void sendDeathState() {
        if (matchState == null) {
            return;
        }
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("type", "death");
        payload.put("userId", matchState.getLocalUserId());
        payload.put("dead", "true");
        game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_PLAYER_DEATH,
                Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
    }

    private void checkTeamWipe() {
        if (localDead && remoteDead) {
            finishMatch("Game Over", "All players died. Match ended.", true);
        }
    }

    private void abortMatch(String message, boolean broadcast) {
        if (ended) {
            return;
        }
        ended = true;
        if (broadcast && matchState != null && game.getNakamaMatchService().isConnected()) {
            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("type", "abort");
            payload.put("message", message);
            game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_LEVEL_ABORT,
                    Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
        }
        cleanupMatchConnection();
        game.setScreen(new CoopStatusScreen(game, "Connection Error", message));
    }

    private void finishMatch(String title, String message, boolean broadcast) {
        if (ended) {
            return;
        }
        ended = true;
        if (broadcast && matchState != null && game.getNakamaMatchService().isConnected()) {
            Map<String, String> payload = new LinkedHashMap<>();
            payload.put("type", "result");
            payload.put("title", title);
            payload.put("message", message);
            game.getNakamaMatchService().sendMatchData(matchState.getMatchId(), CoopProtocol.OP_LEVEL_RESULT,
                    Serialization.toJson(payload).getBytes(StandardCharsets.UTF_8));
        }
        cleanupMatchConnection();
        game.setScreen(new CoopStatusScreen(game, title, message));
    }

    private void cleanupMatchConnection() {
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
        Gdx.app.postRunnable(() -> abortMatch("Connection lost during co-op level.", false));
    }

    @Override
    public void onError(Error error) {
        if (error != null) {
            Gdx.app.postRunnable(() -> abortMatch("Socket error: " + error.getMessage(), false));
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
        if (matchData.getOpCode() == CoopProtocol.OP_LEVEL_ABORT) {
            Gdx.app.postRunnable(() -> abortMatch(data.getOrDefault("message", "A player disconnected."), false));
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_LEVEL_RESULT) {
            Gdx.app.postRunnable(() -> finishMatch(
                    data.getOrDefault("title", "Match Ended"),
                    data.getOrDefault("message", "Co-op match finished."), false));
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_PLAYER_DEATH) {
            String senderId = data.get("userId");
            if (senderId != null && !senderId.equals(matchState.getLocalUserId())) {
                Gdx.app.postRunnable(() -> {
                    remoteDead = Boolean.parseBoolean(data.getOrDefault("dead", "false"));
                    statusLabel.setText(remoteDead
                            ? "Teammate died. If you die too, the match ends."
                            : "Teammate is alive.");
                    checkTeamWipe();
                });
            }
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent presenceEvent) {
        if (matchState == null || presenceEvent == null || !matchState.getMatchId().equals(presenceEvent.getMatchId())) {
            return;
        }
        if (presenceEvent.getLeaves() != null && !presenceEvent.getLeaves().isEmpty()) {
            Gdx.app.postRunnable(() -> abortMatch("A player disconnected from the level.", false));
        }
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (bg != null) bg.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
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
