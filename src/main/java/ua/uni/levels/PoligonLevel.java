package ua.uni.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ua.uni.MainGame;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import ua.uni.entity.*;
import ua.uni.utilite.BodyEditorLoader;

public class PoligonLevel extends Plevel {


    public PoligonLevel(MainGame game) {
        super(game);
    }

    // ОГОЛОШЕННЯ ОБ'ЄКТІВ
    @Override
    protected void buildLevel() {
        spawnClone(2, 9);
        spawnClone(4, 9);
        spawnClone(6, 9);
        spawnClone(8, 9);

        RootThin rootThin = new RootThin(world, 5, 4, 10f);

        Clonable clonablew = new Clonable(this, world, 15, 0, 2f);


        BigRock1 rock1 = new BigRock1(world, 20, 2, 8f);
        Root3 root_intro = new Root3(world, 35, 17, 8f);
        BigRock2 rock2 = new BigRock2(world, 50, 3, 10f);



        Saw1 saw_gate_top = new Saw1(world, 65, 14, 6f);
        Saw1 saw_gate_bot = new Saw1(world, 65, 4, 6f);

        Saw2 saw_big = new Saw2(world, 85, 9, 14f);

        Saw1 saw_stairs1 = new Saw1(world, 100, 3, 5f);
        Saw1 saw_stairs2 = new Saw1(world, 105, 9, 5f);



        Root1 r1 = new Root1(world, 125, 2, 6f);
        Root4 r4 = new Root4(world, 128, 18, 9f);

        BigRock1 rock_block = new BigRock1(world, 140, 2, 12f);
        Root6 r6 = new Root6(world, 142, 16, 5f);

        Root2 r2 = new Root2(world, 150, 1, 7f);
        Root7 r7 = new Root7(world, 155, 17, 6f);
        Root3 r3 = new Root3(world, 160, 2, 8f);



        Plank1 plank1 = new Plank1(world, 175, 16, 12f);

        BigRock2 final_rock = new BigRock2(world, 188, 3, 10f);
        Plank1 plank2 = new Plank1(world, 192, 15, 10f);

        Saw2 final_saw1 = new Saw2(world, 205, 2, 8f);
        Saw2 final_saw2 = new Saw2(world, 205, 16, 8f);


    }



}
