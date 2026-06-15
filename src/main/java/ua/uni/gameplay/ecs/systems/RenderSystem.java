package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ua.uni.gameplay.ecs.components.PlaceComponent;
import ua.uni.gameplay.ecs.components.TextureComponent;


// Система яка встановлює відповідні текстури (Спрайти) до об'єктів


public class RenderSystem extends IteratingSystem {

    private final SpriteBatch batch;
    private final ComponentMapper<PlaceComponent> placeMapper = ComponentMapper.getFor(PlaceComponent.class);
    private final ComponentMapper<TextureComponent> textureMapper = ComponentMapper.getFor(TextureComponent.class);

    public RenderSystem(SpriteBatch batch) {
        super(Family.all(PlaceComponent.class, TextureComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime) {
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlaceComponent place = placeMapper.get(entity);
        TextureComponent texture = textureMapper.get(entity);

        if (texture.texture != null) {
            float drawX = place.x - (texture.width / 2f);
            float drawY = place.y - (texture.height / 2f);
            float originX = texture.width / 2f;
            float originY = texture.height / 2f;

            batch.draw(
                    texture.texture,
                    drawX, drawY,
                    originX, originY,
                    texture.width, texture.height,
                    1f, 1f,
                    place.rotation,
                    0, 0,
                    texture.texture.getWidth(), texture.texture.getHeight(),
                    false, false
            );
        }
    }
}