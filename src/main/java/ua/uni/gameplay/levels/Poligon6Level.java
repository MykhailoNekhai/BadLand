package ua.uni.gameplay.levels;

import ua.uni.bootstrap.GameServices;
import ua.uni.gameplay.factory.EntityFactory;

public class Poligon6Level extends Plevel {

    public Poligon6Level(GameServices services) {
        super(services);
        levelNumber = 5; 
    }

    @Override
    protected void buildLevel() {
        spawnClone(5, 9);
        
        // ==========================================
        // БИОМ 1: Зеленая ловушка (0 - 120)
        // ==========================================
        
        // Вход в лес - статика плотно наложена друг на друга (красивые кучи)
        EntityFactory.createObstacle(engine, world, "grass-1", 10f, 0f, 0f, 18f);
        EntityFactory.createObstacle(engine, world, "liana-1", 12f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "rock-10", 15f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 18f, 0f, 0f, 10f);
        
        // Сложная преграда из гигантских лопухов
        EntityFactory.createObstacle(engine, world, "burdock-large", 25f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "burdock-large", 28f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "burdock-large", 31f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "item-clone", 28f, 9f, 0f, 2f);
        
        // Корни и губки
        EntityFactory.createObstacle(engine, world, "branch-root", 40f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "sponge-9", 42f, 19f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 40f, 0f, 0f, 8f);
        
        EntityFactory.createObstacle(engine, world, "item-superclone", 48f, 5f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-small", 50f, 5f, 0f, 2f);
        
        // Протискиваемся сквозь маленькие колючки (размер уменьшен до 1.5, чтобы не пикселили!)
        EntityFactory.createObstacle(engine, world, "wood-branch-10", 60f, 0f, 45f, 14f);
     //   EntityFactory.createObstacle(engine, world, "thorne-1", 60f, 4f, 90f, 1.5f);
        EntityFactory.createObstacle(engine, world, "thorne-2", 62f, 7f, 45f, 1.5f);
        EntityFactory.createObstacle(engine, world, "thorne-1", 64f, 3f, 0f, 1.5f);
        
        EntityFactory.createObstacle(engine, world, "liana-4", 65f, 18f, 180f, 14f);
        EntityFactory.createObstacle(engine, world, "rock-3", 65f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "item-speed", 70f, 9f, 0f, 2f);
        
        // Скалистый спуск
        EntityFactory.createObstacle(engine, world, "rock-10", 85f, 18f, 180f, 16f); // Давит сверху
        EntityFactory.createObstacle(engine, world, "swamp-tip", 85f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "swamp-tip", 90f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "swamp-tip", 95f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "sponge-2", 90f, 18f, 180f, 4f);
        
        EntityFactory.createObstacle(engine, world, "huge-curve", 110f, 0f, 0f, 14f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 115f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "item-big", 110f, 16f, 0f, 2f); 
        
        // Заполняем провал 115-130
        EntityFactory.createObstacle(engine, world, "grass-1", 120f, 0f, 0f, 10f);
        EntityFactory.createObstacle(engine, world, "liana-2", 122f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 125f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "wood-branch-2", 128f, 18f, 180f, 10f);
        
        // ==========================================
        // БИОМ 2: Заброшенный Индастриал (120 - 250)
        // ==========================================
        
        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 130f, 0f, 0f, 12f);
        EntityFactory.createObstacle(engine, world, "wire-4", 130f, 18f, 180f, 15f);
        EntityFactory.createObstacle(engine, world, "leca", 135f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "brick-filler", 140f, 0f, 0f, 8f);
        
        // Заполняем пустоту 140-150
        EntityFactory.createObstacle(engine, world, "pipe-short", 145f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "leca", 147f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "wire-3", 148f, 18f, 180f, 10f);
        
        // Шестерни АДЕКВАТНОГО размера (6-8f), но стоят плотной группой
        EntityFactory.createSaw(engine, world, "gear-2", 150f, 4f, 0f, 8f, -0.5f);
        EntityFactory.createSaw(engine, world, "gear-2", 150f, 14f, 0f, 8f, -0.5f);
        EntityFactory.createSaw(engine, world, "gear", 158f, 9f, 0f, 6f, 0.5f);
        
        EntityFactory.createObstacle(engine, world, "pipe-chain-2", 155f, 18f, 180f, 1f);
        EntityFactory.createObstacle(engine, world, "item-speed", 165f, 9f, 0f, 2f);
        
        // Шредеры сильно уменьшены (размер 6f вместо 10-15f), расставлены шахматкой
        EntityFactory.createSaw(engine, world, "shredder-large", 175f, 3f, 0f, 6f, -5f);
        EntityFactory.createSaw(engine, world, "shredder-large", 175f, 15f, 180f, 6f, 5f);
        EntityFactory.createObstacle(engine, world, "wire-3", 175f, 18f, 180f, 10f);
        
        EntityFactory.createSaw(engine, world, "shredder-large", 185f, 9f, 90f, 6f, 5f);
        EntityFactory.createObstacle(engine, world, "pipe-short", 185f, 0f, 0f, 5f);
        EntityFactory.createObstacle(engine, world, "pipe-short", 185f, 18f, 180f, 5f);
        
        EntityFactory.createSaw(engine, world, "shredder-large", 195f, 3f, 0f, 6f, -5f);
        EntityFactory.createSaw(engine, world, "shredder-large", 195f, 15f, 180f, 6f, 5f);
        
        EntityFactory.createObstacle(engine, world, "item-small", 205f, 9f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-superclone", 210f, 9f, 0f, 2f);
        
        // Коридор шипов (очень плотно)
        EntityFactory.createObstacle(engine, world, "spike-bot", 220f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "spike-bot", 225f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "spike-bot", 230f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "spike-bot", 235f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "spike-bot", 240f, 0f, 0f, 8f);
        
        // ==========================================
        // БИОМ 3: Арочная Вентиляция (250 - 350)
        // ==========================================
        
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 255f, 0f, 90f, 12f);
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 255f, 18f, 90f, 12f);
        EntityFactory.createObstacle(engine, world, "wire-1", 258f, 18f, 180f, 1f);
        
