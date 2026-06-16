package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.gameplay.ecs.components.GodzillaComponent;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.gameplay.stats.LevelStats;

public class GodzillaPullSystem extends IteratingSystem {
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private World world;
    private LevelStats levelStats;

    public GodzillaPullSystem(World world, LevelStats levelStats) {
        super(Family.all(GodzillaComponent.class, PhysicsComponent.class).get());
        this.world = world;
        this.levelStats = levelStats;
    }

    @Override
    protected void processEntity(Entity godzillaEntity, float deltaTime) {
        PhysicsComponent godzillaPhys = physMapper.get(godzillaEntity);
        if (godzillaPhys == null || godzillaPhys.body == null) return;

        Vector2 godzillaPos = godzillaPhys.body.getPosition();
        // Встановлюємо точку притягування (точний центр годзіли)
        Vector2 suckPoint = godzillaPos.cpy();

        float activationRadius = 12.0f; // Відстань, на якій тінь починає всмоктуватися
        float suckRadius = 1.0f; // Відстань, на якій тінь зникає всередині труби

        ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class).get());

        for (int i = 0; i < players.size(); ++i) {
            Entity playerEntity = players.get(i);
            PhysicsComponent playerPhys = physMapper.get(playerEntity);
            PlayerComponent playerComp = playerMapper.get(playerEntity);

            if (playerPhys.body == null || playerComp.isDead) continue;

            Vector2 playerPos = playerPhys.body.getPosition();
            float distance = playerPos.dst(suckPoint);

            if (distance < activationRadius && playerPos.x < suckPoint.x + 1f) {
                // Коли тінь перетинає поріг, гравець втрачає контроль
                playerComp.isFinished = true;
                playerComp.isSucked = true; // власна позначка, щоб тінь не впиралася
                levelStats.levelFinished = true; // хоча б одна тінь дійшла до кінця

                // Робимо тінь сенсором, щоб вона більше не відштовхувалася від стін/труби
                for (Fixture fixture : playerPhys.body.getFixtureList()) {
                    if (!fixture.isSensor()) {
                        fixture.setSensor(true);
                    }
                }

                // Вимикаємо гравітацію, щоб тінь не провалювалась вниз
                playerPhys.body.setGravityScale(0f);

                // Задаємо точну пряму швидкість до центру (ніякої інерції та орбіт)
                Vector2 pullDirection = suckPoint.cpy().sub(playerPos);
                // Чим ближче до центру, тим точніше всмоктується
                pullDirection.nor().scl(14.0f);
                playerPhys.body.setLinearVelocity(pullDirection);

                // Задаємо легке обертання для красивого затягування
                playerPhys.body.setAngularVelocity(10f);

                // Якщо досягли центру всмоктування — зникаємо
                if (distance < suckRadius) {
                    levelStats.collectedClones++;
                    world.destroyBody(playerPhys.body);
                    getEngine().removeEntity(playerEntity);
                }
            }
        }
    }
}
