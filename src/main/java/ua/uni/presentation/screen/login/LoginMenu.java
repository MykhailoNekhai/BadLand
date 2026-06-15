package ua.uni.presentation.screen.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.bootstrap.MainGame;
import ua.uni.platform.auth.FirebaseAuthService;
import ua.uni.core.logging.AppLogger;
import ua.uni.presentation.screen.menu.main.Menu;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

public class LoginMenu implements Screen {
    private final MainGame game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont btnFont;
    private BitmapFont fieldFont;
    private Texture bg;
    private Texture btnTex;
    private Texture fieldTex;
    private Texture dotTex;
    private final GlyphLayout titleLayout = new GlyphLayout();
    private TextField emailField;
    private TextField passwordField;
    private TextField nicknameField;
    private Label statusLabel;

    private float elapsed = 0f;

    public LoginMenu(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        elapsed = 0f;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().enterMenuContext();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/mainmenu_bg_generated.png"));
        btnTex   = roundedRect(460, 90, 30, new Color(0f, 0f, 0f, 0.92f));
        fieldTex = roundedRect(560, 80, 22, new Color(0.07f, 0.07f, 0.09f, 0.96f));
        dotTex   = softCircleTexture(14);

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter tp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tp.size = 108;
        tp.color = new Color(1f, 0.86f, 0.36f, 1f);
        tp.borderWidth = 2.2f;
        tp.borderColor = new Color(0.08f, 0.04f, 0.01f, 1f);
        tp.shadowOffsetX = 3;
        tp.shadowOffsetY = -3;
        tp.shadowColor = new Color(0f, 0f, 0f, 0.45f);
        tp.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = gen.generateFont(tp);

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = 46;
        bp.color = new Color(0.95f, 0.96f, 0.98f, 1f);
        bp.borderWidth = 1.6f;
        bp.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        bp.characters = LanguageButton.FONT_CHARACTERS;
        btnFont = gen.generateFont(bp);

        FreeTypeFontGenerator.FreeTypeFontParameter fp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fp.size = 38;
        fp.color = new Color(0.95f, 0.93f, 0.88f, 1f);
        fp.borderWidth = 1.0f;
        fp.borderColor = new Color(0.04f, 0.03f, 0.02f, 1f);
        fp.characters = LanguageButton.FONT_CHARACTERS;
        fieldFont = gen.generateFont(fp);

        gen.dispose();
        buildUi();
    }

