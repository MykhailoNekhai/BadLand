package ua.uni.presentation.screen.menu.account;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ua.uni.presentation.screen.menu.account.service.AccountMenuService;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

final class AccountSecurityPanel {
    private final BitmapFont smallFont;
    private final Texture itemBtn;
    private final AccountMenuService accountMenuService;
    private final Runnable onOpenChangeEmail;
    private final Runnable onSendResetPassword;
    private final Runnable onLogout;

    AccountSecurityPanel(
            BitmapFont smallFont,
            Texture itemBtn,
            AccountMenuService accountMenuService,
            Runnable onOpenChangeEmail,
            Runnable onSendResetPassword,
            Runnable onLogout) {
        this.smallFont = smallFont;
        this.itemBtn = itemBtn;
        this.accountMenuService = accountMenuService;
        this.onOpenChangeEmail = onOpenChangeEmail;
        this.onSendResetPassword = onSendResetPassword;
        this.onLogout = onLogout;
    }

    Table build() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(itemBtn);
        style.down = new TextureRegionDrawable(itemBtn);
        style.over = new TextureRegionDrawable(itemBtn);
        style.font = smallFont;
        style.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        style.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        style.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        TextButton changeEmail = new TextButton(LanguageButton.t("CHANGE_EMAIL"), style);
        TextButton resetPassword = new TextButton(LanguageButton.t("RESET_PASSWORD"), style);
        TextButton logout = new TextButton(LanguageButton.t("LOG_OUT"), style);

        changeEmail.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { onOpenChangeEmail.run(); }
        });
        resetPassword.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { onSendResetPassword.run(); }
        });
        logout.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { onLogout.run(); }
        });

        Table table = new Table();
        table.top().padTop(18);
        table.add(changeEmail).width(320).height(78).padBottom(10).row();
        table.add(resetPassword).width(320).height(78).padBottom(10).row();
        table.add(logout).width(320).height(78).padBottom(16).row();
        return table;
    }
}
