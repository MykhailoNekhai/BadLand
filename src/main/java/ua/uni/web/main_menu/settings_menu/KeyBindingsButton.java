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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.config.GameSettings;
import ua.uni.game.MainGame;

public class KeyBindingsButton implements Screen {
    private final MainGame game;
    private Stage stage;
    private Texture bg;
    private Texture rowBtn;
    private Texture rowBtnDim;
    private Texture backBtn;
    private BitmapFont font;
    private BitmapFont smallFont;
    private BitmapFont titleFont;
    private float elapsed;

    private int moveLeft;
    private int moveRight;
    private int moveUp;
    private int moveDown;

    private TextButton leftBtn;
    private TextButton rightBtn;
    private TextButton upBtn;
    private TextButton downBtn;

    public KeyBindingsButton(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().playMenuMusic();

        moveLeft = GameSettings.getMoveLeft();
        moveRight = GameSettings.getMoveRight();
        moveUp = GameSettings.getMoveUp();
        moveDown = GameSettings.getMoveDown();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        rowBtn = roundedRect(760, 84, 22, new Color(0f, 0f, 0f, 0.85f));
        rowBtnDim = roundedRect(760, 84, 22, new Color(0f, 0f, 0f, 0.55f));
        backBtn = roundedRect(220, 86, 30, new Color(0f, 0f, 0f, 0.95f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 38;
        p.color = new Color(1f, 0.86f, 0.36f, 1f);
        p.borderWidth = 1.2f;
        p.borderColor = Color.BLACK;
        p.characters = LanguageButton.FONT_CHARACTERS;
        font = generator.generateFont(p);

        FreeTypeFontGenerator.FreeTypeFontParameter ps = new FreeTypeFontGenerator.FreeTypeFontParameter();
        ps.size = 28;
        ps.color = new Color(0.75f, 0.75f, 0.75f, 1f);
        ps.borderWidth = 1.0f;
        ps.borderColor = Color.BLACK;
        ps.characters = LanguageButton.FONT_CHARACTERS;
        smallFont = generator.generateFont(ps);

        FreeTypeFontGenerator.FreeTypeFontParameter pt = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pt.size = 80;
        pt.color = Color.WHITE;
        pt.borderWidth = 2f;
        pt.borderColor = Color.BLACK;
        pt.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(pt);
        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        TextButton.TextButtonStyle activeStyle = new TextButton.TextButtonStyle();
        activeStyle.up = new TextureRegionDrawable(rowBtn);
        activeStyle.down = new TextureRegionDrawable(rowBtn);
        activeStyle.over = new TextureRegionDrawable(rowBtn);
        activeStyle.font = font;

        TextButton.TextButtonStyle futureStyle = new TextButton.TextButtonStyle();
        futureStyle.up = new TextureRegionDrawable(rowBtnDim);
        futureStyle.down = new TextureRegionDrawable(rowBtnDim);
        futureStyle.over = new TextureRegionDrawable(rowBtnDim);
        futureStyle.font = smallFont;

        leftBtn = new TextButton("", activeStyle);
        rightBtn = new TextButton("", activeStyle);
        upBtn = new TextButton("", activeStyle);
        downBtn = new TextButton("", activeStyle);

        leftBtn.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    toggleMoveLeft();
                                    refreshBindingLabels();
                                }
                            }
        );
        rightBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleMoveRight();
                refreshBindingLabels();
            }
        });
        upBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleMoveUp();
                refreshBindingLabels();
            }
        });
        downBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleMoveDown();
                refreshBindingLabels();
            }
        });
        refreshBindingLabels();


        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top().center().padTop(14);
        titleTable.add(new Label(LanguageButton.t("KEY_BINDINGS"),
                new Label.LabelStyle(titleFont, Color.WHITE)));
        stage.addActor(titleTable);

        Table rows = new Table();
        rows.top();
        rows.add(leftBtn).width(760).height(84).padBottom(10).row();
        rows.add(rightBtn).width(760).height(84).padBottom(10).row();
        rows.add(upBtn).width(760).height(84).padBottom(10).row();
        rows.add(downBtn).width(760).height(84).padBottom(18).row();

        ScrollPane scroll = new ScrollPane(rows);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);

        Table scrollWrap = new Table();
        scrollWrap.setFillParent(true);
        scrollWrap.center().padTop(110).padBottom(40);
        scrollWrap.add(scroll).width(800).expandY().fillY();
        stage.addActor(scrollWrap);

        TextButton.TextButtonStyle backStyle = new TextButton.TextButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtn);
        backStyle.over = new TextureRegionDrawable(backBtn);
        backStyle.down = new TextureRegionDrawable(backBtn);
        backStyle.font = font;
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
    }

    private void refreshBindingLabels() {
        String hint = "(" + LanguageButton.t("CLICK_TO_TOGGLE") + ")";
        leftBtn.setText(LanguageButton.t("MOVE_LEFT") + ": " + Input.Keys.toString(moveLeft) + "   " + hint);
        rightBtn.setText(LanguageButton.t("MOVE_RIGHT") + ": " + Input.Keys.toString(moveRight) + "   " + hint);
        upBtn.setText(LanguageButton.t("JUMP") + ": " + Input.Keys.toString(moveUp) + "   " + hint);
        downBtn.setText(LanguageButton.t("INTERACT") + ": " + Input.Keys.toString(moveDown) + "   " + hint);
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SettingsMenu(game));
            return;
        }
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        float drift = (float) Math.sin(elapsed * 0.10f) * 12f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -20f - drift, 0f, w + 80f, h);
        batch.setColor(0f, 0f, 0f, 0.30f);
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

    private void toggleMoveLeft() {
        if (moveLeft == Input.Keys.A) {
            moveLeft = Input.Keys.LEFT;
        } else {
            moveLeft = Input.Keys.A;
        }
        GameSettings.setMoveLeft(moveLeft);
        refreshBindingLabels();
    }

    private void toggleMoveRight() {
        if (moveRight == Input.Keys.D) {
            moveRight = Input.Keys.RIGHT;
        } else {
            moveRight = Input.Keys.D;
        }
        GameSettings.setMoveRight(moveRight);
    }

    private void toggleMoveUp() {
        if (moveUp == Input.Keys.W) {
            moveUp = Input.Keys.UP;
        } else {
            moveUp = Input.Keys.W;
        }
        GameSettings.setMoveUp(moveUp);
    }

    private void toggleMoveDown() {
        if (moveDown == Input.Keys.S) {
            moveDown = Input.Keys.DOWN;
        } else {
            moveDown = Input.Keys.S;
        }
        GameSettings.setMoveDown(moveDown);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        bg.dispose();
        rowBtn.dispose();
        rowBtnDim.dispose();
        backBtn.dispose();
        font.dispose();
        smallFont.dispose();
        titleFont.dispose();
    }
}
