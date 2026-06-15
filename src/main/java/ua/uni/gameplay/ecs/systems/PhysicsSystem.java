package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlaceComponent;

// Система яка надає усім об'єктам фізичні властивості і розташовує їх на карті

public class PhysicsSystem extends IteratingSystem {

    private final ComponentMapper<PlaceComponent> placeMapper = ComponentMapper.getFor(PlaceComponent.class);
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);

    public PhysicsSystem() {
        super(Family.all(PlaceComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlaceComponent place = placeMapper.get(entity);
        PhysicsComponent physics = physMapper.get(entity);

        if (physics.body != null) {
            place.x = physics.body.getPosition().x;
            place.y = physics.body.getPosition().y;
            place.rotation = physics.body.getAngle() * MathUtils.radiansToDegrees;
        }
    }
}