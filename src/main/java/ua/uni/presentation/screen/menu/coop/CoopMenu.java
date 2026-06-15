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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
    private Texture panelVignette;
    private Texture itemBtn;
    private Texture halfItemBtn;
    private Texture fieldTex;
    private Texture dotTex;

    private BitmapFont titleFont;
    private BitmapFont itemFont;
    private BitmapFont smallFont;

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

    private TextButton.TextButtonStyle itemStyle;
    private TextButton.TextButtonStyle halfItemStyle;
    private TextButton.TextButtonStyle readyOnStyle;
    private TextButton.TextButtonStyle readyOffStyle;

    private int trailHead;
    private float elapsed;
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

        panel = gradientPanel(740, 1000, 48, 18, 12,
                new Color(0.06f, 0.08f, 0.13f, 0.95f),
                new Color(0.04f, 0.12f, 0.06f, 0.95f));
        panelVignette = panelVignetteTexture(740, 1000, 48, 18, 12);
        itemBtn = roundedRect(668, 80, 24, new Color(0f, 0f, 0f, 1f));
        halfItemBtn = roundedRect(329, 76, 22, new Color(0f, 0f, 0f, 1f));
        fieldTex = roundedRect(668, 76, 24, new Color(0.07f, 0.07f, 0.08f, 0.96f));
        dotTex = softDotTexture(14);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 68;
        titleParams.color = new Color(0.40f, 0.92f, 0.40f, 1f);
        titleParams.borderWidth = 1.8f;
        titleParams.borderColor = new Color(0.03f, 0.08f, 0.03f, 1f);
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter itemParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        itemParams.size = 52;
        itemParams.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemParams.borderWidth = 1.6f;
        itemParams.borderColor = new Color(0.04f, 0.05f, 0.03f, 1f);
        itemParams.characters = LanguageButton.FONT_CHARACTERS;
        itemFont = generator.generateFont(itemParams);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 32;
        smallParams.color = Color.WHITE;
        smallParams.borderWidth = 1.0f;
        smallParams.borderColor = Color.BLACK;
        smallParams.characters = LanguageButton.FONT_CHARACTERS;
        smallFont = generator.generateFont(smallParams);

        generator.dispose();

        buildUi();
        refreshLobbyUi(LanguageButton.t("CREATE_OR_JOIN_MATCH"));
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle infoStyle = new Label.LabelStyle(smallFont, new Color(0.92f, 0.92f, 0.88f, 1f));

        itemStyle = new TextButton.TextButtonStyle();
        itemStyle.up = new TextureRegionDrawable(itemBtn);
        itemStyle.down = new TextureRegionDrawable(itemBtn);
        itemStyle.over = new TextureRegionDrawable(itemBtn);
        itemStyle.font = itemFont;
        itemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemStyle.overFontColor = new Color(0.55f, 1f, 0.55f, 1f);
        itemStyle.downFontColor = new Color(0.55f, 1f, 0.55f, 1f);

        halfItemStyle = new TextButton.TextButtonStyle();
        halfItemStyle.up = new TextureRegionDrawable(halfItemBtn);
        halfItemStyle.down = new TextureRegionDrawable(halfItemBtn);
        halfItemStyle.over = new TextureRegionDrawable(halfItemBtn);
        halfItemStyle.font = itemFont;
        halfItemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        halfItemStyle.overFontColor = new Color(0.55f, 1f, 0.55f, 1f);
        halfItemStyle.downFontColor = new Color(0.55f, 1f, 0.55f, 1f);

        readyOffStyle = itemStyle;

        readyOnStyle = new TextButton.TextButtonStyle();
        readyOnStyle.up = new TextureRegionDrawable(itemBtn);
        readyOnStyle.down = new TextureRegionDrawable(itemBtn);
        readyOnStyle.over = new TextureRegionDrawable(itemBtn);
        readyOnStyle.font = itemFont;
        readyOnStyle.fontColor = new Color(0.40f, 0.92f, 0.40f, 1f);
        readyOnStyle.overFontColor = new Color(0.55f, 1f, 0.55f, 1f);
        readyOnStyle.downFontColor = new Color(0.55f, 1f, 0.55f, 1f);

        TextButton.TextButtonStyle backBtnStyle = new TextButton.TextButtonStyle();
        backBtnStyle.font = itemFont;
        backBtnStyle.fontColor = new Color(0.95f, 0.90f, 0.65f, 1f);
        backBtnStyle.overFontColor = new Color(0.55f, 1f, 0.55f, 1f);
        backBtnStyle.downFontColor = new Color(0.55f, 1f, 0.55f, 1f);

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
        fieldStyle.font = itemFont;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.messageFont = smallFont;
        fieldStyle.messageFontColor = new Color(0.65f, 0.65f, 0.67f, 1f);
        fieldStyle.cursor = new TextureRegionDrawable(solidTexture(3, 60, Color.WHITE));
        fieldStyle.background = new TextureRegionDrawable(fieldTex);

        Label titleLabel = new Label(LanguageButton.t("COOP_LOBBY"), titleStyle);
        Label infoLabel = new Label(LanguageButton.t("COOP_LOBBY_INFO"), infoStyle);
        infoLabel.setAlignment(Align.center);

        roleLabel = new Label("", infoStyle);
        sessionLabel = new Label("", infoStyle);
        matchLabel = new Label("", infoStyle);
        levelLabel = new Label("", infoStyle);
        playersLabel = new Label("", infoStyle);
        playersLabel.setAlignment(Align.topLeft);
        playersLabel.setWrap(true);
        statusLabel = new Label("", infoStyle);
        statusLabel.setWrap(true);
        statusLabel.setAlignment(Align.center);

        matchIdField = new TextField("", fieldStyle);
        matchIdField.setMessageText(LanguageButton.t("ENTER_MATCH_ID"));

        TextButton createButton = new TextButton(LanguageButton.t("CREATE_MATCH"), itemStyle);
        TextButton joinButton = new TextButton(LanguageButton.t("JOIN_MATCH"), itemStyle);
        TextButton leaveButton = new TextButton(LanguageButton.t("LEAVE_MATCH"), itemStyle);
        TextButton backButton = new TextButton(LanguageButton.t("BACK"), backBtnStyle);
        readyButton = new TextButton(LanguageButton.t("READY_OFF"), readyOffStyle);
        startButton = new TextButton(LanguageButton.t("START"), itemStyle);
        levelPrevButton = new TextButton(LanguageButton.t("LEVEL_PREV"), halfItemStyle);
        levelNextButton = new TextButton(LanguageButton.t("LEVEL_NEXT"), halfItemStyle);

        createButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playStart(0.8f);
                createMatch();
            }
        });
        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                joinMatch();
            }
        });
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.70f);
                leaveMatchAndDisconnect(LanguageButton.t("LEFT_LOBBY"));
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                leaveMatchAndDisconnect(LanguageButton.t("CLOSED_COOP_LOBBY"));
                game.setScreen(new Menu(game));
            }
        });
        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.68f);
                toggleReady();
            }
        });
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playStart(0.85f);
                startMatchIfPossible();
            }
        });
        levelPrevButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.62f);
                changeLevel(-1);
            }
        });
        levelNextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.62f);
                changeLevel(1);
            }
        });

        Table levelRow = new Table();
        levelRow.add(levelPrevButton).width(329).height(70).padRight(10);
        levelRow.add(levelNextButton).width(329).height(70);

        Table panelContent = new Table();
        panelContent.defaults().center().padLeft(36).padRight(36);
        panelContent.add(titleLabel).padTop(26).padBottom(12).row();
        panelContent.add(infoLabel).width(668).padBottom(8).row();
        panelContent.add(roleLabel).width(668).padBottom(2).row();
        panelContent.add(sessionLabel).width(668).padBottom(2).row();
        panelContent.add(matchLabel).width(668).padBottom(2).row();
        panelContent.add(levelLabel).width(668).padBottom(6).row();
        panelContent.add(levelRow).padBottom(8).row();
        panelContent.add(matchIdField).width(668).height(70).padBottom(8).row();
        panelContent.add(createButton).width(668).height(76).padBottom(5).row();
        panelContent.add(joinButton).width(668).height(76).padBottom(5).row();
        panelContent.add(readyButton).width(668).height(76).padBottom(5).row();
        panelContent.add(startButton).width(668).height(76).padBottom(5).row();
        panelContent.add(leaveButton).width(668).height(76).padBottom(5).row();
        panelContent.add(backButton).padBottom(8).row();
        panelContent.add(playersLabel).width(668).height(90).padBottom(6).row();
        panelContent.add(statusLabel).width(668).padBottom(12).row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(panel)));
        panelStack.add(new Image(new TextureRegionDrawable(panelVignette)));
        panelStack.add(panelContent);

        Table panelWrap = new Table();
        panelWrap.setFillParent(true);
        panelWrap.center();
        panelWrap.add(panelStack).width(740).height(1000);
        stage.addActor(panelWrap);

        Actor[] revealList = {titleLabel, infoLabel, matchIdField,
                createButton, joinButton, readyButton, startButton, leaveButton, backButton};
        for (int i = 0; i < revealList.length; i++) {
            Actor a = revealList[i];
            a.getColor().a = 0f;
            a.addAction(Actions.sequence(
                    Actions.delay(0.06f * i),
                    Actions.fadeIn(0.30f, Interpolation.fade)
            ));
        }

        panelStack.addAction(Actions.sequence(
                Actions.delay(0.05f),
                Actions.run(() -> panelStack.setOrigin(panelStack.getWidth() / 2f, panelStack.getHeight() / 2f)),
                Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.005f, 1.005f, 2.2f, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 2.2f, Interpolation.sine)
                ))
        ));
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
        readyButton.setStyle(isReady ? readyOnStyle : readyOffStyle);
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
        elapsed += delta;
        AudioManager.get().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            leaveMatchAndDisconnect(LanguageButton.t("CLOSED_COOP_LOBBY"));
            game.setScreen(new Menu(game));
            return;
        }

        updateTrail();

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 0.95f);
        batch.draw(fg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        stage.act(delta);
        stage.draw();

        batch.begin();
        for (int i = 0; i < TRAIL_LEN; i++) {
            int idx = (trailHead - 1 - i + TRAIL_LEN) % TRAIL_LEN;
            float ta = (TRAIL_LEN - i) / (float) TRAIL_LEN * 0.45f;
            float ts = 8f - i * 0.4f;
            batch.setColor(0.40f, 0.95f, 0.40f, ta);
            batch.draw(dotTex, trailX[idx] - ts, trailY[idx] - ts, ts * 2f, ts * 2f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    private void updateTrail() {
        mouseTmp.set(Gdx.input.getX(), Gdx.input.getY());
        stage.screenToStageCoordinates(mouseTmp);
        trailX[trailHead] = mouseTmp.x;
        trailY[trailHead] = mouseTmp.y;
        trailHead = (trailHead + 1) % TRAIL_LEN;
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
        if (panelVignette != null) panelVignette.dispose();
        if (itemBtn != null) itemBtn.dispose();
        if (halfItemBtn != null) halfItemBtn.dispose();
        if (fieldTex != null) fieldTex.dispose();
        if (dotTex != null) dotTex.dispose();
        if (titleFont != null) titleFont.dispose();
        if (itemFont != null) itemFont.dispose();
        if (smallFont != null) smallFont.dispose();
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

    private Texture gradientPanel(int w, int h, int radius, int padX, int padY, Color topColor, Color bottomColor) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int innerH = h - 2 * padY;
        for (int y = padY; y < h - padY; y++) {
            int x0, x1;
            if (y < padY + radius) {
                int dy = (padY + radius) - y;
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else if (y >= h - padY - radius) {
                int dy = y - (h - padY - radius - 1);
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else {
                x0 = padX;
                x1 = w - padX - 1;
            }
            float t = (y - padY) / (float) (innerH - 1);
            float r = topColor.r + (bottomColor.r - topColor.r) * t;
            float g = topColor.g + (bottomColor.g - topColor.g) * t;
            float b = topColor.b + (bottomColor.b - topColor.b) * t;
            float a = topColor.a + (bottomColor.a - topColor.a) * t;
            p.setColor(r, g, b, a);
            p.drawLine(x0, y, x1, y);
        }
        Texture tex = new Texture(p);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return tex;
    }

    private Texture panelVignetteTexture(int w, int h, int radius, int padX, int padY) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int cx = w / 2;
        int cy = h / 2;
        float max = (float) Math.sqrt((double) (cx * cx) + (double) (cy * cy));
        for (int y = padY; y < h - padY; y++) {
            int x0, x1;
            if (y < padY + radius) {
                int dy = (padY + radius) - y;
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else if (y >= h - padY - radius) {
                int dy = y - (h - padY - radius - 1);
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else {
                x0 = padX;
                x1 = w - padX - 1;
            }
            for (int x = x0; x <= x1; x++) {
                float dx2 = x - cx;
                float dy2 = y - cy;
                float d = (float) Math.sqrt((double) (dx2 * dx2) + (double) (dy2 * dy2)) / max;
                float a = Math.max(0f, Math.min(1f, (d - 0.55f) * 1.4f));
                if (a > 0f) {
                    p.setColor(0f, 0f, 0f, a * 0.45f);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(p);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return tex;
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

    private Texture softDotTexture(int diameter) {
        Pixmap p = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        float cx = diameter / 2f;
        float cy = diameter / 2f;
        float maxR = diameter / 2f;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                float t = Math.min(1f, d / maxR);
                float a = (1f - t) * (1f - t);
                if (a > 0f) {
                    p.setColor(1f, 1f, 1f, a);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(p);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return tex;
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
