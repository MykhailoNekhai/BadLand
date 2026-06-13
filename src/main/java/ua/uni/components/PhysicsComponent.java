package ua.uni.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;


// Компонент який накладає фізичні характеристики box2D з json файлу у assetData

public class PhysicsComponent implements Component, Pool.Poolable {
    public Body body;

    @Override
    public void reset() {
        body=null;
    }
}