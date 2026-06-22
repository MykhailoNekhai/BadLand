package ua.uni.presentation.screen.menu.coop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.UserPresence;
import ua.uni.bootstrap.GameServices;
import ua.uni.bootstrap.RuntimeProfile;
import ua.uni.core.logging.AppLogger;
import ua.uni.platform.online.CoopMatchState;
import ua.uni.platform.online.NakamaSocket;
import ua.uni.platform.online.lobby.CoopLobbyController;
import ua.uni.platform.online.lobby.LobbyPlayer;
import ua.uni.presentation.screen.menu.core.PMenu;
import ua.uni.presentation.screen.menu.factory.FontQuality;
import ua.uni.presentation.screen.menu.settings.LanguageButton;
import ua.uni.presentation.screen.menu.ui.MenuFx;
import ua.uni.utility.serialization.Serialization;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class CoopMenu extends PMenu
        implements NakamaSocket.EventListener, CoopLobbyController.Listener {

    private static final String LOG_TAG = "CoopLobby";
    private static final String DEVICE_PREFS = "badland_online_device";
    private static final String DEVICE_ID_KEY = "device_id";
    private static final int TRAIL_LEN = 10;

    private final float[] trailX = new float[TRAIL_LEN];
    private final float[] trailY = new float[TRAIL_LEN];
    private final Vector2 mouseTmp = new Vector2();

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

    private TextButton.TextButtonStyle readyOnStyle;
    private TextButton.TextButtonStyle readyOffStyle;

    private CoopLobbyController controller;
    private int trailHead;
    private float elapsed;

    public CoopMenu(GameServices services) {
        super(services);
    }

    @Override
    public void show() {
        beginMenuShow();

        controller = new CoopLobbyController(services.nakamaMatch(), this);

        bg = new Texture(Gdx.files.internal("game-resourses/menu/coop_bg_custom.png"));
        fg = new Texture(Gdx.files.internal("game-resourses/menu/coop_fg_hd.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        fg.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        panel = textures().gradientPanel(740, 1000, 48, 18, 12,
                new Color(0.06f, 0.08f, 0.13f, 0.95f),
                new Color(0.04f, 0.12f, 0.06f, 0.95f));
        panelVignette = textures().panelVignetteTexture(740, 1000, 48, 18, 12);
        itemBtn = textures().roundedRect(668, 80, 24, new Color(0f, 0f, 0f, 1f));
        halfItemBtn = textures().roundedRect(329, 76, 22, new Color(0f, 0f, 0f, 1f));
        fieldTex = textures().roundedRect(668, 76, 24, new Color(0.07f, 0.07f, 0.08f, 0.96f));
        dotTex = textures().softDotTexture(14);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 68;
        titleParams.color = new Color(0.40f, 0.92f, 0.40f, 1f);
        titleParams.borderWidth = 1.8f;
        titleParams.borderColor = new Color(0.03f, 0.08f, 0.03f, 1f);
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(titleParams);
        titleFont = generator.generateFont(titleParams);
        FontQuality.fixScale(titleFont);

        FreeTypeFontGenerator.FreeTypeFontParameter itemParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        itemParams.size = 52;
        itemParams.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemParams.borderWidth = 1.6f;
        itemParams.borderColor = new Color(0.04f, 0.05f, 0.03f, 1f);
        itemParams.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(itemParams);
        itemFont = generator.generateFont(itemParams);
        FontQuality.fixScale(itemFont);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 32;
        smallParams.color = Color.WHITE;
        smallParams.borderWidth = 1.0f;
        smallParams.borderColor = Color.BLACK;
        smallParams.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(smallParams);
        smallFont = generator.generateFont(smallParams);
        FontQuality.fixScale(smallFont);

        generator.dispose();

        buildUi();
        refreshLobbyUi(LanguageButton.t("CREATE_OR_JOIN_MATCH"));
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle infoStyle = new Label.LabelStyle(smallFont, new Color(0.92f, 0.92f, 0.88f, 1f));

        TextButton.TextButtonStyle itemStyle = new TextButton.TextButtonStyle();
        itemStyle.up = new TextureRegionDrawable(itemBtn);
        itemStyle.down = new TextureRegionDrawable(itemBtn);
        itemStyle.over = new TextureRegionDrawable(itemBtn);
        itemStyle.font = itemFont;
        itemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemStyle.overFontColor = new Color(0.55f, 1f, 0.55f, 1f);
        itemStyle.downFontColor = new Color(0.55f, 1f, 0.55f, 1f);

        TextButton.TextButtonStyle halfItemStyle = new TextButton.TextButtonStyle();
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
        fieldStyle.cursor = new TextureRegionDrawable(textures().solidTexture(3, 60, Color.WHITE));
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
            @Override public void changed(ChangeEvent e, Actor a) { audio().playStart(0.8f); doCreateMatch(); }
        });
        joinButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { audio().playSelect(0.72f); doJoinMatch(); }
        });
        leaveButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                audio().playSelect(0.70f);
                leaveMatchAndDisconnect(LanguageButton.t("LEFT_LOBBY"));
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                audio().playSelect(0.72f);
                MenuFx.runAfterGoldButtonPress(a, () -> {
                    leaveMatchAndDisconnect(LanguageButton.t("CLOSED_COOP_LOBBY"));
                    navigator().goToMainMenu();
                });
            }
        });
        readyButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                audio().playSelect(0.68f);
                MenuFx.runAfterGoldButtonPress(a, () -> controller.toggleReady());
            }
        });
        startButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { audio().playStart(0.85f); controller.startMatch(); }
        });
        levelPrevButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { audio().playSelect(0.62f); controller.changeLevel(-1); }
        });
        levelNextButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { audio().playSelect(0.62f); controller.changeLevel(1); }
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
            Actor actor = revealList[i];
            actor.getColor().a = 0f;
            actor.addAction(Actions.sequence(
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

    // — Network operations (need services, stay in UI layer) —

    private void doCreateMatch() {
        try {
            Session session = ensureConnectedSession();
            Match match = services.nakamaMatch().createMatch();
            if (match == null || match.getMatchId() == null || match.getMatchId().isBlank()) {
                throw new IllegalStateException("Nakama returned an empty match");
            }
            controller.onMatchCreated(session.getUserId(), match);
            matchIdField.setText(controller.getCurrentMatchId());
            AppLogger.info(LOG_TAG, "Created match id=" + controller.getCurrentMatchId());
        } catch (Exception e) {
            refreshLobbyUi(userFacingError("create", e));
            AppLogger.error(LOG_TAG, "Failed to create coop match", e);
        }
    }

    private void doJoinMatch() {
        String matchId = matchIdField.getText().trim();
        if (matchId.isEmpty()) { refreshLobbyUi(LanguageButton.t("ENTER_MATCH_ID_FIRST")); return; }
        try {
            Session session = ensureConnectedSession();
            Match match = services.nakamaMatch().joinMatch(matchId);
            if (match == null || match.getMatchId() == null || match.getMatchId().isBlank()) {
                throw new IllegalStateException(LanguageButton.t("INVALID_MATCH_RESPONSE"));
            }
            controller.onMatchJoined(session.getUserId(), match);
            matchIdField.setText(controller.getCurrentMatchId());
            AppLogger.info(LOG_TAG, "Joined match id=" + controller.getCurrentMatchId());
        } catch (Exception e) {
            refreshLobbyUi(userFacingError("join", e));
            AppLogger.error(LOG_TAG, "Failed to join coop match", e);
        }
    }

    private void leaveMatchAndDisconnect(String message) {
        String matchId = controller.getCurrentMatchId();
        if (matchId != null && services.nakamaMatch().isConnected()) {
            try { services.nakamaMatch().leaveMatch(matchId); }
            catch (Exception e) { AppLogger.error(LOG_TAG, "Failed to leave coop match cleanly", e); }
        }
        if (services.nakamaMatch().isConnected()) services.nakamaMatch().disconnect();
        services.clearCoopMatchState();
        controller.reset();
        matchIdField.setText("");
        refreshLobbyUi(message);
    }

    private void handoffLobbyConnection() {
        String matchId = controller.getCurrentMatchId();
        if (matchId != null && services.nakamaMatch().isConnected()) {
            try { services.nakamaMatch().leaveMatch(matchId); }
            catch (Exception e) { AppLogger.error(LOG_TAG, "Failed to leave lobby during server handoff", e); }
        }
        if (services.nakamaMatch().isConnected()) {
            try { services.nakamaMatch().disconnect(); }
            catch (Exception e) { AppLogger.error(LOG_TAG, "Failed to disconnect Nakama during handoff", e); }
        }
    }

    private Session ensureConnectedSession() {
        if (!services.onlineConfig().isEnabled()) {
            throw new IllegalStateException("Online mode is disabled in nakama.properties");
        }
        Session session = services.nakamaSession().restoreSession();
        if (session == null) session = createFallbackSession();
        services.nakamaSession().refreshIfNeeded(session);
        if (!services.nakamaMatch().isConnected()) {
            services.nakamaMatch().connect(session, this);
        }
        return session;
    }

    private Session createFallbackSession() {
        String uid = services.session().getUid().trim();
        if (!uid.isEmpty()) {
            String email = services.session().getEmail().trim();
            return services.nakamaSession().authenticateFirebaseUser(uid, usernameFromEmailOrUid(email, uid));
        }
        String deviceId = getOrCreateDeviceId();
        return services.nakamaSession().authenticateDevice(deviceId, "guest-" + deviceId.substring(0, 8));
    }

    // — CoopLobbyController.Listener —

    @Override
    public void onStateChanged(String statusMessage) {
        refreshLobbyUi(statusMessage);
    }

    @Override
    public void onStartLevel(int level, boolean keepConnection) {
        services.setCoopMatchState(new CoopMatchState(
                controller.getCurrentMatchId(), controller.getSelfUserId(),
                controller.getHostUserId(), level, controller.getPlayers().size()));
        if (!keepConnection) handoffLobbyConnection();
        navigator().goToCoopLevel(level);
    }

    @Override
    public void onHostLeft() {
        leaveMatchAndDisconnect(LanguageButton.t("HOST_DISCONNECTED"));
    }

    // — NakamaSocket.EventListener —

    @Override
    public void onDisconnect(Throwable throwable) {
        if (controller.isGameStarting()) return;
        Gdx.app.postRunnable(() -> refreshLobbyUi(LanguageButton.t("DISCONNECTED_FROM_LOBBY")));
    }

    @Override
    public void onError(Error error) {
        if (error == null) return;
        Gdx.app.postRunnable(() -> refreshLobbyUi(LanguageButton.tf("SOCKET_ERROR_FMT", error.getMessage())));
    }

    @Override
    public void onMatchData(MatchData matchData) {
        if (matchData == null || !matchData.getMatchId().equals(controller.getCurrentMatchId())) return;
        Map<String, String> data = Serialization.fromJson(new String(matchData.getData(), StandardCharsets.UTF_8));
        if (data != null) Gdx.app.postRunnable(() -> controller.handleMatchData(matchData.getOpCode(), data));
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent event) {
        if (event == null || !event.getMatchId().equals(controller.getCurrentMatchId())) return;
        Gdx.app.postRunnable(() -> {
            if (event.getJoins() != null) {
                for (UserPresence p : event.getJoins()) controller.handlePresenceJoined(p);
            }
            if (event.getLeaves() != null) {
                for (UserPresence p : event.getLeaves()) {
                    if (controller.handlePresenceLeft(p.getUserId())) return;
                }
            }
        });
    }

    // — UI rendering —

    private void refreshLobbyUi(String message) {
        String matchId = controller.getCurrentMatchId();
        String selfId = controller.getSelfUserId();
        String hostId = controller.getHostUserId();
        boolean host = controller.isHost();
        boolean ready = controller.isReady();
        int level = controller.getSelectedLevel();
        Map<String, LobbyPlayer> lobbyPlayers = controller.getPlayers();

        String roleValue = matchId == null
                ? LanguageButton.t("ROLE_NONE")
                : (host ? LanguageButton.t("ROLE_HOST") : LanguageButton.t("ROLE_GUEST"));
        roleLabel.setText(LanguageButton.tf("ROLE_FMT", roleValue));
        sessionLabel.setText(LanguageButton.tf("SESSION_FMT",
                selfId == null ? LanguageButton.t("SESSION_DISCONNECTED") : shortId(selfId)));
        matchLabel.setText(LanguageButton.tf("MATCH_FMT",
                matchId == null ? LanguageButton.t("MATCH_NONE") : matchId));
        levelLabel.setText(LanguageButton.tf("LEVEL_FMT", String.format("%02d", level),
                host ? LanguageButton.t("LEVEL_HOST_SELECTS") : LanguageButton.t("LEVEL_HOST_CONTROLS")));

        StringBuilder playersText = new StringBuilder(LanguageButton.t("PLAYERS_HEADER"));
        if (lobbyPlayers.isEmpty()) {
            playersText.append(LanguageButton.t("WAITING_FOR_LOBBY"));
        } else {
            boolean first = true;
            for (LobbyPlayer player : lobbyPlayers.values()) {
                if (!first) playersText.append('\n');
                playersText.append(player.userId.equals(hostId)
                                ? LanguageButton.t("PLAYER_HOST_PREFIX")
                                : LanguageButton.t("PLAYER_GUEST_PREFIX"))
                        .append(player.username).append(" - ")
                        .append(player.ready ? LanguageButton.t("PLAYER_READY") : LanguageButton.t("PLAYER_NOT_READY"));
                first = false;
            }
        }
        playersLabel.setText(playersText.toString());

        readyButton.setText(ready ? LanguageButton.t("READY_ON") : LanguageButton.t("READY_OFF"));
        readyButton.setStyle(ready ? readyOnStyle : readyOffStyle);
        readyButton.setDisabled(!controller.isInMatch());
        levelPrevButton.setDisabled(!host || !controller.isInMatch());
        levelNextButton.setDisabled(!host || !controller.isInMatch());
        startButton.setDisabled(!controller.canStartMatch());

        if (message != null) statusLabel.setText(message);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        audio().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            leaveMatchAndDisconnect(LanguageButton.t("CLOSED_COOP_LOBBY"));
            navigator().goToMainMenu();
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
        batch.setColor(1f, 1f, 1f, 0.96f);
        batch.draw(fg, -32f, -20f, w + 96f, h + 36f);
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
    public void hide() { endMenuHide(); }

    @Override
    public void dispose() {
        if (!controller.isPreserveConnectionOnDispose()) {
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

    // — Helpers —

    private String getOrCreateDeviceId() {
        Preferences preferences = Gdx.app.getPreferences(RuntimeProfile.prefsName(DEVICE_PREFS));
        String stored = preferences.getString(DEVICE_ID_KEY, "").trim();
        if (!stored.isEmpty()) return stored;
        String created = UUID.randomUUID().toString();
        preferences.putString(DEVICE_ID_KEY, created);
        preferences.flush();
        return created;
    }

    private String usernameFromEmailOrUid(String email, String uid) {
        if (!email.isBlank()) {
            int at = email.indexOf('@');
            String prefix = at > 0 ? email.substring(0, at) : email;
            return sanitizeUsername(prefix, uid);
        }
        return sanitizeUsername("player-" + uid.substring(0, Math.min(8, uid.length())), uid);
    }

    private String sanitizeUsername(String candidate, String fallbackSeed) {
        String s = candidate.toLowerCase().replaceAll("[^a-z0-9_\\-]", "_").replaceAll("_+", "_");
        if (s.length() < 3) s = "player_" + fallbackSeed.substring(0, Math.min(6, fallbackSeed.length())).toLowerCase();
        if (s.length() > 24) s = s.substring(0, 24);
        return s;
    }

    private String shortId(String value) {
        if (value == null || value.isBlank()) return "unknown";
        return value.length() <= 8 ? value : value.substring(0, 8);
    }

    private String userFacingError(String action, Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) current = current.getCause();
        String message = current.getMessage();
        if (message == null || message.isBlank()) message = current.getClass().getSimpleName();
        String normalized = message.toLowerCase();
        if (normalized.contains("unavailable") || normalized.contains("connection refused")
                || normalized.contains("failed to connect") || normalized.contains("socket"))
            return "Cannot connect to the online server. Start Nakama and try again.";
        if (normalized.contains("not found") || normalized.contains("match id")
                || normalized.contains("empty joined match"))
            return "Match not found. Check the match ID and ask the host for a fresh code.";
        if (normalized.contains("not connected"))
            return "Online connection was not established. Try reopening the co-op menu.";
        if ("join".equals(action))
            return "Failed to join the match. Check the code and make sure the host is still in the lobby.";
        if ("create".equals(action))
            return "Failed to create the match. Check the online server and try again.";
        return message;
    }
}
