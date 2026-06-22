package ua.uni.gameplay.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import ua.uni.gameplay.ecs.components.PhysicsComponent;
import ua.uni.gameplay.ecs.components.PlayerComponent;
import ua.uni.gameplay.ecs.components.TextureComponent;
import ua.uni.gameplay.ecs.components.WingComponent;
import ua.uni.core.config.GameSettings;
import ua.uni.utility.physics.BodyEditorLoader;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.gameplay.factory.EntityFactory;


// Система, яка забирає усі тіні (підконтрольних користувачу) та надає характеристики та управління кожному з об'єктів


public class ShadowSystem extends IteratingSystem {
    private static final float WING_FLAP_CYCLE_SECONDS = 0.36f;
    private static final int[] WING_FRAME_SEQUENCE = {2, 3, 4, 3, 2, 1, 0, 1, 2};

    private final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<PhysicsComponent> physMapper = ComponentMapper.getFor(PhysicsComponent.class);
    private final ComponentMapper<TextureComponent> texMapper = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<WingComponent> wingMapper = ComponentMapper.getFor(WingComponent.class);

    private final World world;

    public ShadowSystem(World world) {
        // беремо тільки ті об'єкти, які містять в собі компоненти PlayerComponent та PhysicsComponent одночасно
        super(Family.all(PlayerComponent.class, PhysicsComponent.class, TextureComponent.class).get());
        this.world = world;
    }

    // Метод, що застосовує нижче інструкцію до усіх об'єктів shadow
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
       // збираємо з об'єкту інформацію
        PlayerComponent player = playerMapper.get(entity);
        PhysicsComponent phys = physMapper.get(entity);
// перевірка на null
        if (phys.body == null || player.isDead) {
            return;
        }

        if (player.receivedBonus != null) {
            String bonusToApply = player.receivedBonus;
            player.receivedBonus = null;

            // бонуси, спочатку на клони, потім всі інші
            if ("item-clone".equals(bonusToApply)) {
                float currentScale = player.shadowSizeScale * 1.2f;
               float spawnX = phys.body.getPosition().x - 0.8f;
                float spawnY = phys.body.getPosition().y + 0.8f;
                
                Entity clone = EntityFactory.createPlayer(getEngine(), world, spawnX, spawnY, currentScale);
                
                PlayerComponent cloneP = clone.getComponent(PlayerComponent.class);
                if (cloneP != null) {
                    cloneP.shadowSizeScale = player.shadowSizeScale;
                    cloneP.speedModifier = player.speedModifier;
                    cloneP.verticalSpeed = player.verticalSpeed;
                }
                
                PhysicsComponent clonePhys = clone.getComponent(PhysicsComponent.class);
                if (clonePhys != null) {
                    clonePhys.body.setGravityScale(phys.body.getGravityScale());
                }

            } else {
                for (Entity pEntity : getEngine().getEntitiesFor(Family.all(PlayerComponent.class, PhysicsComponent.class, TextureComponent.class).get())) {
                    PlayerComponent playerComponent = playerMapper.get(pEntity);
                    PhysicsComponent physicsComponent = physMapper.get(pEntity);
                    if (physicsComponent.body == null || playerComponent.isDead){
                        continue;
                    }

                    if ("item-big".equals(bonusToApply)) {
                        playerComponent.shadowSizeScale += 0.5f;
                        if (playerComponent.shadowSizeScale > 3.5f) {
                            playerComponent.shadowSizeScale = 3.5f;
                        }
                        playerComponent.needsResize = true;
                        playerComponent.speedModifier *= 0.85f;

                        if (playerComponent.speedModifier < 0.4f) {
                            playerComponent.speedModifier = 0.4f;
                        }
                        playerComponent.verticalSpeed *= 0.85f;
                        physicsComponent.body.setGravityScale(physicsComponent.body.getGravityScale() * 1.15f);
                        
                    } else if ("item-small".equals(bonusToApply)) {
                        playerComponent.shadowSizeScale -= 0.5f;
                        if (playerComponent.shadowSizeScale < 0.5f) {
                            playerComponent.shadowSizeScale = 0.5f;
                        }
                        playerComponent.needsResize = true;
                        playerComponent.speedModifier *= 1.15f;

                        if (playerComponent.speedModifier > 2.0f) {
                            playerComponent.speedModifier = 2.0f;
                        }
                        playerComponent.verticalSpeed *= 1.15f;
                        physicsComponent.body.setGravityScale(physicsComponent.body.getGravityScale() * 0.85f);
                        
                    } else if ("item-slow".equals(bonusToApply)) {
                        playerComponent.speedModifier *= 0.6f;
                        if (playerComponent.speedModifier < 0.3f) {
                            playerComponent.speedModifier = 0.3f;
                        }
                        
                    } else if ("item-speed".equals(bonusToApply)) {
                        playerComponent.speedModifier *= 2.2f;
                        if (playerComponent.speedModifier > 10.0f) {
                            playerComponent.speedModifier = 10.0f;
                        }
                    }
                }
            }
        }

