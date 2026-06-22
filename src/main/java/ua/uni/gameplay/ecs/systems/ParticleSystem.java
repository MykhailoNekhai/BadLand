package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ua.uni.gameplay.ecs.components.ParticleComponent;

public class ParticleSystem extends IteratingSystem {
    private final SpriteBatch batch;
    private final ComponentMapper<ParticleComponent> pm = ComponentMapper.getFor(ParticleComponent.class);

    public ParticleSystem(SpriteBatch batch) {
        super(Family.all(ParticleComponent.class).get());
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
        ParticleComponent particle = pm.get(entity);
        if (particle.effect != null) {
            particle.effect.draw(batch, deltaTime);
            if (particle.effect.isComplete()) {
                particle.effect.free();
                getEngine().removeEntity(entity);
            }
        } else {
            getEngine().removeEntity(entity);
        }
    }
}