    private void buildUi() {
        TextFieldStyle fieldStyle = new TextFieldStyle();
        fieldStyle.font = fieldFont;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.messageFontColor = new Color(0.55f, 0.54f, 0.52f, 1f);
        fieldStyle.messageFont = fieldFont;
        fieldStyle.cursor = new TextureRegionDrawable(roundedRect(3, 44, 1, Color.WHITE));
        fieldStyle.background = new TextureRegionDrawable(fieldTex);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up   = new TextureRegionDrawable(btnTex);
        btnStyle.down = new TextureRegionDrawable(btnTex);
        btnStyle.over = new TextureRegionDrawable(btnTex);
        btnStyle.font = btnFont;
        btnStyle.fontColor     = new Color(0.95f, 0.96f, 0.98f, 1f);
        btnStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        btnStyle.downFontColor = new Color(1f, 0.82f, 0.32f, 1f);

        Label.LabelStyle statusStyle = new Label.LabelStyle(fieldFont, new Color(1f, 0.90f, 0.50f, 1f));

        emailField    = new TextField("", fieldStyle);
        emailField.setMessageText(LanguageButton.t("EMAIL"));
        passwordField = new TextField("", fieldStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText(LanguageButton.t("PASSWORD"));
        nicknameField = new TextField("", fieldStyle);
        nicknameField.setMessageText(LanguageButton.t("NICKNAME_REGISTER"));

        TextButton loginBtn    = new TextButton(LanguageButton.t("LOGIN"), btnStyle);
        TextButton registerBtn = new TextButton(LanguageButton.t("REGISTER"), btnStyle);
        TextButton resetBtn    = new TextButton(LanguageButton.t("RESET_PASSWORD"), btnStyle);
        TextButton resendBtn   = new TextButton(LanguageButton.t("RESEND_VERIFICATION"), btnStyle);
        statusLabel = new Label("", statusStyle);

        setupButtonFx(loginBtn);
        setupButtonFx(registerBtn);
        setupButtonFx(resetBtn);
        setupButtonFx(resendBtn);

        loginBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { doLogin(); }
        });
        registerBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { doRegister(); }
        });
        resetBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { doPasswordReset(); }
        });
        resendBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { doResendVerification(); }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add().height(170f).row();
        root.add(emailField).width(560).height(80).padBottom(12).row();
        root.add(passwordField).width(560).height(80).padBottom(12).row();
        root.add(nicknameField).width(560).height(80).padBottom(22).row();
        root.add(loginBtn).width(460).height(90).padBottom(10).row();
        root.add(registerBtn).width(460).height(90).padBottom(10).row();
        root.add(resetBtn).width(460).height(90).padBottom(10).row();
        root.add(resendBtn).width(460).height(90).padBottom(18).row();
        root.add(statusLabel).padTop(4);

        stage.addActor(root);
        animateFormIn(root);
    }

    private void setupButtonFx(TextButton btn) {
        btn.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                AudioManager.get().playHover();
                btn.addAction(Actions.scaleTo(1.02f, 1.02f, 0.14f, Interpolation.smooth));
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                btn.addAction(Actions.scaleTo(1f, 1f, 0.14f, Interpolation.smooth));
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                spawnRipple(btn, x, y);
                btn.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.06f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.12f, Interpolation.swingOut)
                ));
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    private void spawnRipple(TextButton button, float localX, float localY) {
        com.badlogic.gdx.math.Vector2 worldPos = new com.badlogic.gdx.math.Vector2(localX, localY);
        button.localToStageCoordinates(worldPos);
        Image ripple = new Image(new TextureRegionDrawable(dotTex));
        ripple.setSize(20f, 20f);
        ripple.setOrigin(10f, 10f);
        ripple.setPosition(worldPos.x - 10f, worldPos.y - 10f);
        ripple.setColor(1f, 0.85f, 0.40f, 0.55f);
        ripple.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(14f, 14f, 0.55f, Interpolation.sineOut),
                        Actions.fadeOut(0.55f, Interpolation.fade)
                ),
                Actions.removeActor()
        ));
        stage.addActor(ripple);
    }

    private void animateFormIn(Table root) {
        float delay = 0.08f;
        for (Actor child : root.getChildren()) {
            child.getColor().a = 0f;
            child.setScale(0.96f);
            child.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.parallel(
                            Actions.fadeIn(0.36f, Interpolation.fade),
                            Actions.scaleTo(1f, 1f, 0.36f, Interpolation.sineOut)
                    )
            ));
            delay += 0.07f;
        }
    }

    private Texture softCircleTexture(int diameter) {
        Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        float cx = diameter / 2f, cy = diameter / 2f, maxR = diameter / 2f;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - cx, dy = y - cy;
                float t = Math.min(1f, (float) Math.sqrt(dx * dx + dy * dy) / maxR);
                float alpha = (1f - t) * (1f - t);
                if (alpha > 0f) { pixmap.setColor(1f, 1f, 1f, alpha); pixmap.drawPixel(x, y); }
            }
        }
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    private void doLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LanguageButton.t("FILL_EMAIL_PASSWORD"));
            return;
        }
        try {
            AppLogger.info("Auth", "Login attempt");
            FirebaseAuthService.AuthResult result = game.getAuthService().signIn(email, password);
            if (!game.getAuthService().isEmailVerified(result.idToken())) {
                statusLabel.setText(LanguageButton.t("VERIFY_EMAIL_FIRST"));
                AppLogger.info("Auth", "Login blocked: email not verified");
                return;
            }
            game.getSessionManager().save(result);
            game.getPlayerDataSyncService().bootstrapFromCloud();
            game.getPlayerDataSyncService().syncProfileHeartbeat();
            AppLogger.info("Auth", "Login success. uid=" + result.uid());
            game.setScreen(new Menu(game));
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("LOGIN_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Login failed", e);
        }
    }

    private void doRegister() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String nickname = nicknameField.getText().trim();
        if (email.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            statusLabel.setText(LanguageButton.t("FILL_REGISTER_FIELDS"));
            return;
        }
        try {
            AppLogger.info("Auth", "Register attempt");
            FirebaseAuthService.AuthResult result = game.getAuthService().signUp(email, password);
            game.getFirestoreService().createUserProfile(result.idToken(), result.uid(), nickname, email, "uk");
            game.getAuthService().sendEmailVerification(result.idToken());
            statusLabel.setText(LanguageButton.t("REGISTERED_VERIFY_LOGIN"));
            AppLogger.info("Auth", "Register success. uid=" + result.uid());
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("REGISTER_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Register failed", e);
        }
    }

    private void doPasswordReset() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText(LanguageButton.t("ENTER_EMAIL_FIRST"));
            return;
        }
        try {
            AppLogger.info("Auth", "Password reset request");
            game.getAuthService().sendPasswordResetEmail(email);
            statusLabel.setText(LanguageButton.t("PASSWORD_RESET_SENT"));
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("RESET_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Password reset failed", e);
        }
    }

    private void doResendVerification() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LanguageButton.t("FILL_EMAIL_PASSWORD_FIRST"));
            return;
        }
        try {
            AppLogger.info("Auth", "Resend verification request");
            FirebaseAuthService.AuthResult result = game.getAuthService().signIn(email, password);
            game.getAuthService().sendEmailVerification(result.idToken());
            statusLabel.setText(LanguageButton.t("VERIFICATION_SENT_AGAIN"));
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("RESEND_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Resend verification failed", e);
        }
    }

    private Texture roundedRect(int w, int h, int r, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillRectangle(r, 0, w - r * 2, h);
        pixmap.fillRectangle(0, r, w, h - r * 2);
        pixmap.fillCircle(r, r, r);
        pixmap.fillCircle(w - r - 1, r, r);
        pixmap.fillCircle(r, h - r - 1, r);
        pixmap.fillCircle(w - r - 1, h - r - 1, r);
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;

        var batch = stage.getBatch();
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0, 0, w, h);
        batch.setColor(0f, 0f, 0f, 0.42f);
        batch.draw(bg, 0, 0, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        drawTitle(batch, w, h - 60f);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawTitle(com.badlogic.gdx.graphics.g2d.Batch batch, float vw, float topY) {
        float fadeIn = Math.min(elapsed / 0.55f, 1f);
        float pulse  = fadeIn < 1f ? fadeIn : (0.93f + (float) Math.sin(elapsed * 1.6f) * 0.07f);

        titleLayout.setText(titleFont, "SHADOW FLIGHT");
        float x = (vw - titleLayout.width) * 0.5f;

        Color c = titleFont.getColor();
        c.set(1f, 0.86f, 0.36f, pulse);
        titleFont.draw(batch, titleLayout, x, topY);
        c.set(1f, 0.86f, 0.36f, 1f);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void hide() {
        AudioManager.get().leaveMenuContext();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        titleFont.dispose();
        btnFont.dispose();
        fieldFont.dispose();
        bg.dispose();
        btnTex.dispose();
        fieldTex.dispose();
        dotTex.dispose();
    }
}
