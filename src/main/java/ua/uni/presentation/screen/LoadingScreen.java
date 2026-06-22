package ua.uni.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ua.uni.bootstrap.GameServices;
import ua.uni.core.config.GameSettings;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

import java.util.function.Supplier;

public class LoadingScreen implements Screen {
    private static final float W = 1280f;
    private static final float H = 720f;
    private static final float GEAR_SIZE = 96f;
    private static final float SPIN_SPEED = 80f;
    private static final float TIP_WRAP_WIDTH = W * 0.54f;
    private static final float MIN_DISPLAY_SECONDS = 0.55f;

    private final GameServices services;
    private final Runnable backgroundWork;
    private final Supplier<Screen> nextScreenFactory;
    private final int tipIndex;

    private SpriteBatch batch;
    private Viewport viewport;
    private Texture gear;
    private Texture backgroundGradient;
    private Texture backgroundGlow;
    private Texture backgroundGlowInner;
    private Texture pixel;
    private BitmapFont loadingFont;
    private BitmapFont tipLabelFont;
    private BitmapFont tipTextFont;
    private GlyphLayout loadingLayout;
    private GlyphLayout tipLabelLayout;
    private GlyphLayout tipTextLayout;

    private float spinAngle;
    private float elapsed;
    private float fontScale;
    private volatile boolean workDone = false;
    private boolean skipRequested = false;
    private InputAdapter inputAdapter;

    public LoadingScreen(GameServices services, Runnable backgroundWork, Supplier<Screen> nextScreenFactory) {
        this.services = services;
        this.backgroundWork = backgroundWork;
        this.nextScreenFactory = nextScreenFactory;
        this.tipIndex = MathUtils.random(TIPS_EN.length - 1);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        viewport = new FitViewport(W, H);
        viewport.apply(true);

        gear = new Texture(Gdx.files.internal("game-resourses/textures/gear.png"));
        gear.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        backgroundGradient = makeMultiStopGradient(64, 512, new Color[]{
                new Color(0.03f, 0.03f, 0.03f, 1f),
                new Color(0.07f, 0.06f, 0.05f, 1f),
                new Color(0.13f, 0.12f, 0.10f, 1f),
                new Color(0.19f, 0.17f, 0.14f, 1f),
                new Color(0.24f, 0.21f, 0.17f, 1f),
        });
        backgroundGlow = makeGlowTexture(480,
                new Color(1.0f, 0.78f, 0.25f, 0.34f),
                new Color(0.90f, 0.55f, 0.10f, 0f));
        backgroundGlowInner = makeGlowTexture(220,
                new Color(1.0f, 0.95f, 0.60f, 0.72f),
                new Color(1.0f, 0.80f, 0.30f, 0f));

        pixel = solidPixel();

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        float screenScale = Math.max(1f, (float) Gdx.graphics.getWidth() / W);
        fontScale = 1f / screenScale;

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = LanguageButton.FONT_CHARACTERS;
        p.minFilter = TextureFilter.Linear;
        p.magFilter = TextureFilter.Linear;
        p.hinting = FreeTypeFontGenerator.Hinting.Full;
        p.gamma = 1.4f;

        p.size = Math.round(32 * screenScale);
        p.color = new Color(0.88f, 0.84f, 0.74f, 1f);
        p.borderWidth = 1.4f * screenScale;
        p.borderColor = new Color(0f, 0f, 0f, 0.8f);
        loadingFont = gen.generateFont(p);
        loadingFont.getData().setScale(fontScale);

        p.size = Math.round(20 * screenScale);
        p.color = new Color(1.0f, 0.82f, 0.28f, 1f);
        p.borderWidth = 0.9f * screenScale;
        p.borderColor = new Color(0f, 0f, 0f, 0.6f);
        tipLabelFont = gen.generateFont(p);
        tipLabelFont.getData().setScale(fontScale);

        p.size = Math.round(26 * screenScale);
        p.color = new Color(0.88f, 0.84f, 0.74f, 1f);
        p.borderWidth = 1.1f * screenScale;
        p.borderColor = new Color(0f, 0f, 0f, 0.7f);
        tipTextFont = gen.generateFont(p);
        tipTextFont.getData().setScale(fontScale);

        gen.dispose();

        loadingLayout = new GlyphLayout(loadingFont, LanguageButton.t("LOADING"));
        tipLabelLayout = new GlyphLayout(tipLabelFont, LanguageButton.t("SURVIVING_TIP"));

        boolean isUk = "UK".equals(GameSettings.getLanguage());
        String tip = (isUk ? TIPS_UK : TIPS_EN)[tipIndex];
        tipTextLayout = new GlyphLayout();
        tipTextLayout.setText(tipTextFont, tip, tipTextFont.getColor(), TIP_WRAP_WIDTH, Align.center, true);

        inputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
                    skipRequested = true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                skipRequested = true;
                return false;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);

