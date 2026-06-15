package ua.uni.gameplay.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;


// Компонент який розташовуватиме об'єкт по координатах (у метрах) та обертає об'єкт згідно з заданим параметром користувачем

public class PlaceComponent implements Component, Pool.Poolable {
    public float x = 0.0f;
    public float y = 0.0f;
    public float rotation = 0.0f;

    @Override
    public void reset() {
        x=0.0f;
        y=0.0f;
        rotation=0.0f;
    }
}