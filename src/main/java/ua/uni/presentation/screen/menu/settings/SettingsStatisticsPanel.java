package ua.uni.presentation.screen.menu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ua.uni.gameplay.achievements.AchievementManager;

final class SettingsStatisticsPanel {
    private static final float STATS_WIDTH = 400f;
    private static final float COLUMN_GAP = 24f;

    private final BitmapFont smallFont;
    private final BitmapFont itemFont;
    private final AchievementManager achievements;

    SettingsStatisticsPanel(BitmapFont smallFont, BitmapFont itemFont, AchievementManager achievements) {
        this.smallFont = smallFont;
        this.itemFont = itemFont;
        this.achievements = achievements;
    }

    Table build() {
        int totalScore  = achievements.getTotalScore();
        int deaths      = achievements.getTotalDeaths();
        int wins        = achievements.getTotalWins();
        int losses      = achievements.getTotalLosses();
        int unlocked    = achievements.getUnlockedCount();
        int total       = achievements.getTotalCount();
        int playSeconds = achievements.getTotalPlaySeconds();

        Label.LabelStyle section = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle value   = new Label.LabelStyle(itemFont,  new Color(0.98f, 0.95f, 0.88f, 1f));

        float half = (STATS_WIDTH - COLUMN_GAP) / 2f;

        Table t = new Table();
        t.top().left().padTop(10);
        t.defaults().left();

        t.add(new Label(LanguageButton.t("SCORE"), section)).left().padBottom(6).row();
        t.add(new Label(String.valueOf(totalScore), value)).left().padBottom(18).row();

        Table winsRow = new Table();
        winsRow.left();
        winsRow.add(new Label(LanguageButton.t("WINS"),   section)).left().width(half).padRight(COLUMN_GAP);
        winsRow.add(new Label(LanguageButton.t("LOSSES"), section)).left().width(half);
        t.add(winsRow).left().width(STATS_WIDTH).padBottom(6).row();

        Table winsVals = new Table();
        winsVals.left();
        winsVals.add(new Label(String.valueOf(wins),   value)).left().width(half).padRight(COLUMN_GAP);
        winsVals.add(new Label(String.valueOf(losses), value)).left().width(half);
        t.add(winsVals).left().width(STATS_WIDTH).padBottom(18).row();

        Table achRow = new Table();
        achRow.left();
        achRow.add(new Label(LanguageButton.t("ACHIEVEMENTS"), section)).left().width(half).padRight(COLUMN_GAP);
        achRow.add(new Label(LanguageButton.t("DEATHS"),       section)).left().width(half);
        t.add(achRow).left().width(STATS_WIDTH).padBottom(6).row();

        Table achVals = new Table();
        achVals.left();
        achVals.add(new Label(unlocked + " / " + total,  value)).left().width(half).padRight(COLUMN_GAP);
        achVals.add(new Label(String.valueOf(deaths),     value)).left().width(half);
        t.add(achVals).left().width(STATS_WIDTH).padBottom(18).row();

        t.add(new Label(LanguageButton.t("PLAY_TIME"), section)).left().padBottom(6).row();
        t.add(new Label(formatPlayTime(playSeconds),    value)).left().row();
        return t;
    }

    private String formatPlayTime(int totalSeconds) {
        int hours   = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        if (hours > 0)   return LanguageButton.tf("HOURS_MINUTES_FMT", hours, minutes);
        if (minutes > 0) return LanguageButton.tf("MINUTES_FMT", minutes);
        return LanguageButton.tf("SECONDS_FMT", totalSeconds);
    }
}
