package ua.uni.gameplay.levels;

import ua.uni.bootstrap.GameServices;
import ua.uni.gameplay.factory.EntityFactory;

public class Poligon4Level extends Plevel {

    public Poligon4Level(GameServices services) {
        super(services);
        levelNumber = 3; 
    }

    @Override
    protected void buildLevel() {
        spawnClone(5, 9);
        
        // --- 1. Резкий подъем наверх ---
        // Строим огромную гору снизу (функционально блокирует путь)
        EntityFactory.createObstacle(engine, world, "huge-curve", 15f, 0f, 0f, 15f);
        EntityFactory.createObstacle(engine, world, "rock-10", 18f, 2f, 0f, 12f);
        EntityFactory.createObstacle(engine, world, "wood-branch-5", 20f, 7f, -30f, 10f); // Ветки торчат из горы
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 12f, 0f, 0f, 8f);
        
        // Потолок свободен, но украшен (игрок должен лететь здесь, Y ~ 15)
        EntityFactory.createObstacle(engine, world, "liana-2", 15f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "sponge-4", 18f, 18f, 180f, 4f);
        
        // --- 2. Резкий спуск вниз ---
        // Теперь массивная скала растет с потолка (функционально давит вниз)
        EntityFactory.createObstacle(engine, world, "rock-10", 35f, 18f, 180f, 15f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 40f, 18f, 180f, 14f);
        EntityFactory.createObstacle(engine, world, "wood-stump-3", 35f, 13.5f, 180f, 8f); // Нависает прямо над проходом
        
        // Узкий проход в самом низу (Y ~ 2)
        EntityFactory.createObstacle(engine, world, "item-small", 30f, 3f, 0f, 2f); 
        EntityFactory.createObstacle(engine, world, "grass-1", 35f, 0f, 0f, 4f);
        EntityFactory.createObstacle(engine, world, "swamp-tip", 38f, 0f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "rock-3", 42f, 0f, 0f, 4f);
        
