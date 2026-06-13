package ua.uni.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;


// Компонент який має базові характеристики нашої тіні (швидкості та статуси), фізичні знаходятся у json конфігурації відповідно

public class PlayerComponent implements Component, Pool.Poolable{
// formula verticalSpeed = 27.33*mass*gravityScale
    public final float verticalSpeed = 28.5f;
    public final float baseSpeed = 5f;
    public final float baseSpeedCap = 8f;
    public final float backwardSpeed = -8f;

    public final float maxFowardSpeed = 8f;
    public final float maxBackwardSpeed = -2f;
    public boolean isDead = false;
    public boolean moveUp = false;
    public boolean moveDown = false;
    public boolean moveLeft = false;
    public boolean moveRight = false;


    @Override
    public void reset() {
    isDead = false;
    moveUp = false;
    moveDown = false;
    moveLeft = false;
    moveRight = false;

    }
}
