package ua.uni.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.components.BonusComponent;
import ua.uni.components.PhysicsComponent;

public class BonusSystem extends IteratingSystem {
// система, що реалізовує бонуси
    private final ComponentMapper<BonusComponent> bonusMapper = ComponentMapper.getFor(BonusComponent.class);
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final World world;

    public BonusSystem(World world) {
        super(Family.all(BonusComponent.class).get());
        this.world = world;
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
        }
    }
}
