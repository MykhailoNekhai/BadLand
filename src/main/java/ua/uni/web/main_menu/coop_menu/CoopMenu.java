package ua.uni.web.main_menu.coop_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.UserPresence;
import ua.uni.audio.services.AudioManager;
import ua.uni.game.MainGame;
import ua.uni.game.RuntimeProfile;
import ua.uni.levels.CoopPoligonLevel;
import ua.uni.levels.CoopRuinsLevel;
import ua.uni.logging.AppLogger;
import ua.uni.online.CoopMatchState;
import ua.uni.online.CoopProtocol;
import ua.uni.online.NakamaSocket;
import ua.uni.online.Serialization;
import ua.uni.web.main_menu.Menu;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CoopMenu implements Screen, NakamaSocket.EventListener {
    private static final String LOG_TAG = "CoopLobby";
    private static final String DEVICE_PREFS = "badland_online_device";
    private static final String DEVICE_ID_KEY = "device_id";
    private static final int MAX_LEVELS = 10;
    private static final int TRAIL_LEN = 10;

    private final MainGame game;
    private final Map<String, LobbyPlayer> players = new LinkedHashMap<>();
    private final float[] trailX = new float[TRAIL_LEN];
    private final float[] trailY = new float[TRAIL_LEN];
    private final Vector2 mouseTmp = new Vector2();

    private Stage stage;
    private Texture bg;
    private Texture fg;
    private Texture panel;
    private Texture buttonTex;
    private Texture fieldTex;
    private Texture vignette;
    private Texture dotTex;
    private BitmapFont titleFont;
    private BitmapFont uiFont;
    private BitmapFont statusFont;
    private TextField matchIdField;
    private Label roleLabel;
    private Label sessionLabel;
    private Label matchLabel;
    private Label playersLabel;
    private Label levelLabel;
    private Label statusLabel;
    private TextButton readyButton;
    private TextButton startButton;
    private TextButton levelPrevButton;
    private TextButton levelNextButton;
    private int trailHead;
    private int selectedLevel = 1;
    private String currentMatchId;
    private String hostUserId;
    private String selfUserId;
    private Session currentSession;
    private boolean isHost;
    private boolean isReady;
    private boolean preserveConnectionOnDispose;
    private boolean gameStarting;

    public CoopMenu(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().playMenuMusic();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/coop_bg_generated.png"));
        fg = new Texture(Gdx.files.internal("game-resourses/menu/coop_fg_generated.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        fg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        panel = roundedRect(860, 620, 42, Color.BLACK);
        buttonTex = roundedRect(280, 82, 28, new Color(0f, 0f, 0f, 0.92f));
        fieldTex = roundedRect(580, 76, 24, new Color(0.07f, 0.07f, 0.08f, 0.96f));
        vignette = makeVignette(1280, 720);
        dotTex = softCircleTexture(14);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 100;
        titleParams.color = Color.WHITE;
        titleParams.borderWidth = 2f;
        titleParams.borderColor = Color.BLACK;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter uiParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        uiParams.size = 40;
        uiParams.color = Color.WHITE;
        uiFont = generator.generateFont(uiParams);

        FreeTypeFontGenerator.FreeTypeFontParameter statusParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        statusParams.size = 28;
        statusParams.color = new Color(0.92f, 0.92f, 0.92f, 1f);
        statusFont = generator.generateFont(statusParams);
        generator.dispose();

        buildUi();
        refreshLobbyUi("Create a room or join by match ID.");
    }

    private void buildUi() {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(buttonTex);
        buttonStyle.over = new TextureRegionDrawable(buttonTex);
        buttonStyle.down = new TextureRegionDrawable(buttonTex);
        buttonStyle.font = uiFont;

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
        fieldStyle.font = uiFont;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.messageFont = statusFont;
        fieldStyle.messageFontColor = new Color(0.65f, 0.65f, 0.67f, 1f);
        fieldStyle.cursor = new TextureRegionDrawable(solidTexture(3, 44, Color.WHITE));
        fieldStyle.background = new TextureRegionDrawable(fieldTex);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label.LabelStyle infoStyle = new Label.LabelStyle(statusFont, Color.WHITE);

        Label titleLabel = new Label("CO-OP LOBBY", titleStyle);
        Label infoLabel = new Label("Host selects level and starts. Everyone must be ready.", infoStyle);
        infoLabel.setAlignment(Align.center);

        roleLabel = new Label("", infoStyle);
        sessionLabel = new Label("", infoStyle);
        matchLabel = new Label("", infoStyle);
        playersLabel = new Label("", infoStyle);
        playersLabel.setAlignment(Align.topLeft);
        playersLabel.setWrap(true);
        levelLabel = new Label("", infoStyle);
        statusLabel = new Label("", infoStyle);
        statusLabel.setWrap(true);
        statusLabel.setAlignment(Align.center);

        matchIdField = new TextField("", fieldStyle);
        matchIdField.setMessageText("Enter match ID");

        TextButton createButton = new TextButton("CREATE MATCH", buttonStyle);
        TextButton joinButton = new TextButton("JOIN MATCH", buttonStyle);
        TextButton leaveButton = new TextButton("LEAVE MATCH", buttonStyle);
        TextButton backButton = new TextButton("BACK", buttonStyle);
        readyButton = new TextButton("READY: OFF", buttonStyle);
        startButton = new TextButton("START", buttonStyle);
        levelPrevButton = new TextButton("LEVEL -", buttonStyle);
        levelNextButton = new TextButton("LEVEL +", buttonStyle);

        createButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playStart(0.8f);
                createMatch();
            }
        });
        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.72f);
                joinMatch();
            }
        });
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.70f);
                leaveMatchAndDisconnect("Left lobby.");
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.72f);
                leaveMatchAndDisconnect("Closed coop lobby.");
                game.setScreen(new Menu(game));
            }
        });
        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.68f);
                toggleReady();
            }
        });
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playStart(0.85f);
                startMatchIfPossible();
            }
        });
        levelPrevButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.62f);
                changeLevel(-1);
            }
        });
        levelNextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.62f);
                changeLevel(1);
            }
        });

        Table content = new Table();
        content.center();
        content.defaults().padBottom(12f);
        content.add(titleLabel).padBottom(4f).row();
        content.add(infoLabel).width(690f).padBottom(16f).row();
        content.add(roleLabel).width(690f).row();
        content.add(sessionLabel).width(690f).row();
        content.add(matchLabel).width(690f).row();
        content.add(levelLabel).width(690f).padBottom(4f).row();

        Table levelRow = new Table();
        levelRow.add(levelPrevButton).width(240f).height(76f).padRight(14f);
        levelRow.add(levelNextButton).width(240f).height(76f);
        content.add(levelRow).padBottom(16f).row();

        content.add(matchIdField).width(580f).height(76f).padBottom(18f).row();

        Table buttonRowTop = new Table();
        buttonRowTop.add(createButton).width(280f).height(82f).padRight(12f);
        buttonRowTop.add(joinButton).width(280f).height(82f);
        content.add(buttonRowTop).padBottom(10f).row();

        Table buttonRowMid = new Table();
        buttonRowMid.add(readyButton).width(280f).height(82f).padRight(12f);
        buttonRowMid.add(startButton).width(280f).height(82f);
        content.add(buttonRowMid).padBottom(10f).row();

        Table buttonRowBottom = new Table();
        buttonRowBottom.add(leaveButton).width(280f).height(82f).padRight(12f);
        buttonRowBottom.add(backButton).width(280f).height(82f);
        content.add(buttonRowBottom).padBottom(14f).row();

        content.add(playersLabel).width(690f).height(110f).padBottom(12f).row();
        content.add(statusLabel).width(690f).row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(panel)));
        panelStack.add(content);

        Table panelWrap = new Table();
        panelWrap.setFillParent(true);
        panelWrap.center();
        panelWrap.add(panelStack).width(860f).height(620f).padTop(16f);
        stage.addActor(panelWrap);
    }

    private void createMatch() {
        try {
            Session session = ensureConnectedSession();
            Match match = game.getNakamaMatchService().createMatch();
            if (match == null || match.getMatchId() == null || match.getMatchId().isBlank()) {
                throw new IllegalStateException("Nakama returned an empty match");
            }
            currentSession = session;
            selfUserId = session.getUserId();
            currentMatchId = match.getMatchId();
            hostUserId = selfUserId;
            isHost = true;
            isReady = false;
            selectedLevel = 1;
            gameStarting = false;
            players.clear();
            hydratePlayersFromMatch(match);
            matchIdField.setText(currentMatchId);
            broadcastLobbySnapshot();
            AppLogger.info(LOG_TAG, "Created coop match id=" + currentMatchId + " host=" + selfUserId);
            refreshLobbyUi("Match created. Waiting for players.");
        } catch (Exception e) {
            refreshLobbyUi(userFacingError("create", e));
            AppLogger.error(LOG_TAG, "Failed to create coop match", e);
        }
    }

    private void joinMatch() {
        String matchId = matchIdField.getText().trim();
        if (matchId.isEmpty()) {
            refreshLobbyUi("Enter a match ID first.");
            return;
        }
        try {
            Session session = ensureConnectedSession();
            Match match = game.getNakamaMatchService().joinMatch(matchId);
            if (match == null || match.getMatchId() == null || match.getMatchId().isBlank()) {
                throw new IllegalStateException("Nakama returned an empty joined match");
            }
            currentSession = session;
            selfUserId = session.getUserId();
            currentMatchId = match.getMatchId();
            isHost = false;
            isReady = false;
            gameStarting = false;
            players.clear();
            hydratePlayersFromMatch(match);
            matchIdField.setText(currentMatchId);
            sendReadyState();
            AppLogger.info(LOG_TAG, "Joined coop match id=" + currentMatchId + " guest=" + selfUserId);
            refreshLobbyUi("Joined match. Waiting for host lobby sync.");
        } catch (Exception e) {
            refreshLobbyUi(userFacingError("join", e));
            AppLogger.error(LOG_TAG, "Failed to join coop match", e);
        }
    }

    private void hydratePlayersFromMatch(Match match) {
        addPresence(match.getSelf(), false);
        if (match.getPresences() == null) {
            return;
        }
        for (UserPresence presence : match.getPresences()) {
            addPresence(presence, false);
        }
    }

    private Session ensureConnectedSession() {
        if (!game.getOnlineConfig().isEnabled()) {
            throw new IllegalStateException("Online mode is disabled in nakama.properties");
        }

        Session session = game.getNakamaSessionService().restoreSession();
        if (session == null) {
            session = createFallbackSession();
        }
        game.getNakamaSessionService().refreshIfNeeded(session);

        if (!game.getNakamaMatchService().isConnected()) {
            game.getNakamaMatchService().connect(session, this);
        }
        currentSession = session;
        selfUserId = session.getUserId();
        return session;
    }

    private Session createFallbackSession() {
        String uid = game.getSessionManager().getUid().trim();
        if (!uid.isEmpty()) {
            String email = game.getSessionManager().getEmail().trim();
            return game.getNakamaSessionService().authenticateFirebaseUser(uid, usernameFromEmailOrUid(email, uid));
        }

        String deviceId = getOrCreateDeviceId();
        return game.getNakamaSessionService().authenticateDevice(deviceId, "guest-" + deviceId.substring(0, 8));
    }

    private void toggleReady() {
        if (currentMatchId == null || selfUserId == null) {
            refreshLobbyUi("Join or create a match first.");
            return;
        }
        isReady = !isReady;
        LobbyPlayer self = players.get(selfUserId);
        if (self != null) {
            self.ready = isReady;
        }
        sendReadyState();
        if (isHost) {
            broadcastLobbySnapshot();
        } else {
            refreshLobbyUi("Ready state sent to host.");
        }
        refreshLobbyUi(null);
    }

    private void changeLevel(int delta) {
        if (!isHost || currentMatchId == null) {
            refreshLobbyUi("Only the host can select level.");
            return;
        }
        selectedLevel += delta;
        if (selectedLevel < 1) selectedLevel = MAX_LEVELS;
        if (selectedLevel > MAX_LEVELS) selectedLevel = 1;
        broadcastLobbySnapshot();
        refreshLobbyUi("Host selected level " + String.format("%02d", selectedLevel) + ".");
    }

    private void startMatchIfPossible() {
        if (!isHost) {
            refreshLobbyUi("Only the host can start the match.");
            return;
        }
        if (players.size() < 2) {
            refreshLobbyUi("Need at least 2 players.");
            return;
        }
        if (!allPlayersReady()) {
            refreshLobbyUi("Everyone must be ready before start.");
            return;
        }
        Map<String, String> message = new LinkedHashMap<>();
        message.put("type", "start");
        message.put("level", String.valueOf(selectedLevel));
        game.getNakamaMatchService().sendMatchData(currentMatchId, CoopProtocol.OP_START_GAME,
                Serialization.toJson(message).getBytes(StandardCharsets.UTF_8));
        startSelectedLevel(selectedLevel);
    }

    private boolean allPlayersReady() {
        if (players.isEmpty()) {
            return false;
        }
        for (LobbyPlayer player : players.values()) {
            if (!player.ready) {
                return false;
            }
        }
        return true;
    }

    private void sendReadyState() {
        if (currentMatchId == null || selfUserId == null) {
            return;
        }
        Map<String, String> message = new LinkedHashMap<>();
        message.put("type", "ready");
        message.put("userId", selfUserId);
        message.put("ready", String.valueOf(isReady));
        game.getNakamaMatchService().sendMatchData(currentMatchId, CoopProtocol.OP_READY,
                Serialization.toJson(message).getBytes(StandardCharsets.UTF_8));
    }

    private void broadcastLobbySnapshot() {
        if (!isHost || currentMatchId == null || hostUserId == null) {
            return;
        }
        Map<String, String> message = new LinkedHashMap<>();
        message.put("type", "snapshot");
        message.put("hostUserId", hostUserId);
        message.put("selectedLevel", String.valueOf(selectedLevel));
        message.put("players", encodePlayers());
        game.getNakamaMatchService().sendMatchData(currentMatchId, CoopProtocol.OP_LOBBY_SNAPSHOT,
                Serialization.toJson(message).getBytes(StandardCharsets.UTF_8));
    }

    private String encodePlayers() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (LobbyPlayer player : players.values()) {
            if (!first) {
                builder.append(';');
            }
            builder.append(player.userId).append(',')
                    .append(player.username.replace(",", "_").replace(";", "_")).append(',')
                    .append(player.ready);
            first = false;
        }
        return builder.toString();
    }

    private void applySnapshot(Map<String, String> snapshot) {
        String snapshotHost = snapshot.get("hostUserId");
        if (snapshotHost != null && !snapshotHost.isBlank()) {
            hostUserId = snapshotHost;
        }
        String levelValue = snapshot.get("selectedLevel");
        if (levelValue != null && !levelValue.isBlank()) {
            try {
                selectedLevel = Integer.parseInt(levelValue);
            } catch (NumberFormatException ignored) {
            }
        }
        String playersValue = snapshot.get("players");
        if (playersValue != null) {
            players.clear();
            if (!playersValue.isBlank()) {
                String[] rows = playersValue.split(";");
                for (String row : rows) {
                    String[] parts = row.split(",", 3);
                    if (parts.length < 3) {
                        continue;
                    }
                    LobbyPlayer player = new LobbyPlayer(parts[0], parts[1]);
                    player.ready = Boolean.parseBoolean(parts[2]);
                    players.put(player.userId, player);
                }
            }
        }
        isHost = selfUserId != null && selfUserId.equals(hostUserId);
        LobbyPlayer self = players.get(selfUserId);
        if (self != null) {
            isReady = self.ready;
        }
    }

    private void addPresence(UserPresence presence, boolean ready) {
        if (presence == null || presence.getUserId() == null) {
            return;
        }
        LobbyPlayer existing = players.get(presence.getUserId());
        String username = presence.getUsername();
        if (username == null || username.isBlank()) {
            username = shortId(presence.getUserId());
        }
        if (existing == null) {
            existing = new LobbyPlayer(presence.getUserId(), username);
            players.put(existing.userId, existing);
        }
        existing.username = username;
        existing.ready = ready;
    }

    private void leaveMatchAndDisconnect(String message) {
        preserveConnectionOnDispose = false;
        gameStarting = false;
        game.clearCoopMatchState();
        if (currentMatchId != null && game.getNakamaMatchService().isConnected()) {
            try {
                game.getNakamaMatchService().leaveMatch(currentMatchId);
            } catch (Exception e) {
                AppLogger.error(LOG_TAG, "Failed to leave coop match cleanly", e);
            }
        }
        if (game.getNakamaMatchService().isConnected()) {
            game.getNakamaMatchService().disconnect();
        }
        currentMatchId = null;
        hostUserId = null;
        isHost = false;
        isReady = false;
        players.clear();
        matchIdField.setText("");
        refreshLobbyUi(message);
    }

    private void startSelectedLevel(int level) {
        preserveConnectionOnDispose = true;
        gameStarting = true;
        game.setCoopMatchState(new CoopMatchState(currentMatchId, selfUserId, hostUserId, level));
        if (level == 1) {
            game.setScreen(new CoopPoligonLevel(game));
            return;
        }
        if (level == 2) {
            game.setScreen(new CoopRuinsLevel(game));
            return;
        }
        game.setScreen(new CoopLevelPlayScreen(game, level));
    }

    private void refreshLobbyUi(String message) {
        String role = "Role: " + (currentMatchId == null ? "not in lobby" : (isHost ? "HOST" : "GUEST"));
        String session = "Session: " + (selfUserId == null ? "disconnected" : shortId(selfUserId));
        String match = "Match: " + (currentMatchId == null ? "none" : currentMatchId);
        String level = "Level: " + String.format("%02d", selectedLevel) + (isHost ? " (host selects)" : " (host controls)");
        StringBuilder playersText = new StringBuilder("Players:\n");
        if (players.isEmpty()) {
            playersText.append("Waiting for lobby...");
        } else {
            boolean first = true;
            for (LobbyPlayer player : players.values()) {
                if (!first) {
                    playersText.append('\n');
                }
                playersText.append(player.userId.equals(hostUserId) ? "[HOST] " : "[GUEST] ")
                        .append(player.username)
                        .append(" - ")
                        .append(player.ready ? "READY" : "NOT READY");
                first = false;
            }
        }

        roleLabel.setText(role);
        sessionLabel.setText(session);
        matchLabel.setText(match);
        levelLabel.setText(level);
        playersLabel.setText(playersText.toString());
        readyButton.setText(isReady ? "READY: ON" : "READY: OFF");
        readyButton.setDisabled(currentMatchId == null);
        levelPrevButton.setDisabled(!isHost || currentMatchId == null);
        levelNextButton.setDisabled(!isHost || currentMatchId == null);
        startButton.setDisabled(!isHost || currentMatchId == null || !allPlayersReady() || players.size() < 2);
        if (message != null) {
            statusLabel.setText(message);
        }
    }

    @Override
    public void onDisconnect(Throwable throwable) {
        if (gameStarting) {
            return;
        }
        Gdx.app.postRunnable(() -> refreshLobbyUi("Disconnected from lobby."));
    }

    @Override
    public void onError(Error error) {
        if (error == null) {
            return;
        }
        Gdx.app.postRunnable(() -> refreshLobbyUi("Socket error: " + error.getMessage()));
    }

    @Override
    public void onMatchData(MatchData matchData) {
        if (matchData == null || currentMatchId == null || !currentMatchId.equals(matchData.getMatchId())) {
            return;
        }
        Map<String, String> data = Serialization.fromJson(new String(matchData.getData(), StandardCharsets.UTF_8));
        if (data == null) {
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_LOBBY_SNAPSHOT) {
            Gdx.app.postRunnable(() -> {
                applySnapshot(data);
                refreshLobbyUi("Lobby updated.");
            });
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_READY && isHost) {
            String userId = data.get("userId");
            boolean ready = Boolean.parseBoolean(data.getOrDefault("ready", "false"));
            Gdx.app.postRunnable(() -> {
                LobbyPlayer player = players.get(userId);
                if (player != null) {
                    player.ready = ready;
                    broadcastLobbySnapshot();
                    refreshLobbyUi(player.username + " changed ready state.");
                }
            });
            return;
        }
        if (matchData.getOpCode() == CoopProtocol.OP_START_GAME) {
            String levelValue = data.get("level");
            int level = selectedLevel;
            if (levelValue != null) {
                try {
                    level = Integer.parseInt(levelValue);
                } catch (NumberFormatException ignored) {
                }
            }
            int finalLevel = level;
            Gdx.app.postRunnable(() -> startSelectedLevel(finalLevel));
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent presenceEvent) {
        if (presenceEvent == null || currentMatchId == null || !currentMatchId.equals(presenceEvent.getMatchId())) {
            return;
        }
        Gdx.app.postRunnable(() -> {
            if (presenceEvent.getJoins() != null) {
                for (UserPresence join : presenceEvent.getJoins()) {
                    addPresence(join, false);
                }
            }
            if (presenceEvent.getLeaves() != null) {
                for (UserPresence leave : presenceEvent.getLeaves()) {
                    players.remove(leave.getUserId());
                    if (leave.getUserId() != null && leave.getUserId().equals(hostUserId) && !isHost) {
                        leaveMatchAndDisconnect("Host disconnected. Lobby closed.");
                        return;
                    }
                }
            }
            if (isHost) {
                broadcastLobbySnapshot();
            }
            refreshLobbyUi("Lobby presence updated.");
        });
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            leaveMatchAndDisconnect("Closed coop lobby.");
            game.setScreen(new Menu(game));
            return;
        }

        updateTrail();
        var batch = stage.getBatch();
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 0.95f);
        batch.draw(fg, 0f, 0f, w, h);
        batch.setColor(0f, 0f, 0f, 0.18f);
        batch.draw(vignette, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        stage.act(delta);
        stage.draw();
        drawTrail();
    }

    private void updateTrail() {
        mouseTmp.set(Gdx.input.getX(), Gdx.input.getY());
        stage.screenToStageCoordinates(mouseTmp);
        trailX[trailHead] = mouseTmp.x;
        trailY[trailHead] = mouseTmp.y;
        trailHead = (trailHead + 1) % TRAIL_LEN;
    }

    private void drawTrail() {
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        for (int i = 0; i < TRAIL_LEN; i++) {
            int idx = (trailHead - 1 - i + TRAIL_LEN) % TRAIL_LEN;
            float alpha = (TRAIL_LEN - i) / (float) TRAIL_LEN * 0.45f;
            float size = 8f - i * 0.4f;
            batch.setColor(0.78f, 0.92f, 0.42f, alpha);
            batch.draw(dotTex, trailX[idx] - size, trailY[idx] - size, size * 2f, size * 2f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
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
        if (!preserveConnectionOnDispose) {
            leaveMatchAndDisconnect("Disposed coop lobby.");
        }
        if (stage != null) stage.dispose();
        if (bg != null) bg.dispose();
        if (fg != null) fg.dispose();
        if (panel != null) panel.dispose();
        if (buttonTex != null) buttonTex.dispose();
        if (fieldTex != null) fieldTex.dispose();
        if (vignette != null) vignette.dispose();
        if (dotTex != null) dotTex.dispose();
        if (titleFont != null) titleFont.dispose();
        if (uiFont != null) uiFont.dispose();
        if (statusFont != null) statusFont.dispose();
    }

    private String getOrCreateDeviceId() {
        Preferences preferences = Gdx.app.getPreferences(RuntimeProfile.prefsName(DEVICE_PREFS));
        String stored = preferences.getString(DEVICE_ID_KEY, "").trim();
        if (!stored.isEmpty()) {
            return stored;
        }
        String created = UUID.randomUUID().toString();
        preferences.putString(DEVICE_ID_KEY, created);
        preferences.flush();
        return created;
    }

    private String usernameFromEmailOrUid(String email, String uid) {
        if (!email.isBlank()) {
            int atIndex = email.indexOf('@');
            String prefix = atIndex > 0 ? email.substring(0, atIndex) : email;
            return sanitizeUsername(prefix, uid);
        }
        return sanitizeUsername("player-" + uid.substring(0, Math.min(8, uid.length())), uid);
    }

    private String sanitizeUsername(String candidate, String fallbackSeed) {
        String sanitized = candidate.toLowerCase().replaceAll("[^a-z0-9_\\-]", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        if (sanitized.length() < 3) {
            sanitized = "player_" + fallbackSeed.substring(0, Math.min(6, fallbackSeed.length())).toLowerCase();
        }
        if (sanitized.length() > 24) {
            sanitized = sanitized.substring(0, 24);
        }
        return sanitized;
    }

    private String shortId(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.length() <= 8 ? value : value.substring(0, 8);
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.isBlank() ? current.getClass().getSimpleName() : message;
    }

    private String userFacingError(String action, Throwable throwable) {
        String message = rootMessage(throwable);
        String normalized = message.toLowerCase();

        if (normalized.contains("unavailable") || normalized.contains("connection refused")
                || normalized.contains("failed to connect") || normalized.contains("socket")) {
            return "Cannot connect to the online server. Start Nakama and try again.";
        }
        if (normalized.contains("not found") || normalized.contains("match id")
                || normalized.contains("empty joined match")) {
            return "Match not found. Check the match ID and ask the host for a fresh code.";
        }
        if (normalized.contains("not connected")) {
            return "Online connection was not established. Try reopening the co-op menu.";
        }
        if ("join".equals(action)) {
            return "Failed to join the match. Check the code and make sure the host is still in the lobby.";
        }
        if ("create".equals(action)) {
            return "Failed to create the match. Check the online server and try again.";
        }
        return message;
    }

    private Texture makeVignette(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        int cx = w / 2;
        int cy = h / 2;
        float maxDist = (float) Math.sqrt((cx * cx) + (cy * cy));
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float t = (float) Math.sqrt((dx * dx) + (dy * dy)) / maxDist;
                float a = Math.min(1f, Math.max(0f, (t - 0.26f) * 1.18f));
                pixmap.setColor(0f, 0f, 0f, a);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture roundedRect(int w, int h, int r, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillRectangle(r, 0, w - (r * 2), h);
        pixmap.fillRectangle(0, r, w, h - (r * 2));
        pixmap.fillCircle(r, r, r);
        pixmap.fillCircle(w - r - 1, r, r);
        pixmap.fillCircle(r, h - r - 1, r);
        pixmap.fillCircle(w - r - 1, h - r - 1, r);
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
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

    private Texture softCircleTexture(int diameter) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        float center = (diameter - 1) * 0.5f;
        float radius = center;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - center;
                float dy = y - center;
                float dist = (float) Math.sqrt((dx * dx) + (dy * dy));
                float t = Math.min(1f, dist / radius);
                float alpha = 1f - (t * t);
                pixmap.setColor(1f, 1f, 1f, alpha);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private static final class LobbyPlayer {
        private final String userId;
        private String username;
        private boolean ready;

        private LobbyPlayer(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }
    }
}
