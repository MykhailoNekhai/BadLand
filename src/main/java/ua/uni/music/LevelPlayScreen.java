package ua.uni.music;

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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.MainGame;
import ua.uni.screens.GameScreen;

public class LevelPlayScreen implements Screen {
    private final MainGame game;
    private final int level;
    private Stage stage;
    private Texture bg;
    private BitmapFont font;

    public LevelPlayScreen(MainGame game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        bg = solidTexture(2, 2, new Color(0.03f, 0.03f, 0.04f, 1f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 82;
        p.color = Color.WHITE;
        p.borderWidth = 2f;
        p.borderColor = Color.BLACK;
        font = generator.generateFont(p);
        generator.dispose();

        Label label = new Label("LEVEL " + String.format("%02d", level), new Label.LabelStyle(font, Color.WHITE));
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(label);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new GameScreen(game));
            return;
        }
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, 0f, 0f, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        batch.end();
        stage.act(delta);
        stage.draw();
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
        font.dispose();
    }
}
