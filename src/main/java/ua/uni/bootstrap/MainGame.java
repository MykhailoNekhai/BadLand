package ua.uni.bootstrap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ua.uni.audio.services.AudioManager;
import ua.uni.core.config.GameSettings;
import ua.uni.presentation.screen.menu.settings.AchievementsButton;
import ua.uni.utility.config.ConfigLoader;

public class MainGame extends Game {
    private GameServices services;
    private AchievementPopupRenderer popupRenderer;
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ConfigLoader.load();
        GameSettings.load();

        services = new GameServices(this, batch);
        popupRenderer = new AchievementPopupRenderer(services.achievements());

        services.achievements().setListener(services.sync());
        GameSettings.setSettingsChangeListener(services.sync()::syncSettings);

        setScreen(new IntroScreen(services));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.F8)) {
            resetAchievementsForDebug();
        }
        services.achievements().onPlayTime(delta);
        super.render();
        popupRenderer.update(delta);
        popupRenderer.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (services != null) services.dispose();
        if (popupRenderer != null) popupRenderer.dispose();
        if (batch != null) batch.dispose();
    }

    private void resetAchievementsForDebug() {
        services.achievements().resetAll();
        if (services.sync() != null) {
            services.sync().syncProgressSnapshot("ACHIEVEMENTS_RESET", "debug-f8");
        }
        popupRenderer.reset();
        if (getScreen() instanceof AchievementsButton) {
            setScreen(new AchievementsButton(services));
        }
        Gdx.app.log("Achievements", "Achievements reset with F8");
    }
}