        Thread worker = new Thread(() -> {
            try {
                backgroundWork.run();
            } catch (Exception ignored) {
            } finally {
                workDone = true;
            }
        }, "loading-screen-worker");
        worker.setDaemon(true);
        worker.start();
    }

    @Override
    public void render(float delta) {
        spinAngle += SPIN_SPEED * delta;

        boolean held = Gdx.input.isKeyPressed(Input.Keys.F);
        if (!held) {
            elapsed += delta;
        }

        boolean canTransition = workDone && (elapsed >= MIN_DISPLAY_SECONDS || skipRequested);
        if (canTransition) {
            services.setScreen(nextScreenFactory.get());
            return;
        }

        ScreenUtils.clear(0.03f, 0.03f, 0.03f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // gradient background — full screen
        batch.setColor(Color.WHITE);
        batch.draw(backgroundGradient, 0, 0, W, H);

        float gearCx = W / 2f;
        float gearCy = H * 0.60f;

        // outer ambient glow behind gear
        float outerW = 460f, outerH = 520f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(backgroundGlow,
                gearCx - outerW / 2f, gearCy - outerH * 0.52f,
                outerW, outerH);

        // inner bright glow
        float innerW = 200f, innerH = 230f;
        batch.draw(backgroundGlowInner,
                gearCx - innerW / 2f, gearCy - innerH * 0.50f,
                innerW, innerH);

        // spinning gear
        batch.setColor(0.86f, 0.82f, 0.72f, 1f);
        batch.draw(gear,
                gearCx - GEAR_SIZE / 2f, gearCy - GEAR_SIZE / 2f,
                GEAR_SIZE / 2f, GEAR_SIZE / 2f,
                GEAR_SIZE, GEAR_SIZE,
                1f, 1f, spinAngle,
                0, 0, gear.getWidth(), gear.getHeight(),
                false, false);

        // "LOADING" label
        float loadingY = gearCy - GEAR_SIZE / 2f - 12f;
        loadingFont.draw(batch, loadingLayout, (W - loadingLayout.width) / 2f, loadingY);

        // divider
        float dividerY = H * 0.365f;
        batch.setColor(0.35f, 0.30f, 0.22f, 0.9f);
        batch.draw(pixel, W * 0.32f, dividerY, W * 0.36f, 1f);

        // "SURVIVING TIP"
        float tipLabelY = dividerY - 9f;
        tipLabelFont.draw(batch, tipLabelLayout, (W - tipLabelLayout.width) / 2f, tipLabelY);

        // tip text
        float tipTextY = tipLabelY - tipLabelFont.getCapHeight() - 14f;
        tipTextFont.draw(batch, tipTextLayout, (W - TIP_WRAP_WIDTH) / 2f, tipTextY);

        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        if (viewport != null) viewport.update(w, h, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == inputAdapter) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (gear != null) gear.dispose();
        if (backgroundGradient != null) backgroundGradient.dispose();
        if (backgroundGlow != null) backgroundGlow.dispose();
        if (backgroundGlowInner != null) backgroundGlowInner.dispose();
        if (pixel != null) pixel.dispose();
        if (loadingFont != null) loadingFont.dispose();
        if (tipLabelFont != null) tipLabelFont.dispose();
        if (tipTextFont != null) tipTextFont.dispose();
    }

    private static Texture makeMultiStopGradient(int width, int height, Color[] stops) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int n = stops.length;
        for (int y = 0; y < height; y++) {
            float t = y / (float) (height - 1);
            float segF = t * (n - 1);
            int seg = Math.min((int) segF, n - 2);
            float lt = segF - seg;
            Color c0 = stops[seg], c1 = stops[seg + 1];
            pixmap.setColor(
                    c0.r + (c1.r - c0.r) * lt,
                    c0.g + (c1.g - c0.g) * lt,
                    c0.b + (c1.b - c0.b) * lt,
                    1f);
            pixmap.drawLine(0, y, width, y);
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private static Texture makeGlowTexture(int size, Color centerColor, Color edgeColor) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float center = size / 2f;
        float radius = size / 2f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - center;
                float dy = y - center;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float alpha = Math.max(0f, 1f - (distance / radius));
                alpha = alpha * alpha;
                float r = edgeColor.r + (centerColor.r - edgeColor.r) * alpha;
                float g = edgeColor.g + (centerColor.g - edgeColor.g) * alpha;
                float b = edgeColor.b + (centerColor.b - edgeColor.b) * alpha;
                float a = edgeColor.a + (centerColor.a - edgeColor.a) * alpha;
                pixmap.setColor(r, g, b, a);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private static Texture solidPixel() {
        Pixmap p = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private static final String[] TIPS_EN = {
        "COMPLETED A LEVEL?\nPLAY IT AGAIN TO SAVE\nMORE CLONES!",
        "WATCH THE SAWS.\nTIMING IS EVERYTHING.",
        "SOME OBSTACLES MOVE.\nLOOK BEFORE YOU LEAP.",
        "EVERY DEATH BRINGS YOU\nONE STEP CLOSER.",
        "IN CO-OP, ONE FALLS —\nTHE OTHER CARRIES ON.",
        "SPEED SAVES CLONES.\nFAST FINISH, MORE SURVIVORS.",
        "THE FOREST DOESN'T FORGIVE\nSECOND MISTAKES.",
        "PATIENCE IS A WEAPON.\nRUSH AND YOU WILL FALL.",
        "CLONES DON'T COMPLAIN.\nBUT THEY DO REMEMBER.",
        "A SAW THAT SPINS\nIS A SAW THAT WINS.",
        "THE FINISH LINE WAITS.\nDO YOU?",
        "SHADOWS MOVE FASTER\nTHAN YOU THINK.",
        "THREE DEATHS ON ONE OBSTACLE?\nSTUDY IT. THEN CONQUER IT.",
        "THE FOREST IS ALIVE.\nSO ARE ITS TRAPS.",
        "YOUR CLONE DIED SO YOU\nCOULD LEARN FROM IT.",
        "OBSTACLES HAVE PATTERNS.\nFIND THEM.",
        "CO-OP IS NOT A RACE\nAGAINST YOUR TEAMMATE.",
        "THE GROUND IS NOT\nALWAYS YOUR FRIEND.",
        "A PERFECT RUN STARTS\nWITH ONE GOOD JUMP.",
        "EVERY CLONE YOU SAVE\nIS A VICTORY.",
        "THE FOREST REMEMBERS\nEVERY STEP YOU TAKE.",
        "JUMP EARLY. LAND SAFE.\nTHINK AHEAD.",
        "DEATH IS A CHECKPOINT\nIN DISGUISE.",
        "YOU HAVE MADE IT\nTHIS FAR. KEEP GOING.",
        "FAST FINGERS WIN RACES.\nCALM MIND WINS LEVELS.",
        "THE BEST CLONE\nIS THE ONE THAT SURVIVES.",
    };

    private static final String[] TIPS_UK = {
        "ПРОЙШЛИ РІВЕНЬ?\nЗАПУСТІТЬ ЗНОВУ ЩОБ\nВРЯТУВАТИ БІЛЬШЕ КЛОНІВ!",
        "СТЕЖТЕ ЗА ПИЛАМИ.\nТАЙМІНГ — ЦЕ ВСЕ.",
        "ДЕЯКІ ПЕРЕШКОДИ РУХАЮТЬСЯ.\nДИВІТЬСЯ ПЕРЕД СТРИБКОМ.",
        "КОЖНА СМЕРТЬ НАБЛИЖАЄ\nВАС ДО ФІНІШУ.",
        "У КО-ОП: ОДИН ПАДАЄ —\nІНШИЙ ПРОДОВЖУЄ.",
        "ШВИДКІСТЬ РЯТУЄ КЛОНІВ.\nШВИДКИЙ ФІНІШ — БІЛЬШЕ ВИЖИВШИХ.",
        "ЛІС НЕ ВИБАЧАЄ\nДРУГИХ ПОМИЛОК.",
        "ТЕРПІННЯ — ЦЕ ЗБРОЯ.\nПОСПІШИШ — ВПАДЕШ.",
        "КЛОНИ НЕ СКАРЖАТЬСЯ.\nАЛЕ ПАМ'ЯТАЮТЬ.",
        "ПИЛА ЩО КРУТИТЬСЯ —\nПИЛА ЩО ПЕРЕМАГАЄ.",
        "ФІНІШНА ЛІНІЯ ЧЕКАє.\nА ВИ?",
        "ТІНІ РУХАЮТЬСЯ ШВИДШЕ\nНІЖ ЗДАЄТЬСЯ.",
        "ТРИ СМЕРТІ НА ОДНІЙ ПЕРЕШКОДІ?\nВИВЧИТЬ ЇЇ. ПОТІМ ЗДОЛАЙТЕ.",
        "ЛІС ЖИВИЙ.\nЯК І ЙОГО ПАСТКИ.",
        "ВАШ КЛОН ЗАГИНУВ,\nЩОБ ВИ НАВЧИЛИСЬ.",
        "ПЕРЕШКОДИ МАЮТЬ ПАТЕРНИ.\nЗНАЙДІТЬ ЇХ.",
        "КО-ОП — ЦЕ НЕ ГОНКА\nПРОТИ НАПАРНИКА.",
        "ПІДЛОГА — ЦЕ НЕ\nЗАВЖДИ ВАШ ДРУГ.",
        "ІДЕАЛЬНИЙ ЗАБІГ\nПОЧИНАЄТЬСЯ З ОДНОГО СТРИБКА.",
        "КОЖЕН ВРЯТОВАНИЙ КЛОН —\nЦЕ ПЕРЕМОГА.",
        "ЛІС ПАМ'ЯТАЄ\nКОЖЕН ВАШ КРОК.",
        "СТРИБАЙТЕ РАНІШЕ. ПРИЗЕМЛЯЙТЕСЬ БЕЗПЕЧНО.\nДУМАЙТЕ ВПЕРЕД.",
        "СМЕРТЬ — ЦЕ ЧЕКПОЙНТ\nПІД МАСКУВАННЯМ.",
        "ВИ ЗАЙШЛИ ТАК ДАЛЕКО.\nПРОДОВЖУЙТЕ.",
        "ШВИДКІ ПАЛЬЦІ ВИГРАЮТЬ ЗАБІГИ.\nСПОКІЙНИЙ РОЗУм ВИГРАЄ РІВНІ.",
        "НАЙКРАЩИЙ КЛОН —\nТОЙ ЩО ВИЖИВ.",
    };
}
