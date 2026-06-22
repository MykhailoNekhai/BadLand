package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import ua.uni.gameplay.ecs.components.MineComponent;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.audio.services.AudioManager;

public class MineSystem extends IteratingSystem {
    private ComponentMapper<MineComponent> mineMapper = ComponentMapper.getFor(MineComponent.class);
    private ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);

    public MineSystem() {
        super(Family.all(MineComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MineComponent mine = mineMapper.get(entity);
        if (mine.isExploded) {
            PhysicsComponent minePhys = physMapper.get(entity);
            if (minePhys != null && minePhys.body != null) {
                Vector2 minePos = minePhys.body.getPosition();
                float explosionRadius = 8f; 
                float explosionForce = 35f; 
                
                for (Entity playerEntity : getEngine().getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get())) {
                    PhysicsComponent playerPhys = physMapper.get(playerEntity);
                    if (playerPhys != null && playerPhys.body != null) {
                        float dist = playerPhys.body.getPosition().dst(minePos);
                        if (dist < explosionRadius) {
                            Vector2 pushDir = playerPhys.body.getPosition().cpy().sub(minePos);
                            if (pushDir.len2() < 0.001f) {
                                float randAngle = com.badlogic.gdx.math.MathUtils.random(0f, com.badlogic.gdx.math.MathUtils.PI2);
                                pushDir.set((float)Math.cos(randAngle), (float)Math.sin(randAngle));
                            }
                            pushDir.nor();
                            float forceMultiplier = 1f - (dist / explosionRadius);
                            playerPhys.body.applyLinearImpulse(
                                pushDir.scl(explosionForce * forceMultiplier * playerPhys.body.getMass()), 
                                playerPhys.body.getWorldCenter(), true
                            );
                        }
                    }
                }
                
                AudioManager.get().playExplosionSound(1f); 
                
                getEngine().removeEntity(entity);
                minePhys.body.getWorld().destroyBody(minePhys.body);
                minePhys.body = null;
            }
        }
    }
}
