package ua.uni.web.main_menu.account_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.JsonObject;
import ua.uni.auth.FirebaseAuthService;
import ua.uni.audio.services.AudioManager;
import ua.uni.config.GameSettings;
import ua.uni.game.MainGame;
import ua.uni.logging.AppLogger;
import ua.uni.web.login_menu.LoginMenu;
import ua.uni.web.main_menu.Menu;
import ua.uni.web.main_menu.settings_menu.LanguageButton;

public class AccountMenu implements Screen {
    private final MainGame game;
    private Stage stage;

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

    private TextButton statisticsButton;
    private TextButton achievementsButton;
    private TextButton customizeButton;
    private TextButton changeAvatarButton;
    private TextButton logoutButton;

    public AccountMenu(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        languageIndex = Math.max(0, indexOf(LanguageButton.LANGUAGES, GameSettings.getLanguage()));
        AudioManager.get().enterMenuContext();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        panel = gradientPanel(620, 980, 48, 18, 12,
                new Color(0.06f, 0.08f, 0.13f, 0.95f),
                new Color(0.14f, 0.08f, 0.04f, 0.95f));
        panelVignette = panelVignetteTexture(620, 980, 48, 18, 12);
        backBtn = roundedRect(180, 72, 24, new Color(0f, 0f, 0f, 0.92f));
        itemBtn = roundedRect(560, 80, 24, new Color(0f, 0f, 0f, 1f));
        sliderTrack = horizontalGradientTrack(320, 14, 7,
                new Color(0.55f, 0.32f, 0.10f, 0.96f),
                new Color(1f, 0.93f, 0.56f, 0.96f));
        sliderKnob = circleWithHaloTexture(56, 16,
                new Color(1f, 0.93f, 0.62f, 1f),
                new Color(1f, 0.85f, 0.40f, 0.55f));
        dotTex = softDotTexture(14);
        sidePanelBg = gradientPanel(380, 640, 38, 14, 12,
                new Color(0.08f, 0.10f, 0.15f, 0.98f),
                new Color(0.16f, 0.10f, 0.05f, 0.98f));
        profileIcon = new Texture(Gdx.files.internal("game-resourses/menu/user-profile.png"));
        profileIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter items = new FreeTypeFontGenerator.FreeTypeFontParameter();
        items.size = 56;
        items.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        items.borderWidth = 1.6f;
        items.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        items.characters = LanguageButton.FONT_CHARACTERS;
        itemFont = generator.generateFont(items);

        FreeTypeFontGenerator.FreeTypeFontParameter back = new FreeTypeFontGenerator.FreeTypeFontParameter();
        back.size = 46;
        back.color = Color.WHITE;
        back.borderWidth = 1.2f;
        back.borderColor = Color.BLACK;
        back.characters = LanguageButton.FONT_CHARACTERS;
        backFont = generator.generateFont(back);

        FreeTypeFontGenerator.FreeTypeFontParameter small = new FreeTypeFontGenerator.FreeTypeFontParameter();
        small.size = 34;
        small.color = Color.WHITE;
        small.borderWidth = 1.0f;
        small.borderColor = Color.BLACK;
        small.characters = LanguageButton.FONT_CHARACTERS;
        smallFont = generator.generateFont(small);

        FreeTypeFontGenerator.FreeTypeFontParameter title = new FreeTypeFontGenerator.FreeTypeFontParameter();
        title.size = 68;
        title.color = new Color(1f, 0.86f, 0.36f, 1f);
        title.borderWidth = 1.8f;
        title.borderColor = new Color(0.10f, 0.05f, 0.02f, 1f);
        title.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(title);

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

        titleLabel = new Label("ACCOUNT", titleLs);
        achievementsButton = new TextButton("SECURITY", itemStyle);
        customizeButton = new TextButton("CUSTOMIZE", itemStyle);
        changeAvatarButton = new TextButton("CHANGE AVATAR", itemStyle);

        TextButton.TextButtonStyle readyStyle = new TextButton.TextButtonStyle();
        readyStyle.font = itemFont;
        readyStyle.fontColor = new Color(0.95f, 0.90f, 0.65f, 1f);
        readyStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        readyStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        readyButton = new TextButton("BACK", readyStyle);
        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.75f);
                game.setScreen(new Menu(game));
            }
        });

        profileSnapshot = loadProfileSnapshot();
        soundsLabel = new Label("", ls);
        nicknameLabel = new Label(profileSnapshot.nickname(), profileNameStyle);
        idLabel = new Label("ID: " + profileSnapshot.id(), profileIdStyle);

        Table avatarBlock = new Table();
        avatarBlock.center().padTop(4f);
        avatarBlock.add(new Image(new TextureRegionDrawable(itemBtn))).width(160).height(160).padBottom(10f).row();
        avatarBlock.add(nicknameLabel).center();

        panelContent.add(titleLabel).padTop(26).padBottom(18).row();
        panelContent.add(avatarBlock).width(560).padBottom(20).row();
        panelContent.add(changeAvatarButton).width(560).height(80).padBottom(10).row();
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
        panelWrap.add(panelStack).width(620).height(820);

        stage.addActor(panelWrap);

        Actor[] revealList = {titleLabel, avatarBlock, changeAvatarButton, achievementsButton,
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
                AudioManager.get().playSelect(0.72f);
                if (activeLeftPanel != null) closeSidePanel(true);
                else openSidePanel(true, "AVATAR", buildAvatarContent());
            }
        });
        achievementsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                if (activeRightPanel != null) closeSidePanel(false);
                else openSidePanel(false, "SECURITY", buildSecurityContent());
            }
        });
        customizeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                if (activeLeftPanel != null) closeSidePanel(true);
                else openSidePanel(true, "CUSTOMIZE", buildCustomizeContent());
            }
        });

        refreshLabels();
    }

    private Table buildStatisticsContent() {
        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle valueStyle = new Label.LabelStyle(itemFont, new Color(0.98f, 0.95f, 0.88f, 1f));

        Table table = new Table();
        table.center().padTop(20);
        table.add(new Label("NICKNAME", sectionStyle)).padBottom(6).row();
        table.add(new Label(profileSnapshot.nickname(), valueStyle)).padBottom(18).row();
        table.add(new Label("ID", sectionStyle)).padBottom(6).row();
        table.add(new Label(profileSnapshot.id(), valueStyle)).padBottom(18).row();
        table.add(new Label("EMAIL", sectionStyle)).padBottom(6).row();
        table.add(new Label(profileSnapshot.email(), valueStyle)).padBottom(18).row();
        table.add(new Label("DEATHS", sectionStyle)).padBottom(6).row();
        table.add(new Label(String.valueOf(profileSnapshot.deaths()), valueStyle)).padBottom(18).row();
        table.add(new Label("PLAY TIME", sectionStyle)).padBottom(6).row();
        table.add(new Label(formatPlayTime(profileSnapshot.playSeconds()), valueStyle)).padBottom(18).row();
        table.add(new Label("LEVELS", sectionStyle)).padBottom(6).row();
        table.add(new Label(profileSnapshot.completedLevels() + " / " + profileSnapshot.totalLevels(), valueStyle)).padBottom(18).row();
        table.add(new Label("ACHIEVEMENTS", sectionStyle)).padBottom(6).row();
        table.add(new Label(profileSnapshot.unlockedAchievements() + " / " + profileSnapshot.totalAchievements(), valueStyle)).padBottom(18).row();
        table.add(new Label("SCORE", sectionStyle)).padBottom(6).row();
        table.add(new Label("SOON", valueStyle)).row();
        return table;
    }

    private Table buildSecurityContent() {
        TextButton.TextButtonStyle itemStyle = new TextButton.TextButtonStyle();
        itemStyle.up = new TextureRegionDrawable(itemBtn);
        itemStyle.down = new TextureRegionDrawable(itemBtn);
        itemStyle.over = new TextureRegionDrawable(itemBtn);
        itemStyle.font = smallFont;
        itemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        itemStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        Label.LabelStyle hintStyle = new Label.LabelStyle(smallFont, new Color(0.98f, 0.95f, 0.88f, 1f));

        TextButton changeEmail = new TextButton("CHANGE EMAIL", itemStyle);
        TextButton resetPassword = new TextButton("RESET PASSWORD", itemStyle);
        TextButton logout = new TextButton("LOG OUT", itemStyle);
        Label hint = new Label(profileSnapshot.hasSession()
                ? "Firebase actions will use your current account email."
                : "Login first to enable Firebase actions.", hintStyle);
        hint.setWrap(true);

        changeEmail.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                AppLogger.info("Account", "Change email flow placeholder");
            }
        });
        resetPassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendResetPassword();
            }
        });
        logout.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.80f);
                game.getSessionManager().clear();
                game.setScreen(new LoginMenu(game));
            }
        });

        Table table = new Table();
        table.top().padTop(18);
        table.add(changeEmail).width(320).height(78).padBottom(10).row();
        table.add(resetPassword).width(320).height(78).padBottom(10).row();
        table.add(logout).width(320).height(78).padBottom(16).row();
        table.add(hint).width(320);
        return table;
    }

    private Table buildCustomizeContent() {
        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle valueStyle = new Label.LabelStyle(itemFont, new Color(0.98f, 0.95f, 0.88f, 1f));

        Table table = new Table();
        table.center().padTop(20);
        table.add(new Label("SKINS", sectionStyle)).padBottom(6).row();
        table.add(new Label("SOON", valueStyle)).padBottom(18).row();
        table.add(new Label("TRAILS", sectionStyle)).padBottom(6).row();
        table.add(new Label("SOON", valueStyle)).padBottom(18).row();
        table.add(new Label("COSMETICS", sectionStyle)).padBottom(6).row();
        table.add(new Label("SOON", valueStyle)).row();
        return table;
    }

    private Table buildAvatarContent() {
        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle valueStyle = new Label.LabelStyle(itemFont, new Color(0.98f, 0.95f, 0.88f, 1f));

        Table table = new Table();
        table.center().padTop(20);
        table.add(new Label("AVATAR CHANGE", sectionStyle)).padBottom(6).row();
        table.add(new Label("SOON", valueStyle)).padBottom(18).row();
        table.add(new Label("UPLOAD TARGET", sectionStyle)).padBottom(6).row();
        table.add(new Label("FIREBASE STORAGE", valueStyle)).row();
        return table;
    }

    private void openSidePanel(boolean fromLeft, String titleText, Table contentTable) {
        if (fromLeft && activeLeftPanel != null) return;
        if (!fromLeft && activeRightPanel != null) return;

        float screenW = stage.getViewport().getWorldWidth();
        float screenH = stage.getViewport().getWorldHeight();
        int panelW = 380;
        int panelH = 640;
        float y = (screenH - panelH) / 2f;
        float startX = fromLeft ? -panelW - 20f : screenW + 20f;
        float endX = fromLeft ? 40f : screenW - panelW - 40f;

        Label.LabelStyle titleStyle = new Label.LabelStyle(itemFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label titleLabel = new Label(titleText, titleStyle);

        Table inner = new Table();
        inner.top().padTop(28).padLeft(20).padRight(20).padBottom(20);
        inner.add(titleLabel).center().padBottom(4).row();
        inner.add(contentTable).expand().fill().row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(sidePanelBg)));
        panelStack.add(inner);

        panelStack.setSize(panelW, panelH);
        panelStack.setPosition(startX, y);
        panelStack.addAction(Actions.moveTo(endX, y, 0.30f, Interpolation.sineOut));
        AudioManager.get().playPanelInOut(0.70f);

        stage.addActor(panelStack);
        if (fromLeft) activeLeftPanel = panelStack;
        else activeRightPanel = panelStack;
    }

    private void closeSidePanel(boolean fromLeft) {
        Actor panelActor = fromLeft ? activeLeftPanel : activeRightPanel;
        if (panelActor == null) return;
        float screenW = stage.getViewport().getWorldWidth();
        int panelW = 380;
        float exitX = fromLeft ? -panelW - 20f : screenW + 20f;
        panelActor.addAction(Actions.sequence(
                Actions.moveTo(exitX, panelActor.getY(), 0.22f, Interpolation.sineIn),
                Actions.removeActor()
        ));
        AudioManager.get().playPanelInOut(0.60f);
        if (fromLeft) activeLeftPanel = null;
        else activeRightPanel = null;
    }

    private void refreshLabels() {
        profileSnapshot = loadProfileSnapshot();
        titleLabel.setText("ACCOUNT");
        nicknameLabel.setText(profileSnapshot.nickname());
        changeAvatarButton.setText("CHANGE AVATAR");
        achievementsButton.setText("SECURITY");
        customizeButton.setText("CUSTOMIZE");
        readyButton.setText("BACK");
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
        AudioManager.get().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (activeLeftPanel != null || activeRightPanel != null) {
                if (activeRightPanel != null) closeSidePanel(false);
                if (activeLeftPanel != null) closeSidePanel(true);
            } else {
                game.setScreen(new Menu(game));
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
        Texture t = new Texture(pixmap);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return t;
    }


    private Texture circleWithHaloTexture(int diameter, int innerRadius, Color core, Color halo) {
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
                if (d <= innerRadius) {
                    p.setColor(core);
                    p.drawPixel(x, y);
                } else if (d <= maxR) {
                    float t = (d - innerRadius) / (maxR - innerRadius);
                    float a = 1f - t;
                    a = a * a;
                    p.setColor(halo.r, halo.g, halo.b, halo.a * a);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(p);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return tex;
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
                float a = 1f - t;
                a = a * a;
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

    private Texture gradientPanel(int w, int h, int radius, int padX, int padY, Color topColor, Color bottomColor) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int innerH = h - 2 * padY;
        for (int y = padY; y < h - padY; y++) {
            int x0;
            int x1;
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
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture panelVignetteTexture(int w, int h, int radius, int padX, int padY) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int cx = w / 2;
        int cy = h / 2;
        float max = (float) Math.sqrt((double) (cx * cx) + (double) (cy * cy));
        for (int y = padY; y < h - padY; y++) {
            int x0;
            int x1;
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
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture horizontalGradientTrack(int w, int h, int radius, Color leftColor, Color rightColor) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        for (int x = 0; x < w; x++) {
            int y0;
            int y1;
            if (x < radius) {
                int dx = radius - x;
                int dy = (int) Math.sqrt((double) (radius * radius) - (double) (dx * dx));
                y0 = (h / 2) - dy;
                y1 = (h / 2) + dy;
            } else if (x >= w - radius) {
                int dx = x - (w - radius - 1);
                int dy = (int) Math.sqrt((double) (radius * radius) - (double) (dx * dx));
                y0 = (h / 2) - dy;
                y1 = (h / 2) + dy;
            } else {
                y0 = 0;
                y1 = h - 1;
            }
            float t = x / (float) (w - 1);
            float r = leftColor.r + (rightColor.r - leftColor.r) * t;
            float g = leftColor.g + (rightColor.g - leftColor.g) * t;
            float b = leftColor.b + (rightColor.b - leftColor.b) * t;
            float a = leftColor.a + (rightColor.a - leftColor.a) * t;
            p.setColor(r, g, b, a);
            p.drawLine(x, y0, x, y1);
        }
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
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
        itemFont.dispose();
        backFont.dispose();
        smallFont.dispose();
        titleFont.dispose();
    }

    private void sendResetPassword() {
        if (!profileSnapshot.hasSession() || profileSnapshot.email().isBlank()) {
            AppLogger.info("Account", "Reset password skipped: no active account email");
            return;
        }
        try {
            AudioManager.get().playSelect(0.72f);
            game.getAuthService().sendPasswordResetEmail(profileSnapshot.email());
            AppLogger.info("Account", "Password reset email sent to " + profileSnapshot.email());
        } catch (Exception e) {
            AppLogger.error("Account", "Password reset failed", e);
        }
    }

    private void resendVerificationEmail() {
        if (!profileSnapshot.hasSession()) {
            AppLogger.info("Account", "Resend verification skipped: no active session");
            return;
        }
        try {
            AudioManager.get().playSelect(0.72f);
            game.getAuthService().sendEmailVerification(game.getSessionManager().getIdToken());
            AppLogger.info("Account", "Verification email sent again");
        } catch (Exception e) {
            AppLogger.error("Account", "Resend verification failed", e);
        }
    }

    private ProfileSnapshot loadProfileSnapshot() {
        int deaths = game.getAchievementManager().getTotalDeaths();
        int playSeconds = game.getAchievementManager().getTotalPlaySeconds();
        int completedLevels = game.getAchievementManager().getCompletedLevelsCount();
        int totalLevels = game.getAchievementManager().getTotalLevels();
        int unlockedAchievements = game.getAchievementManager().getUnlockedCount();
        int totalAchievements = game.getAchievementManager().getTotalCount();
        boolean hasSession = game.getSessionManager().hasSession();
        String email = game.getSessionManager().getEmail().trim();
        String nickname = nicknameFromEmail(email);

        if (hasSession) {
            try {
                JsonObject profile = game.getFirestoreService().getUserProfile(
                        game.getSessionManager().getIdToken(),
                        game.getSessionManager().getUid()
                );
                String firestoreNickname = stringField(profile, "nickname");
                String firestoreEmail = stringField(profile, "email");
                if (!firestoreNickname.isBlank()) {
                    nickname = firestoreNickname;
                }
                if (!firestoreEmail.isBlank()) {
                    email = firestoreEmail;
                }
            } catch (Exception e) {
                AppLogger.error("Account", "Profile fetch failed", e);
            }
        }

        if (nickname.isBlank()) {
            nickname = "LOCAL PILOT";
        }
        if (email.isBlank()) {
            email = "LOCAL PROFILE";
        }
        String id = hasSession ? game.getSessionManager().getUid() : "LOCAL";
        String accountCreated = "LOCAL";
        String lastLogin = "LOCAL";
        if (hasSession) {
            try {
                FirebaseAuthService.AccountMetadata metadata = game.getAuthService()
                        .getAccountMetadata(game.getSessionManager().getIdToken());
                accountCreated = formatTimestamp(metadata.createdAt());
                lastLogin = formatTimestamp(metadata.lastLoginAt());
            } catch (Exception e) {
                AppLogger.error("Account", "Account metadata fetch failed", e);
            }
        }
        return new ProfileSnapshot(nickname, email, id, accountCreated, lastLogin, deaths, playSeconds, completedLevels, totalLevels,
                unlockedAchievements, totalAchievements, hasSession);
    }

    private String stringField(JsonObject profile, String key) {
        if (profile == null || !profile.has("fields")) {
            return "";
        }
        JsonObject fields = profile.getAsJsonObject("fields");
        if (fields == null || !fields.has(key)) {
            return "";
        }
        JsonObject value = fields.getAsJsonObject(key);
        if (value == null || !value.has("stringValue")) {
            return "";
        }
        return value.get("stringValue").getAsString();
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
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m";
        }
        return totalSeconds + "s";
    }

    private String formatTimestamp(String millisString) {
        if (millisString == null || millisString.isBlank()) {
            return "UNKNOWN";
        }
        try {
            long millis = Long.parseLong(millisString);
            java.time.Instant instant = java.time.Instant.ofEpochMilli(millis);
            java.time.ZonedDateTime dt = instant.atZone(java.time.ZoneId.systemDefault());
            return dt.toLocalDate() + " " + dt.toLocalTime().withSecond(0).withNano(0);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private record ProfileSnapshot(
            String nickname,
            String email,
            String id,
            String accountCreated,
            String lastLogin,
            int deaths,
            int playSeconds,
            int completedLevels,
            int totalLevels,
            int unlockedAchievements,
            int totalAchievements,
            boolean hasSession
    ) {}
}
