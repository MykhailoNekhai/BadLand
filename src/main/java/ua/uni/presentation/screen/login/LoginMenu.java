package ua.uni.presentation.screen.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ua.uni.bootstrap.GameServices;
import ua.uni.core.logging.AppLogger;
import ua.uni.platform.auth.FirebaseAuthService;
import ua.uni.presentation.screen.menu.core.PMenu;
import ua.uni.presentation.screen.menu.factory.FontQuality;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

public class LoginMenu extends PMenu {
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

    public LoginMenu(GameServices services) {
        super(services);
    }

    @Override
    public void show() {
        elapsed = 0f;
        beginLoginShow();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/login_bg_generated.png"));
        btnTex   = textures().roundedRect(460, 90, 30, new Color(0f, 0f, 0f, 0.92f));
        fieldTex = textures().roundedRect(560, 80, 22, new Color(0f, 0f, 0f, 1f));
        dotTex   = textures().softDotTexture(14);

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter tp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tp.size = 94;
        tp.color = new Color(0.16f, 0.09f, 0.02f, 1f);
        tp.borderWidth = 0.8f;
        tp.borderColor = new Color(1f, 0.82f, 0.38f, 0.92f);
        tp.shadowOffsetX = 0;
        tp.shadowOffsetY = 0;
        tp.shadowColor = new Color(0f, 0f, 0f, 0f);
        tp.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(tp);
        titleFont = gen.generateFont(tp);
        FontQuality.fixScale(titleFont);

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = 36;
        bp.color = new Color(0.95f, 0.96f, 0.98f, 1f);
        bp.borderWidth = 1.6f;
        bp.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        bp.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(bp);
        btnFont = gen.generateFont(bp);
        FontQuality.fixScale(btnFont);

        FreeTypeFontGenerator.FreeTypeFontParameter fp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fp.size = 38;
        fp.color = new Color(0.95f, 0.93f, 0.88f, 1f);
        fp.borderWidth = 1.0f;
        fp.borderColor = new Color(0.04f, 0.03f, 0.02f, 1f);
        fp.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(fp);
        fieldFont = gen.generateFont(fp);
        FontQuality.fixScale(fieldFont);

        gen.dispose();
        buildUi();
    }

    private void buildUi() {
        TextFieldStyle fieldStyle = new TextFieldStyle();
        fieldStyle.font = fieldFont;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.messageFontColor = new Color(0.92f, 0.78f, 0.42f, 0.92f);
        fieldStyle.messageFont = fieldFont;
        fieldStyle.cursor = new TextureRegionDrawable(textures().roundedRect(3, 44, 1, Color.WHITE));
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
        resetBtn.getLabel().setFontScale(0.86f);
        resendBtn.getLabel().setFontScale(0.72f);

        fx().setupButtonFx(loginBtn, stage, dotTex);
        fx().setupButtonFx(registerBtn, stage, dotTex);
        fx().setupButtonFx(resetBtn, stage, dotTex);
        fx().setupButtonFx(resendBtn, stage, dotTex);

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
        root.add().height(270f).row();
        root.add(emailField).width(560).height(80).padBottom(12).row();
        root.add(passwordField).width(560).height(80).padBottom(12).row();
        root.add(nicknameField).width(560).height(80).padBottom(22).row();

        Table buttonGrid = new Table();
        buttonGrid.add(loginBtn).width(300).height(78).padRight(12).padBottom(10);
        buttonGrid.add(registerBtn).width(300).height(78).padBottom(10).row();
        buttonGrid.add(resetBtn).width(300).height(78).padRight(12);
        buttonGrid.add(resendBtn).width(300).height(78);

        root.add(buttonGrid).padBottom(18).row();
        root.add(statusLabel).padTop(4);

        stage.addActor(root);
        animateFormIn(root);
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

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LanguageButton.t("FILL_EMAIL_PASSWORD"));
            return;
        }
        try {
            AppLogger.info("Auth", "Login attempt");
            FirebaseAuthService.AuthResult result = services.auth().signIn(email, password);
            if (!services.auth().isEmailVerified(result.idToken())) {
                statusLabel.setText(LanguageButton.t("VERIFY_EMAIL_FIRST"));
                AppLogger.info("Auth", "Login blocked: email not verified");
                return;
            }
            services.session().save(result);
            services.sync().bootstrapFromCloud();
            services.sync().syncProfileHeartbeat();
            AppLogger.info("Auth", "Login success. uid=" + result.uid());
            navigator().goToMainMenu();
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("LOGIN_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Login failed", e);
        }
    }

    private void doRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String nickname = nicknameField.getText().trim();
        if (email.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            statusLabel.setText(LanguageButton.t("FILL_REGISTER_FIELDS"));
            return;
        }
        try {
            AppLogger.info("Auth", "Register attempt");
            FirebaseAuthService.AuthResult result = services.auth().signUp(email, password);
            services.firestore().createUserProfile(result.idToken(), result.uid(), nickname, email, "uk");
            services.auth().sendEmailVerification(result.idToken());
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
            services.auth().sendPasswordResetEmail(email);
            statusLabel.setText(LanguageButton.t("PASSWORD_RESET_SENT"));
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("RESET_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Password reset failed", e);
        }
    }

    private void doResendVerification() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LanguageButton.t("FILL_EMAIL_PASSWORD_FIRST"));
            return;
        }
        try {
            AppLogger.info("Auth", "Resend verification request");
            FirebaseAuthService.AuthResult result = services.auth().signIn(email, password);
            services.auth().sendEmailVerification(result.idToken());
            statusLabel.setText(LanguageButton.t("VERIFICATION_SENT_AGAIN"));
        } catch (Exception e) {
            statusLabel.setText(LanguageButton.tf("RESEND_FAILED_FMT", e.getMessage()));
            AppLogger.error("Auth", "Resend verification failed", e);
        }
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
        batch.setColor(0f, 0f, 0f, 0.24f);
        batch.draw(bg, 0, 0, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        drawTitle(batch, w, h - 310f);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawTitle(com.badlogic.gdx.graphics.g2d.Batch batch, float vw, float topY) {
        float fadeIn = Math.min(elapsed / 0.55f, 1f);
        float alpha = fadeIn < 1f ? fadeIn : 1f;

        titleLayout.setText(titleFont, "SHADOW FLIGHT");
        float x = Math.round((vw - titleLayout.width) * 0.5f);
        float y = Math.round(topY);

        Color c = titleFont.getColor();
        c.set(0.16f, 0.09f, 0.02f, alpha);
        titleFont.draw(batch, titleLayout, x, y);
        c.set(0.16f, 0.09f, 0.02f, 1f);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void hide() {
        endLoginHide();
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
