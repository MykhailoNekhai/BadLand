package ua.uni;

import com.badlogic.gdx.Game;
import ua.uni.screens.MenuScreen;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
