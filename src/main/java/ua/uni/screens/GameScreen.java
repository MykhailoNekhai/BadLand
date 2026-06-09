package ua.uni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.MainGame;
import ua.uni.levels.PoligonLevel;
import ua.uni.music.LevelPlayScreen;

public class GameScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private Texture bg;
    private Texture levelCard;
    private Texture exitButtonBg;
    private Texture vignette;
    private Texture transitionBlack;
    private BitmapFont titleFont;
    private BitmapFont cardFont;
    private Sound uiHover;
    private Sound uiSelect;
    private Music music;
    private float elapsed;
    private float transitionAlpha;
    private boolean startTransition;
    private int selectedLevel = -1;

    public GameScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        levelCard = makeLevelCardTexture(230, 300);
        exitButtonBg = roundedRect(220, 86, 30, new Color(0f, 0f, 0f, 0.95f));
        vignette = makeVignette(1280, 720);
        transitionBlack = solidTexture(2, 2, Color.BLACK);
        uiHover = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/ui_hover.wav"));
        uiSelect = Gdx.audio.newSound(Gdx.files.internal("game-resourses/audio/ui_select.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("game-resourses/audio/menu_music.mp3"));
        music.setLooping(true);
        music.setVolume(0.62f);
        music.play();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter pTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTitle.size = 110;
        pTitle.color = Color.WHITE;
        pTitle.borderWidth = 2f;
        pTitle.borderColor = new Color(0.1f, 0.1f, 0.1f, 1f);
        titleFont = generator.generateFont(pTitle);
        FreeTypeFontGenerator.FreeTypeFontParameter pCard = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pCard.size = 44;
        pCard.color = new Color(1f, 0.86f, 0.36f, 1f);
        pCard.borderWidth = 1.4f;
        pCard.borderColor = new Color(0f, 0f, 0f, 1f);
        cardFont = generator.generateFont(pCard);

        generator.dispose();

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

        TextButton.TextButtonStyle exitStyle = new TextButton.TextButtonStyle();
        exitStyle.up = new TextureRegionDrawable(exitButtonBg);
        exitStyle.over = new TextureRegionDrawable(exitButtonBg);
        exitStyle.down = new TextureRegionDrawable(exitButtonBg);
        exitStyle.font = cardFont;
        TextButton exit = new TextButton("EXIT", exitStyle);
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                uiSelect.play(0.75f);
                game.setScreen(new MenuScreen(game));
            }
        });
        Table exitTable = new Table();
        exitTable.setFillParent(true);
        exitTable.top().left().padTop(20).padLeft(20);
        exitTable.add(exit).width(220).height(86);
        stage.addActor(exitTable);

        Table grid = new Table();
        grid.setFillParent(true);
        grid.center().padTop(180);

        int level = 1;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                grid.add(buildCard(level)).width(230).height(300).pad(16);
                level++;
            }
            grid.row();
        }
        stage.addActor(grid);
    }

    private Stack buildCard(int level) {
        Image base = new Image(new TextureRegionDrawable(levelCard));
        Label.LabelStyle ls = new Label.LabelStyle(cardFont, cardFont.getColor());
        Label num = new Label(String.format("%02d", level), ls);
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.bottom().padBottom(20);
        overlay.add(num);
        Stack s = new Stack();
        s.add(base);
        s.add(overlay);
        s.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (startTransition) return;
                uiSelect.play(0.85f);
                s.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.05f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.10f, Interpolation.sineOut)
                ));
                selectedLevel = level;
                startTransition = true;
            }

            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (!startTransition) uiHover.play(0.35f);
            }
        });
        return s;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        if (startTransition) {
            transitionAlpha = Math.min(1f, transitionAlpha + (delta / 0.45f));
            if (transitionAlpha >= 1f && selectedLevel > 0) {
                game.setScreen(new PoligonLevel(game));
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

        float bgParallax = (float) Math.sin(elapsed * 0.10f) * 14f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -20f - bgParallax, 0f, w + 80f, h);

        batch.setColor(0f, 0f, 0f, 0.30f);
        batch.draw(vignette, 0f, 0f, w, h);
        if (transitionAlpha > 0f) {
            batch.setColor(0f, 0f, 0f, transitionAlpha);
            batch.draw(transitionBlack, 0f, 0f, w, h);
        }
        batch.setColor(1f, 1f, 1f, 1f);

        batch.end();
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

    private Texture solidTexture(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return t;
    }

    private Texture makeLevelCardTexture(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        // Main elongated black card body.
        pixmap.setColor(0f, 0f, 0f, 0.98f);
        fillRoundedRect(pixmap, 6, 0, w - 12, h - 8, 32);
        pixmap.fillCircle(36, 20, 22);
        pixmap.fillCircle(w - 37, 20, 22);

        // Top preview window area (warm yellow, in upper half).
        int previewBottom = h - 176;
        int previewTop = h - 24;
        for (int y = previewBottom; y < previewTop; y++) {
            float t = (y - previewBottom) / (float) (previewTop - previewBottom);
            float r = 0.95f - (0.10f * t);
            float g = 0.84f - (0.08f * t);
            float b = 0.42f - (0.16f * t);
            pixmap.setColor(r, g, b, 1f);
            pixmap.drawLine(18, y, w - 19, y);
        }

        // Simple dark silhouettes inside preview.
        pixmap.setColor(0f, 0f, 0f, 0.34f);
        pixmap.fillCircle(42, previewBottom + 20, 22);
        pixmap.fillCircle(w - 45, previewTop - 18, 18);
        pixmap.fillRectangle(18, previewBottom + 30, w - 36, 4);
        pixmap.fillRectangle(18, previewBottom + 68, w - 36, 5);

        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
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
        if (music != null) music.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        bg.dispose();
        levelCard.dispose();
        exitButtonBg.dispose();
        vignette.dispose();
        transitionBlack.dispose();
        titleFont.dispose();
        cardFont.dispose();
        if (uiHover != null) uiHover.dispose();
        if (uiSelect != null) uiSelect.dispose();
        if (music != null) music.dispose();
    }
}
