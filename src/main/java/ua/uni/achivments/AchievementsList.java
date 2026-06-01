package ua.uni.achivments;

import ua.uni.exceptions.app.AchievementNotFoundException;
import ua.uni.objects.ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementsList {
    private final List<Achievements> all = new ArrayList<>();
    private final Map<ID, Achievements> byId = new HashMap<>();
    private final Map<String, Achievements> byCode = new HashMap<>();

    public AchievementsList() {
        register(new Achievements(new ID(1), "LEVEL_01", "Level 01", AchievementsRarity.Common, "Complete level 1"));
        register(new Achievements(new ID(2), "LEVEL_02", "Level 02", AchievementsRarity.Common, "Complete level 2"));
        register(new Achievements(new ID(3), "LEVEL_03", "Level 03", AchievementsRarity.Common, "Complete level 3"));
        register(new Achievements(new ID(4), "LEVEL_04", "Level 04", AchievementsRarity.Common, "Complete level 4"));
        register(new Achievements(new ID(5), "LEVEL_05", "Level 05", AchievementsRarity.Common, "Complete level 5"));
        register(new Achievements(new ID(6), "LEVEL_06", "Level 06", AchievementsRarity.Rare, "Complete level 6"));
        register(new Achievements(new ID(7), "LEVEL_07", "Level 07", AchievementsRarity.Rare, "Complete level 7"));
        register(new Achievements(new ID(8), "LEVEL_08", "Level 08", AchievementsRarity.Rare, "Complete level 8"));
        register(new Achievements(new ID(9), "LEVEL_09", "Level 09", AchievementsRarity.Epic, "Complete level 9"));
        register(new Achievements(new ID(10), "LEVEL_10", "Level 10", AchievementsRarity.Legendary, "Complete level 10"));

        register(new Achievements(new ID(11), "DEATH_1_TOTAL", "One Fall", AchievementsRarity.Common,
                "Die 1 time in total"));
        register(new Achievements(new ID(12), "DEATH_5_TOTAL", "Unlucky Five", AchievementsRarity.Rare,
                "Die 5 times in total"));
        register(new Achievements(new ID(13), "DEATH_10_TOTAL", "Try Again", AchievementsRarity.Epic,
                "Die 10 times in total"));
        register(new Achievements(new ID(14), "FIRST_TRY_ANY_LEVEL", "First Try", AchievementsRarity.Epic,
                "Complete any level from first attempt"));
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
