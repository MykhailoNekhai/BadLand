package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;


// Компонент який має базові характеристики нашої тіні (швидкості та статуси), фізичні знаходятся у json конфігурації відповідно

public class PlayerComponent implements Component, Pool.Poolable{
// formula verticalSpeed = 27.33*mass*gravityScale
    public float verticalSpeed = 25.33f;
    public float baseSpeed = 3f;
    public float baseSpeedCap = 5f;
    public float backwardSpeed = -2f;
    public float maxFowardSpeed = 6f;
    public float maxBackwardSpeed = -2f;
    public float speedModifier = 1.0f;
    public float shadowSizeScale = 1.0f;
    public boolean needsResize = false;
    public String receivedBonus = null;
    public boolean isDead = false;
    public boolean moveUp = false;
    public boolean moveDown = false;
    public boolean moveLeft = false;
    public boolean moveRight = false;
    public boolean isFinished = false;
    public boolean isSucked = false;
    public boolean isGrowingFromBonus = false;
    public boolean ignoreCloneCollision = false;

    @Override
    public void reset() {
        verticalSpeed = 25.33f;
        baseSpeed = 5f;
        baseSpeedCap = 8f;
        backwardSpeed = -2f;
        maxFowardSpeed = 6f;
        maxBackwardSpeed = -2f;
        speedModifier = 1.0f;
        shadowSizeScale = 1.0f;
        needsResize = false;
        receivedBonus = null;
        isDead = false;
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
        isFinished = false;
        isSucked = false;
        isGrowingFromBonus = false;
        ignoreCloneCollision = false;
    }
}
