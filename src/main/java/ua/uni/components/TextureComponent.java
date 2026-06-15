package ua.uni.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;


// Компонент який накладає текстуру (спрайт) на об'єкт

public class TextureComponent implements Component, Pool.Poolable {
    public Texture texture;
    public float width;
    public float height;

    @Override
    public void reset() {
        texture = null;
        width = 0f;
        height = 0f;
    }
}