package ua.uni.gameplay.achievements;

import ua.uni.core.exceptions.app.AchievementNotFoundException;
import ua.uni.core.value.ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementsList {
    private final List<Achievements> all = new ArrayList<>();
    private final Map<ID, Achievements> byId = new HashMap<>();
    private final Map<String, Achievements> byCode = new HashMap<>();

    public AchievementsList() {
        register(new Achievements(new ID(1), "LEVEL_01", "First Wings", AchievementsRarity.Common, "Finish the first level."));
        register(new Achievements(new ID(2), "LEVEL_02", "Into the Thorns", AchievementsRarity.Common, "Survive and finish level 2."));
        register(new Achievements(new ID(3), "LEVEL_03", "Through the Mist", AchievementsRarity.Common, "Make your way through level 3."));
        register(new Achievements(new ID(4), "LEVEL_04", "Dark Current", AchievementsRarity.Common, "Reach the end of level 4."));
        register(new Achievements(new ID(5), "LEVEL_05", "No Turning Back", AchievementsRarity.Common, "Complete the fifth level."));
        register(new Achievements(new ID(6), "LEVEL_06", "Deeper Below", AchievementsRarity.Rare, "Push forward and clear level 6."));
        register(new Achievements(new ID(7), "LEVEL_07", "The Hollow Path", AchievementsRarity.Rare, "Finish the dangerous path of level 7."));
        register(new Achievements(new ID(8), "LEVEL_08", "Where Light Fades", AchievementsRarity.Rare, "Complete the darkness of level 8."));
        register(new Achievements(new ID(9), "LEVEL_09", "Edge of Silence", AchievementsRarity.Epic, "Overcome level 9."));
        register(new Achievements(new ID(10), "LEVEL_10", "Fading Echo", AchievementsRarity.Legendary, "Cross the hollow dark of level 10."));
        register(new Achievements(new ID(20), "LEVEL_11", "After the Echo", AchievementsRarity.Common, "Push through and finish level 11."));
        register(new Achievements(new ID(21), "LEVEL_12", "Rootbound", AchievementsRarity.Common, "Survive the hazards of level 12."));
        register(new Achievements(new ID(22), "LEVEL_13", "Veil of Ash", AchievementsRarity.Rare, "Complete the trial of level 13."));
        register(new Achievements(new ID(23), "LEVEL_14", "Cold Hollow", AchievementsRarity.Rare, "Reach the end of level 14."));
        register(new Achievements(new ID(24), "LEVEL_15", "Cinder Passage", AchievementsRarity.Epic, "Complete level 15."));
        register(new Achievements(new ID(25), "LEVEL_16", "Below the Canopy", AchievementsRarity.Rare, "Finish the depths of level 16."));
        register(new Achievements(new ID(26), "LEVEL_17", "Through the Static", AchievementsRarity.Rare, "Overcome level 17."));
        register(new Achievements(new ID(27), "LEVEL_18", "Last Ember", AchievementsRarity.Epic, "Clear the dangers of level 18."));
        register(new Achievements(new ID(28), "LEVEL_19", "Silent Descent", AchievementsRarity.Epic, "Complete level 19."));
        register(new Achievements(new ID(29), "LEVEL_20", "Beyond the Dark", AchievementsRarity.Legendary, "Finish level 20."));

        register(new Achievements(new ID(11), "DEATH_1_TOTAL", "First Fall", AchievementsRarity.Common,
                "Die for the first time."));
        register(new Achievements(new ID(12), "DEATH_5_TOTAL", "Broken Wings", AchievementsRarity.Rare,
                "Accumulate 5 total deaths."));
        register(new Achievements(new ID(13), "DEATH_10_TOTAL", "Worn by Darkness", AchievementsRarity.Epic,
                "Accumulate 10 total deaths."));
        register(new Achievements(new ID(14), "FIRST_TRY_ANY_LEVEL", "One Breath", AchievementsRarity.Epic,
                "Complete any level on your first attempt."));
        register(new Achievements(new ID(15), "PLAYTIME_10_MIN", "Into the Gloom", AchievementsRarity.Common,
                "Spend 10 minutes in the game."));
        register(new Achievements(new ID(16), "PLAYTIME_30_MIN", "Lost in the Wild", AchievementsRarity.Rare,
                "Spend 30 minutes in the game."));
        register(new Achievements(new ID(17), "PLAYTIME_60_MIN", "One with the Dark", AchievementsRarity.Epic,
                "Spend an hour in the game."));
        register(new Achievements(new ID(18), "ALL_LEVELS_COMPLETE", "The END?", AchievementsRarity.Legendary,
                "Complete all levels."));
        register(new Achievements(new ID(19), "COOP_SESSION", "Never Alone", AchievementsRarity.Rare,
                "Start a co-op session."));
        register(new Achievements(new ID(30), "WINS_1_TOTAL", "First Victory", AchievementsRarity.Common,
                "Win for the first time."));
        register(new Achievements(new ID(31), "WINS_5_TOTAL", "Rising Winner", AchievementsRarity.Common,
                "Reach 5 total victories."));
        register(new Achievements(new ID(32), "WINS_10_TOTAL", "Steady Flight", AchievementsRarity.Rare,
                "Reach 10 total victories."));
        register(new Achievements(new ID(33), "WINS_15_TOTAL", "Shadow Conqueror", AchievementsRarity.Epic,
                "Reach 15 total victories."));
        register(new Achievements(new ID(34), "WINS_20_TOTAL", "Master of the Hollow", AchievementsRarity.Legendary,
                "Reach 20 total victories."));
        register(new Achievements(new ID(35), "LOSSES_1_TOTAL", "First Defeat", AchievementsRarity.Common,
                "Lose for the first time."));
        register(new Achievements(new ID(36), "LOSSES_5_TOTAL", "Fallen Again", AchievementsRarity.Common,
                "Reach 5 total defeats."));
        register(new Achievements(new ID(37), "LOSSES_10_TOTAL", "Marked by Failure", AchievementsRarity.Rare,
                "Reach 10 total defeats."));
        register(new Achievements(new ID(38), "LOSSES_15_TOTAL", "Into the Abyss", AchievementsRarity.Epic,
                "Reach 15 total defeats."));
        register(new Achievements(new ID(39), "LOSSES_20_TOTAL", "Still Not Broken", AchievementsRarity.Legendary,
                "Reach 20 total defeats."));
        register(new Achievements(new ID(40), "SCORE_1000_TOTAL", "First Spark", AchievementsRarity.Common,
                "Reach a total score of 1,000."));
        register(new Achievements(new ID(41), "SCORE_5000_TOTAL", "Glow Gatherer", AchievementsRarity.Common,
                "Reach a total score of 5,000."));
        register(new Achievements(new ID(42), "SCORE_10000_TOTAL", "Ash Collector", AchievementsRarity.Rare,
                "Reach a total score of 10,000."));
        register(new Achievements(new ID(43), "SCORE_15000_TOTAL", "Rising Flame", AchievementsRarity.Rare,
                "Reach a total score of 15,000."));
        register(new Achievements(new ID(44), "SCORE_20000_TOTAL", "Cinder Keeper", AchievementsRarity.Epic,
                "Reach a total score of 20,000."));
        register(new Achievements(new ID(45), "SCORE_25000_TOTAL", "Shadow Hoard", AchievementsRarity.Epic,
                "Reach a total score of 25,000."));
        register(new Achievements(new ID(46), "SCORE_30000_TOTAL", "Ember Lord", AchievementsRarity.Epic,
                "Reach a total score of 30,000."));
        register(new Achievements(new ID(47), "SCORE_50000_TOTAL", "Dark Treasury", AchievementsRarity.Legendary,
                "Reach a total score of 50,000."));
        register(new Achievements(new ID(48), "SCORE_100000_TOTAL", "Crown of Shadows", AchievementsRarity.Legendary,
                "Reach a total score of 100,000."));
    }

    public List<Achievements> getAll() {
        return List.copyOf(all);
    }

    public Achievements findById(ID id) {
        Achievements achievement = byId.get(id);
        if (achievement == null) {
            throw new AchievementNotFoundException("No achievement with id: " + id);
        }
        return achievement;
    }

    public Achievements findByCode(String code) {
        String normalized = normalizeCode(code);
        Achievements achievement = byCode.get(normalized);
        if (achievement == null) {
            throw new AchievementNotFoundException("No achievement with code: " + normalized);
        }
        return achievement;
    }

    private void register(Achievements achievement) {
        String code = normalizeCode(achievement.getCode());
        if (byId.containsKey(achievement.getId())) {
            throw new IllegalArgumentException("Duplicate achievement id: " + achievement.getId());
        }
        if (byCode.containsKey(code)) {
            throw new IllegalArgumentException("Duplicate achievement code: " + code);
        }
        achievement.setCode(code);
        all.add(achievement);
        byId.put(achievement.getId(), achievement);
        byCode.put(code, achievement);
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Achievement code must not be blank");
        }
        return code.trim().toUpperCase();
    }
}
