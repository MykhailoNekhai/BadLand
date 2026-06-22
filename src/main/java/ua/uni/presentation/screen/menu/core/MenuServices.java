package ua.uni.presentation.screen.menu.core;

import ua.uni.bootstrap.GameServices;
import ua.uni.audio.services.AudioManager;
import ua.uni.presentation.screen.menu.factory.MenuFontFactory;
import ua.uni.presentation.screen.menu.factory.MenuTextureFactory;
import ua.uni.presentation.screen.menu.navigation.MenuNavigator;
import ua.uni.presentation.screen.menu.ui.MenuFx;
import ua.uni.presentation.screen.menu.ui.SidePanelService;

public final class MenuServices {
    private final MenuNavigator navigator;
    private final AudioManager audio;
    private final MenuTextureFactory textures;
    private final MenuFontFactory fonts;
    private final MenuFx fx;
    private final SidePanelService sidePanels;

    public MenuServices(GameServices services) {
        this.audio = AudioManager.get();
        this.navigator = new MenuNavigator(services);
        this.textures = new MenuTextureFactory();
        this.fonts = new MenuFontFactory();
        this.fx = new MenuFx(audio);
        this.sidePanels = new SidePanelService(audio);
    }

    public MenuNavigator navigator() {
        return navigator;
    }

    public AudioManager audio() {
        return audio;
    }

    public MenuTextureFactory textures() {
        return textures;
    }

    public MenuFontFactory fonts() {
        return fonts;
    }

    public MenuFx fx() {
        return fx;
    }

    public SidePanelService sidePanels() {
        return sidePanels;
    }
}
