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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.MainGame;

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
                Gdx.files.internal("assets/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 86;
        p.color = new Color(0.95f, 0.96f, 0.98f, 1f);
        p.borderWidth = 2.2f;
        p.borderColor = new Color(0.10f, 0.12f, 0.16f, 1f);
        p.shadowOffsetX = 0;
        p.shadowOffsetY = 5;
        p.shadowColor = new Color(0f, 0f, 0f, 0.62f);
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

        bg = new Texture(Gdx.files.internal("assets/menu/2b3aa97f-1bf0-480c-8a7f-055b791148a9.png"));
        fg = new Texture(Gdx.files.internal("assets/menu/mainmenu_fg_generated.png"));
        buttonUp = roundedRect(470, 108, 46, new Color(0f, 0f, 0f, 1f));
        buttonOver = roundedRect(470, 108, 46, new Color(0f, 0f, 0f, 1f));
        buttonDown = roundedRect(470, 108, 46, new Color(0f, 0f, 0f, 1f));
        iconUp = roundedRect(84, 84, 42, new Color(0.04f, 0.04f, 0.05f, 0.90f));
        iconOver = roundedRect(84, 84, 42, new Color(0.10f, 0.10f, 0.12f, 0.96f));
        settingsIcon = new Texture(Gdx.files.internal("assets/menu/gear.png"));
        profileIcon = new Texture(Gdx.files.internal("assets/menu/user-profile.png"));
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

        buildUi();
    }

    private void buildUi() {
        TextButtonStyle menuStyle = new TextButtonStyle();
        menuStyle.up = new TextureRegionDrawable(buttonUp);
        menuStyle.over = new TextureRegionDrawable(buttonOver);
        menuStyle.down = new TextureRegionDrawable(buttonDown);
        menuStyle.font = menuFont;
        menuStyle.fontColor = new Color(0.95f, 0.96f, 0.97f, 1f);
        menuStyle.overFontColor = Color.WHITE;
        menuStyle.downFontColor = new Color(0.80f, 0.84f, 0.90f, 1f);

        TextButton single = new TextButton("SINGLE PLAYER", menuStyle);
        TextButton coop = new TextButton("COOP", menuStyle);
        TextButton options = new TextButton("OPTIONS", menuStyle);
        TextButton exit = new TextButton("EXIT", menuStyle);

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
                triggerGameTransition(single, coop, options, exit);
            }
        });
        coop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                triggerGameTransition(single, coop, options, exit);
            }
        });
        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                triggerGameTransition(single, coop, options, exit);
            }
        });
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
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

        ImageButton.ImageButtonStyle settingsStyle = new ImageButton.ImageButtonStyle();
        settingsStyle.up = new TextureRegionDrawable(settingsIcon);
        settingsStyle.over = new TextureRegionDrawable(settingsIcon);
        settingsStyle.down = new TextureRegionDrawable(settingsIcon);
        ImageButton settings = new ImageButton(settingsStyle);

        ImageButton.ImageButtonStyle profileStyle = new ImageButton.ImageButtonStyle();
        profileStyle.up = new TextureRegionDrawable(profileIcon);
        profileStyle.over = new TextureRegionDrawable(profileIcon);
        profileStyle.down = new TextureRegionDrawable(profileIcon);
        ImageButton profile = new ImageButton(profileStyle);

        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                triggerGameTransition(single, coop, options, exit);
            }
        });
        profile.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                triggerGameTransition(single, coop, options, exit);
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
        drawBackground();
        stage.act(delta);
        stage.draw();
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

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(vignette, 0f, 0f, w, h);
        batch.setColor(0f, 0f, 0f, 0.20f);
        batch.draw(vignette, 0f, 0f, w, h);

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
                button.clearActions();
                button.addAction(Actions.parallel(
                        Actions.color(new Color(1f, 1f, 1f, 1f), 0.14f),
                        Actions.scaleTo(1.02f, 1.02f, 0.14f, Interpolation.smooth)
                ));
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.clearActions();
                button.addAction(Actions.parallel(
                        Actions.color(new Color(1f, 1f, 1f, 1f), 0.14f),
                        Actions.scaleTo(1f, 1f, 0.14f, Interpolation.smooth)
                ));
            }

            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int buttonCode) {
                button.clearActions();
                button.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.06f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.12f, Interpolation.swingOut)
                ));
                return super.touchDown(event, x, y, pointer, buttonCode);
            }
        });
    }

    private void setupIconHoverFx(ImageButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
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
    }
}
