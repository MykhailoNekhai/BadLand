package ua.uni.gameplay.levels;

import ua.uni.bootstrap.GameServices;
import ua.uni.gameplay.factory.EntityFactory;

public class Poligon3Level extends Plevel {

    public Poligon3Level(GameServices services) {
        super(services);
        levelNumber = 2; // Этот уровень привязан ко второй кнопке
    }

    @Override
    protected void buildLevel() {
        // Спавн игрока в начале уровня
        spawnClone(8, 9);
        
        // --- Введение (Густая природа) ---
        EntityFactory.createObstacle(engine, world, "grass-1", 10f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 11f, 0f, 0f, 5f); // Больше фактуры внизу
        EntityFactory.createObstacle(engine, world, "sponge-1", 12f, 0f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "liana-3", 13f, 18f, 180f, 9f); // Свисает сверху
        
        EntityFactory.createObstacle(engine, world, "wood-branch-1", 15f, 0f, 45f, 6f);
        EntityFactory.createObstacle(engine, world, "branch-tip-3", 16f, 0f, 0f, 3f); // Мелкая веточка для объема
        
        EntityFactory.createObstacle(engine, world, "rock-4", 17f, 18f, 180f, 8f); // Камень свисает с потолка
        EntityFactory.createObstacle(engine, world, "sponge-2", 20f, 0f, 0f, 4f);
        EntityFactory.createObstacle(engine, world, "rock-11", 22f, 0f, 0f, 6f); // Каменный уступ
        EntityFactory.createObstacle(engine, world, "wood-stump-3", 25f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "branch-tip-5", 28f, 0f, 0f, 5f);

        // --- Переход в Индастриал ---
        EntityFactory.createObstacle(engine, world, "wire-1", 32f, 18f, 180f, 8f); // Свисающий провод (начинается заброшка)
        EntityFactory.createObstacle(engine, world, "pipe-corner-1", 35f, 2f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "leca", 36f, 0f, 0f, 4f); // Строительный мусор на полу
        
        EntityFactory.createObstacle(engine, world, "filler-1", 38f, 0f, 0f, 6f); 
        EntityFactory.createObstacle(engine, world, "pipe-straight-1", 42f, 16f, 180f, 8f);
        EntityFactory.createObstacle(engine, world, "pipe-short", 43f, 18f, 180f, 4f); // Обрезок трубы
        
        // --- Опасная зона (асимметричные шестеренки) ---
        EntityFactory.createSaw(engine, world, "gear", 48f, 2f, 0f, 7f, -3f); 
        EntityFactory.createObstacle(engine, world, "brick-filler", 49f, 0f, 0f, 5f); // Обломки под шестеренкой
        EntityFactory.createObstacle(engine, world, "sharp-rock-1", 50f, 18f, 180f, 4f); // Острый камень угрожающе свисает
        
        EntityFactory.createObstacle(engine, world, "item-speed", 52f, 10f, 0f, 2f); 
        EntityFactory.createSaw(engine, world, "gear-2", 56f, 15f, 0f, 5f, 4f); 
        EntityFactory.createObstacle(engine, world, "wire-4", 57f, 18f, 180f, 6f); // Оборванные провода вокруг шестеренки
        
        EntityFactory.createObstacle(engine, world, "rock-5", 58f, 0f, 0f, 5f);
        EntityFactory.createObstacle(engine, world, "random-filler", 61f, 0f, 0f, 4f); // Еще мусор
        
        // --- Шредеры в шахматном порядке ---
        EntityFactory.createSaw(engine, world, "shredder-large", 65f, 16f, 180f, 10f, -2f);
        EntityFactory.createObstacle(engine, world, "pipe-chain-1", 66f, 18f, 180f, 6f); // Цепь с потолка
        
        EntityFactory.createObstacle(engine, world, "tube-part", 68f, 0f, 0f, 5f); // Статика на полу
        EntityFactory.createSaw(engine, world, "shredder-large", 75f, 2f, 0f, 10f, 2f);
        EntityFactory.createObstacle(engine, world, "tube-connection", 76f, 0f, 0f, 4f); // Крепление от трубы на полу
        
        // --- Зона с клонами ---
        EntityFactory.createObstacle(engine, world, "item-clone", 80f, 5f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-clone", 82f, 12f, 0f, 2f);
        EntityFactory.createObstacle(engine, world, "item-clone", 84f, 8f, 0f, 2f);
        
        // --- Смешанный лес из труб и лиан ---
        EntityFactory.createObstacle(engine, world, "liana-2", 85f, 18f, 180f, 12f);
        EntityFactory.createObstacle(engine, world, "burdock-large", 86f, 0f, 0f, 4f); // Большой лопух
        EntityFactory.createObstacle(engine, world, "swamp-tip", 88f, 0f, 0f, 1f); // Болотная растительность внизу
        
        EntityFactory.createObstacle(engine, world, "pipe-straight-2", 90f, 0f, 90f, 8f);
        EntityFactory.createObstacle(engine, world, "small-branch-3", 92f, 18f, 180f, 6f); // Ветки наросли прямо на трубе
        
        EntityFactory.createObstacle(engine, world, "liana-4", 98f, 18f, 180f, 14f);
        EntityFactory.createObstacle(engine, world, "rock-7", 99f, 0f, 0f, 3f); // Мелкий камень
        
        EntityFactory.createObstacle(engine, world, "pipe-medium", 102f, 0f, 90f, 10f);
        EntityFactory.createObstacle(engine, world, "sponge-8", 103f, 0f, 0f, 3f); // Губки разрослись
        EntityFactory.createObstacle(engine, world, "sponge-4", 104f, 18f, 180f, 3f);
        EntityFactory.createObstacle(engine, world, "wood-stump-5", 106f, 18f, 180f, 5f); // Пень на потолке

        // --- Интерактивная дверь ---
        EntityFactory.createObstacle(engine, world, "rock-1", 112f, 0f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "roundy-pipes-large-2", 114f, 18f, 180f, 8f); // Изогнутые трубы на потолке
        
        // Пила
        EntityFactory.createSaw(engine, world, "propeller-slim-large", 122f, 15f, 0f, 12f, 1f);
        EntityFactory.createObstacle(engine, world, "wire-3", 122f, 18f, 180f, 7f); // Свисающий длинный провод
        EntityFactory.createObstacle(engine, world, "fat-curve", 124f, 0f, 0f, 5f); // Извилистая деталь на полу
        
        EntityFactory.createObstacle(engine, world, "item-small", 128f, 9f, 0f, 2f); 
        
        // --- Асимметричный туннель ---
        EntityFactory.createObstacle(engine, world, "tube-mask", 136f, 0f, 0f, 4f); // Маска входа в туннель (визуальный переход)
        EntityFactory.createObstacle(engine, world, "tube-shallow-1", 138f, 2f, 0f, 6f);
        EntityFactory.createObstacle(engine, world, "rock-8", 139f, 0f, 0f, 2f); // Камушек перед трубой
        
        EntityFactory.createObstacle(engine, world, "rock-6", 140f, 18f, 180f, 4f); // Статика над туннелем
        EntityFactory.createObstacle(engine, world, "sponge-9", 144f, 18f, 180f, 5f); // Нарост губок на потолке
        EntityFactory.createObstacle(engine, world, "tube-steep-1", 148f, 15f, 180f, 6f);
        
        EntityFactory.createObstacle(engine, world, "liana-5", 152f, 18f, 180f, 12f); // Лиана на выходе из туннеля
        EntityFactory.createObstacle(engine, world, "grass-1", 150f, 0f, 0f, 5f);
        
        // --- Разнесенные пропеллеры ---
        EntityFactory.createSaw(engine, world, "propeller-large", 162f, 3f, 0f, 9f, 3f);
        EntityFactory.createObstacle(engine, world, "brick-filler", 164f, 18f, 180f, 6f); // Разрушения наверху
        
        EntityFactory.createObstacle(engine, world, "wood-stump-2", 168f, 18f, 180f, 6f); // Декор сверху
        EntityFactory.createSaw(engine, world, "shredder-large", 172f, 14f, 180f, 7f, -4f);
        EntityFactory.createObstacle(engine, world, "sharp-rock-3", 174f, -2f, 0f, 1f); // Мелкие острые камни
        EntityFactory.createObstacle(engine, world, "wire-2", 176f, 18f, 180f, 6f); // Свисающие провода
        
        EntityFactory.createObstacle(engine, world, "item-big", 178f, 9f, 0f, 2f);
        
        // --- Разрушенный мост из бревен ---
        EntityFactory.createObstacle(engine, world, "branch-root", 182f, 0f, 0f, 8f); // Массивное основание моста
        EntityFactory.createObstacle(engine, world, "wood-branch-5", 188f, 1.5f, 15f, 10f);
        EntityFactory.createObstacle(engine, world, "thorne-1", 186f, 5f, 45f, 2f); // Колючки на бревне (визуал)
        
        EntityFactory.createObstacle(engine, world, "sponge-5", 188f, 18f, 180f, 4f);
        EntityFactory.createObstacle(engine, world, "wood-branch-6", 195f, 2f, 30f, 12f);
        EntityFactory.createObstacle(engine, world, "small-branch", 196f, 1f, 0f, 6f); // Мелкие ветки под мостом

        EntityFactory.createObstacle(engine, world, "rock-10", 202f, 18f, 180f, 15f);
        EntityFactory.createObstacle(engine, world, "swampy-rock-1", 204f, 0f, 0f, 6f); // Болотистый камень внизу
        EntityFactory.createObstacle(engine, world, "grass-1", 205f, 0f, 0f, 8f);

        // --- Огромная мясорубка в конце ---
        EntityFactory.createSaw(engine, world, "gear-2", 220f, 5f, 0f, 16f, -1f);
        EntityFactory.createObstacle(engine, world, "leca", 222f, 0f, 0f, 5f); // Куча мусора на полу под шестеренкой
        
        EntityFactory.createObstacle(engine, world, "wood-branch-2", 225f, 18f, 180f, 10f); 
        EntityFactory.createObstacle(engine, world, "wire-1", 228f, 18f, 180f, 8f); // Провод запутался в ветках
        
        EntityFactory.createObstacle(engine, world, "pipe-chain-2", 238f, 18f, 180f, 6f); // Свисающая цепь перед финальной пилой
        EntityFactory.createSaw(engine, world, "shredder-large", 242f, 14f, 180f, 10f, -5f);
        EntityFactory.createObstacle(engine, world, "rock-3", 245f, 0f, 0f, 7f); 

        // --- Финальная прямая (природа возвращается) ---
        EntityFactory.createObstacle(engine, world, "grass-1", 255f, 0f, 0f, 15f);
        EntityFactory.createObstacle(engine, world, "sponge-3", 256f, 0f, 0f, 3f);
        EntityFactory.createObstacle(engine, world, "liana-1", 258f, 18f, 180f, 8f);
        
        EntityFactory.createObstacle(engine, world, "wood-stump-1", 265f, 0f, 0f, 8f);
        EntityFactory.createObstacle(engine, world, "sponge-7", 266f, 0f, 0f, 4f); // Губки на финальном пне
        
        EntityFactory.createObstacle(engine, world, "filler-1", 268f, 18f, 180f, 6f);
        EntityFactory.createObstacle(engine, world, "wood-branch-10", 270f, 18f, 180f, 10f);
        EntityFactory.createObstacle(engine, world, "swamp-tip", 272f, 0f, 0f, 4f); // Растение у самого портала
        
        // --- Конец уровня (портал) ---
        createPortal(285f);
    }
}
