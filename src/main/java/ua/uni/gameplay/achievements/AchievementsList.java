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
        register(new Achievements(new ID(10), "LEVEL_10", "Fading Echo", AchievementsRarity.Legendary, "Cross the hollow dark of level 10"));

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
