package ua.uni.gameplay.levels;

import ua.uni.bootstrap.MainGame;
import ua.uni.gameplay.factory.EntityFactory;

public class Poligon2Level extends Plevel {




    public Poligon2Level(MainGame game) {
        super(game);
        levelNumber = 1;
    }

    // ОГОЛОШЕННЯ ОБ'ЄКТІВ
    @Override
    protected void buildLevel() {
     //   spawnClone(2, 9);
     //   spawnClone(4, 9);
       // spawnClone(6, 9);
        spawnClone(8, 1);

        EntityFactory.createObstacle(engine, world, "hammer-1", 8f, 15f, 0f, 1.4f);
        EntityFactory.createObstacle(engine, world, "wood-stump-1", 20f, 0f, 90f, 2f);

        EntityFactory.createObstacle(engine, world, "item-clone", 25f, 1f, 0f, 1.4f);
    EntityFactory.createSaw(engine, world, "gear-2", 0f, 10f, 0f, 20f, 0.5f);


    EntityFactory.createObstacle(engine, world, "roundy-pipes-large", 5f, 15f, -45f, 7f);
  //      EntityFactory.createObstacle(engine, world, "pipe-corner-1", 10f, 15f, 0f, 5f);
        EntityFactory.createObstacle(engine, world, "wood-branch-5", 50f, 15f, 180f, 20f);

        EntityFactory.createSaw(engine, world, "propeller-slim-large", 50f, 0f, 0f, 8f, -1f);

        EntityFactory.createObstacle(engine, world, "branch-tip-11", 55f, 0f, 0f, 8f);

        EntityFactory.createSaw(engine, world, "shredder-large", 60f, 0f, 0f, 8f, 4f);
        EntityFactory.createObstacle(engine, world, "item-clone", 70f, 1f, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "item-big", 75f, 0f, 0f, 2f);

        EntityFactory.createSaw(engine, world, "shredder-large", 80f, 0f, 0f, 2f, 1f);



        EntityFactory.createSaw(engine, world, "wood-branch-16", 65f, 14f, 0f, 30f, 4f);

        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 110f, 2f, 90f, 5f);

        EntityFactory.createSaw(engine, world, "propeller-small", 109.5f, 4f, 90f, 5f, 2f);


        EntityFactory.createSaw(engine, world, "propeller-small", 110.5f, 14f, -90f, 5f, -2f);

        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 110f, 16f, -90f, 5f);

        EntityFactory.createSaw(engine, world, "gear", 80f, 15f, 0f, 20f, 0.5f);
        EntityFactory.createObstacle(engine, world, "rock-5", 90f, 0f, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 92f, 17f, 30f, 20f);

        EntityFactory.createObstacle(engine, world, "wire-4", 110f, 2f, 10f, 15f);

        EntityFactory.createObstacle(engine, world, "wire-2", 115f, 15f, 10f, 15f);

        EntityFactory.createObstacle(engine, world, "item-small", 118f, 3f, 10f, 2f);
        EntityFactory.createObstacle(engine, world, "liana-1", 125f, 0f, 180f, 15f);

        EntityFactory.createSaw(engine, world, "shredder-large", 130f, 15f, 180f, 15f, 3f);

        EntityFactory.createObstacle(engine, world, "item-slow", 133f, 3f, 0f, 2f);

        EntityFactory.createSaw(engine, world, "shredder-large", 137f, 1.5f, 0f, 5f, 5f);

        EntityFactory.createSaw(engine, world, "shredder-large", 140f, 4f, 0f, 5f, 5f);

        EntityFactory.createSaw(engine, world, "shredder-large", 143f, 7f, 0f, 5f, 5f);

        EntityFactory.createObstacle(engine, world, "wood-branch-9", 142f, 2f, 45f, 12f);

        EntityFactory.createObstacle(engine, world, "wood-branch-9", 144f, 4f, 45f, 12f);

        EntityFactory.createObstacle(engine, world, "wood-branch-10", 148, 6f, 0f, 12f);

        EntityFactory.createObstacle(engine, world, "item-small", 148f, 2f, 0f, 1.5f);

        EntityFactory.createObstacle(engine, world, "rock-5", 160, 15f, 180f, 15f);

        EntityFactory.createObstacle(engine, world, "item-big", 160, 2f, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "item-big", 164, 2f, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "wood-branch-7", 173, 2f, 90f, 15f);

        EntityFactory.createObstacle(engine, world, "pipe-straight-1", 173, 7f, 0f, 5f);

        EntityFactory.createObstacle(engine, world, "pipe-straight-1", 177, 7f, 0f, 5f);

        EntityFactory.createObstacle(engine, world, "grass-1", 177, 5f, 0f, 9f);

        EntityFactory.createObstacle(engine, world, "pipe-straight-1", 181, 7f, 0f, 5f);
EntityFactory.createObstacle(engine, world, "branch-tip-7", 181, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "item-clone", 182, 9f, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 185, 6.5f, 150f, 5f);
        EntityFactory.createObstacle(engine, world, "saloon-door-2", 193, 8f, 180f, 2f);

        EntityFactory.createObstacle(engine, world, "item-clone", 186, 2f, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "wood-branch-10", 200, 0f, 0f, 15f);

        EntityFactory.createSaw(engine, world, "propeller-large", 197, 3f, 0f, 5f, -4f);


        EntityFactory.createObstacle(engine, world, "rock-4", 208, 5f, 0f, 8f);

        EntityFactory.createSaw(engine, world, "propeller-slim-large", 218, 11f, 0f, 10f, 0.5f);

        EntityFactory.createObstacle(engine, world, "wood-stump-2", 230, 15f, 0f, 5f);

       EntityFactory.createObstacle(engine, world, "tube-shallow-1", 235, 0f, 0f, 5f);

        EntityFactory.createObstacle(engine, world, "item-speed", 239, 1f, 0f, 2f);
       EntityFactory.createObstacle(engine, world, "wood-branch-3", 250, 15, 0f, 15f);

        EntityFactory.createObstacle(engine, world, "item-small", 243, 15, 0f, 2f);

        EntityFactory.createObstacle(engine, world, "item-small", 260, 3, 0f, 2f);


        EntityFactory.createObstacle(engine, world, "wood-branch-16", 270, 15, 0f, 15f);

        EntityFactory.createSaw(engine, world, "gear", 270, 4, 0f, 15f, -0.3f);



        createPortal(300f);


    }



}