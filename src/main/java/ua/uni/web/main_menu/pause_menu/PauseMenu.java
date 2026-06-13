package ua.uni.web.main_menu.pause_menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import ua.uni.web.main_menu.settings_menu.LanguageButton;
import ua.uni.web.main_menu.settings_menu.SettingsMenu;
import ua.uni.web.main_menu.single_player_menu.SinglePlayerMenu;

public class PauseMenu {
    private final MainGame game;
    private final Runnable onResume;
    private final Runnable onRestart;
    private final Runnable onCheckpoint;

    private Stage stage;
    private Texture panel;
    private Texture panelVignette;
    private Texture itemBtn;
    private Texture sidePanelBg;
    private BitmapFont itemFont;
    private BitmapFont smallFont;
    private BitmapFont titleFont;
    private TextButton continueButton;
    private TextButton restartButton;
    private TextButton checkpointButton;
    private TextButton settingButton;
    private TextButton exitToMenuButton;
    private SettingsMenu activeSettingsMenu;
    private boolean visible;

    public PauseMenu(MainGame game, Runnable onResume, Runnable onRestart, Runnable onCheckpoint) {
        this.game = game;
        this.onResume = onResume;
        this.onRestart = onRestart;
        this.onCheckpoint = onCheckpoint;
        initResources();
    }

    public void show() {
        visible = true;
        AudioManager.get().enterMenuContext();
        Gdx.input.setInputProcessor(stage);
        stage.clear();
        buildUi();
    }

    public void hide() {
        visible = false;
        AudioManager.get().leaveMenuContext();
    }

    public void handleEscape() {
        if (activeSettingsMenu != null) {
            activeSettingsMenu.handleExternalEscape();
            return;
        }
        onResume.run();
    }

    public void render(float delta) {
        if (!visible) return;
        if (activeSettingsMenu != null) {
            activeSettingsMenu.render(delta);
            return;
        }
        AudioManager.get().updateMenuAmbience(delta);
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (activeSettingsMenu != null) {
            activeSettingsMenu.resize(width, height);
        }
    }

    public void dispose() {
        if (activeSettingsMenu != null) {
            activeSettingsMenu.dispose();
        }
        stage.dispose();
        panel.dispose();
        panelVignette.dispose();
        itemBtn.dispose();
        sidePanelBg.dispose();
        itemFont.dispose();
        smallFont.dispose();
        titleFont.dispose();
    }

    private void initResources() {
        stage = new Stage(new ScreenViewport());

        panel = gradientPanel(620, 980, 48, 18, 12,
                new Color(0.06f, 0.08f, 0.13f, 0.95f),
                new Color(0.14f, 0.08f, 0.04f, 0.95f));
        panelVignette = panelVignetteTexture(620, 980, 48, 18, 12);
        itemBtn = roundedRect(560, 80, 24, new Color(0f, 0f, 0f, 1f));
        sidePanelBg = gradientPanel(380, 640, 38, 14, 12,
                new Color(0.08f, 0.10f, 0.15f, 0.98f),
                new Color(0.16f, 0.10f, 0.05f, 0.98f));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter items = new FreeTypeFontGenerator.FreeTypeFontParameter();
        items.size = 56;
        items.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        items.borderWidth = 1.6f;
        items.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        items.characters = LanguageButton.FONT_CHARACTERS;
        itemFont = generator.generateFont(items);

        FreeTypeFontGenerator.FreeTypeFontParameter small = new FreeTypeFontGenerator.FreeTypeFontParameter();
        small.size = 34;
        small.color = Color.WHITE;
        small.borderWidth = 1.0f;
        small.borderColor = Color.BLACK;
        small.characters = LanguageButton.FONT_CHARACTERS;
        smallFont = generator.generateFont(small);

        FreeTypeFontGenerator.FreeTypeFontParameter title = new FreeTypeFontGenerator.FreeTypeFontParameter();
        title.size = 68;
        title.color = new Color(1f, 0.86f, 0.36f, 1f);
        title.borderWidth = 1.8f;
        title.borderColor = new Color(0.10f, 0.05f, 0.02f, 1f);
        title.characters = LanguageButton.FONT_CHARACTERS;
        titleFont = generator.generateFont(title);

        generator.dispose();
    }

    private void buildUi() {
        Table panelContent = new Table();
        panelContent.defaults().center().padLeft(36).padRight(36);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, titleFont.getColor());

