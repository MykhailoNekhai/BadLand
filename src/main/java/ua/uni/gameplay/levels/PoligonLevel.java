package ua.uni.gameplay.levels;

import ua.uni.bootstrap.GameServices;

public class PoligonLevel extends Plevel {

    public PoligonLevel(GameServices services) {
        super(services);
        levelNumber = 2;
    }

    @Override
    protected void buildLevel() {
        spawnClone(2f, 9f);
        spawnClone(7f, 9f);

        // Вхід: коріння й труби одразу задають стиль локації.
        obstacle("roundy-pipes-large", 13f, 15.7f, -38f, 7f);
        obstacle("wood-stump-1", 24f, 1.0f, 90f, 3f);
        obstacle("item-clone", 27f, 8.8f, 0f, 1.4f);
        obstacle("roundy-pipes-large-2", 32f, 2.2f, 144f, 6f);
        obstacle("wood-branch-5", 43f, 16.2f, 180f, 14f);
        obstacle("branch-tip-11", 51f, 0.9f, 0f, 7f);
        obstacle("liana-4", 56f, 16.4f, 180f, 8f);
        obstacle("wood-branch-10", 64f, 1.1f, 0f, 10f);
        obstacle("wood-stump-2", 72f, 1.4f, 0f, 3.5f);
        obstacle("branch-tip-7", 75f, 16.8f, 180f, 7f);

        // Низький тунель: small перед довгою щілиною з коріння.
        obstacle("item-small", 86f, 8.7f, 0f, 1.4f);
        obstacle("wood-branch-16", 96f, 16.4f, 180f, 14f);
        obstacle("branch-tip-6", 105f, 1.0f, 0f, 6f);
        obstacle("liana-1", 115f, 16.1f, 180f, 12f);
        obstacle("wood-branch-3", 124f, 1.2f, 0f, 11f);
        obstacle("branch-tip-10", 134f, 16.7f, 180f, 6f);
        obstacle("wood-stump-3", 143f, 1.5f, 0f, 4f);
        obstacle("wood-branch-8", 151f, 16.5f, 180f, 8.5f);
        obstacle("branch-tip-3", 159f, 1.1f, 0f, 6.5f);

        // Перша трубна брама: рухома загроза всередині рамки.
        obstacle("item-big", 171f, 8.8f, 0f, 1.4f);
        obstacle("pipe-corner-1", 184f, 2.0f, 90f, 5.2f);
        obstacle("pipe-corner-1", 184f, 16.0f, -90f, 5.2f);
        obstacle("pipe-straight-1", 197f, 2.4f, 0f, 5.5f);
        obstacle("pipe-straight-1", 197f, 15.6f, 0f, 5.5f);
        saw("propeller-small", 207f, 9f, 0f, 3.1f, 1.4f);
        obstacle("pipe-straight-2", 216f, 2.5f, 0f, 5.4f);
        obstacle("pipe-straight-2", 216f, 15.5f, 0f, 5.4f);
        obstacle("tube-steep-1", 229f, 1.1f, 0f, 5.2f);
        obstacle("tube-tip", 238f, 16.6f, 180f, 5.6f);
        obstacle("pipe-chain-1", 247f, 16.3f, 180f, 7f);
        obstacle("pipe-chain-2", 253f, 1.6f, 0f, 7f);

        // Кам'яний завал: чергування підлоги й стелі формує маршрут.
        obstacle("item-clone", 264f, 8.8f, 0f, 1.4f);
        obstacle("item-small", 276f, 8.8f, 0f, 1.4f);
        obstacle("rock-5", 288f, 1.6f, 0f, 4.2f);
        obstacle("rock-4", 298f, 16.2f, 180f, 4.7f);
        obstacle("rock-7", 309f, 1.4f, 0f, 4.2f);
        obstacle("rock-6", 320f, 16.3f, 180f, 4.4f);
        obstacle("rock-10", 331f, 1.4f, 0f, 4.1f);
        obstacle("rock-11", 342f, 16.4f, 180f, 4.2f);
        obstacle("wood-stump-2", 353f, 1.5f, 0f, 4.2f);
        obstacle("wood-branch-7", 363f, 16.5f, 180f, 8f);
        obstacle("branch-tip-8", 372f, 1.0f, 0f, 6f);

        // Небезпечна камера: одна deadly-пила, але вона закріплена між краями.
        obstacle("item-speed", 384f, 8.8f, 0f, 1.4f);
        obstacle("tube-shallow-1", 397f, 1.0f, 0f, 5.4f);
        obstacle("wood-branch-5", 409f, 16.2f, 180f, 12.5f);
        obstacle("branch-tip-11", 419f, 1.0f, 0f, 6f);
        obstacle("pipe-corner-1", 430f, 2.0f, 90f, 4.8f);
        obstacle("pipe-corner-1", 430f, 16.0f, -90f, 4.8f);
        saw("shredder-large", 430f, 9f, 0f, 3.3f, 2.3f);
        obstacle("pipe-straight-1", 442f, 2.5f, 0f, 5.4f);
        obstacle("pipe-straight-1", 442f, 15.5f, 0f, 5.4f);
        obstacle("wood-branch-16", 455f, 1.2f, 0f, 13f);
        obstacle("liana-3", 465f, 16.2f, 180f, 10f);
        obstacle("wood-stump-4", 475f, 1.4f, 0f, 3.8f);

        // Ліс ліан: багато статичних форм, але без випадкової левітації.
        obstacle("item-clone", 486f, 8.8f, 0f, 1.4f);
        obstacle("liana-2", 497f, 16.3f, 180f, 10f);
        obstacle("wood-branch-9", 505f, 1.1f, 0f, 11f);
        obstacle("liana-5", 514f, 16.2f, 180f, 10f);
        obstacle("branch-tip-4", 523f, 1.0f, 0f, 6f);
        obstacle("wood-branch-15", 532f, 16.4f, 180f, 9f);
        obstacle("wood-stump-5", 542f, 1.4f, 0f, 4f);
        obstacle("branch-tip-9", 551f, 16.7f, 180f, 6f);
        obstacle("wood-branch-4", 560f, 1.1f, 0f, 10f);

        // Подвійна брама: дві рухомі перешкоди, кожна має свою рамку.
        obstacle("item-big", 572f, 8.8f, 0f, 1.4f);
        obstacle("pipe-corner-1", 586f, 2.0f, 90f, 5f);
        obstacle("pipe-corner-1", 586f, 16.0f, -90f, 5f);
        saw("propeller-small", 596f, 8.9f, 0f, 2.8f, -1.2f);
        obstacle("pipe-straight-2", 606f, 2.5f, 0f, 5.2f);
        obstacle("pipe-straight-2", 606f, 15.5f, 0f, 5.2f);
        saw("gear", 618f, 9f, 0f, 4.1f, 0.8f);
        obstacle("pipe-corner-1", 630f, 2.0f, 90f, 5f);
        obstacle("pipe-corner-1", 630f, 16.0f, -90f, 5f);
        obstacle("tube-connection", 641f, 1.3f, 0f, 5.4f);
        obstacle("tube-part", 651f, 16.5f, 180f, 5.2f);
        obstacle("item-speed", 660f, 8.8f, 0f, 1.4f);

        // Фінальне коріння: щільно, але бонуси ведуть через потрібну форму.
        obstacle("item-small", 672f, 8.8f, 0f, 1.4f);
        obstacle("wood-branch-1", 684f, 16.2f, 180f, 11f);
        obstacle("branch-tip-2", 693f, 1.0f, 0f, 6f);
        obstacle("wood-branch-2", 703f, 1.2f, 0f, 11f);
        obstacle("branch-tip-12", 713f, 16.7f, 180f, 6f);
        obstacle("rock-3", 723f, 1.5f, 0f, 4f);
        obstacle("rock-8", 733f, 16.2f, 180f, 3.6f);
        obstacle("item-big", 743f, 8.8f, 0f, 1.4f);
        obstacle("wood-stump-3", 754f, 1.4f, 0f, 4.2f);
        obstacle("wood-branch-6", 764f, 16.5f, 180f, 8.5f);

        // Остання рамка перед порталом, щоб фініш не був пустою прямою.
        obstacle("item-clone", 776f, 8.8f, 0f, 1.4f);
        obstacle("pipe-straight-1", 788f, 2.5f, 0f, 5.4f);
        obstacle("pipe-straight-1", 788f, 15.5f, 0f, 5.4f);
        saw("propeller-small", 800f, 9f, 0f, 2.7f, 1.0f);
        obstacle("wood-branch-10", 812f, 1.1f, 0f, 10f);
        obstacle("wood-branch-5", 824f, 16.2f, 180f, 12f);
        obstacle("branch-tip-11", 834f, 1.0f, 0f, 6f);

        createPortal(850f);
    }

    private void obstacle(String name, float x, float y, float angle, float size) {
        scheduleObstacle(name, x, y, angle, size);
    }

    private void saw(String name, float x, float y, float angle, float size, float spinSpeed) {
        scheduleSaw(name, x, y, angle, size, spinSpeed);
    }
}
