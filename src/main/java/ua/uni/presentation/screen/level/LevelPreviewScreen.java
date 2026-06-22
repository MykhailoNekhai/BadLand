package ua.uni.presentation.screen.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.bootstrap.GameServices;
import ua.uni.presentation.screen.menu.singleplayer.SinglePlayerMenu;

public class LevelPreviewScreen implements Screen {
    private final GameServices services;
    private final int level;
    private Stage stage;
    private Texture bg;

    public LevelPreviewScreen(GameServices services, int level) {
        this.services = services;
        this.level = level;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().startLevelMusic();

        bg = solidTexture(2, 2, new Color(0.03f, 0.03f, 0.04f, 1f));
    }

    @Override
    public void render(float delta) {
        AudioManager.get().updateLevelAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            services.setScreen(new SinglePlayerMenu(services));
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
        AudioManager.get().stopLevelMusic();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        bg.dispose();
    }
}
