package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import ua.uni.gameplay.ecs.components.BonusComponent;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;

public class PullSystem extends IteratingSystem {
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);

    private final float pullPower = 20f;

    public PullSystem() {
        super(Family.all(BonusComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity bonusEntity, float deltaTime) {
        // витягуємо інформацію
        PhysicsComponent bonusPhys = physMapper.get(bonusEntity);
        ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
        if (players.size() == 0) return; // звичайна перевірка

        Entity closestPlayer = null;
        float minDistance = Float.MAX_VALUE;
        Vector2 bonusPos = bonusPhys.body.getPosition();

        // перевірка, який клон є найближчим до нашого бонусу

        for (int i = 0; i < players.size(); ++i) {
            Entity playerEntity = players.get(i);
            PhysicsComponent playerPhys = physMapper.get(playerEntity);
            float dist = playerPhys.body.getPosition().dst(bonusPos);
            if (dist < minDistance) {
                minDistance = dist;
                closestPlayer = playerEntity;
            }
        }

        if (closestPlayer != null) {
            PhysicsComponent playerPhys = physMapper.get(closestPlayer);
            waitForPullContact(bonusPhys, playerPhys);
        }
    }

    private void waitForPullContact(PhysicsComponent bonusPhys, PhysicsComponent playerPhys) {
        Vector2 playerPos = playerPhys.body.getPosition();
        Vector2 bonusPos = bonusPhys.body.getPosition();

        float auraRadius = 5.0f; // радіус аури який навколо об'ктів, в межах якого почне виконуватись притягування
        float distanceBetween = playerPos.dst(bonusPos);

        if (distanceBetween <= auraRadius) {

            float mass = bonusPhys.body.getMass();
            float gravScale = bonusPhys.body.getGravityScale();
            float pullPower = 22.33f * mass * gravScale; // загальна формула, майже така ж як і з тінню

            Vector2 pullDirection = playerPos.cpy().sub(bonusPos);
            pullDirection.nor().scl(pullPower);

            bonusPhys.body.applyForceToCenter(pullDirection, true);
        }
    }

}
