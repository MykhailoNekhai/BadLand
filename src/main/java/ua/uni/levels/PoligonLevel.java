package ua.uni.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.MainGame;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ua.uni.entity.Saw;
import ua.uni.utilite.BodyEditorLoader;

public class PoligonLevel extends Plevel {


    public PoligonLevel(MainGame game) {
        super(game);
    }

    // ОГОЛОШЕННЯ ОБ'ЄКТІВ
    @Override
    protected void buildLevel() {

        physicsLoader = new BodyEditorLoader(Gdx.files.internal("game-resourses/assetData/saw2.json"));

        spawnClone(2, 5);

        Saw saw1 = new Saw(world, physicsLoader, 20, 15, 4f);

    }



}
