package ua.uni.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import ua.uni.components.BonusComponent;
import ua.uni.components.PhysicsComponent;
import ua.uni.components.PlayerComponent;

public class PullSystem extends IteratingSystem {
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);

    private final float pullPower = 20f;

    public PullSystem() {
        super(Family.all(BonusComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity bonusEntity, float deltaTime) {
        PhysicsComponent bonusPhys = physMapper.get(bonusEntity);
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
        if (players.size() == 0) return;
        Entity playerEntity = players.get(0);
        PhysicsComponent playerPhys = physMapper.get(playerEntity);
        waitForPullContact(bonusPhys, playerPhys);
    }

    private void waitForPullContact(PhysicsComponent bonusPhys, PhysicsComponent playerPhys) {
        Vector2 playerPos = playerPhys.body.getPosition();
        Vector2 bonusPos = bonusPhys.body.getPosition();

        float auraRadius = 5.0f;
        float distanceBetween = playerPos.dst(bonusPos);

        if (distanceBetween <= auraRadius) {

            float mass = bonusPhys.body.getMass();

            float gravScale = bonusPhys.body.getGravityScale();

            float pullPower = 22.33f * mass * gravScale;

            Vector2 pullDirection = playerPos.cpy().sub(bonusPos);
            pullDirection.nor().scl(pullPower);

            bonusPhys.body.applyForceToCenter(pullDirection, true);
        }
    }

}
