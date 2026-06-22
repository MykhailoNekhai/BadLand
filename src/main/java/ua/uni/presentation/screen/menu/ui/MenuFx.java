package ua.uni.presentation.screen.menu.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import ua.uni.audio.services.AudioManager;

public final class MenuFx {
    private static final float GOLD_PRESS_OFFSET = 10f;
    private static final float GOLD_PRESS_FADE_ALPHA = 0.35f;

    private final AudioManager audio;

    public MenuFx(AudioManager audio) {
        this.audio = audio;
    }

    public void addStaggeredReveal(Actor actor, float delay) {
        actor.getColor().a = 0f;
        actor.setScale(0.96f);
        actor.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.parallel(
                        Actions.fadeIn(0.36f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.36f, Interpolation.sineOut)
                )
        ));
    }

    public void setupButtonFx(TextButton button, Stage stage, Texture dotTexture) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y,
                              int pointer, Actor fromActor) {
                audio.playHover();
                button.addAction(Actions.scaleTo(1.02f, 1.02f, 0.14f, Interpolation.smooth));
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y,
                             int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, 0.14f, Interpolation.smooth));
            }

            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y,
                                     int pointer, int buttonCode) {
                spawnRipple(button, stage, dotTexture, x, y);
                button.addAction(Actions.sequence(
                        Actions.scaleTo(0.96f, 0.96f, 0.06f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.12f, Interpolation.swingOut)
                ));
                return super.touchDown(event, x, y, pointer, buttonCode);
            }
        });
    }

    public void setupIconHoverFx(Actor actor) {
        actor.addListener(new ClickListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y,
                              int pointer, Actor fromActor) {
                audio.playHover();
                actor.clearActions();
                actor.addAction(Actions.scaleTo(1.08f, 1.08f, 0.14f, Interpolation.smooth));
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y,
                             int pointer, Actor toActor) {
                actor.clearActions();
                actor.addAction(Actions.scaleTo(1f, 1f, 0.14f, Interpolation.smooth));
            }
        });
    }

    public static void playGoldButtonPress(Actor actor) {
        runAfterGoldButtonPress(actor, null);
    }

    public static void runAfterGoldButtonPress(Actor actor, Runnable afterPress) {
        actor.clearActions();
        actor.getColor().a = GOLD_PRESS_FADE_ALPHA;
        if (afterPress == null) {
            actor.addAction(goldPressAction());
        } else {
            actor.addAction(Actions.sequence(
                    goldPressAction(),
                    Actions.run(afterPress)
            ));
        }
    }

    private static com.badlogic.gdx.scenes.scene2d.Action goldPressAction() {
        return Actions.sequence(
                Actions.moveBy(-GOLD_PRESS_OFFSET, 0f, 0.05f, Interpolation.sineIn),
                Actions.parallel(
                        Actions.moveBy(GOLD_PRESS_OFFSET, 0f, 0.14f, Interpolation.sineOut),
                        Actions.fadeIn(0.14f, Interpolation.fade)
                )
        );
    }

    public void spawnRipple(TextButton button, Stage stage, Texture dotTexture, float localX, float localY) {
        Vector2 worldPos = new Vector2(localX, localY);
        button.localToStageCoordinates(worldPos);
        Image ripple = new Image(new TextureRegionDrawable(dotTexture));
        ripple.setSize(20f, 20f);
        ripple.setOrigin(10f, 10f);
        ripple.setPosition(worldPos.x - 10f, worldPos.y - 10f);
        ripple.setColor(1f, 0.85f, 0.40f, 0.55f);
        ripple.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(14f, 14f, 0.55f, Interpolation.sineOut),
                        Actions.fadeOut(0.55f, Interpolation.fade)
                ),
                Actions.removeActor()
        ));
        stage.addActor(ripple);
    }
}
