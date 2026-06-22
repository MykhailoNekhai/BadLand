package ua.uni.presentation.screen.menu.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ua.uni.audio.services.AudioManager;
import ua.uni.bootstrap.GameServices;
import ua.uni.presentation.screen.menu.factory.MenuFontFactory;
import ua.uni.presentation.screen.menu.factory.MenuTextureFactory;
import ua.uni.presentation.screen.menu.navigation.MenuNavigator;
import ua.uni.presentation.screen.menu.ui.MenuFx;
import ua.uni.presentation.screen.menu.ui.SidePanelService;

public abstract class PMenu implements Screen {
    protected final GameServices services;
    protected final MenuServices menuServices;
    protected Stage stage;

    protected PMenu(GameServices services) {
        this.services = services;
        this.menuServices = new MenuServices(services);
    }

    protected void beginMenuShow() {
        createStage();
        audio().enterMenuContext();
    }

    protected void beginLoginShow() {
        createStage();
        audio().enterLoginContext();
    }

    protected void createStage() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }

    protected void updateMenuAmbience(float delta) {
        audio().updateMenuAmbience(delta);
    }

    protected void endMenuHide() {
        audio().leaveMenuContext();
        Gdx.input.setInputProcessor(null);
    }

    protected void endLoginHide() {
        audio().leaveLoginContext();
        Gdx.input.setInputProcessor(null);
    }

    protected void drawStage(float delta) {
        stage.act(delta);
        stage.draw();
    }

    protected MenuNavigator navigator() {
        return menuServices.navigator();
    }

    protected AudioManager audio() {
        return menuServices.audio();
    }

    protected MenuTextureFactory textures() {
        return menuServices.textures();
    }

    protected MenuFontFactory fonts() {
        return menuServices.fonts();
    }

    protected MenuFx fx() {
        return menuServices.fx();
    }

    protected SidePanelService sidePanels() {
        return menuServices.sidePanels();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
