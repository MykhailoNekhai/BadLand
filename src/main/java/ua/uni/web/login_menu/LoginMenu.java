package ua.uni.web.login_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.game.MainGame;
import ua.uni.auth.FirebaseAuthService;
import ua.uni.logging.AppLogger;
import ua.uni.web.main_menu.Menu;
import ua.uni.web.main_menu.settings_menu.LanguageButton;

public class LoginMenu implements Screen {
    private final MainGame game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont uiFont;
    private Texture bg;
    private Texture btnTex;
    private Texture fieldTex;
    private final GlyphLayout titleLayout = new GlyphLayout();
    private TextField emailField;
    private TextField passwordField;
    private TextField nicknameField;
    private Label statusLabel;
    private String nicknameOrEmailPrefix;

    public LoginMenu(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        bg = new Texture(Gdx.files.internal("game-resourses/menu/mainmenu_bg_generated.png"));
        btnTex = roundedRect(420, 86, 28, new Color(0f, 0f, 0f, 0.95f));
        fieldTex = roundedRect(560, 78, 20, new Color(0.07f, 0.07f, 0.08f, 0.95f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 114;
        titleParams.color = Color.BLACK;
        titleParams.borderWidth = 0f;
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(titleParams);
        FreeTypeFontGenerator.FreeTypeFontParameter up = new FreeTypeFontGenerator.FreeTypeFontParameter();
        up.size = 44;
        up.color = Color.WHITE;
        up.characters = LanguageButton.FONT_CHARACTERS;
        uiFont = generator.generateFont(up);
        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        TextFieldStyle fieldStyle = new TextFieldStyle();
        fieldStyle.font = uiFont;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.cursor = new TextureRegionDrawable(roundedRect(3, 44, 1, Color.WHITE));
        fieldStyle.background = new TextureRegionDrawable(fieldTex);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(btnTex);
        btnStyle.down = new TextureRegionDrawable(btnTex);
        btnStyle.font = uiFont;

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiFont, Color.WHITE);

        emailField = new TextField("", fieldStyle);
        emailField.setMessageText(LanguageButton.t("EMAIL"));
        passwordField = new TextField("", fieldStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText(LanguageButton.t("PASSWORD"));
        nicknameField = new TextField("", fieldStyle);
        nicknameField.setMessageText(LanguageButton.t("NICKNAME_REGISTER"));

        TextButton loginBtn = new TextButton(LanguageButton.t("LOGIN"), btnStyle);
        TextButton registerBtn = new TextButton(LanguageButton.t("REGISTER"), btnStyle);
        TextButton resetBtn = new TextButton(LanguageButton.t("RESET_PASSWORD"), btnStyle);
        TextButton resendBtn = new TextButton(LanguageButton.t("RESEND_VERIFICATION"), btnStyle);
        statusLabel = new Label("", labelStyle);

        loginBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                doLogin();
            }
        });
        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                doRegister();
            }
        });
        resetBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                doPasswordReset();
            }
        });
        resendBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                doResendVerification();
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add().height(180f).padBottom(24).row();
        root.add(emailField).width(560).height(78).padBottom(12).row();
        root.add(passwordField).width(560).height(78).padBottom(12).row();
        root.add(nicknameField).width(560).height(78).padBottom(20).row();
        root.add(loginBtn).width(420).height(86).padBottom(10).row();
        root.add(registerBtn).width(420).height(86).padBottom(10).row();
        root.add(resetBtn).width(420).height(86).padBottom(10).row();
        root.add(resendBtn).width(420).height(86).padBottom(16).row();
        root.add(statusLabel).padTop(6);
        stage.addActor(root);
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
        String email = emailField.getText().trim();
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
        String email = emailField.getText().trim();
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
        pixmap.fillRectangle(r, 0, w - (r * 2), h);
        pixmap.fillRectangle(0, r, w, h - (r * 2));
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
        var batch = stage.getBatch();
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0, 0, w, h);
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(bg, 0, 0, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        drawTitle(batch, w, h - 72f, 1f);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    private void drawTitle(com.badlogic.gdx.graphics.g2d.Batch batch, float viewportWidth, float topY, float alpha) {
        titleLayout.setText(titleFont, "SHADOW FLIGHT");
        float titleX = (viewportWidth - titleLayout.width) * 0.5f;
        Color color = titleFont.getColor();
        color.set(0f, 0f, 0f, alpha);
        titleFont.draw(batch, titleLayout, titleX, topY);
        color.set(0f, 0f, 0f, 1f);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        titleFont.dispose();
        uiFont.dispose();
        bg.dispose();
        btnTex.dispose();
        fieldTex.dispose();
    }
}
