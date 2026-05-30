package ua.uni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.MainGame;
import ua.uni.logging.AppLogger;

public class SettingsScreen implements Screen {
    private final MainGame game;
    private Stage stage;

    private Texture bg;
    private Texture panel;
    private Texture line;
    private Texture knob;
    private Texture backBtn;
    private Texture edgeGlow;

    private BitmapFont itemFont;
    private BitmapFont backFont;
    private BitmapFont smallFont;

    private boolean soundsOn = true;
    private float elapsed;

    private Label soundsLabel;
    private Table soundsKnobTable;

    public SettingsScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        panel = organicPanel(620, 980, new Color(0.02f, 0.03f, 0.05f, 0.95f));
        edgeGlow = edgeGlowTexture(620, 980);
        line = roundedRect(390, 12, 6, new Color(0.96f, 0.90f, 0.55f, 0.96f));
        knob = circleTexture(42, new Color(1f, 0.93f, 0.62f, 1f));
        backBtn = roundedRect(180, 72, 24, new Color(0f, 0f, 0f, 0.92f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter items = new FreeTypeFontGenerator.FreeTypeFontParameter();
        items.size = 62;
        items.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        items.borderWidth = 1.6f;
        items.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        itemFont = generator.generateFont(items);

        FreeTypeFontGenerator.FreeTypeFontParameter back = new FreeTypeFontGenerator.FreeTypeFontParameter();
        back.size = 46;
        back.color = Color.WHITE;
        back.borderWidth = 1.2f;
        back.borderColor = Color.BLACK;
        backFont = generator.generateFont(back);
        FreeTypeFontGenerator.FreeTypeFontParameter small = new FreeTypeFontGenerator.FreeTypeFontParameter();
        small.size = 34;
        small.color = Color.WHITE;
        small.borderWidth = 1.0f;
        small.borderColor = Color.BLACK;
        smallFont = generator.generateFont(small);

        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        Table panelContent = new Table();
        panelContent.setBackground(new TextureRegionDrawable(panel));
        panelContent.defaults().center().padLeft(36).padRight(36);

        Label.LabelStyle ls = new Label.LabelStyle(itemFont, itemFont.getColor());

        soundsLabel = new Label("SOUNDS", ls);
        Label statistics = new Label("STATISTICS", ls);
        Label achievements = new Label("ACHIEVEMENTS", ls);
        Label language = new Label("LANGUAGE", ls);
        Label credits = new Label("CREDITS", ls);
        Label graphics = new Label("GRAPHICS", ls);
        Label keybind = new Label("KEYBOARD BINDINGS", ls);

        Table soundsRow = new Table();
        soundsRow.center();
        soundsRow.add(soundsLabel).center().padBottom(8).row();

        Table slider = new Table();
        slider.setBackground(new TextureRegionDrawable(line));

        soundsKnobTable = new Table();
        refreshSoundsKnob();

        Table sliderWrap = new Table();
        sliderWrap.add(slider).width(390).height(12).center();
        sliderWrap.row();
        sliderWrap.add(soundsKnobTable).width(390).height(48).center();
        soundsRow.add(sliderWrap).center();

        panelContent.add(soundsRow).width(560).padTop(52).padBottom(16).row();
        panelContent.add(statistics).padBottom(10).row();
        panelContent.add(achievements).padBottom(10).row();
        panelContent.add(language).padBottom(10).row();
        panelContent.add(credits).padBottom(10).row();
        panelContent.add(graphics).padBottom(10).row();
        panelContent.add(keybind).padBottom(30).row();

        Table panelWrap = new Table();
        panelWrap.setFillParent(true);
        panelWrap.center();
        Stack panelStack = new Stack();
        panelStack.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(new TextureRegionDrawable(edgeGlow)));
        panelStack.add(panelContent);
        panelWrap.add(panelStack).width(620).height(980);

        stage.addActor(panelWrap);

        TextButtonStyle backStyle = new TextButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtn);
        backStyle.down = new TextureRegionDrawable(backBtn);
        backStyle.over = new TextureRegionDrawable(backBtn);
        backStyle.font = backFont;
        TextButton back = new TextButton("BACK", backStyle);
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });

        TextButton soundsHitArea = new TextButton("", backStyle);
        soundsHitArea.getLabel().setVisible(false);
        soundsHitArea.setColor(1f, 1f, 1f, 0f);
        soundsHitArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundsOn = !soundsOn;
                refreshSoundsKnob();
            }
        });

        Table hitTable = new Table();
        hitTable.setFillParent(true);
        hitTable.center().padTop(332);
        hitTable.add(soundsHitArea).width(420).height(104);
        stage.addActor(hitTable);

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left().padTop(20).padLeft(20);
        backTable.add(back).width(180).height(72);
        stage.addActor(backTable);

        TextButton.TextButtonStyle logoutStyle = new TextButton.TextButtonStyle();
        logoutStyle.up = new TextureRegionDrawable(backBtn);
        logoutStyle.down = new TextureRegionDrawable(backBtn);
        logoutStyle.over = new TextureRegionDrawable(backBtn);
        logoutStyle.font = smallFont;
        TextButton logout = new TextButton("LOG OUT", logoutStyle);
        logout.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AppLogger.info("Auth", "Logout");
                game.getSessionManager().clear();
                game.setScreen(new LoginScreen(game));
            }
        });
        Table logoutTable = new Table();
        logoutTable.setFillParent(true);
        logoutTable.bottom().right().padBottom(24).padRight(24);
        logoutTable.add(logout).width(200).height(72);
        stage.addActor(logoutTable);
    }

    private void refreshSoundsKnob() {
        soundsKnobTable.clearChildren();
        if (soundsOn) {
            soundsKnobTable.add().expandX();
            soundsKnobTable.add(new Label("", new Label.LabelStyle(itemFont, Color.WHITE))).width(0);
            soundsKnobTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(new TextureRegionDrawable(knob))).size(42, 42).padTop(-20).right();
        } else {
            soundsKnobTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(new TextureRegionDrawable(knob))).size(42, 42).padTop(-20).left();
            soundsKnobTable.add(new Label("", new Label.LabelStyle(itemFont, Color.WHITE))).width(0);
            soundsKnobTable.add().expandX();
        }
        soundsLabel.setColor(soundsOn ? new Color(0.99f, 0.95f, 0.86f, 1f) : new Color(0.60f, 0.57f, 0.50f, 1f));
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        float drift = (float) Math.sin(elapsed * 0.12f) * 10f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -18f - drift, 0f, w + 36f, h);
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 0.85f, 0.35f, 0.08f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        stage.act(delta);
        stage.draw();
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

    private Texture circleTexture(int diameter, Color color) {
        Pixmap p = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        p.setColor(color);
        p.fillCircle(diameter / 2, diameter / 2, diameter / 2 - 2);
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture organicPanel(int w, int h, Color color) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        p.setColor(color);

        fillRoundedRect(p, 18, 12, w - 36, h - 24, 48);
        p.fillTriangle(18, h - 64, 52, h - 20, 104, h - 60);
        p.fillTriangle(w - 18, h - 58, w - 56, h - 18, w - 112, h - 54);
        p.fillTriangle(18, 56, 8, 18, 42, 24);
        p.fillTriangle(w - 18, 50, w - 8, 18, w - 46, 26);

        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture edgeGlowTexture(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int cx = w / 2;
        int cy = h / 2;
        float max = (float) Math.sqrt((cx * cx) + (cy * cy));
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float d = (float) Math.sqrt((dx * dx) + (dy * dy)) / max;
                float edge = Math.max(0f, Math.min(1f, (d - 0.68f) * 2.8f));
                if (edge > 0f) {
                    p.setColor(1f, 0.83f, 0.28f, edge * 0.26f);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private void fillRoundedRect(Pixmap pixmap, int x, int y, int w, int h, int r) {
        pixmap.fillRectangle(x + r, y, w - (r * 2), h);
        pixmap.fillRectangle(x, y + r, w, h - (r * 2));
        pixmap.fillCircle(x + r, y + r, r);
        pixmap.fillCircle(x + w - r - 1, y + r, r);
        pixmap.fillCircle(x + r, y + h - r - 1, r);
        pixmap.fillCircle(x + w - r - 1, y + h - r - 1, r);
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
        bg.dispose();
        panel.dispose();
        edgeGlow.dispose();
        line.dispose();
        knob.dispose();
        backBtn.dispose();
        itemFont.dispose();
        backFont.dispose();
        smallFont.dispose();
    }
}