        if (player.needsResize) {
            resizePlayer(entity, player, phys);
        }

// зчитування клавіш
        if (!player.isFinished) {
            player.moveUp = Gdx.input.isKeyPressed(GameSettings.getMoveUp());
            player.moveDown = Gdx.input.isKeyPressed(GameSettings.getMoveDown());
            player.moveLeft = Gdx.input.isKeyPressed(GameSettings.getMoveLeft());
            player.moveRight = Gdx.input.isKeyPressed(GameSettings.getMoveRight());
        } else if (player.isSucked) {
            player.moveUp = false;
            player.moveDown = false;
            player.moveLeft = false;
            player.moveRight = false;
        } else {
            player.moveUp = false;
            player.moveDown = false;
            player.moveLeft = false;
            player.moveRight = true;
        }

        // формуємо вектор movement від якого і будемо відштовхуватись при руху
        Vector2 movement = new Vector2(0, 0);

        float mass = phys.body.getMass();
        float gravScale = phys.body.getGravityScale();
        float dynamicVerticalSpeed = player.verticalSpeed * mass * gravScale;

        float massRatio = mass / 3.47f;

        // лоігка руху
        if (player.moveUp) {
            movement.y += dynamicVerticalSpeed;
            movement.x += player.baseSpeed * massRatio * player.speedModifier;
        }

        if (player.moveDown) {
            movement.y -= dynamicVerticalSpeed;
            movement.x += player.baseSpeed * massRatio * player.speedModifier;
        }

        if (player.moveRight) {
            movement.x += player.baseSpeedCap * massRatio * player.speedModifier;
        }

        if (player.moveLeft) {
            movement.x += player.backwardSpeed * massRatio * player.speedModifier;
        }

        WingComponent wings = wingMapper.get(entity);
        if (wings != null) {
            TextureComponent tex = texMapper.get(entity);
            if (player.moveUp) {
                wings.isVisible = true;
                wings.flapTime += deltaTime;
                if (wings.flapTime >= WING_FLAP_CYCLE_SECONDS) {
                    wings.flapTime %= WING_FLAP_CYCLE_SECONDS;
                }

                float phase = wings.flapTime / WING_FLAP_CYCLE_SECONDS;
                wings.currentFrame = wingFrameForPhase(phase);
                // Амплітуда руху крил: вгору стандартно, а вниз - набагато сильніше
                float amplitude = tex.height * 0.45f;
                float rawSinValue = -(float)Math.sin(phase * Math.PI * 2f);
                float sinValue = rawSinValue;
                if (sinValue < 0) {
                    sinValue *= 1.6f; // посилюємо рух вниз на 60%
                }
                wings.currentYOffset = sinValue * amplitude;
            } else {
                wings.currentFrame = 2;
                // Плавне згортання: крила повертаються в центр тіла (де вони ховаються на чорному фоні)
                wings.currentYOffset += (0f - wings.currentYOffset) * 12f * deltaTime;
                
                // Коли вони вже повністю сховались - відключаємо малювання
                if (Math.abs(wings.currentYOffset) < 1f) {
                    wings.isVisible = false;
                    wings.flapTime = 0f;
                }
            }
        }

        // застосовуємо силу на наш об'єкт
        if (!movement.isZero()) {
            phys.body.applyForceToCenter(movement, true);
        }

        // нижче обмеження по скорості
        Vector2 velocity = phys.body.getLinearVelocity();

        if (!player.isSucked) {
            if (velocity.x > player.maxFowardSpeed * player.speedModifier) {
                phys.body.setLinearVelocity(player.maxFowardSpeed * player.speedModifier, velocity.y);
            }

            if (velocity.x < player.maxBackwardSpeed * player.speedModifier) {
                phys.body.setLinearVelocity(player.maxBackwardSpeed * player.speedModifier, velocity.y);
            }
        }

        float velY = phys.body.getLinearVelocity().y;

        // логіка наклону коли йде рух
        // кут нашої тіні помноженний на коофіцієнт плавності (хардкод 0.1)
        float targetAngle = velY * 0.1f;

        // 0.78 не вище та не нижче 45 градусів
        if (targetAngle > 0.78f) {
            targetAngle = 0.78f;
        }
        if (targetAngle < -0.78f) {
            targetAngle = -0.78f;
        }

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

    private int wingFrameForPhase(float phase) {
        int frameIndex = (int)(phase * WING_FRAME_SEQUENCE.length);
        if (frameIndex >= WING_FRAME_SEQUENCE.length) {
            frameIndex = WING_FRAME_SEQUENCE.length - 1;
        }
        return WING_FRAME_SEQUENCE[frameIndex];
    }

// метод, що змінює розмір об'єкту (нюанси рушія)
    private void resizePlayer(Entity entity, PlayerComponent player, PhysicsComponent phys) {
        TextureComponent textureComp = texMapper.get(entity);
        if (textureComp == null) {
            return;
        }
        
        float baseSize = 1.2f;
        float newSize = baseSize * player.shadowSizeScale;

        Array<Fixture> fixtures = phys.body.getFixtureList();
        while (fixtures.size > 0) {
            phys.body.destroyFixture(fixtures.get(0));
        }
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 5.0f;
        fixtureDef.friction = 0.25f;
        fixtureDef.restitution = 0.05f;

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/avatar-1.json"));
        float realScale = newSize / 208.0f;
        loader.attachFixture(phys.body, "avatar-1", fixtureDef, realScale);
        
        textureComp.width = newSize;
        textureComp.height = newSize * ((float)textureComp.texture.getHeight() / textureComp.texture.getWidth());
        
        player.needsResize = false;
    }
}
