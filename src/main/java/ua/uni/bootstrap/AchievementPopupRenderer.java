package ua.uni.bootstrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import ua.uni.audio.services.AudioManager;
import ua.uni.gameplay.achievements.AchievementManager;
import ua.uni.gameplay.achievements.Achievements;
import ua.uni.gameplay.achievements.AchievementsRarity;
import ua.uni.presentation.screen.menu.factory.FontQuality;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

final class AchievementPopupRenderer {
    private static final float ACHIEVEMENT_POPUP_DURATION = 4.4f;

    private final AchievementManager achievementManager;
    private final SpriteBatch overlayBatch;
    private final BitmapFont headerFont;
    private final BitmapFont titleFont;
    private final Texture panel;
    private final Texture glow;
    private final Texture pixel;
    private final GlyphLayout headerLayout = new GlyphLayout();
    private final GlyphLayout titleLayout = new GlyphLayout();
    private final Matrix4 projection = new Matrix4();

    private Achievements activeAchievement;
    private float elapsed;

    AchievementPopupRenderer(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;
        overlayBatch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter header = new FreeTypeFontGenerator.FreeTypeFontParameter();
        header.size = 28;
        header.color = new Color(0.92f, 0.93f, 0.88f, 1f);
        header.borderWidth = 1f;
        header.borderColor = new Color(0.02f, 0.02f, 0.02f, 1f);
        header.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(header);
        headerFont = generator.generateFont(header);
        FontQuality.fixScale(headerFont);

        FreeTypeFontGenerator.FreeTypeFontParameter title = new FreeTypeFontGenerator.FreeTypeFontParameter();
        title.size = 36;
        title.color = Color.WHITE;
        title.borderWidth = 1.1f;
        title.borderColor = new Color(0.02f, 0.02f, 0.02f, 1f);
        title.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(title);
        titleFont = generator.generateFont(title);
        FontQuality.fixScale(titleFont);

        generator.dispose();

        panel = createPanelTexture(460, 132);
        glow = createGlowTexture(220);
        pixel = solidTexture(2, 2, Color.WHITE);
    }

    void update(float delta) {
        if (activeAchievement == null) {
            activeAchievement = achievementManager.pollUnlockedAchievement();
            elapsed = 0f;
            if (activeAchievement != null) playSound(activeAchievement);
        }
        if (activeAchievement != null) {
            elapsed += delta;
            if (elapsed >= ACHIEVEMENT_POPUP_DURATION) {
                activeAchievement = achievementManager.pollUnlockedAchievement();
                elapsed = 0f;
                if (activeAchievement != null) playSound(activeAchievement);
            }
        }
    }

    void render() {
        if (activeAchievement == null) return;

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        projection.setToOrtho2D(0f, 0f, screenW, screenH);
        overlayBatch.setProjectionMatrix(projection);
        float panelW = 460f;
        float panelH = 132f;
        float margin = 18f;
        float visibleX = screenW - panelW - margin;
        float hiddenX = screenW + 22f;
        float y = screenH - panelH - 18f;
        float alpha = popupAlpha();
        float x = Interpolation.smoother.apply(hiddenX, visibleX, slideProgress());
        Color rarity = rarityColor(activeAchievement.getRarity());
        Color rarityAccent = rarityAccentColor(activeAchievement.getRarity());

        headerLayout.setText(headerFont, LanguageButton.t("ACHIEVEMENT_UNLOCKED"));
        titleLayout.setText(titleFont, activeAchievement.getTitle());

        Gdx.gl.glEnable(GL20.GL_BLEND);
        overlayBatch.begin();
        overlayBatch.setColor(rarity.r, rarity.g, rarity.b, 0.10f * alpha);
        overlayBatch.draw(glow, x - 120f, y - 48f, panelW + 240f, panelH + 96f);
        overlayBatch.setColor(rarityAccent.r, rarityAccent.g, rarityAccent.b, 0.06f * alpha);
        overlayBatch.draw(glow, x - 64f, y - 24f, panelW + 128f, panelH + 48f);
        overlayBatch.setColor(1f, 1f, 1f, alpha);
        overlayBatch.draw(panel, x, y, panelW, panelH);

        drawGradientGlowBorder(x + 6f, y + 6f, panelW - 12f, panelH - 12f, 14f,
                new Color(rarityAccent.r, rarityAccent.g, rarityAccent.b, 0.95f * alpha),
                new Color(rarity.r, rarity.g, rarity.b, 0.72f * alpha));
        drawGradientGlowBorder(x + 14f, y + 14f, panelW - 28f, panelH - 28f, 7f,
                new Color(rarity.r, rarity.g, rarity.b, 0.50f * alpha),
                new Color(rarityAccent.r, rarityAccent.g, rarityAccent.b, 0.24f * alpha));

        headerFont.setColor(rarity.r, rarity.g, rarity.b, alpha);
        headerFont.draw(overlayBatch, headerLayout, x + ((panelW - headerLayout.width) * 0.5f), y + 106f);
        titleFont.setColor(0.97f, 0.97f, 0.95f, alpha);
        titleFont.draw(overlayBatch, titleLayout, x + ((panelW - titleLayout.width) * 0.5f), y + 64f);
        overlayBatch.setColor(1f, 1f, 1f, 1f);
        overlayBatch.end();

        headerFont.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
    }

