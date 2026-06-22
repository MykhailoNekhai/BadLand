package ua.uni.gameplay.levels;

import ua.uni.bootstrap.GameServices;
import ua.uni.gameplay.factory.EntityFactory;

public class Poligon5Level extends Plevel {

    public Poligon5Level(GameServices services) {
        super(services);
        levelNumber = 4; // Этот уровень привязан ко четвертой кнопке
    }

    @Override
    protected void buildLevel() {
        spawnClone(5, 9);
        
        // --- 1. Военная база / Минное поле ---

        EntityFactory.createObstacle(engine, world, "item-superclone", 12f, 11f, 0f, 2f);

        // Декор на входе
        EntityFactory.createObstacle(engine, world, "wire-1", 15f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "brick-filler", 16f, 0f, 0f, 5f);
        EntityFactory.createObstacle(engine, world, "barbwire", 18f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "liana-3", 18f, 18f, 180f, 10f); // Лиана
        EntityFactory.createObstacle(engine, world, "pipe-short", 19f, 18f, 180f, 4f); // Труба
        
        // Сгущение мин (плотное минное поле)
        EntityFactory.createObstacle(engine, world, "mine", 22f, 5f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 24f, 13f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 26f, 2f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 28f, 16f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 30f, 8f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 34f, 4f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 36f, 14f, 0f, 3f);
        
        // Декор минного поля
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 25f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "barbwire", 32f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "liana-4", 34f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 38f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "sponge-4", 38f, 18f, 180f, 2f);
        
        // --- 2. Мясорубка на скорости ---
        EntityFactory.createObstacle(engine, world, "tube-connection", 42f, 18f, 180f, 5f); 
        EntityFactory.createObstacle(engine, world, "liana-1", 44f, 18f, 180f, 8f); // Лианы между пилами
        EntityFactory.createSaw(engine, world, "shredder-large", 45f, 13f, 0f, 7f, 5f);
        
        EntityFactory.createObstacle(engine, world, "mine", 48f, 1f, 0f, 2.5f); // Мина внизу
        EntityFactory.createObstacle(engine, world, "mine", 49f, 16f, 0f, 2.5f); // Мина вверху
        EntityFactory.createObstacle(engine, world, "rock-8", 48f, 0f, 0f, 1f);
        
        EntityFactory.createSaw(engine, world, "shredder-large", 52f, 5f, 0f, 7f, -5f);
        
        EntityFactory.createObstacle(engine, world, "wire-3", 55f, 18f, 180f, 6f); 
        EntityFactory.createObstacle(engine, world, "liana-2", 56f, 18f, 180f, 10f); 
      //  EntityFactory.createObstacle(engine, world, "mine", 56f, 8f, 0f, 2.5f); // Мина по центру
        
        EntityFactory.createSaw(engine, world, "shredder-large", 59f, 13f, 0f, 7f, 5f);
        EntityFactory.createObstacle(engine, world, "leca", 62f, 0f, 0f, 6f); 
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 63f, 18f, 180f, 8f);

        EntityFactory.createObstacle(engine, world, "item-superclone", 65f, 9f, 0f, 2f); // Даем армию

        EntityFactory.createObstacle(engine, world, "item-superclone", 67f, 9f, 0f, 2f); // Даем армию
      //  EntityFactory.createObstacle(engine, world, "item-speed", 68f, 9f, 0f, 2f); // Еще скорости!
        EntityFactory.createObstacle(engine, world, "pipe-short", 70f, 18f, 180f, 6f);
        
        // --- 3. Игольное ушко (Зажим между шестернями) ---
        EntityFactory.createObstacle(engine, world, "liana-5", 76f, 18f, 180f, 12f); // Густые лианы перед шестернями
        EntityFactory.createSaw(engine, world, "gear-2", 80f, 2f, 0f, 13f, -1f);
        EntityFactory.createSaw(engine, world, "gear-2", 80f, 16f, 0f, 13f, 1f);
        
