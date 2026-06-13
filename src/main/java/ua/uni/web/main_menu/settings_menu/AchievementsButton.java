package ua.uni.web.main_menu.settings_menu;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.game.MainGame;

import java.util.List;

public class AchievementsButton implements Screen {
    private final MainGame game;
    private Stage stage;
    private Texture bg;
    private Texture card;
    private Texture backBtn;
    private BitmapFont titleFont;
    private BitmapFont cardFont;
    private float elapsed;
    private TextButton resetButton;

    public AchievementsButton(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().enterMenuContext();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        card = stoneCardTexture(260, 190);
        backBtn = roundedRect(220, 86, 30, new Color(0f, 0f, 0f, 0.95f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter pTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTitle.size = 96;
        pTitle.color = Color.WHITE;
        pTitle.borderWidth = 2f;
        pTitle.borderColor = Color.BLACK;
        pTitle.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(pTitle);
        FreeTypeFontGenerator.FreeTypeFontParameter pCard = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pCard.size = 30;
        pCard.color = new Color(1f, 0.86f, 0.36f, 1f);
        pCard.borderWidth = 1.2f;
        pCard.borderColor = Color.BLACK;
        pCard.characters = LanguageButton.FONT_CHARACTERS;
        cardFont = generator.generateFont(pCard);
        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        Label title = new Label(LanguageButton.t("ACHIEVEMENTS"), new Label.LabelStyle(titleFont, Color.WHITE));
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top().center().padTop(18);
        titleTable.add(title);
        stage.addActor(titleTable);

        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtn);
        backStyle.over = new TextureRegionDrawable(backBtn);
        backStyle.down = new TextureRegionDrawable(backBtn);
        backStyle.font = cardFont;
        TextButton back = new TextButton(LanguageButton.t("BACK"), backStyle);
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new SettingsMenu(game));
            }
        });
        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left().padTop(20).padLeft(20);
        backTable.add(back).width(220).height(86);
        stage.addActor(backTable);

        TextButton.TextButtonStyle resetStyle = new TextButton.TextButtonStyle();
        resetStyle.up = new TextureRegionDrawable(backBtn);
        resetStyle.over = new TextureRegionDrawable(backBtn);
        resetStyle.down = new TextureRegionDrawable(backBtn);
        resetStyle.font = cardFont;
        resetButton = new TextButton("RESET", resetStyle);
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                resetAchievements();
            }
        });
        Table resetTable = new Table();
        resetTable.setFillParent(true);
        resetTable.top().right().padTop(20).padRight(20);
        resetTable.add(resetButton).width(220).height(86);
        stage.addActor(resetTable);

        List<ua.uni.achivments.Achievements> all = game.getAchievementManager().getCatalog().getAll();
        Table grid = new Table();
        grid.setFillParent(true);
        grid.center().padTop(160);

        int col = 0;
        for (ua.uni.achivments.Achievements achievement : all) {
            grid.add(buildCard(achievement)).width(260).height(190).pad(10);
            col++;
            if (col == 4) {
                grid.row();
                col = 0;
            }
        }
        stage.addActor(grid);
    }

    private Stack buildCard(ua.uni.achivments.Achievements achievement) {
        boolean unlocked = game.getAchievementManager().isUnlocked(achievement.getCode());
        Image base = new Image(new TextureRegionDrawable(card));
        Label.LabelStyle style = new Label.LabelStyle(cardFont, unlocked
                ? new Color(1f, 0.86f, 0.36f, 1f)
                : new Color(0.58f, 0.58f, 0.58f, 1f));
        Label label = new Label(achievement.getCode() + "\n" + (unlocked ? LanguageButton.t("UNLOCKED") : LanguageButton.t("LOCKED")), style);
        label.setAlignment(com.badlogic.gdx.utils.Align.center);
        Table wrap = new Table();
        wrap.setFillParent(true);
        wrap.center();
        wrap.add(label).center();
        Stack s = new Stack();
        s.add(base);
        s.add(wrap);
        return s;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        AudioManager.get().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SettingsMenu(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetAchievements();
        }
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        float drift = (float) Math.sin(elapsed * 0.10f) * 12f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -20f - drift, 0f, w + 80f, h);
        batch.setColor(0f, 0f, 0f, 0.28f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    private void resetAchievements() {
        game.getAchievementManager().resetAll();
        game.setScreen(new AchievementsButton(game));
    }

    private Texture roundedRect(int w, int h, int r, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        fillRoundedRect(pixmap, 0, 0, w, h, r, color);
        Texture t = new Texture(pixmap);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return t;
    }

    private Texture stoneCardTexture(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        fillRoundedRect(pixmap, 10, 12, w - 14, h - 14, 26, new Color(0f, 0f, 0f, 0.30f));
        fillRoundedRect(pixmap, 4, 0, w - 8, h - 10, 28, new Color(0.08f, 0.09f, 0.10f, 0.98f));
        fillRoundedRect(pixmap, 8, 4, w - 16, h - 18, 24, new Color(0.15f, 0.16f, 0.17f, 1f));
        fillRoundedRect(pixmap, 16, 14, w - 32, h - 34, 18, new Color(0.06f, 0.06f, 0.07f, 0.98f));

        for (int y = 18; y < h - 20; y++) {
            float t = (y - 18f) / Math.max(1f, (h - 38f));
            Color band = new Color(
                    0.18f - (0.07f * t),
                    0.19f - (0.07f * t),
                    0.20f - (0.08f * t),
                    0.18f);
            pixmap.setColor(band);
            pixmap.drawLine(18, y, w - 19, y);
        }

        for (int y = 24; y < h - 26; y += 6) {
            for (int x = 22; x < w - 22; x += 7) {
                int noise = ((x * 31) + (y * 17)) % 13;
                float a = 0.03f + (noise / 13f) * 0.06f;
                float shade = 0.16f + (noise % 4) * 0.03f;
                pixmap.setColor(shade, shade, shade + 0.01f, a);
                pixmap.drawPixel(x, y);
                if (noise % 5 == 0 && x + 1 < w - 22 && y + 1 < h - 26) {
                    pixmap.drawPixel(x + 1, y + 1);
                }
            }
        }

        pixmap.setColor(0.30f, 0.31f, 0.33f, 0.22f);
        pixmap.drawLine(28, h - 32, w - 29, h - 32);
        pixmap.drawLine(24, h - 36, w - 25, h - 36);
        pixmap.setColor(0f, 0f, 0f, 0.26f);
        pixmap.drawLine(24, 28, w - 25, 28);
        pixmap.drawLine(28, 24, w - 29, 24);
        pixmap.setColor(0f, 0f, 0f, 0.18f);
        pixmap.drawRectangle(15, 13, w - 30, h - 28);

        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private void fillRoundedRect(Pixmap pixmap, int x, int y, int w, int h, int r, Color color) {
        pixmap.setColor(color);
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
        AudioManager.get().leaveMenuContext();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        bg.dispose();
        card.dispose();
        backBtn.dispose();
        titleFont.dispose();
        cardFont.dispose();
    }
}