        // --- 3. Индастриал: Трубный серпантин (Зигзаг) ---
        // Заставляем лететь змейкой вверх-вниз
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 50f, 0f, 90f, 12f); // Торчит снизу вверх
        EntityFactory.createSaw(engine, world, "propeller-small", 50f, 14f, 0f, 6f, 4f); // Пила над трубой
        EntityFactory.createObstacle(engine, world, "filler-1", 52f, 0f, 0f, 5f); // Мусор
        
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 60f, 18f, 90f, 12f); // Торчит сверху вниз
        EntityFactory.createSaw(engine, world, "propeller-small", 60f, 4f, 0f, 6f, -4f); // Пила под трубой
        EntityFactory.createObstacle(engine, world, "wire-3", 58f, 18f, 180f, 6f); // Провода сверху
        
        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 70f, 0f, 0f, 10f); // Загиб снизу, снова гонит вверх
        EntityFactory.createObstacle(engine, world, "leca", 70f, 0f, 0f, 6f); 
        
        // Зона с клонами перед мясорубкой
        EntityFactory.createObstacle(engine, world, "item-clone", 75f, 12f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-clone", 76f, 14f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-clone", 77f, 10f, 0f, 2f);
        
        // --- 4. Мясорубка на подъеме ---
        EntityFactory.createObstacle(engine, world, "rock-3", 85f, 0f, 0f, 12f); // Гора снизу
        EntityFactory.createSaw(engine, world, "shredder-large", 85f, 13f, 0f, 8f, 2f); // Шредер прямо над горой
        EntityFactory.createSaw(engine, world, "gear", 92f, 17f, 0f, 8f, -1f); // Большая шестерня на потолке
        EntityFactory.createObstacle(engine, world, "brick-filler", 90f, 0f, 0f, 8f);
        
        // --- 5. Глубокий колодец ---
        // Сверху толстый слой камней и труб, проход только по самому полу (Y=1-4)
        EntityFactory.createObstacle(engine, world, "rock-10", 105f, 18f, 180f, 16f);
        EntityFactory.createObstacle(engine, world, "tube-part", 105f, 10f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "tube-steep-1", 112f, 18f, 180f, 14f);
        EntityFactory.createObstacle(engine, world, "rock-5", 118f, 18f, 180f, 10f);
        
        EntityFactory.createObstacle(engine, world, "wire-1", 108f, 18f, 180f, 12f); // Провода свисают до самого низа!
        EntityFactory.createObstacle(engine, world, "wire-4", 115f, 18f, 180f, 12f);
        
        EntityFactory.createObstacle(engine, world, "item-speed", 102f, 2f, 0f, 2f); // Пролететь на скорости под этим завалом
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 110f, 0f, 0f, 4f); // Небольшие ухабы внизу
        
        // --- 6. Узкий коридор пил по центру ---
        // Горы сверху и снизу оставляют ровный коридор посередине (Y=8-10)
        EntityFactory.createObstacle(engine, world, "huge-curve", 130f, 0f, 0f, 10f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 130f, 18f, 180f, 10f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 145f, 0f, 0f, 10f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 145f, 18f, 180f, 10f);
        
        // Декоративные наросты на этих горах
        EntityFactory.createObstacle(engine, world, "sponge-9", 130f, 18f, 180f, 4f);
        EntityFactory.createObstacle(engine, world, "rock-11", 145f, 0f, 0f, 5f);
        
        EntityFactory.createSaw(engine, world, "propeller-large", 130f, 9f, 0f, 6f, 3f); // Прямо в центре коридора
        EntityFactory.createSaw(engine, world, "propeller-large", 145f, 9f, 0f, 6f, -3f);
        
        EntityFactory.createObstacle(engine, world, "item-superclone", 125f, 9f, 0f, 2f);
        
        // --- 7. Открытая местность и гигантская шестерня ---
        EntityFactory.createObstacle(engine, world, "wood-branch-2", 160f, 0f, 30f, 14f);
        EntityFactory.createObstacle(engine, world, "wood-branch-10", 165f, 18f, 180f, 12f);
        
        // Эта шестерня перекрывает центр (Y=9), заставляя игрока выбрать: лететь по самому низу или верху
        EntityFactory.createSaw(engine, world, "gear-2", 175f, 9f, 0f, 16f, 0.5f);
        
        // Направляем бонусами
        EntityFactory.createObstacle(engine, world, "item-small", 170f, 2f, 0f, 2f); // Облет снизу
        EntityFactory.createObstacle(engine, world, "item-small", 170f, 16f, 0f, 2f); // Облет сверху
        
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 175f, 0f, 0f, 5f);
        EntityFactory.createObstacle(engine, world, "sponge-5", 175f, 18f, 180f, 5f);
        
        // --- 8. Густые джунгли (финал) ---
        // Густые заросли, сквозь которые еле видно путь
        EntityFactory.createObstacle(engine, world, "liana-5", 190f, 18f, 180f, 15f);
        EntityFactory.createObstacle(engine, world, "liana-3", 195f, 18f, 180f, 18f); // Лиана почти до пола!
        EntityFactory.createObstacle(engine, world, "liana-4", 200f, 18f, 180f, 12f);
        
        EntityFactory.createObstacle(engine, world, "grass-1", 190f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "branch-root", 195f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "rock-11", 200f, 0f, 0f, 8f);
        
        EntityFactory.createObstacle(engine, world, "item-big", 185f, 9f, 0f, 2f); // Увеличиваем игрока перед джунглями для сложности
        
        // Свисающие пилы, скрытые в лианах
        EntityFactory.createSaw(engine, world, "propeller-slim-large", 192f, 12f, 0f, 8f, 2f);
        EntityFactory.createSaw(engine, world, "propeller-slim-large", 198f, 6f, 0f, 8f, -2f);
        
        createPortal(215f);
    }
}
