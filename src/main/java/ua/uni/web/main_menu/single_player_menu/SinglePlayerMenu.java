package ua.uni.web.main_menu.single_player_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.game.MainGame;
import ua.uni.levels.PoligonLevel;
import ua.uni.levels.RuinsLevel;
import ua.uni.audio.music.Menu;
import ua.uni.web.main_menu.settings_menu.LanguageButton;

public class SinglePlayerMenu implements Screen {
    private static final int LEVELS_PER_PAGE = 10;

    private final MainGame game;
    private Stage stage;
    private Texture bg;
    private Texture levelCard;
    private Texture pageButtonBg;
    private Texture pageButtonActiveBg;
    private Texture vignette;
    private Texture transitionBlack;
    private BitmapFont titleFont;
    private BitmapFont cardFont;
    private Label titleLabel;
    private Table pagerTable;
    private Table pageOneGrid;
    private Table pageTwoGrid;
    private TextButton pageOneButton;
    private TextButton pageTwoButton;
    private int currentPage = 0;
    private float elapsed;
    private float transitionAlpha;
    private boolean startTransition;
    private int selectedLevel = -1;

    public SinglePlayerMenu(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        AudioManager.get().enterMenuContext();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        levelCard = makeLevelCardTexture(230, 300);
        pageButtonBg = roundedRect(84, 64, 18, new Color(0f, 0f, 0f, 1f));
        pageButtonActiveBg = roundedRect(84, 64, 18, new Color(0f, 0f, 0f, 1f));
        vignette = makeVignette(1280, 720);
        transitionBlack = solidTexture(2, 2, Color.BLACK);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter pTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTitle.size = 88;
        pTitle.color = Color.BLACK;
        pTitle.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(pTitle);
        FreeTypeFontGenerator.FreeTypeFontParameter pCard = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pCard.size = 44;
        pCard.color = new Color(1f, 0.86f, 0.36f, 1f);
        pCard.borderWidth = 1.4f;
        pCard.borderColor = new Color(0f, 0f, 0f, 1f);
        pCard.characters = LanguageButton.FONT_CHARACTERS;
        cardFont = generator.generateFont(pCard);

        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        titleLabel = new Label(LanguageButton.t("LEVELS"), titleStyle);
        positionTitle();
        stage.addActor(titleLabel);

        pageOneGrid = buildLevelPage(1);
        pageTwoGrid = buildLevelPage(11);
        pageTwoGrid.setVisible(false);

        Stack pagesStack = new Stack();
        pagesStack.add(pageOneGrid);
        pagesStack.add(pageTwoGrid);

        Table pagesWrap = new Table();
        pagesWrap.setFillParent(true);
        pagesWrap.center().padTop(70);
        pagesWrap.add(pagesStack).width(1280).height(720);
        stage.addActor(pagesWrap);

        TextButton.TextButtonStyle pageStyle = new TextButton.TextButtonStyle();
        pageStyle.up = new TextureRegionDrawable(pageButtonBg);
        pageStyle.over = new TextureRegionDrawable(pageButtonBg);
        pageStyle.down = new TextureRegionDrawable(pageButtonActiveBg);
        pageStyle.checked = new TextureRegionDrawable(pageButtonActiveBg);
        pageStyle.font = cardFont;
        pageStyle.fontColor = new Color(1f, 0.86f, 0.36f, 1f);
        pageStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        pageStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        pageOneButton = new TextButton("1", pageStyle);
        pageTwoButton = new TextButton("2", pageStyle);
        pageOneButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (currentPage != 0) {
                    AudioManager.get().playSelect(0.72f);
                    currentPage = 0;
                    refreshPageSelector();
                }
            }
        });
        pageTwoButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (currentPage != 1) {
                    AudioManager.get().playSelect(0.72f);
                    currentPage = 1;
                    refreshPageSelector();
                }
            }
        });

        pagerTable = new Table();
        pagerTable.add(pageOneButton).width(84).height(64).padRight(12f);
        pagerTable.add(pageTwoButton).width(84).height(64);
        positionPager();
        stage.addActor(pagerTable);

        refreshPageSelector();
    }

    private Table buildLevelPage(int startLevel) {
        Table grid = new Table();
        grid.center();

        int level = startLevel;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                float extraLeftPad = col == 0 ? 58f : 16f;
                grid.add(buildCard(level)).width(230).height(300).padTop(16).padBottom(16).padRight(16).padLeft(extraLeftPad);
                level++;
            }
            grid.row();
        }
        return grid;
    }

    private void refreshPageSelector() {
        pageOneGrid.setVisible(currentPage == 0);
        pageTwoGrid.setVisible(currentPage == 1);
        pageOneButton.setChecked(currentPage == 0);
        pageTwoButton.setChecked(currentPage == 1);
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
                AudioManager.get().playLevelSelect(0.85f);
                s.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.05f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.10f, Interpolation.sineOut)
                ));
                selectedLevel = level;
                startTransition = true;
            }

            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (!startTransition) AudioManager.get().playHover();
            }
        });
        return s;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        AudioManager.get().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new ua.uni.web.main_menu.Menu(game));
            return;
        }
        if (startTransition) {
            transitionAlpha = Math.min(1f, transitionAlpha + (delta / 0.45f));
            if (transitionAlpha >= 1f && selectedLevel > 0) {
                if (selectedLevel == 1) {
                    game.setScreen(new PoligonLevel(game));
                } else if (selectedLevel == 2) {
                    game.setScreen(new RuinsLevel(game));
                } else {
                    game.setScreen(new Menu(game, selectedLevel));
                }
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

    private void positionTitle() {
        GlyphLayout layout = new GlyphLayout(titleFont, titleLabel.getText());
        float x = (stage.getViewport().getWorldWidth() - layout.width) / 2f;
        float y = stage.getViewport().getWorldHeight() - layout.height - 40f;
        titleLabel.setPosition(x, y);
    }

    private void positionPager() {
        if (pagerTable == null) return;
        pagerTable.pack();
        float x = (stage.getViewport().getWorldWidth() - pagerTable.getWidth()) / 2f;
        float y = 6f;
        pagerTable.setPosition(x, y);
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

        pixmap.setColor(0f, 0f, 0f, 0.98f);
        fillRoundedRect(pixmap, 10, 10, w - 20, h - 20, 34);

        pixmap.setBlending(Blending.None);
        pixmap.setColor(0f, 0f, 0f, 0f);
        fillRoundedRect(pixmap, 22, 22, w - 44, h - 44, 26);
        pixmap.setBlending(Blending.SourceOver);

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
        if (titleLabel != null) positionTitle();
        if (pagerTable != null) positionPager();
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
        levelCard.dispose();
        pageButtonBg.dispose();
        pageButtonActiveBg.dispose();
        vignette.dispose();
        transitionBlack.dispose();
        titleFont.dispose();
        cardFont.dispose();
    }
}
