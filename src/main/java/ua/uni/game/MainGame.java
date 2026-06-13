package ua.uni.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import ua.uni.achivments.AchievementManager;
import ua.uni.achivments.Achievements;
import ua.uni.achivments.AchievementsRarity;
import ua.uni.auth.FirebaseAuthService;
import ua.uni.auth.FirebaseConfig;
import ua.uni.auth.FirestoreService;
import ua.uni.auth.SessionManager;
import ua.uni.audio.services.AudioManager;
import ua.uni.config.GameSettings;
import ua.uni.online.NakamaClient;
import ua.uni.online.CoopMatchState;
import ua.uni.online.NakamaMatchService;
import ua.uni.online.NakamaSessionService;
import ua.uni.online.NakamaSocket;
import ua.uni.online.OnlineConfig;
import ua.uni.online.OnlineSessionStore;
import ua.uni.web.login_menu.LoginMenu;
import ua.uni.web.main_menu.Menu;
import ua.uni.web.main_menu.settings_menu.AchievementsButton;
import ua.uni.web.main_menu.settings_menu.LanguageButton;

public class MainGame extends Game {
    private static final float ACHIEVEMENT_POPUP_DURATION = 4.4f;
    private static final boolean DEV_SKIP_LOGIN = true;

    private FirebaseConfig firebaseConfig;
    private FirebaseAuthService authService;
    private FirestoreService firestoreService;
    private SessionManager sessionManager;
    private OnlineConfig onlineConfig;
    private NakamaClient nakamaClient;
    private com.heroiclabs.nakama.Client onlineClient;
    private OnlineSessionStore onlineSessionStore;
    private NakamaSessionService nakamaSessionService;
    private NakamaMatchService nakamaMatchService;
    private CoopMatchState coopMatchState;
    private AchievementManager achievementManager;
    private SpriteBatch overlayBatch;
    private BitmapFont popupHeaderFont;
    private BitmapFont popupTitleFont;
    private Texture popupPanel;
    private Texture popupGlow;
    private Texture popupPixel;
    private final GlyphLayout popupHeaderLayout = new GlyphLayout();
    private final GlyphLayout popupTitleLayout = new GlyphLayout();
    private Achievements activePopupAchievement;
    private float popupElapsed;

