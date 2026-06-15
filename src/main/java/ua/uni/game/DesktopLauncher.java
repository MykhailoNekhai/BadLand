package ua.uni.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ua.uni.online.gameplay.server.DedicatedServerMain;

public class DesktopLauncher {

    public static void main(String[] args) {
        if (args != null) {
            for (String arg : args) {
                if ("--server".equals(arg)) {
                    DedicatedServerMain.main(args);
                    return;
                }
            }
        }
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        String title = "BadLand Prototype";
        if (!RuntimeProfile.isDefault()) {
            title += " [" + RuntimeProfile.get() + "]";
        }
        config.setTitle(title);
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        new Lwjgl3Application(new MainGame(), config);
    }
}
