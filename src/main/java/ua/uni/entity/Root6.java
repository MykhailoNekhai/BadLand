package ua.uni.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.utilite.BodyEditorLoader;

public class Root6 extends BaseEntity {
    private static BodyEditorLoader sharedLoader;

    public Root6(World world, float x, float y, float size) {
        if (sharedLoader == null) {
            sharedLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/Root6.json"));
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.1f;

        createPhysicsBody(world, sharedLoader, "Root6", bodyDef, fixtureDef, size, 159f);
    }
}
