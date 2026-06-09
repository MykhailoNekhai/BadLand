package ua.uni.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.utilite.BodyEditorLoader;

public class Saw2 extends BaseEntity {
    private static BodyEditorLoader sharedLoader;

    public Saw2(World world, float x, float y, float size) {
        if (sharedLoader == null) {
            sharedLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/saw2.json"));
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 5.0f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.2f;

        createPhysicsBody(world, sharedLoader, "saw", bodyDef, fixtureDef, size, 380f);



        this.body.setAngularVelocity(-1f);
    }
}
