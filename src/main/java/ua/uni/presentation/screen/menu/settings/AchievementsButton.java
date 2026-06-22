package ua.uni.presentation.screen.menu.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import ua.uni.bootstrap.GameServices;
import ua.uni.presentation.screen.menu.core.PMenu;
import ua.uni.presentation.screen.menu.factory.FontQuality;
import ua.uni.core.config.GameSettings;
import ua.uni.gameplay.achievements.Achievements;
import ua.uni.gameplay.achievements.AchievementsRarity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AchievementsButton extends PMenu {
    private static final int ACHIEVEMENT_COLUMNS = 4;
    private static final int ACHIEVEMENT_ROWS = 3;
    private static final int ACHIEVEMENTS_PER_PAGE = ACHIEVEMENT_COLUMNS * ACHIEVEMENT_ROWS;
    private static final int PAGE_COUNT = 4;
    private static final float CARD_WIDTH = 260f;
    private static final float CARD_HEIGHT = 190f;

    private Texture bg;
    private Texture card;
    private Texture commonAchievementArt;
    private Texture rareAchievementArt;
    private Texture epicAchievementArt;
    private Texture legendaryAchievementArt;
    private Texture pageButtonBg;
    private Texture pageButtonActiveBg;
    private Texture tooltipBg;
    private BitmapFont titleFont;
    private BitmapFont cardFont;
    private BitmapFont tooltipTitleFont;
    private BitmapFont tooltipBodyFont;
    private Label titleLabel;
    private Table pagerTable;
    private Table tooltipTable;
    private Table pageOneGrid;
    private Table pageTwoGrid;
    private Table pageThreeGrid;
    private Table pageFourGrid;
    private Label tooltipTitleLabel;
    private Label tooltipRarityLabel;
    private Label tooltipStatusLabel;
    private Label tooltipMessageLabel;
    private TextButton pageOneButton;
    private TextButton pageTwoButton;
    private TextButton pageThreeButton;
    private TextButton pageFourButton;
    private int currentPage = 0;
    private float elapsed;

    public AchievementsButton(GameServices services) {
        super(services);
    }

    @Override
    public void show() {
        beginMenuShow();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        card = frameCardTexture(260, 190);
        commonAchievementArt = new Texture(Gdx.files.internal("game-resourses/menu/achievements/common-achievement.png"));
        commonAchievementArt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        rareAchievementArt = new Texture(Gdx.files.internal("game-resourses/menu/achievements/rare-achievement.png"));
        rareAchievementArt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        epicAchievementArt = new Texture(Gdx.files.internal("game-resourses/menu/achievements/epic-achievement.png"));
        epicAchievementArt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        legendaryAchievementArt = new Texture(Gdx.files.internal("game-resourses/menu/achievements/legendary-achievement.png"));
        legendaryAchievementArt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pageButtonBg = textures().roundedRect(84, 64, 18, new Color(0f, 0f, 0f, 1f));
        pageButtonActiveBg = textures().roundedRect(84, 64, 18, new Color(0f, 0f, 0f, 1f));
        tooltipBg = textures().roundedRect(430, 215, 24, new Color(0f, 0f, 0f, 0.92f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter pTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTitle.size = 96;
        pTitle.color = Color.BLACK;
        pTitle.borderWidth = 0f;
        pTitle.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(pTitle);
        titleFont = generator.generateFont(pTitle);
        FontQuality.fixScale(titleFont);
        FreeTypeFontGenerator.FreeTypeFontParameter pCard = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pCard.size = 30;
        pCard.color = new Color(1f, 0.86f, 0.36f, 1f);
        pCard.borderWidth = 1.2f;
        pCard.borderColor = Color.BLACK;
        pCard.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(pCard);
        cardFont = generator.generateFont(pCard);
        FontQuality.fixScale(cardFont);
        FreeTypeFontGenerator.FreeTypeFontParameter pTooltipTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTooltipTitle.size = 38;
        pTooltipTitle.color = new Color(1f, 0.92f, 0.55f, 1f);
        pTooltipTitle.borderWidth = 1.3f;
        pTooltipTitle.borderColor = Color.BLACK;
        pTooltipTitle.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(pTooltipTitle);
        tooltipTitleFont = generator.generateFont(pTooltipTitle);
        FontQuality.fixScale(tooltipTitleFont);
        FreeTypeFontGenerator.FreeTypeFontParameter pTooltipBody = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pTooltipBody.size = 23;
        pTooltipBody.color = new Color(0.96f, 0.93f, 0.86f, 1f);
        pTooltipBody.borderWidth = 0.9f;
        pTooltipBody.borderColor = Color.BLACK;
        pTooltipBody.characters = LanguageButton.FONT_CHARACTERS;
        FontQuality.apply(pTooltipBody);
        tooltipBodyFont = generator.generateFont(pTooltipBody);
        FontQuality.fixScale(tooltipBodyFont);
        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.BLACK);
        titleLabel = new Label(LanguageButton.t("ACHIEVEMENTS"), titleStyle);
        titleLabel.pack();
        float titleX = (stage.getViewport().getWorldWidth() - titleLabel.getWidth()) / 2f;
        float titleY = stage.getViewport().getWorldHeight() - titleLabel.getHeight() - 4f;
        titleLabel.setPosition(titleX, titleY);
        stage.addActor(titleLabel);

        List<Achievements> all = new ArrayList<>(services.achievements().getCatalog().getAll());
        all.sort(Comparator.comparingInt(achievement -> achievement.getRarity().ordinal()));
        pageOneGrid = buildAchievementPage(all, 0);
        pageTwoGrid = buildAchievementPage(all, ACHIEVEMENTS_PER_PAGE);
        pageThreeGrid = buildAchievementPage(all, ACHIEVEMENTS_PER_PAGE * 2);
        pageFourGrid = buildAchievementPage(all, ACHIEVEMENTS_PER_PAGE * 3);
        pageTwoGrid.setVisible(false);
        pageThreeGrid.setVisible(false);
        pageFourGrid.setVisible(false);

        Stack pagesStack = new Stack();
        pagesStack.add(pageOneGrid);
        pagesStack.add(pageTwoGrid);
        pagesStack.add(pageThreeGrid);
        pagesStack.add(pageFourGrid);

        Table pagesWrap = new Table();
        pagesWrap.setFillParent(true);
        pagesWrap.center().padTop(120);
        pagesWrap.add(pagesStack).width(1280).height(640);
        stage.addActor(pagesWrap);

        Table tooltipWrap = new Table();
        tooltipWrap.setFillParent(true);
        tooltipWrap.top().right().padTop(40f).padRight(0f);
        tooltipWrap.add(buildTooltipPanel()).width(430).height(215);
        stage.addActor(tooltipWrap);

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
        pageThreeButton = new TextButton("3", pageStyle);
        pageFourButton = new TextButton("4", pageStyle);

        pageOneButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (currentPage != 0) {
                    audio().playSelect(0.72f);
                    currentPage = 0;
                    refreshPageSelector();
                }
            }
        });
        pageTwoButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (currentPage != 1) {
                    audio().playSelect(0.72f);
                    currentPage = 1;
                    refreshPageSelector();
                }
            }
        });
        pageThreeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (currentPage != 2) {
                    audio().playSelect(0.72f);
                    currentPage = 2;
                    refreshPageSelector();
                }
            }
        });
        pageFourButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (currentPage != 3) {
                    audio().playSelect(0.72f);
                    currentPage = 3;
                    refreshPageSelector();
                }
            }
        });

        pagerTable = new Table();
        pagerTable.add(pageOneButton).width(84).height(64).padRight(12f);
        pagerTable.add(pageTwoButton).width(84).height(64).padRight(12f);
        pagerTable.add(pageThreeButton).width(84).height(64).padRight(12f);
        pagerTable.add(pageFourButton).width(84).height(64);
        positionPager();
        stage.addActor(pagerTable);

        refreshPageSelector();
    }

    private Table buildTooltipPanel() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(tooltipTitleFont, new Color(1f, 0.92f, 0.55f, 1f));
        Label.LabelStyle metaStyle = new Label.LabelStyle(tooltipBodyFont, new Color(0.78f, 0.78f, 0.78f, 1f));
        Label.LabelStyle bodyStyle = new Label.LabelStyle(tooltipBodyFont, new Color(0.96f, 0.93f, 0.86f, 1f));

        tooltipTitleLabel = new Label("", titleStyle);
        tooltipTitleLabel.setWrap(true);
        tooltipTitleLabel.setAlignment(Align.topLeft);

        tooltipRarityLabel = new Label("", metaStyle);
        tooltipStatusLabel = new Label("", metaStyle);

        tooltipMessageLabel = new Label("", bodyStyle);
        tooltipMessageLabel.setWrap(true);
        tooltipMessageLabel.setAlignment(Align.topLeft);

        Table text = new Table();
        text.top().left();
        text.defaults().left();
        text.add(tooltipTitleLabel).width(386f).padBottom(6f).row();
        text.add(tooltipRarityLabel).padBottom(4f).row();
        text.add(tooltipStatusLabel).padBottom(8f).row();
        text.add(tooltipMessageLabel).width(386f).growY().top().row();

        tooltipTable = new Table();
        tooltipTable.setBackground(new TextureRegionDrawable(tooltipBg));
        tooltipTable.pad(18f);
        tooltipTable.top().left();
        tooltipTable.add(text).grow();

        clearTooltip();
        return tooltipTable;
    }

    private Table buildAchievementPage(List<Achievements> all, int startIndex) {
        Table grid = new Table();
        grid.center();

        int achievementIndex = startIndex;
        for (int row = 0; row < ACHIEVEMENT_ROWS; row++) {
            for (int col = 0; col < ACHIEVEMENT_COLUMNS; col++) {
                Stack cardStack = achievementIndex < all.size()
                        ? buildCard(all.get(achievementIndex))
                        : buildEmptyCard();
                float extraLeftPad = col == 0 ? 42f : 10f;
                float extraRightPad = col == ACHIEVEMENT_COLUMNS - 1 ? 42f : 10f;
                grid.add(cardStack)
                        .width(CARD_WIDTH)
                        .height(CARD_HEIGHT)
                        .padTop(10f)
                        .padBottom(10f)
                        .padLeft(extraLeftPad)
                        .padRight(extraRightPad);
                achievementIndex++;
            }
            grid.row();
        }
        return grid;
    }

    private void refreshPageSelector() {
        pageOneGrid.setVisible(currentPage == 0);
        pageTwoGrid.setVisible(currentPage == 1);
        pageThreeGrid.setVisible(currentPage == 2);
        pageFourGrid.setVisible(currentPage == 3);

        pageOneButton.setChecked(currentPage == 0);
        pageTwoButton.setChecked(currentPage == 1);
        pageThreeButton.setChecked(currentPage == 2);
        pageFourButton.setChecked(currentPage == 3);
    }

    private Stack buildCard(Achievements achievement) {
        boolean unlocked = services.achievements().isUnlocked(achievement.getCode());
        Image base = new Image(new TextureRegionDrawable(card));
        Image art = null;
        if (achievement.getRarity() == AchievementsRarity.Common) {
            art = new Image(new TextureRegionDrawable(commonAchievementArt));
        } else if (achievement.getRarity() == AchievementsRarity.Rare) {
            art = new Image(new TextureRegionDrawable(rareAchievementArt));
        } else if (achievement.getRarity() == AchievementsRarity.Epic) {
            art = new Image(new TextureRegionDrawable(epicAchievementArt));
        } else if (achievement.getRarity() == AchievementsRarity.Legendary) {
            art = new Image(new TextureRegionDrawable(legendaryAchievementArt));
        }
        Label.LabelStyle style = new Label.LabelStyle(cardFont, unlocked
                ? new Color(1f, 0.86f, 0.36f, 1f)
                : new Color(0.58f, 0.58f, 0.58f, 1f));
        Label label = new Label(unlocked ? achievement.getTitle() : "LOCKED", style);
        label.setAlignment(Align.center);
        Table artWrap = new Table();
        artWrap.setFillParent(true);
        if (art != null) {
            artWrap.center();
            artWrap.add(art).width(216f).height(146f);
        }
        Table wrap = new Table();
        wrap.setFillParent(true);
        wrap.center();
        wrap.add(label).center().width(210f);
        Stack s = new Stack();
        s.add(base);
        if (artWrap.hasChildren()) {
            s.add(artWrap);
        }
        s.add(wrap);
        s.addListener(new ClickListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer,
                              com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (pointer != -1) return;
                updateTooltip(achievement, unlocked);
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer,
                             com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                if (pointer != -1) return;
                clearTooltip();
            }
        });
        return s;
    }

    private void updateTooltip(Achievements achievement, boolean unlocked) {
        tooltipTitleLabel.setText(unlocked ? achievement.getTitle() : lockedTitleText());
        tooltipRarityLabel.setText(rarityText(achievement.getRarity()));
        tooltipRarityLabel.setColor(rarityColor(achievement.getRarity()));
        tooltipStatusLabel.setText(statusText(unlocked));
        tooltipStatusLabel.setColor(unlocked ? new Color(0.72f, 0.93f, 0.68f, 1f) : new Color(0.85f, 0.70f, 0.70f, 1f));
        tooltipMessageLabel.setText(unlocked ? achievement.getMessage() : lockedMessageText());
    }

    private void clearTooltip() {
        tooltipTitleLabel.setText("");
        tooltipRarityLabel.setText("");
        tooltipRarityLabel.setColor(new Color(0.78f, 0.78f, 0.78f, 1f));
        tooltipStatusLabel.setText("");
        tooltipMessageLabel.setText("");
        tooltipStatusLabel.setColor(new Color(0.78f, 0.78f, 0.78f, 1f));
    }

    private Stack buildEmptyCard() {
        Image base = new Image(new TextureRegionDrawable(card));
        base.setColor(1f, 1f, 1f, 0.38f);
        Stack stack = new Stack();
        stack.add(base);
        return stack;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        audio().updateMenuAmbience(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator().goToSettings();
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
        batch.setColor(0f, 0f, 0f, 0.28f);
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
        fillRoundedRect(pixmap, 0, 0, w, h, r, color);
        Texture t = new Texture(pixmap);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return t;
    }

    private Texture frameCardTexture(int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        pixmap.setColor(0f, 0f, 0f, 0.98f);
        fillRoundedRect(pixmap, 10, 10, w - 20, h - 20, 30, Color.BLACK);

        pixmap.setBlending(Blending.None);
        pixmap.setColor(0f, 0f, 0f, 0f);
        fillRoundedRect(pixmap, 22, 22, w - 44, h - 44, 22, new Color(0f, 0f, 0f, 0f));
        pixmap.setBlending(Blending.SourceOver);

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
        if (pagerTable != null) positionPager();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        endMenuHide();
    }

    @Override
    public void dispose() {
        stage.dispose();
        bg.dispose();
        card.dispose();
        commonAchievementArt.dispose();
        rareAchievementArt.dispose();
        epicAchievementArt.dispose();
        legendaryAchievementArt.dispose();
        pageButtonBg.dispose();
        pageButtonActiveBg.dispose();
        tooltipBg.dispose();
        titleFont.dispose();
        cardFont.dispose();
        tooltipTitleFont.dispose();
        tooltipBodyFont.dispose();
    }

    private void positionPager() {
        if (pagerTable == null) return;
        pagerTable.pack();
        float x = (stage.getViewport().getWorldWidth() - pagerTable.getWidth()) / 2f;
        float y = 8f;
        pagerTable.setPosition(x, y);
    }

    private String rarityText(AchievementsRarity rarity) {
        String prefix = "UK".equals(GameSettings.getLanguage()) ? "Рідкість: " : "Rarity: ";
        String value = switch (rarity) {
            case Common -> "UK".equals(GameSettings.getLanguage()) ? "Common" : "Common";
            case Rare -> "UK".equals(GameSettings.getLanguage()) ? "Rare" : "Rare";
            case Epic -> "UK".equals(GameSettings.getLanguage()) ? "Epic" : "Epic";
            case Legendary -> "UK".equals(GameSettings.getLanguage()) ? "Legendary" : "Legendary";
        };
        return prefix + value;
    }

    private Color rarityColor(AchievementsRarity rarity) {
        return switch (rarity) {
            case Common -> new Color(0.42f, 0.78f, 1f, 1f);
            case Rare -> new Color(0.96f, 0.58f, 0.18f, 1f);
            case Epic -> new Color(0.50f, 0.34f, 0.90f, 1f);
            case Legendary -> new Color(0.95f, 0.80f, 0.24f, 1f);
        };
    }

    private String statusText(boolean unlocked) {
        if ("UK".equals(GameSettings.getLanguage())) {
            return unlocked ? "Статус: Unlocked" : "Статус: Locked";
        }
        return unlocked ? "Status: Unlocked" : "Status: Locked";
    }

    private String lockedTitleText() {
        return "UK".equals(GameSettings.getLanguage()) ? "LOCKED" : "LOCKED";
    }

    private String lockedMessageText() {
        return "UK".equals(GameSettings.getLanguage())
                ? "Розблокуйте цю ачівку, щоб побачити її повний опис."
                : "Unlock this achievement to reveal its full description.";
    }
}
