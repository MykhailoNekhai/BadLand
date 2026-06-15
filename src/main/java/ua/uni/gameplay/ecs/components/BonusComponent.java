package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class BonusComponent implements Component, Pool.Poolable {
    public String type;
    public boolean isCollected = false;

    @Override
    public void reset() {
        type = null;
        isCollected = false;
    }
}
