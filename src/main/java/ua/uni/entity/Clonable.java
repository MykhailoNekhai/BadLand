package ua.uni.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ua.uni.utilite.BodyEditorLoader;
import ua.uni.levels.Plevel; // Не забудь импортировать свой уровень!

public class Clonable extends BaseEntity {
    private static BodyEditorLoader sharedLoader;

    private Plevel level;
    private World world;
    private boolean isCollected = false;

    public Clonable(Plevel level, World world, float x, float y, float size) {
        this.level = level;
        this.world = world;

        if (sharedLoader == null) {
            sharedLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/Clonable.json"));
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;

        createPhysicsBody(world, sharedLoader, "Clonable", bodyDef, fixtureDef, size, 75f);
    }

    public void collect() {
        if (isCollected) return;
        isCollected = true;

        float spawnX = body.getPosition().x;
        float spawnY = body.getPosition().y;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                level.spawnClone(spawnX+0.5f, spawnY);
                world.destroyBody(body);
            }
        });
    }
}