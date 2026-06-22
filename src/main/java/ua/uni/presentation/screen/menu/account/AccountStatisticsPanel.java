package ua.uni.presentation.screen.menu.account;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

final class AccountStatisticsPanel {
    private final BitmapFont smallFont;
    private final BitmapFont itemFont;

    AccountStatisticsPanel(BitmapFont smallFont, BitmapFont itemFont) {
        this.smallFont = smallFont;
        this.itemFont = itemFont;
    }

    Table build(ProfileSnapshot snapshot) {
        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle valueStyle = new Label.LabelStyle(itemFont, new Color(0.98f, 0.95f, 0.88f, 1f));

        Table table = new Table();
        table.center().padTop(20);
        table.add(new Label(LanguageButton.t("NICKNAME"), sectionStyle)).padBottom(6).row();
        table.add(new Label(snapshot.nickname(), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("ID"), sectionStyle)).padBottom(6).row();
        table.add(new Label(snapshot.id(), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("EMAIL").toUpperCase(), sectionStyle)).padBottom(6).row();
        table.add(new Label(snapshot.email(), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("DEATHS"), sectionStyle)).padBottom(6).row();
        table.add(new Label(String.valueOf(snapshot.deaths()), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("PLAY_TIME"), sectionStyle)).padBottom(6).row();
        table.add(new Label(formatPlayTime(snapshot.playSeconds()), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("LEVELS"), sectionStyle)).padBottom(6).row();
        table.add(new Label(snapshot.completedLevels() + " / " + snapshot.totalLevels(), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("ACHIEVEMENTS"), sectionStyle)).padBottom(6).row();
        table.add(new Label(snapshot.unlockedAchievements() + " / " + snapshot.totalAchievements(), valueStyle)).padBottom(18).row();
        table.add(new Label(LanguageButton.t("SCORE"), sectionStyle)).padBottom(6).row();
        table.add(new Label(LanguageButton.t("SOON"), valueStyle)).row();
        return table;
    }

    private String formatPlayTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) return LanguageButton.tf("HOURS_MINUTES_FMT", hours, minutes);
        if (minutes > 0) return LanguageButton.tf("MINUTES_FMT", minutes);
        return LanguageButton.tf("SECONDS_FMT", totalSeconds);
    }
}