        // Заполняем пустоту 260-275
        EntityFactory.createObstacle(engine, world, "tube-connection", 262f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "item-slow", 265f, 9f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "rock-10", 268f, 0f, 0f, 2f);
   //     EntityFactory.createObstacle(engine, world, "pipe-chain-2", 270f, 18f, 180f, 15f);
        EntityFactory.createObstacle(engine, world, "brick-filler", 272f, 0f, 0f, 6f);

        // Пропеллеры в трубах
        EntityFactory.createObstacle(engine, world, "tube-steep-1", 275f, 0f, 0f, 8f);
        EntityFactory.createSaw(engine, world, "propeller-small", 278f, 10f, 0f, 5f, 6f);
        
        EntityFactory.createObstacle(engine, world, "tube-steep-1", 285f, 18f, 180f, 8f);
        EntityFactory.createSaw(engine, world, "propeller-small", 288f, 8f, 0f, 5f, -6f);
        
        EntityFactory.createObstacle(engine, world, "rock-11", 280f, 0f, 0f, 1f);
        
        // Арки и тонкие пилы (slim-large) - размер уменьшен до 7f! (было 16f)
        EntityFactory.createSaw(engine, world, "propeller-slim-large", 305f, 9f, 0f, 7f, 1f);
        EntityFactory.createObstacle(engine, world, "arc", 305f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "arc", 305f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "liana-5", 308f, 18f, 180f, 2f);
        
        EntityFactory.createSaw(engine, world, "propeller-slim-large", 320f, 9f, 0f, 7f, -1f);
        EntityFactory.createObstacle(engine, world, "arc", 320f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "arc", 320f, 18f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "tube-connection", 322f, 18f, 180f, 6f);
        
