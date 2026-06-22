package ua.uni.presentation.screen.menu.account;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ua.uni.presentation.screen.menu.account.service.PlayerAppearanceService;
import ua.uni.presentation.screen.menu.settings.LanguageButton;
import ua.uni.presentation.screen.menu.ui.MenuFx;

final class AccountAppearancePanel {
    private static final int CUSTOMIZE_ARROW_WIDTH = 50;
    private static final int CUSTOMIZE_ARROW_HEIGHT = 56;
    private static final int SELECTOR_WIDTH = 400;
    private static final int STYLE_SELECTOR_HEIGHT = 124;
    private static final int COLOR_SELECTOR_HEIGHT = 112;
    private static final int SKIN_PREVIEW_WIDTH = 58;
    private static final int SKIN_PREVIEW_HEIGHT = 64;
    private static final int EYE_STYLE_PREVIEW_SIZE = 76;
    private static final int COLOR_PREVIEW_SIZE = 48;
    private static final int CUSTOMIZE_ROW_GAP = 10;
    private static final int APPLY_BUTTON_WIDTH = 190;
    private static final int APPLY_BUTTON_HEIGHT = 58;
    private static final float EYE_STYLE_GLOW_ALPHA = 0.78f;
    private static final float EYE_COLOR_GLOW_ALPHA = 0.50f;

    private final BitmapFont smallFont;
    private final Texture skinSelectorBg;
    private final Texture colorSelectorBg;
    private final Texture arrowBtn;
    private final Texture dotTex;
    private final Texture skinPreviewTex;
    private final Map<String, Texture> eyePreviewTextures;
    private final PlayerAppearanceService appearanceService;
    private final Runnable onPlaySelect;
    private final List<SelectorBinding> bindings = new ArrayList<>();

    AccountAppearancePanel(
            BitmapFont smallFont,
            Texture skinSelectorBg,
            Texture colorSelectorBg,
            Texture arrowBtn,
            Texture dotTex,
            Texture skinPreviewTex,
            Map<String, Texture> eyePreviewTextures,
            PlayerAppearanceService appearanceService,
            Runnable onPlaySelect) {
        this.smallFont = smallFont;
        this.skinSelectorBg = skinSelectorBg;
        this.colorSelectorBg = colorSelectorBg;
        this.arrowBtn = arrowBtn;
        this.dotTex = dotTex;
        this.skinPreviewTex = skinPreviewTex;
        this.eyePreviewTextures = eyePreviewTextures;
        this.appearanceService = appearanceService;
        this.onPlaySelect = onPlaySelect;
    }

    Table build() {
        bindings.clear();
        Table table = new Table();
        table.center().padTop(2);
        table.add(buildSelector(SelectorKind.EYE_STYLE))
                .width(SELECTOR_WIDTH).height(STYLE_SELECTOR_HEIGHT)
                .padBottom(CUSTOMIZE_ROW_GAP).row();
        table.add(buildSelector(SelectorKind.EYE_COLOR))
                .width(SELECTOR_WIDTH).height(COLOR_SELECTOR_HEIGHT)
                .padBottom(8).row();
        table.add(buildApplyButton())
                .width(APPLY_BUTTON_WIDTH).height(APPLY_BUTTON_HEIGHT).row();
        return table;
    }