        TextButton.TextButtonStyle itemStyle = new TextButton.TextButtonStyle();
        itemStyle.up = new TextureRegionDrawable(itemBtn);
        itemStyle.down = new TextureRegionDrawable(itemBtn);
        itemStyle.over = new TextureRegionDrawable(itemBtn);
        itemStyle.font = itemFont;
        itemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        itemStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        Label titleLabel = new Label(LanguageButton.t("PAUSE"), titleStyle);
        continueButton = new TextButton(LanguageButton.t("CONTINUE"), itemStyle);
        restartButton = new TextButton(LanguageButton.t("RESTART"), itemStyle);
        checkpointButton = new TextButton(LanguageButton.t("CHECKPOINT"), itemStyle);
        settingButton = new TextButton(LanguageButton.t("SETTING"), itemStyle);
        exitToMenuButton = new TextButton(LanguageButton.t("EXIT_TO_MENU"), itemStyle);

        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.75f);
                onResume.run();
            }
        });
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.75f);
                onRestart.run();
            }
        });
        checkpointButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                onCheckpoint.run();
            }
        });
        settingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.72f);
                openSettingsOverlay();
            }
        });
        exitToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.get().playSelect(0.75f);
                hide();
                game.setScreen(new SinglePlayerMenu(game));
            }
        });

        panelContent.add(titleLabel).padTop(40).padBottom(28).row();
        panelContent.add(continueButton).width(560).height(80).padBottom(10).row();
        panelContent.add(restartButton).width(560).height(80).padBottom(10).row();
        panelContent.add(checkpointButton).width(560).height(80).padBottom(10).row();
        panelContent.add(settingButton).width(560).height(80).padBottom(10).row();
        panelContent.add(exitToMenuButton).width(560).height(80).padBottom(18).row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(panel)));
        panelStack.add(new Image(new TextureRegionDrawable(panelVignette)));
        panelStack.add(panelContent);

        Table panelWrap = new Table();
        panelWrap.setFillParent(true);
        panelWrap.center();
        panelWrap.add(panelStack).width(620).height(980);
        stage.addActor(panelWrap);

        Actor[] revealList = {titleLabel, continueButton, restartButton, checkpointButton, settingButton, exitToMenuButton};
        for (int i = 0; i < revealList.length; i++) {
            Actor actor = revealList[i];
            actor.getColor().a = 0f;
            actor.addAction(Actions.sequence(
                    Actions.delay(0.08f * i),
                    Actions.fadeIn(0.35f)
            ));
        }

        panelStack.addAction(Actions.sequence(
                Actions.delay(0.05f),
                Actions.run(() -> panelStack.setOrigin(panelStack.getWidth() / 2f, panelStack.getHeight() / 2f)),
                Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.006f, 1.006f, 2.2f, com.badlogic.gdx.math.Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 2.2f, com.badlogic.gdx.math.Interpolation.sine)
                ))
        ));
    }

    private void openSettingsOverlay() {
        if (activeSettingsMenu != null) return;
        activeSettingsMenu = new SettingsMenu(game, this::closeSettingsOverlay);
        activeSettingsMenu.show();
    }

    private void closeSettingsOverlay() {
        if (activeSettingsMenu == null) return;
        activeSettingsMenu.hide();
        activeSettingsMenu.dispose();
        activeSettingsMenu = null;
        Gdx.input.setInputProcessor(stage);
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
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture gradientPanel(int w, int h, int radius, int padX, int padY, Color topColor, Color bottomColor) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        int innerH = h - 2 * padY;
        for (int y = padY; y < h - padY; y++) {
            int x0;
            int x1;
            if (y < padY + radius) {
                int dy = (padY + radius) - y;
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else if (y >= h - padY - radius) {
                int dy = y - (h - padY - radius - 1);
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else {
                x0 = padX;
                x1 = w - padX - 1;
            }
            float t = (y - padY) / (float) (innerH - 1);
            float r = topColor.r + (bottomColor.r - topColor.r) * t;
            float g = topColor.g + (bottomColor.g - topColor.g) * t;
            float b = topColor.b + (bottomColor.b - topColor.b) * t;
            float a = topColor.a + (bottomColor.a - topColor.a) * t;
            pixmap.setColor(r, g, b, a);
            pixmap.drawLine(x0, y, x1, y);
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private Texture panelVignetteTexture(int w, int h, int radius, int padX, int padY) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        int cx = w / 2;
        int cy = h / 2;
        float max = (float) Math.sqrt((double) (cx * cx) + (double) (cy * cy));
        for (int y = padY; y < h - padY; y++) {
            int x0;
            int x1;
            if (y < padY + radius) {
                int dy = (padY + radius) - y;
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else if (y >= h - padY - radius) {
                int dy = y - (h - padY - radius - 1);
                int dx = (int) Math.sqrt((double) (radius * radius) - (double) (dy * dy));
                x0 = padX + radius - dx;
                x1 = w - padX - radius + dx;
            } else {
                x0 = padX;
                x1 = w - padX - 1;
            }
            for (int x = x0; x <= x1; x++) {
                float dx2 = x - cx;
                float dy2 = y - cy;
                float d = (float) Math.sqrt((double) (dx2 * dx2) + (double) (dy2 * dy2)) / max;
                float a = Math.max(0f, Math.min(1f, (d - 0.55f) * 1.4f));
                if (a > 0f) {
                    pixmap.setColor(0f, 0f, 0f, a * 0.45f);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        Texture texture = new Texture(pixmap);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }
}
