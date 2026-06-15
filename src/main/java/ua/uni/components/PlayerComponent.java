package ua.uni.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;


// Компонент який має базові характеристики нашої тіні (швидкості та статуси), фізичні знаходятся у json конфігурації відповідно

public class PlayerComponent implements Component, Pool.Poolable{
// formula verticalSpeed = 27.33*mass*gravityScale
    public float verticalSpeed = 27.33f;
    public float baseSpeed = 5f;
    public float baseSpeedCap = 8f;
    public float backwardSpeed = -8f;
    public float maxFowardSpeed = 8f;
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


    @Override
    public void reset() {
        verticalSpeed = 27.33f;
        baseSpeed = 5f;
        baseSpeedCap = 8f;
        backwardSpeed = -8f;
        maxFowardSpeed = 8f;
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
    }
}
