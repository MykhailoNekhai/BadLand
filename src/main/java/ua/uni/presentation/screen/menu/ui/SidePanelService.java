package ua.uni.presentation.screen.menu.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ua.uni.audio.services.AudioManager;

public final class SidePanelService {
    private final AudioManager audio;

    public SidePanelService(AudioManager audio) {
        this.audio = audio;
    }

    public Actor open(Stage stage, boolean fromLeft, String titleText, Table contentTable,
                      Texture panelBackground, BitmapFont titleFont, int panelWidth, int panelHeight,
                      float sidePadding, float topPadding, float horizontalPadding, float bottomPadding) {
        float screenWidth = stage.getViewport().getWorldWidth();
        float screenHeight = stage.getViewport().getWorldHeight();
        float y = (screenHeight - panelHeight) / 2f;
        float startX = fromLeft ? -panelWidth - 20f : screenWidth + 20f;
        float endX = fromLeft ? sidePadding : screenWidth - panelWidth - sidePadding;

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label titleLabel = new Label(titleText, titleStyle);

        Table inner = new Table();
        inner.top().padTop(topPadding).padLeft(horizontalPadding).padRight(horizontalPadding).padBottom(bottomPadding);
        inner.add(titleLabel).center().padBottom(2).row();
        inner.add(contentTable).expand().fill().row();

        Stack panelStack = new Stack();
        panelStack.add(new Image(new TextureRegionDrawable(panelBackground)));
        panelStack.add(inner);
        panelStack.setSize(panelWidth, panelHeight);
        panelStack.setPosition(startX, y);
        panelStack.addAction(Actions.moveTo(endX, y, 0.30f, Interpolation.sineOut));

        audio.playPanelInOut(0.70f);
        stage.addActor(panelStack);
        return panelStack;
    }

    public void close(Stage stage, Actor panel, boolean fromLeft) {
        if (panel == null) {
            return;
        }
        float screenWidth = stage.getViewport().getWorldWidth();
        float panelWidth = panel.getWidth();
        float exitX = fromLeft ? -panelWidth - 20f : screenWidth + 20f;
        panel.addAction(Actions.sequence(
                Actions.moveTo(exitX, panel.getY(), 0.22f, Interpolation.sineIn),
                Actions.removeActor()
        ));
        audio.playPanelInOut(0.60f);
    }
}