    private TextButton buildApplyButton() {
        TextButton.TextButtonStyle applyStyle = new TextButton.TextButtonStyle();
        applyStyle.font = smallFont;
        applyStyle.fontColor = new Color(0.95f, 0.90f, 0.65f, 1f);
        applyStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        applyStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        TextButton applyButton = new TextButton(LanguageButton.t("APPLY"), applyStyle);
        applyButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                onPlaySelect.run();
                MenuFx.runAfterGoldButtonPress(actor, AccountAppearancePanel.this::applySelections);
            }
        });
        return applyButton;
    }

    private Actor buildSelector(SelectorKind kind) {
        int savedIndex = loadIndex(kind);
        final int[] selectedIndex = {wrap(kind, savedIndex)};
        int boxWidth = SELECTOR_WIDTH;
        int boxHeight = selectorHeight(kind);
        int previewW = previewWidth(kind);
        int previewH = previewHeight(kind);
        Texture bg = isColorSelector(kind) ? colorSelectorBg : skinSelectorBg;

        Label.LabelStyle titleStyle = new Label.LabelStyle(smallFont, new Color(1f, 0.86f, 0.36f, 1f));
        Label.LabelStyle valueStyle = new Label.LabelStyle(smallFont, new Color(0.98f, 0.95f, 0.88f, 1f));
        Label.LabelStyle countStyle = new Label.LabelStyle(smallFont, new Color(0.68f, 0.66f, 0.60f, 1f));

        TextButton.TextButtonStyle arrowStyle = new TextButton.TextButtonStyle();
        arrowStyle.up = new TextureRegionDrawable(arrowBtn);
        arrowStyle.down = new TextureRegionDrawable(arrowBtn);
        arrowStyle.over = new TextureRegionDrawable(arrowBtn);
        arrowStyle.font = smallFont;
        arrowStyle.fontColor = new Color(0.98f, 0.95f, 0.88f, 1f);
        arrowStyle.overFontColor = new Color(1f, 0.92f, 0.55f, 1f);
        arrowStyle.downFontColor = new Color(1f, 0.92f, 0.55f, 1f);

        Label titleLabel = new Label(LanguageButton.t(kind.titleKey), titleStyle);
        Label valueLabel = new Label("", valueStyle);
        Label counterLabel = new Label("", countStyle);
        Image previewGlow = new Image(new TextureRegionDrawable(dotTex));
        previewGlow.setScaling(Scaling.stretch);
        Image previewImage = new Image(new TextureRegionDrawable(dotTex));
        previewImage.setScaling(Scaling.fit);

        TextButton prevButton = new TextButton("<", arrowStyle);
        TextButton nextButton = new TextButton(">", arrowStyle);

        SelectorBinding binding = new SelectorBinding(kind, selectedIndex, valueLabel, counterLabel, previewGlow, previewImage);
        bindings.add(binding);
        updateSelector(binding);

        prevButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                changeSelection(binding, -1);
            }
        });
        nextButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                changeSelection(binding, 1);
            }
        });

        int contentWidth = boxWidth - 20;
        int middleWidth = contentWidth - (CUSTOMIZE_ARROW_WIDTH * 2) - 20;
        int middleHeight = boxHeight - 48;
        int labelWidth = middleWidth - previewW - 12;

        Table labelStack = new Table();
        labelStack.left();
        labelStack.add(valueLabel).left().width(labelWidth).height(28).row();
        labelStack.add(counterLabel).left().width(labelWidth).height(22).row();

        Stack previewStack = new Stack();
        if (kind == SelectorKind.SKIN || kind == SelectorKind.EYE_STYLE) {
            Image previewBg = new Image(new TextureRegionDrawable(dotTex));
            previewBg.setScaling(Scaling.stretch);
            previewBg.setColor(0.88f, 0.84f, 0.70f, 0.55f);
            previewStack.add(previewBg);
        }
        previewStack.add(previewGlow);
        previewStack.add(previewImage);

        Table middle = new Table();
        middle.add(previewStack).width(previewW).height(previewH).padRight(12);
        middle.add(labelStack).width(labelWidth).height(Math.max(52, previewH));

        Table content = new Table();
        content.top().padTop(7).padLeft(10).padRight(10).padBottom(7);
        content.add(titleLabel).colspan(3).height(26).padBottom(2).row();
        content.add(prevButton).width(CUSTOMIZE_ARROW_WIDTH).height(CUSTOMIZE_ARROW_HEIGHT).padRight(10);
        content.add(middle).width(middleWidth).height(middleHeight);
        content.add(nextButton).width(CUSTOMIZE_ARROW_WIDTH).height(CUSTOMIZE_ARROW_HEIGHT).padLeft(10).row();

        Stack boxStack = new Stack();
        boxStack.add(new Image(new TextureRegionDrawable(bg)));
        boxStack.add(content);
        return boxStack;
    }

    private void changeSelection(SelectorBinding binding, int direction) {
        binding.selectedIndex[0] = wrap(binding.kind, binding.selectedIndex[0] + direction);
        updateSelector(binding);
        if (binding.kind == SelectorKind.EYE_STYLE) {
            refreshEyeColorPreview();
        }
        playSwap(binding.valueLabel, direction);
        playSwap(binding.previewImage, direction);
        playGlowPulse(binding);
        onPlaySelect.run();
    }

    private void applySelections() {
        for (SelectorBinding binding : bindings) {
            int safe = wrap(binding.kind, binding.selectedIndex[0]);
            switch (binding.kind) {
                case SKIN:
                    binding.selectedIndex[0] = appearanceService.selectSkinIndex(safe);
                    break;
                case BODY_COLOR:
                    binding.selectedIndex[0] = appearanceService.selectColorIndex(safe);
                    break;
                case EYE_STYLE:
                    binding.selectedIndex[0] = appearanceService.selectEyeStyleIndex(safe);
                    break;
                case EYE_COLOR:
                    binding.selectedIndex[0] = appearanceService.selectEyeColorIndex(safe);
                    break;
                default:
                    binding.selectedIndex[0] = safe;
                    break;
            }
            updateSelector(binding);
        }
    }

    private void refreshEyeColorPreview() {
        for (SelectorBinding binding : bindings) {
            if (binding.kind == SelectorKind.EYE_COLOR) {
                updateSelector(binding);
            }
        }
    }

    private void updateSelector(SelectorBinding binding) {
        int safe = wrap(binding.kind, binding.selectedIndex[0]);
        int count = count(binding.kind);
        String name = LanguageButton.t(translationKey(binding.kind, safe));
        binding.valueLabel.setText(name);
        binding.counterLabel.setText((safe + 1) + " / " + count);
        binding.previewImage.setDrawable(new TextureRegionDrawable(previewTexture(binding.kind, safe)));
        binding.previewImage.setColor(previewColor(binding.kind, safe));
        setPreviewGlowBase(binding);
        binding.previewImage.invalidateHierarchy();
    }

    private void setPreviewGlowBase(SelectorBinding binding) {
        float alpha = binding.kind == SelectorKind.EYE_STYLE ? EYE_STYLE_GLOW_ALPHA : EYE_COLOR_GLOW_ALPHA;
        binding.previewGlow.clearActions();
        binding.previewGlow.setColor(1f, 0.74f, 0.22f, alpha);
    }

    private void playGlowPulse(SelectorBinding binding) {
        float baseAlpha = binding.kind == SelectorKind.EYE_STYLE ? EYE_STYLE_GLOW_ALPHA : EYE_COLOR_GLOW_ALPHA;
        binding.previewGlow.clearActions();
        binding.previewGlow.getColor().a = 1f;
        binding.previewGlow.addAction(Actions.sequence(
                Actions.alpha(1f, 0.05f, Interpolation.sineOut),
                Actions.alpha(baseAlpha, 0.26f, Interpolation.sineOut)
        ));
    }

    private void playSwap(Actor actor, int direction) {
        actor.clearActions();
        actor.getColor().a = 0.35f;
        actor.addAction(Actions.sequence(
                Actions.moveBy(-direction * 10f, 0f, 0.05f, Interpolation.sineIn),
                Actions.parallel(
                        Actions.moveBy(direction * 10f, 0f, 0.14f, Interpolation.sineOut),
                        Actions.fadeIn(0.14f, Interpolation.fade)
                )
        ));
    }

    private int loadIndex(SelectorKind kind) {
        switch (kind) {
            case SKIN:
                return appearanceService.loadSkinIndex();
            case BODY_COLOR:
                return appearanceService.loadColorIndex();
            case EYE_STYLE:
                return appearanceService.loadEyeStyleIndex();
            case EYE_COLOR:
                return appearanceService.loadEyeColorIndex();
            default:
                return 0;
        }
    }

    private int count(SelectorKind kind) {
        switch (kind) {
            case SKIN:
                return appearanceService.skinCount();
            case BODY_COLOR:
                return appearanceService.colorCount();
            case EYE_STYLE:
                return appearanceService.eyeStyleCount();
            case EYE_COLOR:
                return appearanceService.eyeColorCount();
            default:
                return 1;
        }
    }

    private String translationKey(SelectorKind kind, int index) {
        switch (kind) {
            case SKIN:
                return appearanceService.skinAt(index).getTranslationKey();
            case BODY_COLOR:
                return appearanceService.colorAt(index).getTranslationKey();
            case EYE_STYLE:
                return appearanceService.eyeStyleAt(index).getTranslationKey();
            case EYE_COLOR:
                return appearanceService.eyeColorAt(index).getTranslationKey();
            default:
                return "";
        }
    }

    private Texture previewTexture(SelectorKind kind, int index) {
        switch (kind) {
            case SKIN:
                return skinPreviewTex;
            case EYE_STYLE:
                return eyeModelPreview(appearanceService.eyeStyleAt(index).getId(), "gray");
            default:
                return dotTex;
        }
    }

    private Color previewColor(SelectorKind kind, int index) {
        switch (kind) {
            case BODY_COLOR:
                return appearanceService.colorValue(index);
            case EYE_COLOR:
                return appearanceService.eyeColorValue(index);
            default:
                return Color.WHITE;
        }
    }

    private int selectorHeight(SelectorKind kind) {
        return isColorSelector(kind) ? COLOR_SELECTOR_HEIGHT : STYLE_SELECTOR_HEIGHT;
    }

    private int previewWidth(SelectorKind kind) {
        if (kind == SelectorKind.EYE_STYLE) {
            return EYE_STYLE_PREVIEW_SIZE;
        }
        if (kind == SelectorKind.EYE_COLOR) {
            return COLOR_PREVIEW_SIZE;
        }
        return isColorSelector(kind) ? COLOR_PREVIEW_SIZE : SKIN_PREVIEW_WIDTH;
    }

    private int previewHeight(SelectorKind kind) {
        if (kind == SelectorKind.EYE_STYLE) {
            return EYE_STYLE_PREVIEW_SIZE;
        }
        if (kind == SelectorKind.EYE_COLOR) {
            return COLOR_PREVIEW_SIZE;
        }
        return isColorSelector(kind) ? COLOR_PREVIEW_SIZE : SKIN_PREVIEW_HEIGHT;
    }

    private Texture eyeModelPreview(String eyeStyleId, String eyeColorId) {
        String style = eyeStyleId == null || eyeStyleId.isBlank() ? "shadow" : eyeStyleId.trim().toLowerCase();
        String color = eyeColorId == null || eyeColorId.isBlank() ? "gray" : eyeColorId.trim().toLowerCase();
        Texture texture = eyePreviewTextures.get(style + ":" + color);
        if (texture != null) {
            return texture;
        }
        texture = eyePreviewTextures.get("shadow:gray");
        return texture != null ? texture : dotTex;
    }

    private boolean isColorSelector(SelectorKind kind) {
        return kind == SelectorKind.BODY_COLOR || kind == SelectorKind.EYE_COLOR;
    }

    private int wrap(SelectorKind kind, int index) {
        int count = count(kind);
        int w = index % count;
        return w < 0 ? w + count : w;
    }

    private enum SelectorKind {
        SKIN("SKINS"),
        BODY_COLOR("COLORS"),
        EYE_STYLE("SKINS"),
        EYE_COLOR("COLORS");

        private final String titleKey;

        SelectorKind(String titleKey) {
            this.titleKey = titleKey;
        }
    }

    private static final class SelectorBinding {
        private final SelectorKind kind;
        private final int[] selectedIndex;
        private final Label valueLabel;
        private final Label counterLabel;
        private final Image previewGlow;
        private final Image previewImage;

        private SelectorBinding(SelectorKind kind, int[] selectedIndex,
                                Label valueLabel, Label counterLabel, Image previewGlow, Image previewImage) {
            this.kind = kind;
            this.selectedIndex = selectedIndex;
            this.valueLabel = valueLabel;
            this.counterLabel = counterLabel;
            this.previewGlow = previewGlow;
            this.previewImage = previewImage;
        }
    }
}
