package ua.uni.presentation.screen.menu.account;

record ProfileSnapshot(
        String nickname,
        String email,
        String id,
        String accountCreated,
        String lastLogin,
        int deaths,
        int playSeconds,
        int completedLevels,
        int totalLevels,
        int unlockedAchievements,
        int totalAchievements,
        boolean hasSession
) {}
