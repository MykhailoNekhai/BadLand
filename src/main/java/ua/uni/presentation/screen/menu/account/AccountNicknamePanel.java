package ua.uni.presentation.screen.menu.account;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ua.uni.core.logging.AppLogger;
import ua.uni.presentation.screen.menu.account.service.AccountMenuService;

final class AccountNicknamePanel {
    private final BitmapFont smallFont;
    private final Texture itemBtn;
    private final Texture cursorTex;
    private final Texture fieldBg;
    private final String currentNickname;
    private final AccountMenuService accountMenuService;
    private final Runnable onRefresh;
    private final Runnable onPlaySelect;

    AccountNicknamePanel(BitmapFont smallFont, Texture itemBtn,
                         Texture cursorTex, Texture fieldBg,
                         String currentNickname,
                         AccountMenuService accountMenuService,
                         Runnable onRefresh, Runnable onPlaySelect) {
        this.smallFont = smallFont;
        this.itemBtn = itemBtn;
        this.cursorTex = cursorTex;
        this.fieldBg = fieldBg;
        this.currentNickname = currentNickname;
        this.accountMenuService = accountMenuService;
        this.onRefresh = onRefresh;
        this.onPlaySelect = onPlaySelect;
    }

    Table build() {
        Label.LabelStyle statusStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.92f, 0.55f, 1f));

        TextField.TextFieldStyle nicknameFieldStyle = new TextField.TextFieldStyle();
        nicknameFieldStyle.font = smallFont;
        nicknameFieldStyle.fontColor = Color.WHITE;
        nicknameFieldStyle.messageFont = smallFont;
        nicknameFieldStyle.messageFontColor = new Color(0.65f, 0.65f, 0.67f, 1f);
        nicknameFieldStyle.cursor = new TextureRegionDrawable(cursorTex);
        nicknameFieldStyle.background = new TextureRegionDrawable(fieldBg);

        TextButton.TextButtonStyle applyStyle = new TextButton.TextButtonStyle();
        applyStyle.up   = new TextureRegionDrawable(itemBtn);
        applyStyle.down = new TextureRegionDrawable(itemBtn);
        applyStyle.over = new TextureRegionDrawable(itemBtn);
        applyStyle.font = smallFont;
        applyStyle.fontColor      = new Color(0.98f, 0.95f, 0.88f, 1f);
        applyStyle.overFontColor  = new Color(1f, 0.92f, 0.55f, 1f);
        applyStyle.downFontColor  = new Color(1f, 0.92f, 0.55f, 1f);

        TextField nicknameField = new TextField(currentNickname, nicknameFieldStyle);
        nicknameField.setMessageText("Nickname");
        Label statusLabel = new Label("", statusStyle);
        statusLabel.setWrap(true);
        TextButton applyButton = new TextButton("APPLY", applyStyle);

        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onPlaySelect.run();
                try {
                    AccountMenuService.UpdateNicknameResult result =
                            accountMenuService.updateNickname(nicknameField.getText());
                    statusLabel.setText(result.message());
                    if (result.success()) {
                        nicknameField.setText(result.nickname());
                        onRefresh.run();
                    }
                } catch (Exception e) {
                    statusLabel.setText("Save failed.");
                    AppLogger.error("Account", "Nickname update failed", e);
                }
            }
        });

        Table table = new Table();
        table.center().padTop(2);
        table.add(nicknameField).width(320).height(54).padBottom(8).row();
        table.add(applyButton).width(320).height(58).padBottom(8).row();
        table.add(statusLabel).width(320).minHeight(42).row();
        return table;
    }
}
