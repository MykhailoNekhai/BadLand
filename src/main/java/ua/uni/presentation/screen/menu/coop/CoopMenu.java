package ua.uni.presentation.screen.menu.coop;

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
import ua.uni.bootstrap.MainGame;
import ua.uni.bootstrap.RuntimeProfile;
//import ua.uni.gameplay.levels.CoopPoligonLevel;
// import ua.uni.gameplay.levels.CoopRuinsLevel;
import ua.uni.core.logging.AppLogger;
import ua.uni.platform.online.CoopMatchState;
import ua.uni.platform.online.CoopProtocol;
import ua.uni.platform.online.NakamaSocket;
import ua.uni.utility.serialization.Serialization;
import ua.uni.presentation.screen.menu.settings.LanguageButton;
import ua.uni.presentation.screen.menu.main.Menu;

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
        AudioManager.get().enterMenuContext();

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
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter uiParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        uiParams.size = 40;
        uiParams.color = Color.WHITE;
        uiParams.characters = LanguageButton.FONT_CHARACTERS;
        uiFont = generator.generateFont(uiParams);

        FreeTypeFontGenerator.FreeTypeFontParameter statusParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        statusParams.size = 28;
        statusParams.color = new Color(0.92f, 0.92f, 0.92f, 1f);
        statusParams.characters = LanguageButton.FONT_CHARACTERS;
        statusFont = generator.generateFont(statusParams);
        generator.dispose();

        buildUi();
        refreshLobbyUi(LanguageButton.t("CREATE_OR_JOIN_MATCH"));
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

        Label titleLabel = new Label(LanguageButton.t("COOP_LOBBY"), titleStyle);
        Label infoLabel = new Label(LanguageButton.t("COOP_LOBBY_INFO"), infoStyle);
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
        matchIdField.setMessageText(LanguageButton.t("ENTER_MATCH_ID"));

        TextButton createButton = new TextButton(LanguageButton.t("CREATE_MATCH"), buttonStyle);
        TextButton joinButton = new TextButton(LanguageButton.t("JOIN_MATCH"), buttonStyle);
        TextButton leaveButton = new TextButton(LanguageButton.t("LEAVE_MATCH"), buttonStyle);
        TextButton backButton = new TextButton(LanguageButton.t("BACK"), buttonStyle);
        readyButton = new TextButton(LanguageButton.t("READY_OFF"), buttonStyle);
        startButton = new TextButton(LanguageButton.t("START"), buttonStyle);
        levelPrevButton = new TextButton(LanguageButton.t("LEVEL_PREV"), buttonStyle);
        levelNextButton = new TextButton(LanguageButton.t("LEVEL_NEXT"), buttonStyle);

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
                leaveMatchAndDisconnect(LanguageButton.t("LEFT_LOBBY"));
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.72f);
                leaveMatchAndDisconnect(LanguageButton.t("CLOSED_COOP_LOBBY"));
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
            refreshLobbyUi(LanguageButton.t("MATCH_CREATED_WAITING"));
        } catch (Exception e) {
            refreshLobbyUi(userFacingError("create", e));
            AppLogger.error(LOG_TAG, "Failed to create coop match", e);
        }
    }

    private void joinMatch() {
        String matchId = matchIdField.getText().trim();
        if (matchId.isEmpty()) {
            refreshLobbyUi(LanguageButton.t("ENTER_MATCH_ID_FIRST"));
            return;
        }
        try {
            Session session = ensureConnectedSession();
            Match match = game.getNakamaMatchService().joinMatch(matchId);
            if (match == null || match.getMatchId() == null || match.getMatchId().isBlank()) {
                throw new IllegalStateException(LanguageButton.t("INVALID_MATCH_RESPONSE"));
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
            refreshLobbyUi(LanguageButton.t("JOINED_MATCH_WAITING"));
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
            refreshLobbyUi(LanguageButton.t("JOIN_OR_CREATE_FIRST"));
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
            refreshLobbyUi(LanguageButton.t("READY_SENT_TO_HOST"));
        }
        refreshLobbyUi(null);
    }

    private void changeLevel(int delta) {
        if (!isHost || currentMatchId == null) {
            refreshLobbyUi(LanguageButton.t("ONLY_HOST_SELECT"));
            return;
        }
        selectedLevel += delta;
        if (selectedLevel < 1) selectedLevel = MAX_LEVELS;
        if (selectedLevel > MAX_LEVELS) selectedLevel = 1;
        broadcastLobbySnapshot();
        refreshLobbyUi(LanguageButton.tf("HOST_SELECTED_LEVEL_FMT", String.format("%02d", selectedLevel)));
    }

    private void startMatchIfPossible() {
        if (!isHost) {
            refreshLobbyUi(LanguageButton.t("ONLY_HOST_START"));
            return;
        }
        if (players.size() < 2) {
            refreshLobbyUi(LanguageButton.t("NEED_AT_LEAST_TWO"));
            return;
        }
        if (!allPlayersReady()) {
            refreshLobbyUi(LanguageButton.t("EVERYONE_READY"));
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
        preserveConnectionOnDispose = false;
        gameStarting = true;
        game.setCoopMatchState(new CoopMatchState(currentMatchId, selfUserId, hostUserId, level, players.size()));
        handoffLobbyConnection();
        game.setScreen(new CoopLevelPlayScreen(game, level));
    }

    private void handoffLobbyConnection() {
        if (currentMatchId != null && game.getNakamaMatchService().isConnected()) {
            try {
                game.getNakamaMatchService().leaveMatch(currentMatchId);
            } catch (Exception e) {
                AppLogger.error(LOG_TAG, "Failed to leave lobby during dedicated server handoff", e);
            }
        }
        if (game.getNakamaMatchService().isConnected()) {
            try {
                game.getNakamaMatchService().disconnect();
            } catch (Exception e) {
                AppLogger.error(LOG_TAG, "Failed to disconnect Nakama during dedicated server handoff", e);
            }
        }
    }

    private void refreshLobbyUi(String message) {
        String roleValue = currentMatchId == null
                ? LanguageButton.t("ROLE_NONE")
                : (isHost ? LanguageButton.t("ROLE_HOST") : LanguageButton.t("ROLE_GUEST"));
        String role = LanguageButton.tf("ROLE_FMT", roleValue);
        String session = LanguageButton.tf("SESSION_FMT",
                selfUserId == null ? LanguageButton.t("SESSION_DISCONNECTED") : shortId(selfUserId));
        String match = LanguageButton.tf("MATCH_FMT",
                currentMatchId == null ? LanguageButton.t("MATCH_NONE") : currentMatchId);
        String level = LanguageButton.tf("LEVEL_FMT", String.format("%02d", selectedLevel),
                isHost ? LanguageButton.t("LEVEL_HOST_SELECTS") : LanguageButton.t("LEVEL_HOST_CONTROLS"));
        StringBuilder playersText = new StringBuilder(LanguageButton.t("PLAYERS_HEADER"));
        if (players.isEmpty()) {
            playersText.append(LanguageButton.t("WAITING_FOR_LOBBY"));
        } else {
            boolean first = true;
            for (LobbyPlayer player : players.values()) {
                if (!first) {
                    playersText.append('\n');
                }
                playersText.append(player.userId.equals(hostUserId)
                                ? LanguageButton.t("PLAYER_HOST_PREFIX")
                                : LanguageButton.t("PLAYER_GUEST_PREFIX"))
                        .append(player.username)
                        .append(" - ")
                        .append(player.ready ? LanguageButton.t("PLAYER_READY") : LanguageButton.t("PLAYER_NOT_READY"));
                first = false;
            }
        }

        roleLabel.setText(role);
        sessionLabel.setText(session);
        matchLabel.setText(match);
        levelLabel.setText(level);
        playersLabel.setText(playersText.toString());
        readyButton.setText(isReady ? LanguageButton.t("READY_ON") : LanguageButton.t("READY_OFF"));
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
        Gdx.app.postRunnable(() -> refreshLobbyUi(LanguageButton.t("DISCONNECTED_FROM_LOBBY")));
    }

    @Override
    public void onError(Error error) {
        if (error == null) {
            return;
        }
        Gdx.app.postRunnable(() -> refreshLobbyUi(LanguageButton.tf("SOCKET_ERROR_FMT", error.getMessage())));
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
                refreshLobbyUi(LanguageButton.t("LOBBY_UPDATED"));
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
                    refreshLobbyUi(LanguageButton.tf("READY_CHANGED_FMT", player.username));
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
                        leaveMatchAndDisconnect(LanguageButton.t("HOST_DISCONNECTED"));
                        return;
                    }
                }
            }
            if (isHost) {
                broadcastLobbySnapshot();
            }
            refreshLobbyUi(LanguageButton.t("LOBBY_PRESENCE_UPDATED"));
        });
    }

    @Override
    public void render(float delta) {
        AudioManager.get().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            leaveMatchAndDisconnect(LanguageButton.t("CLOSED_COOP_LOBBY"));
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
        AudioManager.get().leaveMenuContext();
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
