package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class BonusComponent implements Component, Pool.Poolable {
    public String type;
    public boolean isCollected = false;
    public boolean isAbsorbing = false;
    public com.badlogic.ashley.core.Entity targetPlayer = null;
    public float currentScale = 1.0f;
    public float originalWidth = -1f;

    @Override
    public void reset() {
        type = null;
        isCollected = false;
        isAbsorbing = false;
        targetPlayer = null;
        currentScale = 1.0f;
        originalWidth = -1f;
    }
}
