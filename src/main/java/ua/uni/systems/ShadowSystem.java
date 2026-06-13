package ua.uni.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import ua.uni.components.PhysicsComponent;
import ua.uni.components.PlayerComponent;
import ua.uni.config.GameSettings;


// Система, яка забирає усі тіні (підконтрольних користувачу) та надає характеристики та управління кожному з об'єктів


public class ShadowSystem extends IteratingSystem {

    private final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);

    public ShadowSystem() {
        super(Family.all(PlayerComponent.class, PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = playerMapper.get(entity);
        PhysicsComponent phys = physMapper.get(entity);

        if (phys.body == null || player.isDead) return;

        player.moveUp = Gdx.input.isKeyPressed(GameSettings.getMoveUp());
        player.moveDown = Gdx.input.isKeyPressed(GameSettings.getMoveDown());
        player.moveLeft = Gdx.input.isKeyPressed(GameSettings.getMoveLeft());
        player.moveRight = Gdx.input.isKeyPressed(GameSettings.getMoveRight());

        Vector2 movement = new Vector2(0, 0);

        if (player.moveUp) {
            movement.y += player.verticalSpeed;
            movement.x += player.baseSpeed;
        }

        if (player.moveDown) {
            movement.y -= player.verticalSpeed;
            movement.x += player.baseSpeed;
        }

        if (player.moveRight) {
            movement.x += player.baseSpeedCap;
        }

        if (player.moveLeft) {
            movement.x += player.backwardSpeed;
        }

        if (!movement.isZero()) {
            phys.body.applyForceToCenter(movement, true);
        }

        Vector2 velocity = phys.body.getLinearVelocity();

        if (velocity.x > player.maxFowardSpeed) {
            phys.body.setLinearVelocity(player.maxFowardSpeed, velocity.y);
        }

        if (velocity.x < player.maxBackwardSpeed) {
            phys.body.setLinearVelocity(player.maxBackwardSpeed, velocity.y);
        }

        float velY = phys.body.getLinearVelocity().y;

        float targetAngle = velY * 0.1f;

        if (targetAngle > 0.78f) targetAngle = 0.78f;
        if (targetAngle < -0.78f) targetAngle = -0.78f;

        float currentAngle = phys.body.getAngle();

        float angleError = targetAngle - currentAngle;

        while (angleError > Math.PI) {
            angleError -= (float)(Math.PI * 2);
        }
        while (angleError < -Math.PI) {
            angleError += (float)(Math.PI * 2);
        }

        phys.body.applyTorque(angleError * 15f, true);
    }
}