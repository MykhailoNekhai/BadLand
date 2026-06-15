package ua.uni.bootstrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.uni.core.config.GameSettings;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

public class IntroScreen implements Screen {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;
    private static final int DUST_COUNT = 42;
    private static final float INTRO_DURATION = 8.8f;
    private static final float SKIP_FADE_DURATION = 0.35f;
    private static final float END_FADE_DURATION = 0.85f;
    private static final String INTRO_MUSIC_PATH =
            "game-resourses/audio/catalog/used/menu/background/intro_shadow_forest.wav";
    private static final String TITLE_TEXT = "SHADOW FLIGHT";
    private static final String SUBTITLE_TEXT = "The forest breathes before the fall";

    private final MainGame game;
    private final GlyphLayout titleLayout = new GlyphLayout();
    private final GlyphLayout subtitleLayout = new GlyphLayout();
    private final GlyphLayout skipLayout = new GlyphLayout();
    private final float[] dustX = new float[DUST_COUNT];
    private final float[] dustY = new float[DUST_COUNT];
    private final float[] dustSpeed = new float[DUST_COUNT];
    private final float[] dustSize = new float[DUST_COUNT];
    private final float[] dustAlpha = new float[DUST_COUNT];

    private SpriteBatch batch;
    private Viewport viewport;
    private Texture bg;
    private Texture fg;
    private Texture fogTexture;
    private Texture glowTexture;
    private Texture branchVeil;
    private Texture vignette;
    private Texture pixel;
    private Texture dustTexture;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont skipFont;
    private Music introMusic;
    private InputAdapter inputAdapter;

    private float elapsed;
    private float transitionElapsed;
    private float transitionDuration;
    private boolean transitionStarted;
    private boolean disposeScheduled;

    public IntroScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        viewport.apply(true);

        bg = new Texture(Gdx.files.internal("game-resourses/menu/mainmenu_bg_generated.png"));
        fg = new Texture(Gdx.files.internal("game-resourses/menu/mainmenu_fg_generated.png"));
        fogTexture = makeFogTexture(960, 360);
        glowTexture = makeGlowTexture(760);
        branchVeil = makeBranchVeil(1280, 720);
        vignette = makeVignette(1280, 720);
        pixel = solidTexture(2, 2, Color.WHITE);
        dustTexture = makeDustTexture(32);

