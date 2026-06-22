package ua.uni.gameplay.factory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.math.Vector2;
import ua.uni.gameplay.ecs.components.*;
import ua.uni.core.config.ObjectConfig;
import ua.uni.utility.config.ConfigLoader;
import ua.uni.utility.physics.BodyEditorLoader;


import java.util.HashMap;
import java.util.Map;

// Клас, який створює об'єкти згідно певному, заданмоу нами алгоритму.

public class EntityFactory {
    private static final Map<String, Texture> textureCache = new HashMap<>();
    private static final Map<String, BodyEditorLoader> loaderCache = new HashMap<>();

    public static Entity createObstacle(Engine engine, World world, String objectName, float x, float y, float angleDegrees, float size) {
        Entity entity = buildBaseEntity(engine, world, objectName, x, y, angleDegrees, size);
        if ("godzilla".equals(objectName)) {
            entity.add(engine.createComponent(ua.uni.gameplay.ecs.components.GodzillaComponent.class));
        }
        return entity;
    }

    public static Entity createSaw(Engine engine, World world, String objectName, float x, float y, float angleDegrees, float size, float spinSpeed) {
        Entity entity = buildBaseEntity(engine, world, objectName, x, y, angleDegrees, size);

        PhysicsComponent physComp = entity.getComponent(PhysicsComponent.class);
        physComp.body.setAngularVelocity(spinSpeed);

        if (objectName != null && (objectName.toLowerCase().contains("saw") || objectName.toLowerCase().contains("shredder") || objectName.toLowerCase().contains("propeller"))) {
            SoundSourceComponent audio = engine.createComponent(SoundSourceComponent.class);
            audio.sound = ua.uni.audio.services.AudioManager.get().getCircsawLoopSound();
            audio.maxDistance = 16f;
            audio.baseVolume = 0.5f;
            entity.add(audio);
        }

        return entity;
    }

    public static Entity createPlayer(Engine engine, World world, float x, float y, float size) {
        return buildBaseEntity(engine, world, "avatar-1", x, y, 0f, size);
    }

    private static Entity buildBaseEntity(Engine engine, World world, String objectName, float x, float y, float angleDegrees, float size) {
        Entity entity = engine.createEntity();

        PlaceComponent place = engine.createComponent(PlaceComponent.class);
        place.x = x;
        place.y = y;
        place.rotation = angleDegrees;
        entity.add(place);

        TextureComponent textureComp = engine.createComponent(TextureComponent.class);
        if (!textureCache.containsKey(objectName)) {
            textureCache.put(objectName, new Texture(Gdx.files.internal("game-resourses/textures/" + objectName + ".png")));
        }
        textureComp.texture = textureCache.get(objectName);
        textureComp.width = size;
        textureComp.height = size * ((float)textureComp.texture.getHeight() / textureComp.texture.getWidth());
        entity.add(textureComp);

        ObjectConfig config = ConfigLoader.get(objectName);
        PhysicsComponent physComp = engine.createComponent(PhysicsComponent.class);

        BodyDef bodyDef = new BodyDef();

        switch (config.bodyType) {
            case "Dynamic":
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                break;
            case "Kinematic":
                bodyDef.type = BodyDef.BodyType.KinematicBody;
                break;
            default:
                bodyDef.type = BodyDef.BodyType.StaticBody;
                break;
        }

        bodyDef.position.set(x, y);
        bodyDef.angle = angleDegrees * (float)Math.PI / 180f;
        bodyDef.linearDamping = config.linearDamping;
        bodyDef.angularDamping = config.angularDamping;
        bodyDef.gravityScale = config.gravityScale;

        physComp.body = world.createBody(bodyDef);
        physComp.body.setUserData(entity);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = config.density;
        fixtureDef.friction = config.friction;
        fixtureDef.restitution = config.restitution;

        if (!loaderCache.containsKey(objectName)) {
            loaderCache.put(objectName, new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/" + objectName + ".json")));
        }
        BodyEditorLoader loader = loaderCache.get(objectName);
        
        float realScale = size / config.baseWidth;
        loader.attachFixture(physComp.body, objectName, fixtureDef, realScale);

        entity.add(physComp);

        if (config.isHinged && config.centerX >= 0 && config.centerY >= 0) {
            float pixelWidth = textureComp.texture.getWidth();
            float pixelHeight = textureComp.texture.getHeight();
            float offsetX_pixels = config.centerX - (pixelWidth / 2f);
            float offsetY_pixels = config.centerY - (pixelHeight / 2f);
            float localAnchorX = offsetX_pixels * realScale;
            float localAnchorY = offsetY_pixels * realScale;

            Vector2 hingeWorld = physComp.body.getWorldPoint(new Vector2(localAnchorX, localAnchorY));

            BodyDef bodyDef1 = new BodyDef();
            bodyDef1.type = BodyDef.BodyType.StaticBody;
            bodyDef1.position.set(hingeWorld);
            Body nailBody = world.createBody(bodyDef1);

            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.bodyA = nailBody;
            jointDef.bodyB = physComp.body;
            jointDef.localAnchorA.set(0, 0);
            jointDef.localAnchorB.set(localAnchorX, localAnchorY);

            world.createJoint(jointDef);
        }

        if (config.isDeadly) {
            entity.add(engine.createComponent(DeadlyComponent.class));
        }
        if (config.isPlayer) {
            entity.add(engine.createComponent(PlayerComponent.class));
            entity.add(engine.createComponent(WingComponent.class));
            
            Filter playerFilter = new Filter();
            playerFilter.categoryBits = 0x0002;
            for (Fixture f : physComp.body.getFixtureList()) {
                f.setFilterData(playerFilter);
            }
        }
        if (config.isBonus) {
            BonusComponent bonus = engine.createComponent(BonusComponent.class);
            bonus.type = objectName;
            entity.add(bonus);
        }

        if (objectName != null && objectName.toLowerCase().contains("mine")) {
            entity.add(engine.createComponent(ua.uni.gameplay.ecs.components.MineComponent.class));
        }

        MaterialComponent matComp = engine.createComponent(MaterialComponent.class);
        String lowerName = objectName != null ? objectName.toLowerCase() : "";
        if (lowerName.contains("rock") || lowerName.contains("stone")) {
            matComp.material = "rock";
        } else if (lowerName.contains("pipe") || lowerName.contains("chain") || lowerName.contains("shredder") || lowerName.contains("propeller") || lowerName.contains("gear") || lowerName.contains("lazer") || lowerName.contains("turret") || lowerName.contains("saw") || lowerName.contains("grenade") || lowerName.contains("wire")) {
            matComp.material = "metal";
        } else if (lowerName.contains("sponge") || lowerName.contains("avatar") || lowerName.contains("rubber") || lowerName.contains("burdock")) {
            matComp.material = "rubber";
        } else {
            matComp.material = "wood";
        }
        entity.add(matComp);

        engine.addEntity(entity);


        return entity;
    }
}