    void reset() {
        activeAchievement = null;
        elapsed = 0f;
    }

    void dispose() {
        overlayBatch.dispose();
        headerFont.dispose();
        titleFont.dispose();
        panel.dispose();
        glow.dispose();
        pixel.dispose();
    }

    private void playSound(Achievements achievement) {
        if (achievement.getRarity() == AchievementsRarity.Legendary) {
            AudioManager.get().playAchievementWinner(0.95f);
            return;
        }
        float volume = achievement.getRarity() == AchievementsRarity.Epic ? 1.00f : 0.90f;
        AudioManager.get().playAchievementNotice(volume);
    }

    private void drawGradientGlowBorder(float x, float y, float w, float h, float t, Color edge, Color inner) {
        overlayBatch.setColor(edge);
        overlayBatch.draw(glow, x - (t * 1.45f), y + h - t, w + (t * 2.9f), t * 1.6f);
        overlayBatch.draw(glow, x - (t * 1.45f), y - (t * 0.6f), w + (t * 2.9f), t * 1.6f);
        overlayBatch.draw(glow, x - (t * 0.6f), y - (t * 1.45f), t * 1.6f, h + (t * 2.9f));
        overlayBatch.draw(glow, x + w - t, y - (t * 1.45f), t * 1.6f, h + (t * 2.9f));
        overlayBatch.setColor(inner);
        overlayBatch.draw(glow, x - (t * 0.55f), y + h - (t * 0.7f), w + (t * 1.1f), t * 0.9f);
        overlayBatch.draw(glow, x - (t * 0.55f), y - (t * 0.35f), w + (t * 1.1f), t * 0.9f);
        overlayBatch.draw(glow, x - (t * 0.35f), y - (t * 0.55f), t * 0.9f, h + (t * 1.1f));
        overlayBatch.draw(glow, x + w - (t * 0.55f), y - (t * 0.55f), t * 0.9f, h + (t * 1.1f));
    }

    private float slideProgress() {
        float in = 0.42f;
        float out = 0.52f;
        if (elapsed < in) return elapsed / in;
        if (elapsed > ACHIEVEMENT_POPUP_DURATION - out) {
            return 1f - ((elapsed - (ACHIEVEMENT_POPUP_DURATION - out)) / out);
        }
        return 1f;
    }

    private float popupAlpha() {
        float in = 0.30f;
        float out = 0.40f;
        if (elapsed < in) return elapsed / in;
        if (elapsed > ACHIEVEMENT_POPUP_DURATION - out) {
            return 1f - ((elapsed - (ACHIEVEMENT_POPUP_DURATION - out)) / out);
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

    private Texture createPanelTexture(int w, int h) {
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

    private Texture createGlowTexture(int size) {
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
}
