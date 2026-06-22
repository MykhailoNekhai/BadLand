package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.gameplay.ecs.components.BonusComponent;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.gameplay.ecs.components.TextureComponent;
import ua.uni.gameplay.ecs.components.ParticleComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;

public class BonusSystem extends IteratingSystem {
// система, що реалізовує бонуси
    private final ComponentMapper<BonusComponent> bonusMapper = ComponentMapper.getFor(BonusComponent.class);
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final World world;
    private final ParticleEffectPool whiteSmokePool;

    public BonusSystem(World world, ParticleEffectPool whiteSmokePool) {
        super(Family.all(BonusComponent.class).get());
        this.world = world;
        this.whiteSmokePool = whiteSmokePool;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BonusComponent bonus = bonusMapper.get(entity);

        if (bonus.isCollected) {
            PhysicsComponent phys = physMapper.get(entity);
            if (phys != null && phys.body != null) {
                world.destroyBody(phys.body);
            }
            getEngine().removeEntity(entity);
        } else if (bonus.isAbsorbing && bonus.targetPlayer != null) {
            PhysicsComponent bonusPhys = physMapper.get(entity);
            PhysicsComponent playerPhys = physMapper.get(bonus.targetPlayer);
            
            if (playerPhys == null || playerPhys.body == null || bonusPhys == null || bonusPhys.body == null) {
                bonus.isCollected = true;
                return;
            }
            
            bonusPhys.body.setAwake(false);
            
            Vector2 bonusPos = bonusPhys.body.getPosition();
            Vector2 playerPos = playerPhys.body.getPosition();
            bonusPos.lerp(playerPos, deltaTime * 20f);
            bonusPhys.body.setTransform(bonusPos, bonusPhys.body.getAngle());
            
            bonus.currentScale -= deltaTime * 10.0f;
            
            TextureComponent tex = entity.getComponent(TextureComponent.class);
            if (tex != null) {
                if (bonus.originalWidth < 0) {
                    bonus.originalWidth = tex.width;
                }
                tex.width = bonus.originalWidth * Math.max(0, bonus.currentScale);
                tex.height = tex.width * ((float)tex.texture.getHeight() / tex.texture.getWidth());
            }
            
            if (bonus.currentScale <= 0.05f) {
                PlayerComponent playerComp = bonus.targetPlayer.getComponent(PlayerComponent.class);
                if (playerComp != null) {
                    playerComp.receivedBonus = bonus.type;
                    
                    if (whiteSmokePool != null) {
                        ParticleEffectPool.PooledEffect effect = whiteSmokePool.obtain();
                        Vector2 pos = playerPhys.body.getPosition();
                        effect.setPosition(pos.x, pos.y);
                        Entity particleEntity = new Entity();
                        ParticleComponent pc = new ParticleComponent();
                        pc.effect = effect;
                        particleEntity.add(pc);
                        getEngine().addEntity(particleEntity);
                    }
                }
                bonus.isCollected = true;
            }
        }
    }
}
