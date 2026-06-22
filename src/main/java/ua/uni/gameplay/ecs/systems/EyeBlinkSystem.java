package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import ua.uni.gameplay.ecs.components.EyeComponent;

public class EyeBlinkSystem extends IteratingSystem {
    private static final float BLINK_FRAME_TIME = 0.055f;
    private static final float MIN_NEXT_BLINK = 2.2f;
    private static final float MAX_NEXT_BLINK = 5.2f;

    private final ComponentMapper<EyeComponent> eyeMapper = ComponentMapper.getFor(EyeComponent.class);

    public EyeBlinkSystem() {
        super(Family.all(EyeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EyeComponent eye = eyeMapper.get(entity);
        if (eye.frames == null || eye.frames.length < 3) {
            return;
        }

        eye.blinkTimer += deltaTime;

        if (!eye.isBlinking) {
            eye.currentFrame = EyeComponent.OPEN_FRAME;
            if (eye.blinkTimer >= eye.nextBlinkTime) {
                eye.isBlinking = true;
                eye.blinkTimer = 0f;
                eye.currentFrame = EyeComponent.HALF_CLOSED_FRAME;
            }
            return;
        }

        float t = eye.blinkTimer;
        if (t < BLINK_FRAME_TIME) {
            eye.currentFrame = EyeComponent.HALF_CLOSED_FRAME;
        } else if (t < BLINK_FRAME_TIME * 2f) {
            eye.currentFrame = EyeComponent.CLOSED_FRAME;
        } else if (t < BLINK_FRAME_TIME * 3f) {
            eye.currentFrame = EyeComponent.HALF_CLOSED_FRAME;
        } else {
            eye.currentFrame = EyeComponent.OPEN_FRAME;
            eye.isBlinking = false;
            eye.blinkTimer = 0f;
            eye.nextBlinkTime = MathUtils.random(MIN_NEXT_BLINK, MAX_NEXT_BLINK);
        }
    }
}
