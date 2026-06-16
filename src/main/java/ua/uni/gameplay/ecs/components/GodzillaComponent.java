package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class GodzillaComponent implements Component, Pool.Poolable {
    public boolean isActive = true;

    @Override
    public void reset() {
        isActive = true;
    }
}
