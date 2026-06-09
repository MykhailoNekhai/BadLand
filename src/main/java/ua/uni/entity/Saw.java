package ua.uni.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.utilite.BodyEditorLoader;

public class Saw {
    private Body body;

    // Конструктор
    public Saw(World world, BodyEditorLoader loader, float x, float y, float size) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        this.body = world.createBody(bodyDef);

        this.body.setAngularVelocity(-3.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 5.0f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.2f;

        convertToRealSize(size, loader, fixtureDef);



    }

    public Body getBody() {
        return body;
    }

    private void convertToRealSize(float size, BodyEditorLoader loader, FixtureDef fixtureDef){
        float imagePixels = 740f;

        float realScale = size / imagePixels;

        loader.attachFixture(body, "saw", fixtureDef, realScale);
    }
}
