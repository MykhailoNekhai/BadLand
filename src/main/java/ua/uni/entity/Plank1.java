package ua.uni.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef; // Импорт шарнира
import ua.uni.utilite.BodyEditorLoader;

public class Plank1 extends BaseEntity {
    private static BodyEditorLoader sharedLoader;

    public Plank1(World world, float x, float y, float size) {
        if (sharedLoader == null) {
            sharedLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/Plank1.json"));
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;
        bodyDef.angularDamping = 3.0f;

        createPhysicsBody(world, sharedLoader, "Plank1", bodyDef, fixtureDef, size, 369f);

        BodyDef anchorDef = new BodyDef();
        anchorDef.type = BodyDef.BodyType.StaticBody;

        anchorDef.position.set(x, y);

        Body anchorBody = world.createBody(anchorDef);

        RevoluteJointDef jointDef = new RevoluteJointDef();

        jointDef.initialize(anchorBody, this.body, anchorBody.getPosition());

        world.createJoint(jointDef);
    }
}