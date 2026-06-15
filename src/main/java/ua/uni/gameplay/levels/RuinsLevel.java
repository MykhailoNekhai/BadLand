package ua.uni.gameplay.levels;

import ua.uni.bootstrap.MainGame;

public class RuinsLevel extends Plevel {

    public RuinsLevel(MainGame game) {
        super(game);
        levelNumber = 2;
        finishLineX = 620f;
    }

    @Override
    protected void buildLevel() {
        spawnClone(2, 9);
        spawnClone(4, 9);
        spawnClone(6, 9);
        spawnClone(8, 9);

        // SECTION 1: ENTRY x=14-90
        scheduleObstacle("wood-stump-1", 14f, 2f, 0f, 4.4f);
        scheduleObstacle("branch-root-2", 20f, 2f, 0f, 4.8f);
        scheduleObstacle("wood-stump-3", 26f, 3f, -4f, 4.2f);
        scheduleObstacle("branch-root", 34f, 2f, 0f, 5.2f);
        scheduleObstacle("wood-stump-1", 42f, 2f, 4f, 4.6f);
        scheduleObstacle("branch-root-2", 49f, 3f, 0f, 4.3f);
        scheduleObstacle("wood-stump-3", 56f, 2f, -6f, 4.4f);
        scheduleObstacle("branch-root", 64f, 2f, 0f, 5f);
        scheduleObstacle("wood-stump-1", 72f, 3f, 5f, 4.3f);
        scheduleObstacle("branch-root-2", 79f, 2f, 0f, 4.6f);
        scheduleObstacle("wood-stump-3", 86f, 2f, -4f, 4.1f);

        scheduleObstacle("small-branch", 18f, 16f, 180f, 3.8f);
        scheduleObstacle("liana-1", 28f, 16f, 180f, 6.3f);
        scheduleObstacle("small-branch", 39f, 16f, 180f, 4.1f);
        scheduleObstacle("liana-1", 50f, 16f, 180f, 6f);
        scheduleObstacle("small-branch", 61f, 16f, 180f, 3.9f);
        scheduleObstacle("liana-1", 72f, 16f, 180f, 6.5f);
        scheduleObstacle("small-branch", 84f, 16f, 180f, 4f);

        scheduleObstacle("item-small", 38f, 9f, 0f, 1.4f);
        scheduleObstacle("item-small", 68f, 10f, 0f, 1.4f);

        // SECTION 2: TIGHT CORRIDOR x=92-165
        scheduleObstacle("branch-root", 94f, 2f, 0f, 5.4f);
        scheduleObstacle("wood-stump-1", 101f, 3f, 6f, 4.5f);
        scheduleObstacle("branch-root-2", 108f, 2f, 0f, 4.8f);
        scheduleObstacle("wood-stump-3", 115f, 3f, -5f, 4.2f);
        scheduleObstacle("branch-root", 123f, 2f, 0f, 5.1f);
        scheduleObstacle("wood-stump-1", 130f, 2f, 4f, 4.4f);
        scheduleObstacle("branch-root-2", 137f, 3f, 0f, 4.7f);
        scheduleObstacle("wood-stump-3", 145f, 2f, -5f, 4.3f);
        scheduleObstacle("branch-root", 153f, 2f, 0f, 5.3f);
        scheduleObstacle("wood-stump-1", 161f, 3f, 5f, 4.5f);

        scheduleObstacle("liana-1", 98f, 16f, 180f, 7f);
        scheduleObstacle("small-branch", 107f, 15.5f, 180f, 4.2f);
        scheduleObstacle("liana-1", 117f, 16f, 180f, 6.8f);
        scheduleObstacle("small-branch", 127f, 15.8f, 180f, 4f);
        scheduleObstacle("liana-1", 138f, 16f, 180f, 7.1f);
        scheduleObstacle("small-branch", 149f, 15.6f, 180f, 4.1f);
        scheduleObstacle("liana-1", 160f, 16f, 180f, 6.9f);

        scheduleObstacle("item-big", 112f, 13.5f, 0f, 1.4f);
        scheduleObstacle("item-clone", 146f, 12.8f, 0f, 1.4f);

        // SECTION 3: BARBWIRE x=168-248
        scheduleObstacle("branch-root", 170f, 2f, 0f, 5f);
        scheduleObstacle("wood-stump-3", 178f, 3f, -4f, 4.3f);
        scheduleObstacle("branch-root-2", 186f, 2f, 0f, 4.7f);
        scheduleObstacle("wood-stump-1", 194f, 2f, 5f, 4.5f);
        scheduleObstacle("branch-root", 202f, 3f, 0f, 5.2f);
        scheduleObstacle("wood-stump-3", 210f, 2f, -5f, 4.1f);
        scheduleObstacle("branch-root-2", 218f, 2f, 0f, 4.6f);
        scheduleObstacle("wood-stump-1", 226f, 3f, 5f, 4.4f);
        scheduleObstacle("branch-root", 234f, 2f, 0f, 5.1f);
        scheduleObstacle("wood-stump-3", 242f, 2f, -4f, 4.2f);

        scheduleObstacle("small-branch", 173f, 16f, 180f, 4f);
        scheduleObstacle("small-branch", 185f, 16f, 180f, 4.1f);
        scheduleObstacle("small-branch", 197f, 16f, 180f, 4f);
        scheduleObstacle("small-branch", 209f, 16f, 180f, 4.2f);
        scheduleObstacle("small-branch", 221f, 16f, 180f, 4f);
        scheduleObstacle("small-branch", 233f, 16f, 180f, 4.1f);
        scheduleObstacle("small-branch", 245f, 16f, 180f, 4f);

        scheduleObstacle("barbwire", 180f, 9f, 0f, 3.8f);
        scheduleObstacle("barbwire", 198f, 10f, 18f, 4f);
        scheduleObstacle("barbwire", 216f, 9f, -14f, 4f);
        scheduleObstacle("barbwire", 234f, 10f, 20f, 3.9f);

        scheduleObstacle("item-small", 188f, 3f, 0f, 1.4f);
        scheduleObstacle("item-big", 224f, 13f, 0f, 1.4f);

        // SECTION 4: DOORS x=252-332
        scheduleObstacle("branch-root", 254f, 2f, 0f, 5.2f);
        scheduleObstacle("wood-stump-1", 262f, 3f, 4f, 4.4f);
        scheduleObstacle("branch-root-2", 270f, 2f, 0f, 4.8f);
        scheduleObstacle("wood-stump-3", 278f, 3f, -4f, 4.2f);
        scheduleObstacle("branch-root", 286f, 2f, 0f, 5f);
        scheduleObstacle("wood-stump-1", 294f, 2f, 5f, 4.6f);
        scheduleObstacle("branch-root-2", 302f, 3f, 0f, 4.5f);
        scheduleObstacle("wood-stump-3", 310f, 2f, -5f, 4.3f);
        scheduleObstacle("branch-root", 318f, 2f, 0f, 5.1f);
        scheduleObstacle("wood-stump-1", 326f, 3f, 4f, 4.5f);

        scheduleObstacle("liana-1", 260f, 16f, 180f, 6.7f);
        scheduleObstacle("liana-1", 280f, 16f, 180f, 6.5f);
        scheduleObstacle("liana-1", 300f, 16f, 180f, 6.8f);
        scheduleObstacle("liana-1", 320f, 16f, 180f, 6.6f);

        scheduleObstacle("saloon-door", 272f, 9f, 0f, 5f);
        scheduleObstacle("saloon-door-2", 308f, 9f, 0f, 5f);

        scheduleObstacle("item-clone", 276f, 3f, 0f, 1.4f);
        scheduleObstacle("item-big", 312f, 13f, 0f, 1.4f);

        // SECTION 5: FIRST PROPELLERS x=336-425
        scheduleObstacle("branch-root", 338f, 2f, 0f, 5.3f);
        scheduleObstacle("wood-stump-3", 346f, 3f, -4f, 4.2f);
        scheduleObstacle("branch-root-2", 354f, 2f, 0f, 4.7f);
        scheduleObstacle("wood-stump-1", 362f, 2f, 5f, 4.4f);
        scheduleObstacle("branch-root", 370f, 3f, 0f, 5.1f);
        scheduleObstacle("wood-stump-3", 378f, 2f, -5f, 4.2f);
        scheduleObstacle("branch-root-2", 386f, 2f, 0f, 4.6f);
        scheduleObstacle("wood-stump-1", 394f, 3f, 5f, 4.5f);
        scheduleObstacle("branch-root", 402f, 2f, 0f, 5.2f);
        scheduleObstacle("wood-stump-3", 410f, 2f, -4f, 4.1f);
        scheduleObstacle("branch-root-2", 418f, 2f, 0f, 4.7f);

        scheduleObstacle("small-branch", 344f, 16f, 180f, 4.1f);
        scheduleObstacle("liana-1", 356f, 16f, 180f, 6.9f);
        scheduleObstacle("small-branch", 368f, 16f, 180f, 4f);
        scheduleObstacle("liana-1", 382f, 16f, 180f, 6.8f);
        scheduleObstacle("small-branch", 396f, 16f, 180f, 4.1f);
        scheduleObstacle("liana-1", 410f, 16f, 180f, 7f);
        scheduleObstacle("small-branch", 422f, 16f, 180f, 4f);

        scheduleSaw("propeller-small", 360f, 9f, 0f, 3.5f, 3f);
        scheduleSaw("propeller-small", 392f, 10f, 0f, 3.7f, -3f);

        scheduleObstacle("item-small", 350f, 12f, 0f, 1.4f);
        scheduleObstacle("item-big", 401f, 4f, 0f, 1.4f);

        // SECTION 6: DENSE MID x=430-520
        scheduleObstacle("branch-root", 432f, 2f, 0f, 5f);
        scheduleObstacle("wood-stump-1", 439f, 3f, 4f, 4.4f);
        scheduleObstacle("branch-root-2", 446f, 2f, 0f, 4.7f);
        scheduleObstacle("wood-stump-3", 453f, 3f, -4f, 4.2f);
        scheduleObstacle("branch-root", 461f, 2f, 0f, 5.3f);
        scheduleObstacle("wood-stump-1", 468f, 2f, 5f, 4.5f);
        scheduleObstacle("branch-root-2", 475f, 3f, 0f, 4.6f);
        scheduleObstacle("wood-stump-3", 482f, 2f, -5f, 4.1f);
        scheduleObstacle("branch-root", 490f, 2f, 0f, 5.1f);
        scheduleObstacle("wood-stump-1", 498f, 3f, 4f, 4.3f);
        scheduleObstacle("branch-root-2", 506f, 2f, 0f, 4.7f);
        scheduleObstacle("wood-stump-3", 514f, 2f, -4f, 4.2f);

        scheduleObstacle("liana-1", 436f, 16f, 180f, 7.1f);
        scheduleObstacle("small-branch", 447f, 15.6f, 180f, 4f);
        scheduleObstacle("liana-1", 458f, 16f, 180f, 6.8f);
        scheduleObstacle("small-branch", 470f, 15.5f, 180f, 4.1f);
        scheduleObstacle("liana-1", 482f, 16f, 180f, 7f);
        scheduleObstacle("small-branch", 494f, 15.8f, 180f, 4f);
        scheduleObstacle("liana-1", 507f, 16f, 180f, 6.9f);
        scheduleObstacle("small-branch", 518f, 15.5f, 180f, 4.1f);

        scheduleObstacle("barbwire", 452f, 9f, 0f, 3.8f);
        scheduleObstacle("barbwire", 486f, 10f, 15f, 4f);
        scheduleObstacle("barbwire", 512f, 9f, -12f, 3.8f);

        scheduleObstacle("item-clone", 462f, 13f, 0f, 1.4f);
        scheduleObstacle("item-small", 500f, 4f, 0f, 1.4f);

        // SECTION 7: FINALE x=526-620
        scheduleObstacle("branch-root", 528f, 2f, 0f, 5.1f);
        scheduleObstacle("wood-stump-1", 536f, 3f, 4f, 4.5f);
        scheduleObstacle("branch-root-2", 544f, 2f, 0f, 4.6f);
        scheduleObstacle("wood-stump-3", 552f, 3f, -4f, 4.2f);
        scheduleObstacle("branch-root", 560f, 2f, 0f, 5.2f);
        scheduleObstacle("wood-stump-1", 568f, 2f, 5f, 4.4f);
        scheduleObstacle("branch-root-2", 576f, 3f, 0f, 4.7f);
        scheduleObstacle("wood-stump-3", 584f, 2f, -4f, 4.1f);
        scheduleObstacle("branch-root", 592f, 2f, 0f, 5.1f);
        scheduleObstacle("wood-stump-1", 600f, 3f, 4f, 4.4f);
        scheduleObstacle("branch-root-2", 608f, 2f, 0f, 4.6f);
        scheduleObstacle("wood-stump-3", 616f, 2f, -4f, 4.2f);

        scheduleObstacle("small-branch", 534f, 16f, 180f, 4f);
        scheduleObstacle("liana-1", 548f, 16f, 180f, 6.7f);
        scheduleObstacle("small-branch", 564f, 16f, 180f, 4.1f);
        scheduleObstacle("liana-1", 580f, 16f, 180f, 6.8f);
        scheduleObstacle("small-branch", 596f, 16f, 180f, 4f);
        scheduleObstacle("liana-1", 612f, 16f, 180f, 6.7f);

        scheduleSaw("propeller-small", 556f, 9f, 0f, 3.6f, 3f);
        scheduleObstacle("saloon-door", 590f, 9f, 0f, 5f);

        scheduleObstacle("item-big", 570f, 13f, 0f, 1.4f);
        scheduleObstacle("item-clone", 604f, 4f, 0f, 1.4f);

        createPortal(620f);
    }
}