        for (Texture texture : new Texture[] { bg, fg, fogTexture, glowTexture, branchVeil, vignette, pixel, dustTexture }) {
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 128;
        titleParams.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        titleParams.borderWidth = 2.6f;
        titleParams.borderColor = new Color(0.04f, 0.03f, 0.03f, 1f);
        titleParams.shadowOffsetY = 6;
        titleParams.shadowColor = new Color(0f, 0f, 0f, 0.45f);
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter subtitleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        subtitleParams.size = 24;
        subtitleParams.color = new Color(0.80f, 0.86f, 0.76f, 1f);
        subtitleParams.borderWidth = 1.1f;
        subtitleParams.borderColor = new Color(0.02f, 0.03f, 0.02f, 1f);
        subtitleParams.characters = LanguageButton.FONT_CHARACTERS;
        subtitleFont = generator.generateFont(subtitleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter skipParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        skipParams.size = 30;
        skipParams.color = new Color(0.92f, 0.88f, 0.78f, 1f);
        skipParams.characters = LanguageButton.FONT_CHARACTERS;
        skipFont = generator.generateFont(skipParams);
        generator.dispose();

        titleLayout.setText(titleFont, TITLE_TEXT);
        subtitleLayout.setText(subtitleFont, SUBTITLE_TEXT);
        skipLayout.setText(skipFont, LanguageButton.t("PRESS_ANY_KEY_SKIP"));
        initDust();

        introMusic = Gdx.audio.newMusic(Gdx.files.internal(INTRO_MUSIC_PATH));
        introMusic.setLooping(false);
        introMusic.setVolume(GameSettings.getMusicVolume());
        if (GameSettings.getMusicVolume() > 0f) {
            introMusic.play();
        }

        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                triggerSkip();
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                triggerSkip();
                return true;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        updateDust(delta);

        if (introMusic != null) {
            introMusic.setVolume(GameSettings.getMusicVolume());
        }

        if (!transitionStarted && elapsed >= INTRO_DURATION) {
            startTransition(END_FADE_DURATION);
        }

        if (transitionStarted) {
            transitionElapsed += delta;
            if (!disposeScheduled && transitionElapsed >= transitionDuration) {
                disposeScheduled = true;
                game.setScreen(game.createStartupScreen());
                return;
            }
        }

        float progress = MathUtils.clamp(elapsed / INTRO_DURATION, 0f, 1f);
        float bgAlpha = Interpolation.fade.apply(MathUtils.clamp((elapsed - 0.35f) / 2.8f, 0f, 1f));
        float fgAlpha = Interpolation.fade.apply(MathUtils.clamp((elapsed - 1.25f) / 2.7f, 0f, 1f));
        float titleProgress = MathUtils.clamp((elapsed - 1.6f) / 4.2f, 0f, 1f);
        float titleAlpha = Interpolation.smoother.apply(titleProgress);
        float subtitleAlpha = Interpolation.fade.apply(MathUtils.clamp((elapsed - 2.1f) / 2.4f, 0f, 1f));
        float titleGlow = Interpolation.fade.apply(MathUtils.clamp((elapsed - 3.6f) / 2.2f, 0f, 1f));
        float holdFade = 1f - Interpolation.fade.apply(MathUtils.clamp((elapsed - 7.7f) / 0.9f, 0f, 1f));
        float finalAlpha = titleAlpha * holdFade;
        float finalSubtitleAlpha = subtitleAlpha * holdFade * 0.92f;
        float overlayAlpha = 0.97f - (0.82f * Interpolation.fade.apply(MathUtils.clamp(elapsed / 4.6f, 0f, 1f)));
        float flashAlpha = 0.12f
                * MathUtils.clamp((float) Math.sin(Math.PI * MathUtils.clamp((elapsed - 6.6f) / 1.1f, 0f, 1f)), 0f, 1f);
        float fogDrift = elapsed * 11f;
        float titleScale = introTitleScale();
        float titleY = 372f + 22f * (1f - titleProgress) + titleRiseOffset();
        float subtitleY = titleY - 62f;
        float skipAlpha = 0.25f + 0.3f * MathUtils.sin(MathUtils.PI2 * progress * 2f);
        float canopyAlpha = 0.22f + 0.18f * Interpolation.fade.apply(MathUtils.clamp((elapsed - 1.0f) / 4.5f, 0f, 1f));
        float forestPulse = 0.05f + 0.07f * MathUtils.clamp((float) Math.sin(elapsed * 0.75f), -1f, 1f);
        float fadeToBlack = transitionStarted
                ? Interpolation.fade.apply(MathUtils.clamp(transitionElapsed / transitionDuration, 0f, 1f))
                : 0f;

        ScreenUtils.clear(0.02f, 0.015f, 0.015f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        float bgScale = 1.02f + 0.02f * progress;
        float bgWidth = WORLD_WIDTH * bgScale;
        float bgHeight = WORLD_HEIGHT * bgScale;
        float bgX = -14f - progress * 18f;
        float bgY = -10f - progress * 6f;
        batch.setColor(1f, 1f, 1f, bgAlpha);
        batch.draw(bg, bgX, bgY, bgWidth, bgHeight);

        batch.setColor(0.40f, 0.56f, 0.26f, 0.09f + forestPulse);
        batch.draw(glowTexture, WORLD_WIDTH * 0.48f - 360f, WORLD_HEIGHT * 0.58f - 290f, 720f, 720f);
        batch.setColor(1f, 0.74f, 0.34f, 0.06f + titleGlow * 0.10f);
        batch.draw(glowTexture, WORLD_WIDTH * 0.56f - 320f, WORLD_HEIGHT * 0.50f - 250f, 640f, 640f);

        batch.setColor(1f, 1f, 1f, 0.12f * bgAlpha);
        batch.draw(fogTexture, -120f + fogDrift, 372f, 900f, 310f);
        batch.draw(fogTexture, 420f - fogDrift * 0.45f, 128f, 820f, 260f);
        batch.setColor(0.86f, 0.95f, 0.78f, 0.06f * bgAlpha);
        batch.draw(fogTexture, 180f + fogDrift * 0.22f, 470f, 760f, 180f);

        batch.setColor(1f, 1f, 1f, fgAlpha);
        batch.draw(fg, -10f + progress * 14f, -6f, WORLD_WIDTH * 1.04f, WORLD_HEIGHT * 1.02f);
        batch.setColor(0.03f, 0.04f, 0.03f, canopyAlpha);
        batch.draw(branchVeil, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);

        drawDust();
        drawTitle(finalAlpha, titleGlow, titleScale, titleY);
        drawSubtitle(finalSubtitleAlpha, subtitleY);

        batch.setColor(0f, 0f, 0f, overlayAlpha);
        batch.draw(pixel, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);

        batch.setColor(1f, 1f, 1f, 0.54f);
        batch.draw(vignette, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);

        batch.setColor(1f, 0.95f, 0.88f, MathUtils.clamp(skipAlpha, 0f, 0.5f) * (1f - fadeToBlack));
        skipFont.draw(batch, skipLayout, WORLD_WIDTH - skipLayout.width - 46f, 54f);

        if (flashAlpha > 0f) {
            batch.setColor(1f, 0.84f, 0.55f, flashAlpha);
            batch.draw(pixel, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        }

        if (fadeToBlack > 0f) {
            batch.setColor(0f, 0f, 0f, fadeToBlack);
            batch.draw(pixel, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        }

        batch.end();
    }

    private void drawTitle(float alpha, float glow, float scale, float centerY) {
        if (alpha <= 0f) {
            return;
        }

        float width = titleLayout.width * scale;
        float x = (WORLD_WIDTH - width) * 0.5f;
        float y = centerY + titleLayout.height * 0.5f;

        titleFont.getData().setScale(scale);

        float shadowScale = scale * 1.05f;
        float shadowWidth = titleLayout.width * shadowScale;
        float shadowX = (WORLD_WIDTH - shadowWidth) * 0.5f;
        float shadowY = y - 6f;

        titleFont.getData().setScale(shadowScale);
        titleFont.setColor(0.18f, 0.10f, 0.03f, alpha * (0.26f + glow * 0.12f));
        titleFont.draw(batch, TITLE_TEXT, shadowX, shadowY);

        titleFont.getData().setScale(scale);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.setColor(1f, 0.82f, 0.48f, 0.10f * alpha + 0.24f * glow * alpha);
        titleFont.draw(batch, TITLE_TEXT, x, y);
        batch.setColor(0.72f, 0.92f, 0.62f, 0.05f * alpha + 0.10f * glow * alpha);
        titleFont.draw(batch, TITLE_TEXT, x - 4f, y + 2f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        titleFont.setColor(0.98f, 0.95f, 0.88f, alpha);
        titleFont.draw(batch, TITLE_TEXT, x, y);
        titleFont.getData().setScale(1f);
    }

    private void drawSubtitle(float alpha, float y) {
        if (alpha <= 0f) {
            return;
        }
        float x = (WORLD_WIDTH - subtitleLayout.width) * 0.5f;
        subtitleFont.setColor(0.80f, 0.86f, 0.76f, alpha);
        subtitleFont.draw(batch, subtitleLayout, x, y);
    }

    private void drawDust() {
        for (int i = 0; i < DUST_COUNT; i++) {
            float pulse = 0.75f + 0.25f * MathUtils.sin(elapsed * (0.8f + i * 0.03f) + i);
            batch.setColor(0.95f, 0.88f, 0.62f, dustAlpha[i] * pulse);
            batch.draw(dustTexture, dustX[i], dustY[i], dustSize[i], dustSize[i]);
        }
    }

    private float introTitleScale() {
        float rise = Interpolation.smoother.apply(MathUtils.clamp((elapsed - 1.2f) / 4.8f, 0f, 1f));
        float base = 0.82f + 0.24f * rise;
        float pulseA = pulse(3.1f, 0.24f, 0.06f);
        float pulseB = pulse(5.2f, 0.28f, 0.08f);
        float pulseC = pulse(6.9f, 0.34f, 0.10f);
        return base + pulseA + pulseB + pulseC;
    }

    private float titleRiseOffset() {
        float pullIn = 18f * (1f - Interpolation.smoother.apply(MathUtils.clamp((elapsed - 1.2f) / 4.8f, 0f, 1f)));
        float impact = -10f * pulse(6.9f, 0.34f, 1f);
        return pullIn + impact;
    }

    private float pulse(float center, float halfWidth, float amplitude) {
        float distance = Math.abs(elapsed - center);
        if (distance >= halfWidth) {
            return 0f;
        }
        float norm = 1f - distance / halfWidth;
        return amplitude * Interpolation.fade.apply(norm);
    }

    private void initDust() {
        for (int i = 0; i < DUST_COUNT; i++) {
            resetDust(i, true);
        }
    }

    private void updateDust(float delta) {
        for (int i = 0; i < DUST_COUNT; i++) {
            dustX[i] += dustSpeed[i] * delta;
            dustY[i] += dustSpeed[i] * 0.28f * delta;
            if (dustX[i] > WORLD_WIDTH + 60f || dustY[i] > WORLD_HEIGHT + 40f) {
                resetDust(i, false);
            }
        }
    }

    private void resetDust(int index, boolean randomX) {
        dustSize[index] = MathUtils.random(3.5f, 10f);
        dustSpeed[index] = MathUtils.random(6f, 18f);
        dustAlpha[index] = MathUtils.random(0.05f, 0.20f);
        dustX[index] = randomX ? MathUtils.random(-20f, WORLD_WIDTH + 40f) : MathUtils.random(-60f, -10f);
        dustY[index] = MathUtils.random(80f, WORLD_HEIGHT - 40f);
    }

    private void triggerSkip() {
        if (!transitionStarted) {
            startTransition(SKIP_FADE_DURATION);
        }
    }

    private void startTransition(float duration) {
        transitionStarted = true;
        transitionElapsed = 0f;
        transitionDuration = duration;
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == inputAdapter) {
            Gdx.input.setInputProcessor(null);
        }
        stopIntroMusic();
    }

    @Override
    public void dispose() {
        stopIntroMusic();
        if (batch != null) batch.dispose();
        if (bg != null) bg.dispose();
        if (fg != null) fg.dispose();
        if (fogTexture != null) fogTexture.dispose();
        if (glowTexture != null) glowTexture.dispose();
        if (branchVeil != null) branchVeil.dispose();
        if (vignette != null) vignette.dispose();
        if (pixel != null) pixel.dispose();
        if (dustTexture != null) dustTexture.dispose();
        if (titleFont != null) titleFont.dispose();
        if (subtitleFont != null) subtitleFont.dispose();
        if (skipFont != null) skipFont.dispose();
    }

    private void stopIntroMusic() {
        if (introMusic != null) {
            introMusic.stop();
            introMusic.dispose();
            introMusic = null;
        }
    }

    private Texture solidTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture makeFogTexture(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        for (int x = 0; x < width; x++) {
            float nx = x / (float) width;
            for (int y = 0; y < height; y++) {
                float ny = y / (float) height;
                float arc = MathUtils.sin(nx * MathUtils.PI) * 0.85f;
                float fade = 1f - Math.abs(ny - 0.5f) * 1.8f;
                float alpha = MathUtils.clamp(arc * fade, 0f, 1f);
                pixmap.setColor(0.82f, 0.92f, 0.74f, alpha * 0.18f);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture makeGlowTexture(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float radius = size * 0.5f;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dx = x - radius;
                float dy = y - radius;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float norm = MathUtils.clamp(1f - distance / radius, 0f, 1f);
                float alpha = norm * norm * 0.85f;
                pixmap.setColor(1f, 0.70f, 0.28f, alpha);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture makeDustTexture(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float radius = size * 0.5f;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dx = x - radius;
                float dy = y - radius;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float norm = MathUtils.clamp(1f - distance / radius, 0f, 1f);
                pixmap.setColor(1f, 0.94f, 0.74f, norm * norm * 0.75f);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture makeBranchVeil(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.03f, 0.035f, 0.025f, 1f);
        for (int i = 0; i < 18; i++) {
            float startX = MathUtils.random(-120f, width + 120f);
            float startY = MathUtils.random(height * 0.55f, height + 80f);
            float length = MathUtils.random(220f, 520f);
            float thickness = MathUtils.random(8f, 24f);
            float angle = MathUtils.random(210f, 330f);
            drawBranchStroke(pixmap, startX, startY, length, thickness, angle);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void drawBranchStroke(Pixmap pixmap, float startX, float startY, float length, float thickness, float angleDeg) {
        float radians = angleDeg * MathUtils.degreesToRadians;
        int steps = Math.max(12, (int) (length / 18f));
        for (int i = 0; i < steps; i++) {
            float t = i / (float) (steps - 1);
            float sway = MathUtils.sin(t * MathUtils.PI2 * 1.5f) * 14f;
            float x = startX + MathUtils.cos(radians) * length * t + sway;
            float y = startY + MathUtils.sin(radians) * length * t;
            int radius = Math.max(1, Math.round(thickness * (1f - t * 0.55f)));
            pixmap.fillCircle(Math.round(x), Math.round(y), radius);
        }
    }

    private Texture makeVignette(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        float cx = width * 0.5f;
        float cy = height * 0.56f;
        float maxDistance = (float) Math.sqrt(cx * cx + cy * cy);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float dx = x - cx;
                float dy = y - cy;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float alpha = MathUtils.clamp((distance / maxDistance - 0.24f) / 0.74f, 0f, 1f);
                pixmap.setColor(0f, 0f, 0f, alpha * 0.90f);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
