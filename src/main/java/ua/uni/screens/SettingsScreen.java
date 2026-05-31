package ua.uni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.AudioManager;
import ua.uni.config.GameSettings;
import ua.uni.game.MainGame;
import ua.uni.language.language;
import ua.uni.logging.AppLogger;

public class SettingsScreen implements Screen {
    private final MainGame game;
    private Stage stage;

    private Texture bg;
    private Texture panel;
    private Texture panelVignette;
    private Texture backBtn;
    private Texture itemBtn;
    private Texture sliderTrack;
    private Texture sliderKnob;
    private Texture dotTex;

    private static final int TRAIL_LEN = 10;
    private final float[] trailX = new float[TRAIL_LEN];
    private final float[] trailY = new float[TRAIL_LEN];
    private int trailHead;
    private final Vector2 mouseTmp = new Vector2();

    private BitmapFont itemFont;
    private BitmapFont backFont;
    private BitmapFont smallFont;
    private BitmapFont titleFont;

    private float elapsed;

    private Label titleLabel;
    private Label statusLabel;
    private Label soundsLabel;
    private Slider volumeSlider;

    private int languageIndex;

    private TextButton statisticsButton;
    private TextButton achievementsButton;
    private TextButton languageButton;
    private TextButton creditsButton;
    private TextButton keybindButton;
    private TextButton backButton;
    private TextButton logoutButton;

    public SettingsScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        languageIndex = Math.max(0, indexOf(language.LANGUAGES, GameSettings.getLanguage()));
        AudioManager.get().playMenuMusic();

        bg = new Texture(Gdx.files.internal("game-resourses/menu/levels_bg_generated_hq.png"));
        bg.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        panel = gradientPanel(620, 980, 48, 18, 12,
                new Color(0.06f, 0.08f, 0.13f, 0.95f),
                new Color(0.14f, 0.08f, 0.04f, 0.95f));
        panelVignette = panelVignetteTexture(620, 980, 48, 18, 12);
        backBtn = roundedRect(180, 72, 24, new Color(0f, 0f, 0f, 0.92f));
        itemBtn = roundedRect(560, 80, 24, new Color(0f, 0f, 0f, 1f));
        sliderTrack = horizontalGradientTrack(320, 14, 7,
                new Color(0.55f, 0.32f, 0.10f, 0.96f),
                new Color(1f, 0.93f, 0.56f, 0.96f));
        sliderKnob = circleWithHaloTexture(56, 16,
                new Color(1f, 0.93f, 0.62f, 1f),
                new Color(1f, 0.85f, 0.40f, 0.55f));
        dotTex = softDotTexture(14);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("game-resourses/fonts/american_captain.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter items = new FreeTypeFontGenerator.FreeTypeFontParameter();
        items.size = 56;
        items.color = new Color(0.98f, 0.95f, 0.88f, 1f);
        items.borderWidth = 1.6f;
        items.borderColor = new Color(0.06f, 0.05f, 0.03f, 1f);
        items.characters = language.FONT_CHARACTERS;
        itemFont = generator.generateFont(items);

        FreeTypeFontGenerator.FreeTypeFontParameter back = new FreeTypeFontGenerator.FreeTypeFontParameter();
        back.size = 46;
        back.color = Color.WHITE;
        back.borderWidth = 1.2f;
        back.borderColor = Color.BLACK;
        back.characters = language.FONT_CHARACTERS;
        backFont = generator.generateFont(back);

        FreeTypeFontGenerator.FreeTypeFontParameter small = new FreeTypeFontGenerator.FreeTypeFontParameter();
        small.size = 34;
        small.color = Color.WHITE;
        small.borderWidth = 1.0f;
        small.borderColor = Color.BLACK;
        small.characters = language.FONT_CHARACTERS;
        smallFont = generator.generateFont(small);

        FreeTypeFontGenerator.FreeTypeFontParameter title = new FreeTypeFontGenerator.FreeTypeFontParameter();
        title.size = 68;
        title.color = new Color(1f, 0.86f, 0.36f, 1f);
        title.borderWidth = 1.8f;
        title.borderColor = new Color(0.10f, 0.05f, 0.02f, 1f);
        title.characters = language.FONT_CHARACTERS;
        titleFont = generator.generateFont(title);

        generator.dispose();

        buildUi();
    }

