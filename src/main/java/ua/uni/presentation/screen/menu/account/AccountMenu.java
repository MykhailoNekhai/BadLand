package ua.uni.presentation.screen.menu.account;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import ua.uni.core.config.GameSettings;
import ua.uni.bootstrap.GameServices;
import ua.uni.platform.account.LocalAccountStore;
import ua.uni.presentation.screen.menu.core.PMenu;
import ua.uni.presentation.screen.menu.factory.FontQuality;
import ua.uni.presentation.screen.menu.account.service.AccountMenuService;
import ua.uni.presentation.screen.menu.account.service.AvatarService;
import ua.uni.presentation.screen.menu.account.service.PlayerAppearanceService;
import ua.uni.core.logging.AppLogger;
import ua.uni.presentation.screen.menu.settings.LanguageButton;
import ua.uni.presentation.screen.menu.ui.MenuFx;

public class AccountMenu extends PMenu {
    private static final int AVATAR_TEXTURE_SIZE = 256;
    private static final int DEFAULT_SIDE_PANEL_WIDTH = 390;
    private static final int DEFAULT_SIDE_PANEL_HEIGHT = 400;
    private static final int CUSTOMIZE_SIDE_PANEL_WIDTH = 500;
    private static final int CUSTOMIZE_SIDE_PANEL_HEIGHT = 440;
    private static final float EYE_PREVIEW_SCALE = 0.68f;
    private static final float EYE_PREVIEW_TINT = 0.86f;
    private static final float EYE_PREVIEW_OFFSET_X = 0.15f;
    private static final float EYE_PREVIEW_OFFSET_Y = -0.18f;
    private static final float EYE_PREVIEW_HALO_RADIUS = 0.38f;
    private static final float EYE_PREVIEW_HALO_Y = 0.54f;
    private static final int CUSTOMIZE_ARROW_WIDTH = 50;
    private static final int CUSTOMIZE_ARROW_HEIGHT = 56;
    private static final int SKIN_SELECTOR_WIDTH = 400;
    private static final int SKIN_SELECTOR_HEIGHT = 124;
    private static final int COLOR_SELECTOR_WIDTH = 400;
    private static final int COLOR_SELECTOR_HEIGHT = 112;

    private Texture bg;
    private Texture panel;
    private Texture panelVignette;
    private Texture backBtn;
    private Texture itemBtn;
    private Texture sliderTrack;
    private Texture sliderKnob;
    private Texture dotTex;
    private Texture sidePanelBg;
    private Texture profileIcon;
    private Texture avatarFrame;
    private Texture avatarTexture;
    private Texture skinSelectorBg;
    private Texture colorSelectorBg;
    private Texture arrowBtn;
    private Texture avatarSkinTexture;
    private final Map<String, Texture> eyePreviewTextures = new HashMap<>();

    private Actor activeLeftPanel;
    private Actor activeRightPanel;

    private static final int TRAIL_LEN = 10;
    private final float[] trailX = new float[TRAIL_LEN];
    private final float[] trailY = new float[TRAIL_LEN];
    private int trailHead;
    private final Vector2 mouseTmp = new Vector2();

    private BitmapFont itemFont;
    private BitmapFont backFont;
    private BitmapFont smallFont;
    private BitmapFont titleFont;

    private float elapsed;

    private Label titleLabel;
    private Label soundsLabel;
    private Label nicknameLabel;
    private Label idLabel;
    private Slider volumeSlider;
    private TextButton readyButton;
    private ProfileSnapshot profileSnapshot;

    private int languageIndex;

    private TextButton achievementsButton;
    private TextButton customizeButton;
    private TextButton changeAvatarButton;
    private TextButton resetAchievementsButton;
    private TextButton changeNicknameButton;
    private TextButton logoutButton;
    private Image avatarImage;
    private final LocalAccountStore accountStore = new LocalAccountStore();
    private final AccountMenuService accountMenuService;
    private final AvatarService avatarService;
    private final PlayerAppearanceService appearanceService;
    private volatile boolean avatarPickerBusy;
    private volatile boolean remoteProfileRefreshRunning;
    private volatile boolean screenActive;

    public AccountMenu(GameServices services) {
        super(services);
        this.accountMenuService = new AccountMenuService(services, accountStore);
        this.avatarService = new AvatarService(services, accountStore);
        this.appearanceService = new PlayerAppearanceService(accountStore);
    }