        // Те самые 2 мины на весь уровень (Сюрприз за аркой)
        EntityFactory.createObstacle(engine, world, "mine", 330f, 4f, 0f, 2.5f);
        EntityFactory.createObstacle(engine, world, "mine", 330f, 14f, 0f, 2.5f);

        // Заполняем пустоту 330-345
        EntityFactory.createObstacle(engine, world, "liana-4", 335f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "sponge-8", 338f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "wire-4", 340f, 18f, 180f, 10f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 342f, 0f, 0f, 8f);
        
        // ==========================================
        // БИОМ 4: Скоростной Прорыв (350 - 460)
        // ==========================================
        
        EntityFactory.createObstacle(engine, world, "tube-shallow-1", 345f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "tube-shallow-1", 345f, 18f, 180f, 8f);
        
        EntityFactory.createObstacle(engine, world, "item-superclone", 355f, 9f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-superclone", 360f, 9f, 0f, 2f); // Огромная армия!
        EntityFactory.createObstacle(engine, world, "item-speed", 365f, 9f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-speed", 370f, 9f, 0f, 2f); // Двойное ускорение
        
        // Заполняем ОГРОМНЫЙ провал 370-385 (летим на двойной скорости, нужен густой фон)
        EntityFactory.createObstacle(engine, world, "huge-curve", 375f, 0f, 0f, 10f);
        EntityFactory.createObstacle(engine, world, "huge-curve", 375f, 18f, 180f, 10f);
        EntityFactory.createObstacle(engine, world, "wire-1", 378f, 18f, 180f, 15f);
        EntityFactory.createObstacle(engine, world, "rock-11", 380f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "liana-5", 382f, 18f, 180f, 12f);
        
        // Стена из пропеллеров нормального размера (6f вместо 12-16f), зато их много
        EntityFactory.createSaw(engine, world, "propeller-large", 385f, 3f, 0f, 2f, 8f);
        EntityFactory.createSaw(engine, world, "propeller-large", 385f, 9f, 0f, 2f, -8f);
        EntityFactory.createSaw(engine, world, "propeller-large", 385f, 15f, 0f, 2f, 8f);
        EntityFactory.createObstacle(engine, world, "wire-2", 385f, 18f, 180f, 14f); // Провода между пилами
        
        EntityFactory.createObstacle(engine, world, "rock-10", 395f, 0f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "rock-10", 395f, 18f, 180f, 2f);
        
        // Еще одна плотная стена пил
        EntityFactory.createSaw(engine, world, "shredder-large", 405f, 3f, 0f, 4f, -8f);
        EntityFactory.createSaw(engine, world, "shredder-large", 405f, 9f, 0f, 4f, 8f);
        EntityFactory.createSaw(engine, world, "shredder-large", 405f, 15f, 0f, 4f, -8f);
        EntityFactory.createObstacle(engine, world, "pipe-chain-2", 405f, 18f, 180f, 2f);
        
        EntityFactory.createObstacle(engine, world, "tube-part", 415f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "tube-part", 415f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "item-big", 420f, 9f, 0f, 2f);
        
        // Финальная природа (расслабление перед порталом)
        EntityFactory.createObstacle(engine, world, "grass-1", 430f, 0f, 0f, 12f);
        EntityFactory.createObstacle(engine, world, "grass-1", 440f, 0f, 0f, 15f);
        EntityFactory.createObstacle(engine, world, "liana-1", 435f, 18f, 180f, 14f);
        EntityFactory.createObstacle(engine, world, "liana-3", 445f, 18f, 180f, 16f);
    //    EntityFactory.createObstacle(engine, world, "sponge-3", 440f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "sponge-1", 450f, 18f, 180f, 6f);
        
        createPortal(460f);
    }
}
