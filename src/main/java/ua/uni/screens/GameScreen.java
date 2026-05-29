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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.MainGame;

public class GameScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private Texture bg;
    private Texture cardBody;
    private Texture[] levelThumbs;
    private Texture vignette;
    private BitmapFont titleFont;
    private BitmapFont levelFont;
    private BitmapFont nameFont;
    private float elapsed;

    public GameScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        bg = new Texture(Gdx.files.internal("assets/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        cardBody = splitPanelCard(276, 252, new Color(0f, 0f, 0f, 0.96f));
        levelThumbs = new Texture[10];
        vignette = makeVignette(1280, 720);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("assets/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter pTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTitle.size = 110;
        pTitle.color = Color.WHITE;
        pTitle.borderWidth = 2f;
        pTitle.borderColor = new Color(0.1f, 0.1f, 0.1f, 1f);
        titleFont = generator.generateFont(pTitle);

        FreeTypeFontGenerator.FreeTypeFontParameter pLevel = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pLevel.size = 56;
        pLevel.color = Color.WHITE;
        pLevel.borderWidth = 1.8f;
        pLevel.borderColor = new Color(0.1f, 0.1f, 0.1f, 1f);
        levelFont = generator.generateFont(pLevel);

        FreeTypeFontGenerator.FreeTypeFontParameter pName = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pName.size = 48;
        pName.color = Color.WHITE;
        pName.borderWidth = 1.6f;
        pName.borderColor = new Color(0.08f, 0.08f, 0.08f, 1f);
        nameFont = generator.generateFont(pName);
        generator.dispose();

        for (int i = 0; i < levelThumbs.length; i++) {
            levelThumbs[i] = makeYellowThumb(i);
        }

        buildUi();
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label("LEVELS", titleStyle);

        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top().center().padTop(16);
        titleTable.add(title);
        stage.addActor(titleTable);

        Table grid = new Table();
        grid.setFillParent(true);
        grid.center().padTop(40);

        int level = 1;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                boolean unlocked = level <= 2;
                Stack card = buildLevelCard(level, unlocked);
                grid.add(card).width(276).height(252).pad(18);
                level++;
            }
            grid.row();
        }
        stage.addActor(grid);
    }

    private Stack buildLevelCard(int levelNum, boolean unlocked) {
        Texture thumb = levelThumbs[levelNum - 1];
        String[] names = {"SPIRAL", "SUNROOT", "EMBER", "GLOW", "DUNE", "VINES", "MIRAGE", "NEST", "BURST", "BLOOM"};

        Image body = new Image(new TextureRegionDrawable(cardBody));
        Image topShot = new Image(new TextureRegionDrawable(thumb));

        Label.LabelStyle nameStyle = new Label.LabelStyle(nameFont, unlocked ? Color.WHITE : new Color(0.80f, 0.80f, 0.80f, 1f));
        Label nameLabel = new Label(names[levelNum - 1], nameStyle);
        Label.LabelStyle levelStyle = new Label.LabelStyle(levelFont, Color.WHITE);
        Label levelLabel = new Label(unlocked ? String.valueOf(levelNum) : "LOCK", levelStyle);

        Table inside = new Table();
        inside.setFillParent(true);
        inside.top().padTop(16);
        inside.add(topShot).width(242).height(138).padBottom(8).row();
        inside.add(nameLabel).padBottom(2).row();
        inside.add(levelLabel);

        Stack stack = new Stack();
        stack.add(body);
        stack.add(inside);
        return stack;
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

        float bgParallax = (float) Math.sin(elapsed * 0.10f) * 14f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -20f - bgParallax, 0f, w + 80f, h);

        batch.setColor(0f, 0f, 0f, 0.30f);
        batch.draw(vignette, 0f, 0f, w, h);
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

    private Texture splitPanelCard(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(color);

        // Top and bottom panels (variant 05 split panel).
        fillRoundedRect(pixmap, 14, 84, w - 28, h - 92, 34);
        fillRoundedRect(pixmap, 8, 0, w - 16, 118, 46);
        pixmap.fillCircle(58, 30, 34);
        pixmap.fillCircle(w - 59, 30, 35);

        // Irregular divider band between top and bottom panels.
        pixmap.fillTriangle(24, 104, w / 2 - 16, 118, 24, 84);
        pixmap.fillTriangle(w - 24, 106, w / 2 + 22, 120, w - 24, 86);
        fillRoundedRect(pixmap, 24, 84, w - 48, 32, 16);

        Texture t = new Texture(pixmap);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
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
                float a = Math.min(1f, Math.max(0f, (t - 0.28f) * 1.4f));
                pixmap.setColor(0f, 0f, 0f, a);
                pixmap.drawPixel(x, y);
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture makeYellowThumb(int index) {
        Color[] palette = new Color[] {
                new Color(0.98f, 0.90f, 0.47f, 1f),
                new Color(0.97f, 0.84f, 0.38f, 1f),
                new Color(0.94f, 0.78f, 0.34f, 1f),
                new Color(0.96f, 0.87f, 0.42f, 1f),
                new Color(0.92f, 0.74f, 0.31f, 1f),
                new Color(0.99f, 0.86f, 0.36f, 1f),
                new Color(0.95f, 0.81f, 0.29f, 1f),
                new Color(0.97f, 0.88f, 0.52f, 1f),
                new Color(0.93f, 0.76f, 0.33f, 1f),
                new Color(0.98f, 0.83f, 0.41f, 1f)
        };

        Pixmap p = new Pixmap(242, 138, Pixmap.Format.RGBA8888);
        Color c = palette[index % palette.length];
        for (int y = 0; y < 138; y++) {
            float t = y / 137f;
            float r = c.r * (0.9f + (0.14f * (1f - t)));
            float g = c.g * (0.92f + (0.12f * (1f - t)));
            float b = c.b * (0.86f + (0.10f * (1f - t)));
            p.setColor(r, g, b, 1f);
            p.drawLine(0, y, 241, y);
        }

        p.setColor(0f, 0f, 0f, 0.28f);
        p.fillCircle(38 + (index * 11) % 160, 120, 42);
        p.fillCircle(170 + (index * 7) % 52, 18, 34);
        p.fillRectangle(0, 18 + (index % 5) * 8, 242, 4);
        p.fillRectangle(0, 88 + (index % 4) * 7, 242, 5);

        Texture raw = new Texture(p);
        raw.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return raw;
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
        cardBody.dispose();
        for (Texture thumb : levelThumbs) {
            thumb.dispose();
        }
        vignette.dispose();
        titleFont.dispose();
        levelFont.dispose();
        nameFont.dispose();
    }
}
