package ua.uni.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.MainGame;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class PoligonLevel extends Plevel {


    public PoligonLevel(MainGame game) {
        super(game);
    }

    // ОГОЛОШЕННЯ ОБ'ЄКТІВ
    @Override
    protected void buildLevel() {


        spawnClone(2, 5);

        createGround();

    }

    private void createGround() {

        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set(0, 0);

        // ground shape

        ChainShape groundShape = new ChainShape();
        groundShape.createChain(new Vector2[]{new Vector2(-500, 0), new Vector2(500, 0)});
        // fixture definition
        FixtureDef groundFix = new FixtureDef();
        groundFix.shape = groundShape;
        groundFix.density = 2.5f; //  плотность тела, вес
        groundFix.friction = .25f; // сила трения (от нуля до 1)
        groundFix.restitution = 0.8f; //  коофициент уменьшения высоты отталкивания от поверхности

        Body ball = world.createBody(groundDef);
        Fixture fixture = ball.createFixture(groundFix);

        groundShape.dispose();
    }




}
