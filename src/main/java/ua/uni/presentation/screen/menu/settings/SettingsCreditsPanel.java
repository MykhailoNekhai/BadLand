package ua.uni.presentation.screen.menu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

final class SettingsCreditsPanel {
    private final BitmapFont itemFont;
    private final BitmapFont smallFont;

    SettingsCreditsPanel(BitmapFont itemFont, BitmapFont smallFont) {
        this.itemFont = itemFont;
        this.smallFont = smallFont;
    }

    Table build() {
        Label.LabelStyle heroStyle    = new Label.LabelStyle(itemFont,  new Color(0.98f, 0.95f, 0.88f, 1f));
        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle nameStyle    = new Label.LabelStyle(smallFont, new Color(0.98f, 0.95f, 0.88f, 1f));

        Table t = new Table();
        t.top().left().padTop(20);
        t.defaults().left();

        Label teamTitle = new Label("SHADOW FLIGHT TEAM", heroStyle);
        teamTitle.setAlignment(Align.left);
        t.add(teamTitle).left().padBottom(28).row();

        t.add(new Label(LanguageButton.t("DEVELOPED_BY") + ":", sectionStyle)).left().padBottom(6).row();
        t.add(new Label("Mykhailo Nekhai", nameStyle)).left().padBottom(10).row();
        t.add(new Label("https://github.com/MykhailoNekhai", nameStyle)).left().padBottom(18).row();
        t.add(new Label("Shakhin-Krupchan Damir", nameStyle)).left().padBottom(10).row();
        t.add(new Label("https://github.com/Damir047", nameStyle)).left().padBottom(18).row();
        t.add(new Label("ChatGPT & Claude & Codex & Gemini", nameStyle)).left().padBottom(10).row();
        t.add(new Label(LanguageButton.t("YEAR"), nameStyle)).left().row();
        return t;
    }
}
