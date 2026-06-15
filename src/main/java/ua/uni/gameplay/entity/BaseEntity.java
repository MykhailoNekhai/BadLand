package ua.uni.gameplay.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.utility.physics.BodyEditorLoader;

public abstract class BaseEntity {
    protected Body body;


    protected void createPhysicsBody(
            World world,
            BodyEditorLoader loader,
            String jsonName,
            BodyDef bodyDef,
            FixtureDef fixtureDef,
            float targetSize,
            float imagePixels) {

        this.body = world.createBody(bodyDef);

        float realScale = targetSize / imagePixels;

        loader.attachFixture(this.body, jsonName, fixtureDef, realScale);

        this.body.setUserData(this);
    }

    public Body getBody() {
        return body;
    }
}