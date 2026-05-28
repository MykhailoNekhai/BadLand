package ua.uni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.MainGame;

public class MenuScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private BitmapFont menuFont;
    private BitmapFont iconFont;

    private Texture bg;
    private Texture buttonUp;
    private Texture buttonOver;
    private Texture buttonDown;
    private Texture iconUp;
    private Texture iconOver;
    private Texture settingsIcon;
    private Texture profileIcon;
    private Texture vignette;

    private float elapsed;

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
        generator.dispose();
        iconFont = new BitmapFont();
        iconFont.getData().setScale(1.45f);

        bg = new Texture(Gdx.files.internal("assets/menu/2b3aa97f-1bf0-480c-8a7f-055b791148a9.png"));
        buttonUp = roundedRect(470, 108, 46, new Color(0.04f, 0.04f, 0.05f, 0.88f));
        buttonOver = roundedRect(470, 108, 46, new Color(0.10f, 0.10f, 0.12f, 0.94f));
        buttonDown = roundedRect(470, 108, 46, new Color(0.02f, 0.02f, 0.03f, 0.98f));
        iconUp = roundedRect(84, 84, 42, new Color(0.04f, 0.04f, 0.05f, 0.90f));
        iconOver = roundedRect(84, 84, 42, new Color(0.10f, 0.10f, 0.12f, 0.96f));
        settingsIcon = new Texture(Gdx.files.internal("assets/menu/gear.png"));
        profileIcon = new Texture(Gdx.files.internal("assets/menu/user-profile.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        buttonUp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        buttonOver.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        buttonDown.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        iconUp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        iconOver.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        settingsIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        profileIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        vignette = makeVignette(1280, 720);

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

        single.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        coop.addListener(logChange("COOP MODE SOON"));
        options.addListener(logChange("SETTINGS SOON"));
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.exit();
            }
        });

        Table menuTable = new Table();
        menuTable.setFillParent(true);
        menuTable.left().top().padLeft(120).padTop(210);
        menuTable.add(single).width(470).height(108).left().padBottom(16).row();
        menuTable.add(coop).width(470).height(108).left().padBottom(16).row();
        menuTable.add(options).width(470).height(108).left().padBottom(16).row();
        menuTable.add(exit).width(470).height(108).left();
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

        settings.addListener(logChange("SETTINGS SOON"));
        profile.addListener(logChange("PROFILE SOON"));

        Table topIcons = new Table();
        topIcons.setFillParent(true);
        topIcons.top().left().padLeft(56).padTop(48);
        topIcons.add(settings).width(84).height(84).padRight(12);
        topIcons.add(profile).width(84).height(84);
        stage.addActor(topIcons);
    }

    private ChangeListener logChange(String text) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.log("Menu", text);
            }
        };
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
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
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -18f - bgParallax, 0f, w + 72f, h);

        batch.setColor(0f, 0f, 0f, 0.25f);
        batch.draw(vignette, 0f, 0f, w, h);
        batch.setColor(0f, 0f, 0f, 0.20f);
        batch.draw(vignette, 0f, 0f, w, h);

        // No extra left-side darkening: only global vignette remains.
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
        iconFont.dispose();
        bg.dispose();
        buttonUp.dispose();
        buttonOver.dispose();
        buttonDown.dispose();
        iconUp.dispose();
        iconOver.dispose();
        settingsIcon.dispose();
        profileIcon.dispose();
        vignette.dispose();
    }
}