    @Override
    public void create() {
        GameSettings.load();
        firebaseConfig = FirebaseConfig.loadFromResources();
        authService = new FirebaseAuthService(firebaseConfig);
        firestoreService = new FirestoreService(firebaseConfig);
        sessionManager = new SessionManager();
        onlineConfig = OnlineConfig.loadFromResources();
        nakamaClient = new NakamaClient(onlineConfig);
        onlineClient = nakamaClient.createClient();
        onlineSessionStore = new OnlineSessionStore();
        nakamaSessionService = new NakamaSessionService(onlineClient, onlineConfig, onlineSessionStore);
        nakamaMatchService = new NakamaMatchService(
                new NakamaSocket(onlineClient, onlineConfig.getHost(), onlineConfig.getSocketPort(), onlineConfig.isSsl()));
        achievementManager = new AchievementManager();
        initAchievementPopupUi();
        setScreen(new IntroScreen(this));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F8)) {
            resetAchievementsForDebug();
        }
        achievementManager.onPlayTime(delta);
        super.render();
        updateAchievementPopup(delta);
        renderAchievementPopup();
    }

    public FirebaseAuthService getAuthService() {
        return authService;
    }

    public FirestoreService getFirestoreService() {
        return firestoreService;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public OnlineConfig getOnlineConfig() {
        return onlineConfig;
    }

    public NakamaSessionService getNakamaSessionService() {
        return nakamaSessionService;
    }

    public NakamaMatchService getNakamaMatchService() {
        return nakamaMatchService;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public CoopMatchState getCoopMatchState() {
        return coopMatchState;
    }

    public void setCoopMatchState(CoopMatchState coopMatchState) {
        this.coopMatchState = coopMatchState;
    }

    public void clearCoopMatchState() {
        this.coopMatchState = null;
    }

    public Screen createStartupScreen() {
        if (DEV_SKIP_LOGIN || sessionManager.hasSession()) {
            return new Menu(this);
        }
        return new LoginMenu(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (overlayBatch != null) overlayBatch.dispose();
        if (popupHeaderFont != null) popupHeaderFont.dispose();
        if (popupTitleFont != null) popupTitleFont.dispose();
        if (popupPanel != null) popupPanel.dispose();
        if (popupGlow != null) popupGlow.dispose();
        if (popupPixel != null) popupPixel.dispose();
    }

    private void initAchievementPopupUi() {
        overlayBatch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter header = new FreeTypeFontGenerator.FreeTypeFontParameter();
        header.size = 28;
        header.color = new Color(0.92f, 0.93f, 0.88f, 1f);
        header.borderWidth = 1f;
        header.borderColor = new Color(0.02f, 0.02f, 0.02f, 1f);
        header.characters = LanguageButton.FONT_CHARACTERS;
        popupHeaderFont = generator.generateFont(header);

        FreeTypeFontGenerator.FreeTypeFontParameter title = new FreeTypeFontGenerator.FreeTypeFontParameter();
        title.size = 36;
        title.color = Color.WHITE;
        title.borderWidth = 1.1f;
        title.borderColor = new Color(0.02f, 0.02f, 0.02f, 1f);
        title.characters = LanguageButton.FONT_CHARACTERS;
        popupTitleFont = generator.generateFont(title);

        generator.dispose();

        popupPanel = createAchievementPanelTexture(460, 132);
        popupGlow = createSoftGlowTexture(220);
        popupPixel = solidTexture(2, 2, Color.WHITE);
    }

    private void updateAchievementPopup(float delta) {
        if (activePopupAchievement == null) {
            activePopupAchievement = achievementManager.pollUnlockedAchievement();
            popupElapsed = 0f;
            if (activePopupAchievement != null) {
                playAchievementSound(activePopupAchievement);
            }
        }
        if (activePopupAchievement != null) {
            popupElapsed += delta;
            if (popupElapsed >= ACHIEVEMENT_POPUP_DURATION) {
                activePopupAchievement = achievementManager.pollUnlockedAchievement();
                popupElapsed = 0f;
                if (activePopupAchievement != null) {
                    playAchievementSound(activePopupAchievement);
                }
            }
        }
    }

    private void playAchievementSound(Achievements achievement) {
        if (achievement.getRarity() == AchievementsRarity.Legendary) {
            AudioManager.get().playAchievementWinner(0.95f);
            return;
        }
        float volume = achievement.getRarity() == AchievementsRarity.Epic ? 1.00f : 0.90f;
        AudioManager.get().playAchievementNotice(volume);
    }

    private void renderAchievementPopup() {
        if (activePopupAchievement == null) {
            return;
        }

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float panelW = 460f;
        float panelH = 132f;
        float margin = 18f;
        float visibleX = screenW - panelW - margin;
        float hiddenX = screenW + 22f;
        float y = screenH - panelH - 18f;
        float alpha = popupAlpha();
        float x = Interpolation.smoother.apply(hiddenX, visibleX, popupSlideProgress());
        Color rarity = rarityColor(activePopupAchievement.getRarity());
        Color rarityAccent = rarityAccentColor(activePopupAchievement.getRarity());

        popupHeaderLayout.setText(popupHeaderFont, LanguageButton.t("ACHIEVEMENT_UNLOCKED"));
        popupTitleLayout.setText(popupTitleFont, activePopupAchievement.getTitle());

        overlayBatch.begin();
        overlayBatch.setColor(rarity.r, rarity.g, rarity.b, 0.10f * alpha);
        overlayBatch.draw(popupGlow, x - 120f, y - 48f, panelW + 240f, panelH + 96f);
        overlayBatch.setColor(rarityAccent.r, rarityAccent.g, rarityAccent.b, 0.06f * alpha);
        overlayBatch.draw(popupGlow, x - 64f, y - 24f, panelW + 128f, panelH + 48f);
        overlayBatch.setColor(1f, 1f, 1f, alpha);
        overlayBatch.draw(popupPanel, x, y, panelW, panelH);

        drawGradientGlowBorder(overlayBatch, x + 6f, y + 6f, panelW - 12f, panelH - 12f, 14f,
                new Color(rarityAccent.r, rarityAccent.g, rarityAccent.b, 0.95f * alpha),
                new Color(rarity.r, rarity.g, rarity.b, 0.72f * alpha));
        drawGradientGlowBorder(overlayBatch, x + 14f, y + 14f, panelW - 28f, panelH - 28f, 7f,
                new Color(rarity.r, rarity.g, rarity.b, 0.50f * alpha),
                new Color(rarityAccent.r, rarityAccent.g, rarityAccent.b, 0.24f * alpha));

        popupHeaderFont.setColor(rarity.r, rarity.g, rarity.b, alpha);
        popupHeaderFont.draw(overlayBatch, popupHeaderLayout, x + ((panelW - popupHeaderLayout.width) * 0.5f), y + 106f);

        popupTitleFont.setColor(0.97f, 0.97f, 0.95f, alpha);
        popupTitleFont.draw(overlayBatch, popupTitleLayout, x + ((panelW - popupTitleLayout.width) * 0.5f), y + 64f);
        overlayBatch.setColor(1f, 1f, 1f, 1f);
        overlayBatch.end();

        popupHeaderFont.setColor(Color.WHITE);
        popupTitleFont.setColor(Color.WHITE);
    }

    private void drawGradientGlowBorder(SpriteBatch batch, float x, float y, float w, float h, float thickness,
                                        Color edge, Color inner) {
        batch.setColor(edge);
        batch.draw(popupGlow, x - (thickness * 1.45f), y + h - thickness, w + (thickness * 2.9f), thickness * 1.6f);
        batch.draw(popupGlow, x - (thickness * 1.45f), y - (thickness * 0.6f), w + (thickness * 2.9f), thickness * 1.6f);
        batch.draw(popupGlow, x - (thickness * 0.6f), y - (thickness * 1.45f), thickness * 1.6f, h + (thickness * 2.9f));
        batch.draw(popupGlow, x + w - thickness, y - (thickness * 1.45f), thickness * 1.6f, h + (thickness * 2.9f));

        batch.setColor(inner);
        batch.draw(popupGlow, x - (thickness * 0.55f), y + h - (thickness * 0.7f), w + (thickness * 1.1f), thickness * 0.9f);
        batch.draw(popupGlow, x - (thickness * 0.55f), y - (thickness * 0.35f), w + (thickness * 1.1f), thickness * 0.9f);
        batch.draw(popupGlow, x - (thickness * 0.35f), y - (thickness * 0.55f), thickness * 0.9f, h + (thickness * 1.1f));
        batch.draw(popupGlow, x + w - (thickness * 0.55f), y - (thickness * 0.55f), thickness * 0.9f, h + (thickness * 1.1f));
    }

    private float popupSlideProgress() {
        float in = 0.42f;
        float out = 0.52f;
        if (popupElapsed < in) {
            return popupElapsed / in;
        }
        if (popupElapsed > ACHIEVEMENT_POPUP_DURATION - out) {
            float t = (popupElapsed - (ACHIEVEMENT_POPUP_DURATION - out)) / out;
            return 1f - t;
        }
        return 1f;
    }

    private float popupAlpha() {
        float in = 0.30f;
        float out = 0.40f;
        if (popupElapsed < in) {
            return popupElapsed / in;
        }
        if (popupElapsed > ACHIEVEMENT_POPUP_DURATION - out) {
            float t = (popupElapsed - (ACHIEVEMENT_POPUP_DURATION - out)) / out;
            return 1f - t;
        }
        return 1f;
    }

    private Color rarityColor(AchievementsRarity rarity) {
        return switch (rarity) {
            case Common -> new Color(0.31f, 0.56f, 0.78f, 1f);
            case Rare -> new Color(0.72f, 0.38f, 0.18f, 1f);
            case Epic -> new Color(0.43f, 0.26f, 0.78f, 1f);
            case Legendary -> new Color(0.88f, 0.73f, 0.28f, 1f);
        };
    }

    private Color rarityAccentColor(AchievementsRarity rarity) {
        return switch (rarity) {
            case Common -> new Color(0.66f, 0.84f, 0.98f, 1f);
            case Rare -> new Color(0.93f, 0.66f, 0.30f, 1f);
            case Epic -> new Color(0.78f, 0.57f, 0.96f, 1f);
            case Legendary -> new Color(1f, 0.93f, 0.62f, 1f);
        };
    }

    private Texture createAchievementPanelTexture(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        fillRoundedRect(pixmap, 12, 14, w - 18, h - 20, 22, new Color(0f, 0f, 0f, 0.34f));
        fillRoundedRect(pixmap, 2, 0, w - 4, h - 6, 24, new Color(0.08f, 0.08f, 0.09f, 0.98f));
        fillRoundedRect(pixmap, 6, 4, w - 12, h - 12, 22, new Color(0.18f, 0.19f, 0.20f, 1f));
        fillRoundedRect(pixmap, 12, 10, w - 24, h - 24, 18, new Color(0.06f, 0.06f, 0.07f, 0.98f));

        for (int y = 16; y < h - 16; y++) {
            float t = (y - 16f) / Math.max(1f, (h - 32f));
            float r = 0.24f - (0.10f * t);
            float g = 0.25f - (0.10f * t);
            float b = 0.27f - (0.12f * t);
            pixmap.setColor(r, g, b, 0.20f);
            pixmap.drawLine(16, y, w - 17, y);
        }

        for (int y = 12; y < h - 12; y += 5) {
            for (int x = 14; x < w - 14; x += 6) {
                int noise = ((x * 29) + (y * 13)) % 17;
                float shade = 0.14f + ((noise % 5) * 0.025f);
                float alpha = 0.025f + ((noise % 7) * 0.008f);
                pixmap.setColor(shade, shade, shade + 0.01f, alpha);
                pixmap.drawPixel(x, y);
            }
        }

        pixmap.setColor(0.38f, 0.39f, 0.41f, 0.22f);
        pixmap.drawLine(20, h - 26, w - 21, h - 26);
        pixmap.drawLine(24, h - 30, w - 25, h - 30);
        pixmap.setColor(0f, 0f, 0f, 0.30f);
        pixmap.drawLine(20, 24, w - 21, 24);
        pixmap.drawLine(24, 20, w - 25, 20);
        pixmap.setColor(0.07f, 0.07f, 0.08f, 0.95f);
        fillRoundedRect(pixmap, 20, 24, w - 40, h - 48, 12, new Color(0.07f, 0.07f, 0.08f, 0.95f));
        for (int y = 28; y < h - 28; y++) {
            float t = (y - 28f) / Math.max(1f, (h - 56f));
            pixmap.setColor(0.11f - (0.03f * t), 0.11f - (0.03f * t), 0.12f - (0.04f * t), 0.18f);
            pixmap.drawLine(24, y, w - 25, y);
        }

        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture createSoftGlowTexture(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        float center = size / 2f;
        float maxR = size / 2f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - center;
                float dy = y - center;
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                float t = Math.min(1f, d / maxR);
                float a = 1f - t;
                a = a * a * a;
                if (a > 0f) {
                    pixmap.setColor(1f, 1f, 1f, a);
                    pixmap.drawPixel(x, y);
                }
            }
        }
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

    private void fillRoundedRect(Pixmap pixmap, int x, int y, int w, int h, int r, Color color) {
        pixmap.setColor(color);
        pixmap.fillRectangle(x + r, y, w - (r * 2), h);
        pixmap.fillRectangle(x, y + r, w, h - (r * 2));
        pixmap.fillCircle(x + r, y + r, r);
        pixmap.fillCircle(x + w - r - 1, y + r, r);
        pixmap.fillCircle(x + r, y + h - r - 1, r);
        pixmap.fillCircle(x + w - r - 1, y + h - r - 1, r);
    }

    private void resetAchievementsForDebug() {
        achievementManager.resetAll();
        activePopupAchievement = null;
        popupElapsed = 0f;
        if (getScreen() instanceof AchievementsButton) {
            setScreen(new AchievementsButton(this));
        }
        Gdx.app.log("Achievements", "Achievements reset with F8");
    }
}
