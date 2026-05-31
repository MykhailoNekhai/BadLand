package ua.uni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.AudioManager;
import ua.uni.game.MainGame;
import ua.uni.language.language;

public class MenuScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private BitmapFont menuFont;
    private BitmapFont titleFont;
    private BitmapFont iconFont;

    private Texture bg;
    private Texture fg;
    private Texture buttonUp;
    private Texture buttonOver;
    private Texture buttonDown;
    private Texture iconUp;
    private Texture iconOver;
    private Texture settingsIcon;
    private Texture profileIcon;
    private Texture vignette;
    private Texture midFog;
    private Texture transitionBlack;
    private Texture particleTex;
    private Texture dotTex;

    private static final int PARTICLE_COUNT = 32;
    private final float[] partX = new float[PARTICLE_COUNT];
    private final float[] partY = new float[PARTICLE_COUNT];
    private final float[] partLife = new float[PARTICLE_COUNT];
    private final float[] partMaxLife = new float[PARTICLE_COUNT];
    private final float[] partSize = new float[PARTICLE_COUNT];

    private static final int TRAIL_LEN = 10;
    private final float[] trailX = new float[TRAIL_LEN];
    private final float[] trailY = new float[TRAIL_LEN];
    private int trailHead;
    private final Vector2 mouseTmp = new Vector2();

    private float elapsed;
    private float uiAlpha = 0f;
    private float transitionAlpha = 0f;
    private boolean startTransition;
    private final GlyphLayout titleLayout = new GlyphLayout();

    public MenuScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 86;
        p.color = new Color(0.95f, 0.96f, 0.98f, 1f);
        p.borderWidth = 2.2f;
        p.borderColor = new Color(0.10f, 0.12f, 0.16f, 1f);
        p.shadowOffsetX = 0;
        p.shadowOffsetY = 5;
        p.shadowColor = new Color(0f, 0f, 0f, 0.62f);
        p.characters = language.FONT_CHARACTERS;
        menuFont = generator.generateFont(p);
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 110;
        titleParams.color = Color.BLACK;
        titleParams.borderWidth = 0f;
        titleParams.shadowOffsetX = 0;
        titleParams.shadowOffsetY = 0;
        titleFont = generator.generateFont(titleParams);
        generator.dispose();
        iconFont = new BitmapFont();
        iconFont.getData().setScale(1.45f);

        bg = new Texture(Gdx.files.internal("game-resourses/menu/2b3aa97f-1bf0-480c-8a7f-055b791148a9.png"));
        fg = new Texture(Gdx.files.internal("game-resourses/menu/mainmenu_fg_generated.png"));
        buttonUp = roundedRect(470, 108, 46, new Color(0f, 0f, 0f, 1f));
        buttonOver = roundedRect(470, 108, 46, new Color(0f, 0f, 0f, 1f));
        buttonDown = roundedRect(470, 108, 46, new Color(0f, 0f, 0f, 1f));
        iconUp = roundedRect(84, 84, 42, new Color(0.04f, 0.04f, 0.05f, 0.90f));
        iconOver = roundedRect(84, 84, 42, new Color(0.10f, 0.10f, 0.12f, 0.96f));
        settingsIcon = new Texture(Gdx.files.internal("game-resourses/menu/gear.png"));
        profileIcon = new Texture(Gdx.files.internal("game-resourses/menu/user-profile.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        fg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        buttonUp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        buttonOver.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        buttonDown.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        iconUp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        iconOver.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        settingsIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        profileIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        vignette = makeVignette(1280, 720);
        midFog = makeMidFog(1280, 720);
        transitionBlack = solidTexture(2, 2, Color.BLACK);
        particleTex = softCircleTexture(12);
        dotTex = softCircleTexture(14);
        AudioManager.get().playMenuMusic();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            partX[i] = MathUtils.random(0f, 1280f);
            partY[i] = MathUtils.random(0f, 720f);
            partMaxLife[i] = MathUtils.random(7f, 14f);
            partLife[i] = MathUtils.random(0f, partMaxLife[i]);
            partSize[i] = MathUtils.random(3f, 7f);
        }

        buildUi();
    }

    private void buildUi() {
        TextButtonStyle menuStyle = new TextButtonStyle();
        menuStyle.up = new TextureRegionDrawable(buttonUp);
        menuStyle.over = new TextureRegionDrawable(buttonOver);
        menuStyle.down = new TextureRegionDrawable(buttonDown);
        menuStyle.font = menuFont;
        menuStyle.fontColor = new Color(0.95f, 0.96f, 0.97f, 1f);
        menuStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        menuStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        TextButton single = new TextButton(language.t("SINGLE_PLAYER"), menuStyle);
        TextButton coop = new TextButton(language.t("COOP"), menuStyle);
        TextButton options = new TextButton(language.t("OPTIONS"), menuStyle);
        TextButton exit = new TextButton(language.t("EXIT"), menuStyle);

        setupMenuButtonFx(single);
        setupMenuButtonFx(coop);
        setupMenuButtonFx(options);
        setupMenuButtonFx(exit);
        addStaggeredReveal(single, 0.12f);
        addStaggeredReveal(coop, 0.22f);
        addStaggeredReveal(options, 0.32f);
        addStaggeredReveal(exit, 0.42f);

        single.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.8f);
                triggerGameTransition(single, coop, options, exit);
            }
        });
        coop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.7f);
                Gdx.app.log("Menu", "COOP MODE SOON");
            }
        });
        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.7f);
                game.setScreen(new SettingsScreen(game));
            }
        });
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AudioManager.get().playSelect(0.7f);
                Gdx.app.exit();
            }
        });

        Table menuTable = new Table();
        menuTable.setFillParent(true);
        menuTable.center().padTop(20);
        menuTable.add(single).width(470).height(108).padBottom(16).row();
        menuTable.add(coop).width(470).height(108).padBottom(16).row();
        menuTable.add(options).width(470).height(108).padBottom(16).row();
        menuTable.add(exit).width(470).height(108);
        stage.addActor(menuTable);

        Image settings = new Image(settingsIcon);
        Image profile = new Image(profileIcon);

        settings.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                AudioManager.get().playSelect(0.7f);
                game.setScreen(new SettingsScreen(game));
            }
        });
        profile.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                AudioManager.get().playSelect(0.7f);
                Gdx.app.log("Menu", "PROFILE SOON");
            }
        });
        setupIconHoverFx(settings);
        setupIconHoverFx(profile);
        addStaggeredReveal(settings, 0.16f);
        addStaggeredReveal(profile, 0.22f);

        Table centerIcons = new Table();
        centerIcons.setFillParent(true);
        centerIcons.center().padBottom(690);
        centerIcons.add(settings).width(84).height(84).padRight(14);
        centerIcons.add(profile).width(84).height(84);
        stage.addActor(centerIcons);
    }

    private ChangeListener logChange(String text) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.log("Menu", text);
            }
        };
    }

    private void triggerGameTransition(TextButton single, TextButton coop, TextButton options, TextButton exit) {
        if (startTransition) {
            return;
        }
        startTransition = true;
        single.setDisabled(true);
        coop.setDisabled(true);
        options.setDisabled(true);
        exit.setDisabled(true);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        if (uiAlpha < 1f) {
            uiAlpha = Math.min(1f, uiAlpha + (delta / 0.6f));
        }
        if (startTransition) {
            transitionAlpha = Math.min(1f, transitionAlpha + (delta / 0.45f));
            if (transitionAlpha >= 1f) {
                game.setScreen(new GameScreen(game));
                return;
            }
        }
        updateParticles(delta);
        updateTrail();
        drawBackground();
        stage.act(delta);
        stage.draw();
        drawTrail();
    }

    private void drawTrail() {
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
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

    private void updateParticles(float delta) {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            partLife[i] -= delta;
            if (partLife[i] <= 0f) {
                partX[i] = MathUtils.random(0f, w);
                partY[i] = -10f;
                partMaxLife[i] = MathUtils.random(7f, 14f);
                partLife[i] = partMaxLife[i];
                partSize[i] = MathUtils.random(3f, 7f);
            } else {
                float vy = 14f + ((i * 11) % 17);
                partY[i] += vy * delta;
                partX[i] += MathUtils.sin(elapsed * 0.6f + i * 0.7f) * 14f * delta;
                if (partY[i] > h + 20f) partLife[i] = 0f;
            }
        }
    }

    private void updateTrail() {
        mouseTmp.set(Gdx.input.getX(), Gdx.input.getY());
        stage.screenToStageCoordinates(mouseTmp);
        trailX[trailHead] = mouseTmp.x;
        trailY[trailHead] = mouseTmp.y;
        trailHead = (trailHead + 1) % TRAIL_LEN;
    }

    private void drawBackground() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();

        float bgParallax = (float) Math.sin(elapsed * 0.12f) * 18f;
        float bgZoom = 1f + 0.02f * (float) Math.sin(elapsed * 0.09f);
        float drawW = (w + 72f) * bgZoom;
        float drawH = h * bgZoom;
        float drawX = ((w - drawW) * 0.5f) - bgParallax;
        float drawY = (h - drawH) * 0.5f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, drawX, drawY, drawW, drawH);

        float fogParallax = (float) Math.sin(elapsed * 0.14f) * 10f;
        float fogY = (float) Math.sin(elapsed * 0.19f) * 6f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(midFog, -24f - fogParallax, -6f + fogY, w + 84f, h + 12f);
        batch.setColor(0.08f, 0.08f, 0.08f, 0.92f);
        batch.draw(fg, -32f, -8f, w + 96f, h + 16f);
        batch.setColor(1f, 1f, 1f, 1f);

        float vPulse = 0.23f + 0.06f * (float) Math.sin(elapsed * 0.7f);
        batch.setColor(0f, 0f, 0f, vPulse);
        batch.draw(vignette, 0f, 0f, w, h);
        batch.setColor(0f, 0f, 0f, 0.20f);
        batch.draw(vignette, 0f, 0f, w, h);

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float lt = partLife[i] / partMaxLife[i];
            float pa = (float) Math.sin(lt * Math.PI) * 0.45f;
            if (pa > 0f) {
                batch.setColor(1f, 0.94f, 0.72f, pa);
                float s = partSize[i];
                batch.draw(particleTex, partX[i] - s, partY[i] - s, s * 2f, s * 2f);
            }
        }
        batch.setColor(1f, 1f, 1f, 1f);

        titleLayout.setText(titleFont, "Shadow Flight");
        float titleX = (w - titleLayout.width) * 0.5f;
        float titleY = h - 16f;
        titleFont.getColor().a = uiAlpha;
        titleFont.draw(batch, titleLayout, titleX, titleY);
        titleFont.getColor().a = 1f;


        if (transitionAlpha > 0f) {
            batch.setColor(0f, 0f, 0f, transitionAlpha);
            batch.draw(transitionBlack, 0f, 0f, w, h);
            batch.setColor(1f, 1f, 1f, 1f);
        }

        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
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

    private Texture makeVignette(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        int cx = w / 2;
        int cy = h / 2;
        float maxDist = (float) Math.sqrt((cx * cx) + (cy * cy));
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float t = (float) Math.sqrt((dx * dx) + (dy * dy)) / maxDist;
                float a = Math.min(1f, Math.max(0f, (t - 0.3f) * 1.45f));
                pixmap.setColor(0f, 0f, 0f, a);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture makeMidFog(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        for (int y = 0; y < h; y++) {
            float t = y / (float) (h - 1);
            float alpha = (float) Math.pow(1f - t, 1.65f) * 0.52f;
            pixmap.setColor(0.34f, 0.35f, 0.37f, alpha);
            pixmap.drawLine(0, y, w - 1, y);
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture softCircleTexture(int diameter) {
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
                float a = (1f - t);
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

    private Texture solidTexture(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return t;
    }

    private void setupMenuButtonFx(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                AudioManager.get().playHover();
                button.addAction(Actions.scaleTo(1.02f, 1.02f, 0.14f, Interpolation.smooth));
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, 0.14f, Interpolation.smooth));
            }

            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int buttonCode) {
                spawnRipple(button, x, y);
                button.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.06f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.12f, Interpolation.swingOut)
                ));
                return super.touchDown(event, x, y, pointer, buttonCode);
            }
        });
    }

    private void spawnRipple(TextButton button, float localX, float localY) {
        Vector2 worldPos = new Vector2(localX, localY);
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

    private void setupIconHoverFx(Actor button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                AudioManager.get().playHover();
                button.clearActions();
                button.addAction(Actions.scaleTo(1.08f, 1.08f, 0.14f, Interpolation.smooth));
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.clearActions();
                button.addAction(Actions.scaleTo(1f, 1f, 0.14f, Interpolation.smooth));
            }
        });
    }

    private void addStaggeredReveal(Actor actor, float delay) {
        actor.getColor().a = 0f;
        actor.setScale(0.96f);
        actor.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.parallel(
                        Actions.fadeIn(0.36f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.36f, Interpolation.sineOut)
                )
        ));
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        menuFont.dispose();
        titleFont.dispose();
        iconFont.dispose();
        bg.dispose();
        fg.dispose();
        buttonUp.dispose();
        buttonOver.dispose();
        buttonDown.dispose();
        iconUp.dispose();
        iconOver.dispose();
        settingsIcon.dispose();
        profileIcon.dispose();
        vignette.dispose();
        midFog.dispose();
        transitionBlack.dispose();
        particleTex.dispose();
        dotTex.dispose();
    }
}
