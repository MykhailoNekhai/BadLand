package ua.uni.gameplay.levels;

import ua.uni.gameplay.entity.Plank1;
import ua.uni.bootstrap.MainGame;

public class RuinsLevel extends Plevel {

    public RuinsLevel(MainGame game) {
        super(game);
        levelNumber = 2;
        finishLineX = 255f;
    }

    @Override
    protected void buildLevel() {
        spawnClone(2, 9);
        spawnClone(4, 9);
        spawnClone(6, 9);
        spawnClone(8, 9);
        new Plank1(world, 40, 8, 10f);
        new Plank1(world, 90, 10, 12f);
        new Plank1(world, 145, 6, 9f);
        new Plank1(world, 200, 12, 11f);
    }
}