    @Override
    public void show() {
        screenActive = true;
        beginMenuShow();

        languageIndex = Math.max(0, indexOf(LanguageButton.LANGUAGES, GameSettings.getLanguage()));

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        panel = textures().gradientPanel(620, 980, 48, 18, 12,
                new Color(0.06f, 0.08f, 0.13f, 0.95f),
                new Color(0.14f, 0.08f, 0.04f, 0.95f));
        panelVignette = textures().panelVignetteTexture(620, 980, 48, 18, 12);
        backBtn = textures().roundedRect(180, 72, 24, new Color(0f, 0f, 0f, 0.92f));
        itemBtn = textures().roundedRect(560, 80, 24, new Color(0f, 0f, 0f, 1f));
        sliderTrack = textures().horizontalGradientTrack(320, 14, 7,
                new Color(0.55f, 0.32f, 0.10f, 0.96f),
                new Color(1f, 0.93f, 0.56f, 0.96f));
        sliderKnob = textures().circleWithHaloTexture(56, 16,
                new Color(1f, 0.93f, 0.62f, 1f),
                new Color(1f, 0.85f, 0.40f, 0.55f));
        dotTex = textures().softDotTexture(14);
        sidePanelBg = textures().gradientPanel(380, 640, 38, 14, 12,
                new Color(0.08f, 0.10f, 0.15f, 0.98f),
                new Color(0.16f, 0.10f, 0.05f, 0.98f));
        avatarFrame = textures().roundedRect(160, 160, 32, new Color(0f, 0f, 0f, 1f));
        skinSelectorBg = textures().roundedRect(SKIN_SELECTOR_WIDTH, SKIN_SELECTOR_HEIGHT, 26, new Color(0f, 0f, 0f, 0.84f));
        colorSelectorBg = textures().roundedRect(COLOR_SELECTOR_WIDTH, COLOR_SELECTOR_HEIGHT, 24, new Color(0f, 0f, 0f, 0.84f));
        arrowBtn = textures().roundedRect(CUSTOMIZE_ARROW_WIDTH, CUSTOMIZE_ARROW_HEIGHT, 18, new Color(0f, 0f, 0f, 0.96f));
        profileIcon = new Texture(Gdx.files.internal("game-resourses/menu/user-profile.png"));
        profileIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        avatarTexture = loadAvatarTexture();
        profileIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        loadEyePreviewTextures();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter items = new FreeTypeFontGenerator.FreeTypeFontParameter();
        items.size = 56;
        items.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        items.borderWidth = 1.6f;
        items.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        items.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(items);
        itemFont = generator.generateFont(items);
        FontQuality.fixScale(itemFont);

        FreeTypeFontGenerator.FreeTypeFontParameter back = new FreeTypeFontGenerator.FreeTypeFontParameter();
        back.size = 46;
        back.color = Color.WHITE;
        back.borderWidth = 1.2f;
        back.borderColor = Color.BLACK;
        back.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(back);
        backFont = generator.generateFont(back);
        FontQuality.fixScale(backFont);

        FreeTypeFontGenerator.FreeTypeFontParameter small = new FreeTypeFontGenerator.FreeTypeFontParameter();
        small.size = 34;
        small.color = Color.WHITE;
        small.borderWidth = 1.0f;
        small.borderColor = Color.BLACK;
        small.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(small);
        smallFont = generator.generateFont(small);
        FontQuality.fixScale(smallFont);

        FreeTypeFontGenerator.FreeTypeFontParameter title = new FreeTypeFontGenerator.FreeTypeFontParameter();
        title.size = 68;
        title.color = new Color(1f, 0.86f, 0.36f, 1f);
        title.borderWidth = 1.8f;
        title.borderColor = new Color(0.10f, 0.05f, 0.02f, 1f);
        title.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(title);
        titleFont = generator.generateFont(title);
        FontQuality.fixScale(titleFont);

        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        Table panelContent = new Table();
        panelContent.defaults().center().padLeft(36).padRight(36);

        Label.LabelStyle ls = new Label.LabelStyle(itemFont, itemFont.getColor());
        Label.LabelStyle titleLs = new Label.LabelStyle(titleFont, titleFont.getColor());
        Label.LabelStyle profileNameStyle = new Label.LabelStyle(itemFont, new Color(0.98f, 0.95f, 0.88f, 1f));
        Label.LabelStyle profileIdStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));

        TextButton.TextButtonStyle itemStyle = new TextButton.TextButtonStyle();
        itemStyle.up = new TextureRegionDrawable(itemBtn);
        itemStyle.down = new TextureRegionDrawable(itemBtn);
        itemStyle.over = new TextureRegionDrawable(itemBtn);
        itemStyle.font = itemFont;
        itemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        itemStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        titleLabel = new Label(LanguageButton.t("ACCOUNT"), titleLs);
        achievementsButton = new TextButton(LanguageButton.t("SECURITY"), itemStyle);
        customizeButton = new TextButton(LanguageButton.t("CUSTOMIZE"), itemStyle);
        changeAvatarButton = new TextButton(LanguageButton.t("CHANGE_AVATAR"), itemStyle);
        resetAchievementsButton = new TextButton(LanguageButton.t("RESET_ACHIEVEMENTS"), itemStyle);
        changeNicknameButton = new TextButton(LanguageButton.t("CHANGE_NICKNAME"), itemStyle);

        TextButton.TextButtonStyle readyStyle = new TextButton.TextButtonStyle();
        readyStyle.font = itemFont;
        readyStyle.fontColor = new Color(0.95f, 0.90f, 0.65f, 1f);
        readyStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        readyStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        readyButton = new TextButton(LanguageButton.t("BACK"), readyStyle);
        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.75f);
                MenuFx.runAfterGoldButtonPress(actor, () -> navigator().goToMainMenu());
            }
        });

        profileSnapshot = loadProfileSnapshot();
        soundsLabel = new Label("", ls);
        nicknameLabel = new Label(profileSnapshot.nickname(), profileNameStyle);
        idLabel = new Label(LanguageButton.t("ID") + ": " + profileSnapshot.id(), profileIdStyle);

        Table avatarBlock = new Table();
        avatarBlock.center().padTop(4f);
        avatarImage = new Image(new TextureRegionDrawable(currentAvatarTexture()));
        avatarImage.setScaling(Scaling.stretch);
        Stack avatarStack = new Stack();
        avatarStack.add(new Image(new TextureRegionDrawable(avatarFrame)));
        avatarStack.add(avatarImage);
        avatarBlock.add(avatarStack).width(160).height(160).padBottom(10f).row();
        avatarBlock.add(nicknameLabel).center();

        panelContent.add(titleLabel).padTop(26).padBottom(18).row();
        panelContent.add(avatarBlock).width(560).padBottom(20).row();
        panelContent.add(changeNicknameButton).width(560).height(80).padBottom(10).row();
        panelContent.add(changeAvatarButton).width(560).height(80).padBottom(10).row();
        panelContent.add(resetAchievementsButton).width(560).height(80).padBottom(10).row();
        panelContent.add(achievementsButton).width(560).height(80).padBottom(10).row();
        panelContent.add(customizeButton).width(560).height(80).padBottom(14).row();
        panelContent.add(readyButton).padBottom(14).row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(panel)));
        panelStack.add(new Image(new TextureRegionDrawable(panelVignette)));
        panelStack.add(panelContent);

        Table panelWrap = new Table();
        panelWrap.setFillParent(true);
        panelWrap.center();
        panelWrap.add(panelStack).width(620).height(980);

        stage.addActor(panelWrap);

        Actor[] revealList = {titleLabel, avatarBlock, changeNicknameButton, changeAvatarButton, resetAchievementsButton, achievementsButton,
                customizeButton, readyButton};
        for (int i = 0; i < revealList.length; i++) {
            Actor actor = revealList[i];
            actor.getColor().a = 0f;
            actor.addAction(Actions.sequence(
                    Actions.delay(0.08f * i),
                    Actions.fadeIn(0.35f, Interpolation.fade)
            ));
        }

        panelStack.addAction(Actions.sequence(
                Actions.delay(0.05f),
                Actions.run(() -> panelStack.setOrigin(panelStack.getWidth() / 2f, panelStack.getHeight() / 2f)),
                Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.006f, 1.006f, 2.2f, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 2.2f, Interpolation.sine)
                ))
        ));

        changeAvatarButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                openNativeAvatarPicker();
            }
        });
        resetAchievementsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                resetAchievements();
            }
        });
        changeNicknameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                if (activeLeftPanel != null) closeSidePanel(true);
                else openSidePanel(true, LanguageButton.t("CHANGE_NICKNAME"), buildNicknameContent());
            }
        });
        achievementsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                if (activeRightPanel != null) closeSidePanel(false);
                else openSidePanel(false, LanguageButton.t("SECURITY"), buildSecurityPanel());
            }
        });
        customizeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                if (activeLeftPanel != null) closeSidePanel(true);
                else openSidePanel(true, LanguageButton.t("CUSTOMIZE"), buildAppearancePanel(),
                        CUSTOMIZE_SIDE_PANEL_WIDTH, CUSTOMIZE_SIDE_PANEL_HEIGHT);
            }
        });

        refreshLabels();
        startRemoteProfileRefresh();
    }

    private Table buildSecurityPanel() {
        return new AccountSecurityPanel(
                smallFont, itemBtn, accountMenuService,
                () -> { if (activeLeftPanel != null) closeSidePanel(true);
                        else openSidePanel(true, LanguageButton.t("CHANGE_EMAIL"), buildChangeEmailContent()); },
                this::sendResetPassword,
                () -> { audio().playSelect(0.80f); accountMenuService.logout(); navigator().goToLogin(); }
        ).build();
    }

    private Table buildAppearancePanel() {
        return new AccountAppearancePanel(
                smallFont, skinSelectorBg, colorSelectorBg, arrowBtn, dotTex,
                skinPreviewTexture(), eyePreviewTextures, appearanceService,
                () -> audio().playSelect(0.60f)
        ).build();
    }









    private Table buildNicknameContent() {
        Label.LabelStyle statusStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.92f, 0.55f, 1f));

        TextField.TextFieldStyle nicknameFieldStyle = new TextField.TextFieldStyle();
        nicknameFieldStyle.font = smallFont;
        nicknameFieldStyle.fontColor = Color.WHITE;
        nicknameFieldStyle.messageFont = smallFont;
        nicknameFieldStyle.messageFontColor = new Color(0.65f, 0.65f, 0.67f, 1f);
        nicknameFieldStyle.cursor = new TextureRegionDrawable(textures().solidTexture(3, 34, Color.WHITE));
        nicknameFieldStyle.background = new TextureRegionDrawable(textures().roundedRect(320, 70, 18, new Color(0.07f, 0.07f, 0.08f, 0.96f)));

        TextButton.TextButtonStyle applyStyle = new TextButton.TextButtonStyle();
        applyStyle.up = new TextureRegionDrawable(itemBtn);
        applyStyle.down = new TextureRegionDrawable(itemBtn);
        applyStyle.over = new TextureRegionDrawable(itemBtn);
        applyStyle.font = smallFont;
        applyStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        applyStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        applyStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        TextField nicknameField = new TextField(profileSnapshot.nickname(), nicknameFieldStyle);
        nicknameField.setMessageText("Nickname");
        Label statusLabel = new Label("", statusStyle);
        statusLabel.setWrap(true);
        TextButton applyButton = new TextButton("APPLY", applyStyle);
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                String rawNickname = nicknameField.getText();
                applyButton.setDisabled(true);
                statusLabel.setText("Saving...");
                Thread t = new Thread(() -> {
                    AccountMenuService.UpdateNicknameResult result;
                    try {
                        result = accountMenuService.updateNickname(rawNickname);
                    } catch (Exception e) {
                        AppLogger.error("Account", "Nickname update failed", e);
                        Gdx.app.postRunnable(() -> {
                            applyButton.setDisabled(false);
                            statusLabel.setText("Save failed.");
                        });
                        return;
                    }
                    final AccountMenuService.UpdateNicknameResult r = result;
                    Gdx.app.postRunnable(() -> {
                        applyButton.setDisabled(false);
                        statusLabel.setText(r.message());
                        if (r.success()) {
                            nicknameField.setText(r.nickname());
                            refreshLabels();
                        }
                    });
                }, "account-nickname-save");
                t.setDaemon(true);
                t.start();
            }
        });

        Table table = new Table();
        table.center().padTop(2);
        table.add(nicknameField).width(320).height(54).padBottom(8).row();
        table.add(applyButton).width(320).height(58).padBottom(8).row();
        table.add(statusLabel).width(320).minHeight(42).row();
        return table;
    }

    private Table buildChangeEmailContent() {
        Label.LabelStyle statusStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.92f, 0.55f, 1f));

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
        fieldStyle.font = smallFont;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.messageFont = smallFont;
        fieldStyle.messageFontColor = new Color(0.65f, 0.65f, 0.67f, 1f);
        fieldStyle.cursor = new TextureRegionDrawable(textures().solidTexture(3, 34, Color.WHITE));
        fieldStyle.background = new TextureRegionDrawable(textures().roundedRect(320, 70, 18, new Color(0.07f, 0.07f, 0.08f, 0.96f)));

        TextButton.TextButtonStyle applyStyle = new TextButton.TextButtonStyle();
        applyStyle.up = new TextureRegionDrawable(itemBtn);
        applyStyle.down = new TextureRegionDrawable(itemBtn);
        applyStyle.over = new TextureRegionDrawable(itemBtn);
        applyStyle.font = smallFont;
        applyStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        applyStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        applyStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        TextField emailField = new TextField("", fieldStyle);
        emailField.setMessageText("New email");
        Label statusLabel = new Label("", statusStyle);
        statusLabel.setWrap(true);
        TextButton applyButton = new TextButton("APPLY", applyStyle);

        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio().playSelect(0.72f);
                String newEmail = emailField.getText().trim();
                if (newEmail.isBlank() || !newEmail.contains("@") || !newEmail.contains(".")) {
                    statusLabel.setText("Invalid email.");
                    return;
                }
                if (!profileSnapshot.hasSession()) {
                    statusLabel.setText("Not logged in.");
                    return;
                }
                applyButton.setDisabled(true);
                statusLabel.setText("Updating...");
                Thread t = new Thread(() -> {
                    AccountMenuService.UpdateEmailResult result;
                    try {
                        result = accountMenuService.updateEmail(newEmail);
                    } catch (Exception e) {
                        AppLogger.error("Account", "Email update failed", e);
                        Gdx.app.postRunnable(() -> {
                            applyButton.setDisabled(false);
                            statusLabel.setText("Failed: check email or re-login.");
                        });
                        return;
                    }
                    final AccountMenuService.UpdateEmailResult r = result;
                    Gdx.app.postRunnable(() -> {
                        applyButton.setDisabled(false);
                        statusLabel.setText(r.message());
                        if (r.success()) {
                            refreshLabels();
                        }
                    });
                }, "account-email-save");
                t.setDaemon(true);
                t.start();
            }
        });

        Table table = new Table();
        table.center().padTop(2);
        table.add(emailField).width(320).height(54).padBottom(8).row();
        table.add(applyButton).width(320).height(58).padBottom(8).row();
        table.add(statusLabel).width(320).minHeight(42).row();
        return table;
    }

    private void openSidePanel(boolean fromLeft, String titleText, Table contentTable) {
        openSidePanel(fromLeft, titleText, contentTable, DEFAULT_SIDE_PANEL_WIDTH, DEFAULT_SIDE_PANEL_HEIGHT);
    }

    private void openSidePanel(boolean fromLeft, String titleText, Table contentTable, int panelW, int panelH) {
        if (fromLeft && activeLeftPanel != null) return;
        if (!fromLeft && activeRightPanel != null) return;

        Actor panelStack = sidePanels().open(stage, fromLeft, titleText, contentTable,
                sidePanelBg, itemFont, panelW, panelH, 40f, 16f, 16f, 12f);
        if (fromLeft) activeLeftPanel = panelStack;
        else activeRightPanel = panelStack;
    }

    private void closeSidePanel(boolean fromLeft) {
        Actor panelActor = fromLeft ? activeLeftPanel : activeRightPanel;
        if (panelActor == null) return;
        sidePanels().close(stage, panelActor, fromLeft);
        if (fromLeft) activeLeftPanel = null;
        else activeRightPanel = null;
    }

    private void refreshLabels() {
        profileSnapshot = loadProfileSnapshot();
        titleLabel.setText(LanguageButton.t("ACCOUNT"));
        nicknameLabel.setText(profileSnapshot.nickname());
        idLabel.setText(LanguageButton.t("ID") + ": " + profileSnapshot.id());
        changeAvatarButton.setText(LanguageButton.t("CHANGE_AVATAR"));
        resetAchievementsButton.setText(LanguageButton.t("RESET_ACHIEVEMENTS"));
        changeNicknameButton.setText(LanguageButton.t("CHANGE_NICKNAME"));
        achievementsButton.setText(LanguageButton.t("SECURITY"));
        customizeButton.setText(LanguageButton.t("CUSTOMIZE"));
        readyButton.setText(LanguageButton.t("BACK"));
    }

    private void startRemoteProfileRefresh() {
        if (remoteProfileRefreshRunning || profileSnapshot == null || !profileSnapshot.hasSession()) {
            return;
        }
        remoteProfileRefreshRunning = true;
        Thread thread = new Thread(() -> {
            RemoteProfileSnapshot remoteSnapshot;
            try {
                remoteSnapshot = fetchRemoteProfileSnapshot();
            } finally {
                remoteProfileRefreshRunning = false;
            }
            if (remoteSnapshot.hasUpdates()) {
                Gdx.app.postRunnable(() -> applyRemoteProfileSnapshot(remoteSnapshot));
            }
        }, "account-profile-refresh");
        thread.setDaemon(true);
        thread.start();
    }

    private RemoteProfileSnapshot fetchRemoteProfileSnapshot() {
        AccountMenuService.RemoteProfileData remoteData = accountMenuService.fetchRemoteProfileData();
        String accountCreated = remoteData.accountCreatedAt().isBlank()
                ? ""
                : formatTimestamp(remoteData.accountCreatedAt());
        String lastLogin = remoteData.lastLoginAt().isBlank()
                ? ""
                : formatTimestamp(remoteData.lastLoginAt());
        return new RemoteProfileSnapshot(remoteData.nickname(), remoteData.email(), accountCreated, lastLogin);
    }

    private void applyRemoteProfileSnapshot(RemoteProfileSnapshot remoteSnapshot) {
        if (!screenActive || profileSnapshot == null || nicknameLabel == null || idLabel == null) {
            return;
        }
        String nickname = remoteSnapshot.nickname().isBlank() ? profileSnapshot.nickname() : remoteSnapshot.nickname();
        String email = remoteSnapshot.email().isBlank() ? profileSnapshot.email() : remoteSnapshot.email();
        String accountCreated = remoteSnapshot.accountCreated().isBlank()
                ? profileSnapshot.accountCreated()
                : remoteSnapshot.accountCreated();
        String lastLogin = remoteSnapshot.lastLogin().isBlank()
                ? profileSnapshot.lastLogin()
                : remoteSnapshot.lastLogin();
        if (!remoteSnapshot.nickname().isBlank()) {
            accountStore.saveNickname(remoteSnapshot.nickname());
        }
        profileSnapshot = new ProfileSnapshot(
                nickname,
                email,
                profileSnapshot.id(),
                accountCreated,
                lastLogin,
                profileSnapshot.deaths(),
                profileSnapshot.playSeconds(),
                profileSnapshot.completedLevels(),
                profileSnapshot.totalLevels(),
                profileSnapshot.unlockedAchievements(),
                profileSnapshot.totalAchievements(),
                profileSnapshot.hasSession()
        );
        nicknameLabel.setText(profileSnapshot.nickname());
        idLabel.setText(LanguageButton.t("ID") + ": " + profileSnapshot.id());
    }

    private void resetAchievements() {
        accountMenuService.resetAchievements();
        refreshLabels();
    }

    private static int indexOf(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) return i;
        }
        return -1;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        audio().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (activeLeftPanel != null || activeRightPanel != null) {
                if (activeRightPanel != null) closeSidePanel(false);
                if (activeLeftPanel != null) closeSidePanel(true);
            } else {
                navigator().goToMainMenu();
                return;
            }
        }

        updateTrail();

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        float drift = (float) Math.sin(elapsed * 0.12f) * 10f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -18f - drift, 0f, w + 36f, h);
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 0.85f, 0.35f, 0.08f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        stage.act(delta);
        stage.draw();

        batch.begin();
        for (int i = 0; i < TRAIL_LEN; i++) {
            int idx = (trailHead - 1 - i + TRAIL_LEN) % TRAIL_LEN;
            float ta = (TRAIL_LEN - i) / (float) TRAIL_LEN * 0.45f;
            float ts = 8f - i * 0.4f;
            batch.setColor(1f, 0.85f, 0.40f, ta);
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
        screenActive = false;
        endMenuHide();
    }

    @Override
    public void dispose() {
        screenActive = false;
        stage.dispose();
        bg.dispose();
        panel.dispose();
        panelVignette.dispose();
        backBtn.dispose();
        itemBtn.dispose();
        sliderTrack.dispose();
        sliderKnob.dispose();
        dotTex.dispose();
        sidePanelBg.dispose();
        avatarFrame.dispose();
        skinSelectorBg.dispose();
        colorSelectorBg.dispose();
        arrowBtn.dispose();
        if (avatarSkinTexture != null && avatarSkinTexture != profileIcon) {
            avatarSkinTexture.dispose();
        }
        disposeEyePreviewTextures();
        profileIcon.dispose();
        if (avatarTexture != null && avatarTexture != profileIcon) {
            avatarTexture.dispose();
        }
        itemFont.dispose();
        backFont.dispose();
        smallFont.dispose();
        titleFont.dispose();
    }

    private void applyAvatarPath(String path) {
        if (!screenActive) return;
        if (path == null || path.isBlank()) {
            avatarService.resetAvatar();
            if (avatarTexture != null && avatarTexture != profileIcon) {
                avatarTexture.dispose();
            }
            avatarTexture = profileIcon;
            if (avatarImage != null) {
                avatarImage.setDrawable(new TextureRegionDrawable(profileIcon));
            }
            return;
        }
        try {
            Texture nextTexture = loadCroppedAvatarTexture(Gdx.files.absolute(path));
            if (avatarTexture != null && avatarTexture != profileIcon) {
                avatarTexture.dispose();
            }
            avatarTexture = nextTexture;
            avatarService.saveAvatarPath(path);
            if (avatarImage != null) {
                avatarImage.setDrawable(new TextureRegionDrawable(avatarTexture));
                avatarImage.invalidateHierarchy();
            }
            AppLogger.info("Account", "Avatar selected from local file: " + path);
            avatarService.uploadAvatarToCloudAsync(path);
        } catch (Exception e) {
            AppLogger.error("Account", "Avatar selection failed", e);
        }
    }

    private void openNativeAvatarPicker() {
        if (avatarPickerBusy) {
            AppLogger.info("Account", "Avatar picker is already running");
            return;
        }
        if (!isMacOs()) {
            AppLogger.info("Account", "Native avatar picker is currently supported only on macOS");
            return;
        }
        avatarPickerBusy = true;
        Thread pickerThread = new Thread(() -> {
            try {
                Process process = new ProcessBuilder(
                        "/usr/bin/osascript",
                        "-e",
                        "POSIX path of (choose file with prompt \"Choose avatar image\" of type {\"public.image\"})")
                        .start();
                int exitCode = process.waitFor();
                String selectedPath = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
                String errorOutput = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();
                if (exitCode != 0) {
                    if (!errorOutput.isBlank()) {
                        AppLogger.info("Account", "Avatar picker closed: " + errorOutput);
                    } else {
                        AppLogger.info("Account", "Avatar picker closed without selection");
                    }
                    return;
                }
                if (selectedPath != null && !selectedPath.isBlank()) {
                    final String avatarPath = selectedPath;
                    Gdx.app.postRunnable(() -> applyAvatarPath(avatarPath));
                    return;
                }
                AppLogger.info("Account", "Avatar picker closed without selection");
            } catch (Exception e) {
                AppLogger.error("Account", "Native avatar picker failed", e);
            } finally {
                avatarPickerBusy = false;
            }
        }, "account-avatar-picker");
        pickerThread.setDaemon(true);
        pickerThread.start();
    }

    private boolean isMacOs() {
        String osName = System.getProperty("os.name", "");
        return osName.toLowerCase().contains("mac");
    }

    private Texture loadAvatarTexture() {
        String path = avatarService.loadAvatarPath();
        if (path.isBlank()) {
            return profileIcon;
        }
        try {
            FileHandle handle = Gdx.files.absolute(path);
            if (!handle.exists()) {
                return profileIcon;
            }
            return loadCroppedAvatarTexture(handle);
        } catch (Exception e) {
            AppLogger.error("Account", "Avatar load failed", e);
            return profileIcon;
        }
    }

    private Texture loadCroppedAvatarTexture(FileHandle handle) {
        Pixmap source = new Pixmap(handle);
        try {
            int cropSize = Math.min(source.getWidth(), source.getHeight());
            int srcX = Math.max(0, (source.getWidth() - cropSize) / 2);
            int srcY = Math.max(0, (source.getHeight() - cropSize) / 2);

            Pixmap cropped = new Pixmap(AVATAR_TEXTURE_SIZE, AVATAR_TEXTURE_SIZE, Pixmap.Format.RGBA8888);
            try {
                cropped.drawPixmap(source,
                        srcX, srcY, cropSize, cropSize,
                        0, 0, AVATAR_TEXTURE_SIZE, AVATAR_TEXTURE_SIZE);
                Texture texture = new Texture(cropped);
                texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
                return texture;
            } finally {
                cropped.dispose();
            }
        } finally {
            source.dispose();
        }
    }

    private void loadEyePreviewTextures() {
        disposeEyePreviewTextures();
        String[] styles = {"shadow", "spider", "round", "cluster", "swirl"};
        String[] colors = {"purple", "gray", "green", "cyan", "yellow"};
        for (String style : styles) {
            for (String color : colors) {
                eyePreviewTextures.put(style + ":" + color, createEyeModelPreview(style, color));
            }
        }
    }

    private Texture createEyeModelPreview(String style, String color) {
        Pixmap body = new Pixmap(Gdx.files.internal("game-resourses/textures/avatar-1.png"));
        Pixmap eyes = new Pixmap(Gdx.files.internal(
                "game-resourses/textures/avatar-eyes/" + style + "/animation/" + style + "_" + color + "_open.png"));
        Pixmap result = new Pixmap(body.getWidth(), body.getHeight(), Pixmap.Format.RGBA8888);
        try {
            darkenEyePixmap(eyes, EYE_PREVIEW_TINT);
            result.setBlending(Pixmap.Blending.None);
            result.setColor(0f, 0f, 0f, 0f);
            result.fill();
            result.setBlending(Pixmap.Blending.SourceOver);
            drawEyePreviewHalo(result);
            result.drawPixmap(body, 0, 0);

            int eyeW = Math.round(body.getWidth() * EYE_PREVIEW_SCALE);
            int eyeH = Math.round(eyeW * ((float) eyes.getHeight() / eyes.getWidth()));
            float eyeCenterX = (body.getWidth() / 2f) + (EYE_PREVIEW_OFFSET_X * body.getWidth());
            float eyeCenterYFromBottom = (body.getHeight() / 2f) + (EYE_PREVIEW_OFFSET_Y * body.getHeight());
            int eyeX = Math.round(eyeCenterX - (eyeW / 2f));
            int eyeY = Math.round(body.getHeight() - (eyeCenterYFromBottom + (eyeH / 2f)));

            result.drawPixmap(eyes, 0, 0, eyes.getWidth(), eyes.getHeight(), eyeX, eyeY, eyeW, eyeH);
            Texture texture = new Texture(result);
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            return texture;
        } finally {
            result.dispose();
            eyes.dispose();
            body.dispose();
        }
    }

    private void darkenEyePixmap(Pixmap pixmap, float tint) {
        pixmap.setBlending(Pixmap.Blending.None);
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                int rgba = pixmap.getPixel(x, y);
                int alpha = rgba & 0xff;
                if (alpha == 0) {
                    continue;
                }
                int red = (rgba >>> 24) & 0xff;
                int green = (rgba >>> 16) & 0xff;
                int blue = (rgba >>> 8) & 0xff;
                pixmap.setColor((red * tint) / 255f, (green * tint) / 255f, (blue * tint) / 255f, alpha / 255f);
                pixmap.drawPixel(x, y);
            }
        }
    }

    private void drawEyePreviewHalo(Pixmap pixmap) {
        int centerX = Math.round(pixmap.getWidth() * 0.5f);
        int centerY = Math.round(pixmap.getHeight() * EYE_PREVIEW_HALO_Y);
        int radius = Math.round(Math.min(pixmap.getWidth(), pixmap.getHeight()) * EYE_PREVIEW_HALO_RADIUS);

        pixmap.setColor(0.95f, 0.78f, 0.38f, 0.18f);
        pixmap.fillCircle(centerX, centerY, radius);
        pixmap.setColor(1f, 0.86f, 0.48f, 0.26f);
        pixmap.fillCircle(centerX, centerY, Math.round(radius * 0.78f));
        pixmap.setColor(1f, 0.94f, 0.66f, 0.32f);
        pixmap.fillCircle(centerX, centerY, Math.round(radius * 0.58f));
    }

    private void disposeEyePreviewTextures() {
        for (Texture texture : eyePreviewTextures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        eyePreviewTextures.clear();
    }

    private Texture currentAvatarTexture() {
        return avatarTexture != null ? avatarTexture : profileIcon;
    }

    private Texture skinPreviewTexture() {
        if (avatarSkinTexture != null) {
            return avatarSkinTexture;
        }
        try {
            avatarSkinTexture = new Texture(Gdx.files.internal("game-resourses/textures/avatar-1.png"));
            avatarSkinTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            return avatarSkinTexture;
        } catch (Exception e) {
            AppLogger.error("Account", "Skin preview texture load failed", e);
            avatarSkinTexture = profileIcon;
            return profileIcon;
        }
    }

    private void sendResetPassword() {
        if (!profileSnapshot.hasSession() || profileSnapshot.email().isBlank()) {
            AppLogger.info("Account", "Reset password skipped: no active account email");
            return;
        }
        audio().playSelect(0.72f);
        String email = profileSnapshot.email();
        Thread t = new Thread(() -> {
            try {
                accountMenuService.sendResetPassword(email);
                AppLogger.info("Account", "Password reset email sent to " + email);
            } catch (Exception e) {
                AppLogger.error("Account", "Password reset failed", e);
            }
        }, "account-reset-password");
        t.setDaemon(true);
        t.start();
    }

    private void resendVerificationEmail() {
        if (!profileSnapshot.hasSession()) {
            AppLogger.info("Account", "Resend verification skipped: no active session");
            return;
        }
        audio().playSelect(0.72f);
        Thread t = new Thread(() -> {
            try {
                accountMenuService.resendVerificationEmail();
                AppLogger.info("Account", "Verification email sent again");
            } catch (Exception e) {
                AppLogger.error("Account", "Resend verification failed", e);
            }
        }, "account-resend-verification");
        t.setDaemon(true);
        t.start();
    }

    private ProfileSnapshot loadProfileSnapshot() {
        int deaths = services.achievements().getTotalDeaths();
        int playSeconds = services.achievements().getTotalPlaySeconds();
        int completedLevels = services.achievements().getCompletedLevelsCount();
        int totalLevels = services.achievements().getTotalLevels();
        int unlockedAchievements = services.achievements().getUnlockedCount();
        int totalAchievements = services.achievements().getTotalCount();
        boolean hasSession = services.session().hasSession();
        String email = services.session().getEmail().trim();
        String nickname = accountStore.loadNickname();
        if (nickname.isBlank()) {
            nickname = nicknameFromEmail(email);
        }

        if (nickname.isBlank()) {
            nickname = LanguageButton.t("LOCAL_PILOT");
        }
        if (email.isBlank()) {
            email = LanguageButton.t("LOCAL_PROFILE");
        }
        String id = hasSession ? services.session().getUid() : LanguageButton.t("LOCAL");
        String accountCreated = hasSession ? LanguageButton.t("UNKNOWN") : LanguageButton.t("LOCAL");
        String lastLogin = hasSession ? LanguageButton.t("UNKNOWN") : LanguageButton.t("LOCAL");
        return new ProfileSnapshot(nickname, email, id, accountCreated, lastLogin, deaths, playSeconds, completedLevels, totalLevels,
                unlockedAchievements, totalAchievements, hasSession);
    }

    private String nicknameFromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    private String formatPlayTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) {
            return LanguageButton.tf("HOURS_MINUTES_FMT", hours, minutes);
        }
        if (minutes > 0) {
            return LanguageButton.tf("MINUTES_FMT", minutes);
        }
        return LanguageButton.tf("SECONDS_FMT", totalSeconds);
    }

    private String formatTimestamp(String millisString) {
        if (millisString == null || millisString.isBlank()) {
            return LanguageButton.t("UNKNOWN");
        }
        try {
            long millis = Long.parseLong(millisString);
            java.time.Instant instant = java.time.Instant.ofEpochMilli(millis);
            java.time.ZonedDateTime dt = instant.atZone(java.time.ZoneId.systemDefault());
            return dt.toLocalDate() + " " + dt.toLocalTime().withSecond(0).withNano(0);
        } catch (Exception e) {
            return LanguageButton.t("UNKNOWN");
        }
    }


}