//        EntityFactory.createObstacle(engine, world, "mine", 80f, 9f, 0f, 1.5f); // Маленькая подлянка-мина прямо между шестернями
        
        EntityFactory.createObstacle(engine, world, "brick-filler", 85f, 0f, 0f, 6f); 
        EntityFactory.createObstacle(engine, world, "tube-part", 86f, 18f, 180f, 5f);
        
        // --- 4. Зона колючек и шипов ---
        EntityFactory.createObstacle(engine, world, "rock-4", 88f, 18f, 180f, 5f); 
        EntityFactory.createObstacle(engine, world, "spike-bot", 92f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "liana-2", 94f, 18f, 180f, 8f); 
        EntityFactory.createObstacle(engine, world, "item-small", 95f, 9f, 0f, 2f); 
        EntityFactory.createObstacle(engine, world, "sponge-2", 98f, 0f, 0f, 3f); 
        EntityFactory.createObstacle(engine, world, "spike-bot", 100f, 18f, 180f, 8f);
        
        // Минное ущелье
        EntityFactory.createObstacle(engine, world, "mine", 104f, 12f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 106f, 6f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 108f, 15f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 110f, 2f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "mine", 112f, 10f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "sponge-8", 109f, 18f, 180f, 4f); 
        EntityFactory.createObstacle(engine, world, "barbwire", 115f, 0f, 0f, 10f); 
        EntityFactory.createObstacle(engine, world, "liana-3", 114f, 18f, 180f, 12f);
        
        // --- 5. Заводские трубы и шредеры ---
        EntityFactory.createObstacle(engine, world, "wire-4", 120f, 18f, 180f, 10f); 
        EntityFactory.createObstacle(engine, world, "huge-curve", 125f, 18f, 180f, 10f); 
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 130f, 0f, 90f, 8f); 
        EntityFactory.createObstacle(engine, world, "liana-1", 132f, 18f, 180f, 8f);
        EntityFactory.createSaw(engine, world, "shredder-large", 135f, 12f, 0f, 9f, 2f);
        
        EntityFactory.createObstacle(engine, world, "mine", 140f, 9f, 0f, 4f); // Мина в центре трубы
        
        EntityFactory.createObstacle(engine, world, "huge-curve", 145f, 0f, 0f, 10f); 
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 150f, 18f, 90f, 8f); 
        EntityFactory.createObstacle(engine, world, "liana-4", 152f, 18f, 180f, 10f);
        EntityFactory.createSaw(engine, world, "shredder-large", 155f, 6f, 0f, 9f, -2f);
        EntityFactory.createObstacle(engine, world, "liana-5", 158f, 18f, 180f, 12f); 
        
        // --- 6. Арка и замедление ---
        EntityFactory.createObstacle(engine, world, "arc", 170f, 0f, 0f, 15f); 
        EntityFactory.createObstacle(engine, world, "arc", 170f, 18f, 180f, 15f); 
        EntityFactory.createObstacle(engine, world, "item-slow", 170f, 9f, 0f, 2f); 
        
        // Лианы и провода свисают с арки
        EntityFactory.createObstacle(engine, world, "liana-3", 168f, 18f, 180f, 14f);
        EntityFactory.createObstacle(engine, world, "wire-4", 172f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "pipe-short", 174f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "leca", 175f, 0f, 0f, 8f);
        
        // --- 7. Ветряной туннель (Сплошные шредеры) ---
        EntityFactory.createObstacle(engine, world, "item-big", 185f, 9f, 0f, 2f); 
        
        EntityFactory.createObstacle(engine, world, "rock-11", 190f, 0f, 0f, 8f); 
        EntityFactory.createObstacle(engine, world, "wire-2", 192f, 18f, 180f, 10f); 
        EntityFactory.createObstacle(engine, world, "liana-2", 193f, 18f, 180f, 8f); 
        
        EntityFactory.createSaw(engine, world, "shredder-large", 195f, 5f, 0f, 6f, 5f);
        EntityFactory.createSaw(engine, world, "shredder-large", 195f, 13f, 0f, 6f, -5f);
        
        EntityFactory.createObstacle(engine, world, "sponge-9", 200f, 18f, 180f, 5f); 
//        EntityFactory.createObstacle(engine, world, "mine", 200f, 9f, 0f, 2f); // Мина прямо между парами шредеров
        EntityFactory.createObstacle(engine, world, "liana-5", 202f, 18f, 180f, 15f); 
        
        EntityFactory.createSaw(engine, world, "shredder-large", 205f, 5f, 0f, 6f, -5f);
        EntityFactory.createSaw(engine, world, "shredder-large", 205f, 13f, 0f, 6f, 5f);
        
        EntityFactory.createObstacle(engine, world, "mine", 215f, 9f, 0f, 5f); // Гигантская мина в центре
        EntityFactory.createObstacle(engine, world, "mine", 215f, 2f, 0f, 3f); // Снизу
        EntityFactory.createObstacle(engine, world, "mine", 215f, 16f, 0f, 3f); // Сверху
        
        EntityFactory.createObstacle(engine, world, "wood-branch-2", 218f, 18f, 180f, 10f); 
        EntityFactory.createObstacle(engine, world, "pipe-chain-2", 220f, 18f, 180f, 4f);
        
        // --- 8. Финал (Спасение) ---
        EntityFactory.createObstacle(engine, world, "item-superclone", 225f, 9f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-speed", 228f, 9f, 0f, 2f);
        
        EntityFactory.createObstacle(engine, world, "tube-part", 235f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "tube-part", 235f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "liana-3", 236f, 18f, 180f, 15f); // Лиана перекрывает трубу
        EntityFactory.createObstacle(engine, world, "pipe-chain-2", 240f, 18f, 180f, 2f);
        EntityFactory.createObstacle(engine, world, "rock-10", 245f, 0f, 0f, 2f);
        
        EntityFactory.createObstacle(engine, world, "grass-1", 250f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "mine", 252f, 5f, 0f, 2f); // Прощальные мины
        EntityFactory.createObstacle(engine, world, "mine", 252f, 13f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "liana-1", 255f, 18f, 180f, 10f);
        
        createPortal(270f);
    }
}
