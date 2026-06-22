package ua.uni.presentation.screen.menu.navigation;

import com.badlogic.gdx.Gdx;
import ua.uni.bootstrap.GameServices;
import ua.uni.gameplay.levels.CoopPoligonLevel;
import ua.uni.gameplay.levels.CoopRuinsLevel;
import ua.uni.gameplay.levels.Poligon2Level;
import ua.uni.gameplay.levels.PoligonLevel;
import ua.uni.presentation.screen.LoadingScreen;
import ua.uni.presentation.screen.level.LevelPreviewScreen;
import ua.uni.presentation.screen.login.LoginMenu;
import ua.uni.presentation.screen.menu.account.AccountMenu;
import ua.uni.presentation.screen.menu.coop.CoopLevelPlayScreen;
import ua.uni.presentation.screen.menu.coop.CoopMenu;
import ua.uni.presentation.screen.menu.main.Menu;
import ua.uni.presentation.screen.menu.settings.AchievementsButton;
import ua.uni.presentation.screen.menu.settings.KeyBindingsButton;
import ua.uni.presentation.screen.menu.settings.SettingsMenu;
import ua.uni.presentation.screen.menu.singleplayer.SinglePlayerMenu;

public final class MenuNavigator {
    private final GameServices services;

    public MenuNavigator(GameServices services) {
        this.services = services;
    }

    public void goToMainMenu() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new Menu(services)));
    }

    public void goToLogin() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new LoginMenu(services)));
    }

    public void goToAccount() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new AccountMenu(services)));
    }

    public void goToSettings() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new SettingsMenu(services)));
    }

    public void goToSettings(Runnable onExit) {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new SettingsMenu(services, onExit)));
    }

    public void goToAchievements() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new AchievementsButton(services)));
    }

    public void goToKeyBindings() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new KeyBindingsButton(services)));
    }

    public void goToSinglePlayer() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new SinglePlayerMenu(services)));
    }

    public void goToCoop() {
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new CoopMenu(services)));
    }

    public void goToSinglePlayerLevel(int level) {
        if (level == 1) {
            services.setScreen(new LoadingScreen(services, () -> {}, () -> new Poligon2Level(services)));
            return;
        }
        if (level == 2) {
            services.setScreen(new LoadingScreen(services, () -> {}, () -> new PoligonLevel(services)));
            return;
        }
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new LevelPreviewScreen(services, level)));
    }

    public void goToCoopLevel(int level) {
        if (level == 1) {
            services.setScreen(new LoadingScreen(services, () -> {}, () -> new CoopPoligonLevel(services)));
            return;
        }
        if (level == 2) {
            services.setScreen(new LoadingScreen(services, () -> {}, () -> new CoopRuinsLevel(services)));
            return;
        }
        services.setScreen(new LoadingScreen(services, () -> {}, () -> new CoopLevelPlayScreen(services, level)));
    }

    public void exitGame() {
        Gdx.app.exit();
    }
}
