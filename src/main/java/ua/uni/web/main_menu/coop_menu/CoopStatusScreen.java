package ua.uni.web.main_menu.coop_menu;

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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.game.MainGame;
import ua.uni.web.main_menu.Menu;
import ua.uni.web.main_menu.settings_menu.LanguageButton;

public class CoopStatusScreen implements Screen {
    private final MainGame game;
    private final String title;
    private final String message;
    private Stage stage;
    private Texture bg;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;

    public CoopStatusScreen(MainGame game, String title, String message) {
        this.game = game;
        this.title = title;
        this.message = message;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        bg = solidTexture(2, 2, new Color(0.02f, 0.02f, 0.03f, 1f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 88;
        titleParams.color = Color.WHITE;
        titleParams.borderWidth = 2f;
        titleParams.borderColor = Color.BLACK;
        titleParams.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter bodyParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bodyParams.size = 34;
        bodyParams.color = new Color(0.92f, 0.92f, 0.88f, 1f);
        bodyParams.characters = LanguageButton.FONT_CHARACTERS;
        bodyFont = generator.generateFont(bodyParams);
        generator.dispose();

        Label titleLabel = new Label(title, new Label.LabelStyle(titleFont, Color.WHITE));
        Label bodyLabel = new Label(message + "\n\n" + LanguageButton.t("PRESS_ENTER_ESC_RETURN"), new Label.LabelStyle(bodyFont, Color.WHITE));
        bodyLabel.setWrap(true);
        bodyLabel.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titleLabel).padBottom(20).row();
        table.add(bodyLabel).width(820f);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new Menu(game));
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
        if (stage != null) stage.dispose();
        if (bg != null) bg.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
    }

    private Texture solidTexture(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }
}