    private void buildUi() {
        Table panelContent = new Table();
        panelContent.defaults().center().padLeft(36).padRight(36);

        Label.LabelStyle ls = new Label.LabelStyle(itemFont, itemFont.getColor());
        Label.LabelStyle titleLs = new Label.LabelStyle(titleFont, titleFont.getColor());

        TextButton.TextButtonStyle itemStyle = new TextButton.TextButtonStyle();
        itemStyle.up = new TextureRegionDrawable(itemBtn);
        itemStyle.down = new TextureRegionDrawable(itemBtn);
        itemStyle.over = new TextureRegionDrawable(itemBtn);
        itemStyle.font = itemFont;
        itemStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        itemStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        itemStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        titleLabel = new Label(language.t("OPTIONS"), titleLs);
        statisticsButton = new TextButton(language.t("STATISTICS"), itemStyle);
        achievementsButton = new TextButton(language.t("ACHIEVEMENTS"), itemStyle);
        languageButton = new TextButton(language.t("LANGUAGE") + ": " + language.LANGUAGES[languageIndex], itemStyle);
        creditsButton = new TextButton(language.t("CREDITS"), itemStyle);
        keybindButton = new TextButton(language.t("KEY_BINDINGS"), itemStyle);
        statusLabel = new Label(language.t("READY"), ls);
        statusLabel.setColor(new Color(0.95f, 0.90f, 0.65f, 1f));

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(sliderTrack);
        sliderStyle.knob = new TextureRegionDrawable(sliderKnob);
        volumeSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        volumeSlider.setValue(GameSettings.getMusicVolume());

        soundsLabel = new Label(soundsRowText(), ls);

        Table soundsRow = new Table();
        soundsRow.add(soundsLabel).padBottom(4).row();
        soundsRow.add(volumeSlider).width(420).height(36);

        Stack soundsStack = new Stack();
        soundsStack.add(new Image(new TextureRegionDrawable(itemBtn)));
        soundsStack.add(soundsRow);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float v = volumeSlider.getValue();
                GameSettings.setMusicVolume(v);
                AudioManager.get().applySoundSetting();
                soundsLabel.setText(soundsRowText());
            }
        });

        panelContent.add(titleLabel).padTop(26).padBottom(18).row();
        panelContent.add(soundsStack).width(560).height(110).padBottom(12).row();
        panelContent.add(statisticsButton).width(560).height(80).padBottom(10).row();
        panelContent.add(achievementsButton).width(560).height(80).padBottom(10).row();
        panelContent.add(languageButton).width(560).height(80).padBottom(10).row();
        panelContent.add(creditsButton).width(560).height(80).padBottom(10).row();
        panelContent.add(keybindButton).width(560).height(80).padBottom(14).row();
        panelContent.add(statusLabel).padBottom(14).row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(panel)));
        panelStack.add(new Image(new TextureRegionDrawable(panelVignette)));
        panelStack.add(panelContent);

        Table panelWrap = new Table();
        panelWrap.setFillParent(true);
        panelWrap.center();
        panelWrap.add(panelStack).width(620).height(980);

        stage.addActor(panelWrap);

        Actor[] revealList = {titleLabel, soundsStack, statisticsButton, achievementsButton,
                languageButton, creditsButton, keybindButton, statusLabel};
        for (int i = 0; i < revealList.length; i++) {
            Actor a = revealList[i];
            a.getColor().a = 0f;
            a.addAction(Actions.sequence(
                    Actions.delay(0.08f * i),
                    Actions.fadeIn(0.35f, Interpolation.fade)
            ));
        }

        panelStack.addAction(Actions.sequence(
                Actions.delay(0.05f),
                Actions.run(() -> {
                    panelStack.setOrigin(panelStack.getWidth() / 2f, panelStack.getHeight() / 2f);
                }),
                Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.006f, 1.006f, 2.2f, Interpolation.sine),
                        Actions.scaleTo(1f, 1f, 2.2f, Interpolation.sine)
                ))
        ));

        TextButtonStyle backStyle = new TextButtonStyle();
        backStyle.up = new TextureRegionDrawable(backBtn);
        backStyle.down = new TextureRegionDrawable(backBtn);
        backStyle.over = new TextureRegionDrawable(backBtn);
        backStyle.font = backFont;
        backButton = new TextButton(language.t("BACK"), backStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.top().left().padTop(20).padLeft(20);
        backTable.add(backButton).width(180).height(72);
        stage.addActor(backTable);

        TextButton.TextButtonStyle logoutStyle = new TextButton.TextButtonStyle();
        logoutStyle.up = new TextureRegionDrawable(backBtn);
        logoutStyle.down = new TextureRegionDrawable(backBtn);
        logoutStyle.over = new TextureRegionDrawable(backBtn);
        logoutStyle.font = smallFont;
        logoutButton = new TextButton(language.t("LOG_OUT"), logoutStyle);
        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                AppLogger.info("Auth", "Logout");
                game.getSessionManager().clear();
                game.setScreen(new LoginScreen(game));
            }
        });
        Table logoutTable = new Table();
        logoutTable.setFillParent(true);
        logoutTable.bottom().right().padBottom(24).padRight(24);
        logoutTable.add(logoutButton).width(200).height(72);
        stage.addActor(logoutTable);

        statisticsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                statusLabel.setText(language.t("STATISTICS"));
            }
        });
        achievementsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new AchievementsScreen(game));
            }
        });
        languageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                languageIndex = (languageIndex + 1) % language.LANGUAGES.length;
                GameSettings.setLanguage(language.LANGUAGES[languageIndex]);
                AppLogger.info("Settings", "Language -> " + language.LANGUAGES[languageIndex]);
                refreshLabels();
            }
        });
        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                statusLabel.setText(language.t("CREDITS_TEAM"));
            }
        });
        keybindButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new KeyBindingsScreen(game));
            }
        });

    }

    private void refreshLabels() {
        titleLabel.setText(language.t("OPTIONS"));
        soundsLabel.setText(soundsRowText());
        statisticsButton.setText(language.t("STATISTICS"));
        achievementsButton.setText(language.t("ACHIEVEMENTS"));
        languageButton.setText(language.t("LANGUAGE") + ": " + language.LANGUAGES[languageIndex]);
        creditsButton.setText(language.t("CREDITS"));
        keybindButton.setText(language.t("KEY_BINDINGS"));
        statusLabel.setText(language.t("READY"));
        backButton.setText(language.t("BACK"));
        logoutButton.setText(language.t("LOG_OUT"));
    }

    private static int indexOf(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) return i;
        }
        return -1;
    }

    private String soundsRowText() {
        return language.t("SOUNDS") + ": " + Math.round(GameSettings.getMusicVolume() * 100f) + "%";
    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        updateTrail();

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        var batch = stage.getBatch();
        batch.setProjectionMatrix(stage.getCamera().combined);
        batch.begin();
        float drift = (float) Math.sin(elapsed * 0.12f) * 10f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, -18f - drift, 0f, w + 36f, h);
        batch.setColor(0f, 0f, 0f, 0.35f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 0.85f, 0.35f, 0.08f);
        batch.draw(bg, 0f, 0f, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();

        stage.act(delta);
        stage.draw();

        batch.begin();
        for (int i = 0; i < TRAIL_LEN; i++) {
            int idx = (trailHead - 1 - i + TRAIL_LEN) % TRAIL_LEN;
            float ta = (TRAIL_LEN - i) / (float) TRAIL_LEN * 0.45f;
            float ts = 8f - i * 0.4f;
            batch.setColor(1f, 0.85f, 0.40f, ta);
            batch.draw(dotTex, trailX[idx] - ts, trailY[idx] - ts, ts * 2f, ts * 2f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    private void updateTrail() {
        mouseTmp.set(Gdx.input.getX(), Gdx.input.getY());
        stage.screenToStageCoordinates(mouseTmp);
        trailX[trailHead] = mouseTmp.x;
        trailY[trailHead] = mouseTmp.y;
        trailHead = (trailHead + 1) % TRAIL_LEN;
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

    private Texture circleTexture(int diameter, Color color) {
        Pixmap p = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        p.setColor(color);
        p.fillCircle(diameter / 2, diameter / 2, diameter / 2 - 2);
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture circleWithHaloTexture(int diameter, int innerRadius, Color core, Color halo) {
        Pixmap p = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        float cx = diameter / 2f;
        float cy = diameter / 2f;
        float maxR = diameter / 2f;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d <= innerRadius) {
                    p.setColor(core);
                    p.drawPixel(x, y);
                } else if (d <= maxR) {
                    float t = (d - innerRadius) / (maxR - innerRadius);
                    float a = (1f - t);
                    a = a * a;
                    p.setColor(halo.r, halo.g, halo.b, halo.a * a);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(p);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return tex;
    }

    private Texture softDotTexture(int diameter) {
        Pixmap p = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        float cx = diameter / 2f;
        float cy = diameter / 2f;
        float maxR = diameter / 2f;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                float t = Math.min(1f, d / maxR);
                float a = (1f - t);
                a = a * a;
                if (a > 0f) {
                    p.setColor(1f, 1f, 1f, a);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture tex = new Texture(p);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return tex;
    }

    private Texture gradientPanel(int w, int h, int radius, int padX, int padY, Color topColor, Color bottomColor) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int innerH = h - 2 * padY;
        for (int y = padY; y < h - padY; y++) {
            int x0, x1;
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
            p.setColor(r, g, b, a);
            p.drawLine(x0, y, x1, y);
        }
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture panelVignetteTexture(int w, int h, int radius, int padX, int padY) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        int cx = w / 2;
        int cy = h / 2;
        float max = (float) Math.sqrt((double) (cx * cx) + (double) (cy * cy));
        for (int y = padY; y < h - padY; y++) {
            int x0, x1;
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
                    p.setColor(0f, 0f, 0f, a * 0.45f);
                    p.drawPixel(x, y);
                }
            }
        }
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
        return t;
    }

    private Texture horizontalGradientTrack(int w, int h, int radius, Color leftColor, Color rightColor) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0f);
        p.fill();
        for (int x = 0; x < w; x++) {
            int y0, y1;
            if (x < radius) {
                int dx = radius - x;
                int dy = (int) Math.sqrt((double) (radius * radius) - (double) (dx * dx));
                y0 = (h / 2) - dy;
                y1 = (h / 2) + dy;
            } else if (x >= w - radius) {
                int dx = x - (w - radius - 1);
                int dy = (int) Math.sqrt((double) (radius * radius) - (double) (dx * dx));
                y0 = (h / 2) - dy;
                y1 = (h / 2) + dy;
            } else {
                y0 = 0;
                y1 = h - 1;
            }
            float t = x / (float) (w - 1);
            float r = leftColor.r + (rightColor.r - leftColor.r) * t;
            float g = leftColor.g + (rightColor.g - leftColor.g) * t;
            float b = leftColor.b + (rightColor.b - leftColor.b) * t;
            float a = leftColor.a + (rightColor.a - leftColor.a) * t;
            p.setColor(r, g, b, a);
            p.drawLine(x, y0, x, y1);
        }
        Texture t = new Texture(p);
        t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        p.dispose();
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
        panel.dispose();
        panelVignette.dispose();
        backBtn.dispose();
        itemBtn.dispose();
        sliderTrack.dispose();
        sliderKnob.dispose();
        dotTex.dispose();
        itemFont.dispose();
        backFont.dispose();
        smallFont.dispose();
        titleFont.dispose();
    }
}
