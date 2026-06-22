package ua.uni.presentation.screen.menu.account.service;

import com.badlogic.gdx.graphics.Color;
import java.util.List;
import ua.uni.core.model.account.ColorOption;
import ua.uni.core.model.account.PlayerAppearance;
import ua.uni.core.model.account.SkinOption;
import ua.uni.platform.account.LocalAccountStore;

public final class PlayerAppearanceService {
    private final LocalAccountStore localStore;
    private final List<SkinOption> skins = List.of(
            new SkinOption("classic", "SKIN_CLASSIC", "game-resourses/textures/avatar-1.png", true),
            new SkinOption("runner", "SKIN_RUNNER", "game-resourses/textures/avatar-1.png", true),
            new SkinOption("thorn", "SKIN_THORN", "game-resourses/textures/avatar-1.png", true),
            new SkinOption("night", "SKIN_NIGHT", "game-resourses/textures/avatar-1.png", true)
    );
    private final List<ColorOption> colors = List.of(
            new ColorOption("shadow", "COLOR_SHADOW", 0.10f, 0.10f, 0.12f, 1f, true),
            new ColorOption("amber", "COLOR_AMBER", 1f, 0.76f, 0.24f, 1f, true),
            new ColorOption("moss", "COLOR_MOSS", 0.42f, 0.82f, 0.46f, 1f, true),
            new ColorOption("sky", "COLOR_SKY", 0.44f, 0.76f, 1f, 1f, true)
    );
    private final List<SkinOption> eyeStyles = List.of(
            new SkinOption("shadow", "EYE_STYLE_SHADOW",
                    "game-resourses/textures/avatar-eyes/shadow/animation/shadow_gray_open.png", true),
            new SkinOption("spider", "EYE_STYLE_SPIDER",
                    "game-resourses/textures/avatar-eyes/spider/animation/spider_gray_open.png", true),
            new SkinOption("round", "EYE_STYLE_ROUND",
                    "game-resourses/textures/avatar-eyes/round/animation/round_gray_open.png", true),
            new SkinOption("cluster", "EYE_STYLE_CLUSTER",
                    "game-resourses/textures/avatar-eyes/cluster/animation/cluster_gray_open.png", true),
            new SkinOption("swirl", "EYE_STYLE_SWIRL",
                    "game-resourses/textures/avatar-eyes/swirl/animation/swirl_gray_open.png", true)
    );
    private final List<ColorOption> eyeColors = List.of(
            new ColorOption("purple", "EYE_COLOR_PURPLE", 0.88f, 0.00f, 0.72f, 1f, true),
            new ColorOption("gray", "EYE_COLOR_GRAY", 0.28f, 0.28f, 0.28f, 1f, true),
            new ColorOption("green", "EYE_COLOR_GREEN", 0.04f, 0.82f, 0.04f, 1f, true),
            new ColorOption("cyan", "EYE_COLOR_CYAN", 0.00f, 0.78f, 0.95f, 1f, true),
            new ColorOption("yellow", "EYE_COLOR_YELLOW", 1.00f, 0.82f, 0.00f, 1f, true)
    );

    public PlayerAppearanceService(LocalAccountStore localStore) {
        this.localStore = localStore;
    }

    public PlayerAppearance loadAppearance() {
        return localStore.loadAppearance();
    }

    public List<SkinOption> getAvailableSkins() {
        return skins;
    }

    public List<ColorOption> getAvailableColors() {
        return colors;
    }

    public List<SkinOption> getAvailableEyeStyles() {
        return eyeStyles;
    }

    public List<ColorOption> getAvailableEyeColors() {
        return eyeColors;
    }

    public int loadSkinIndex() {
        return indexOfSkin(loadAppearance().getSkinId());
    }

    public int loadColorIndex() {
        return indexOfColor(loadAppearance().getColorId());
    }

    public int loadEyeStyleIndex() {
        return indexOfEyeStyle(loadAppearance().getEyeStyleId());
    }

    public int loadEyeColorIndex() {
        return indexOfEyeColor(loadAppearance().getEyeColorId());
    }

    public int selectSkinIndex(int index) {
        int safeIndex = wrap(index, skins.size());
        PlayerAppearance appearance = loadAppearance();
        appearance.setSkinId(skins.get(safeIndex).getId());
        localStore.saveAppearance(appearance);
        return safeIndex;
    }

    public int selectColorIndex(int index) {
        int safeIndex = wrap(index, colors.size());
        PlayerAppearance appearance = loadAppearance();
        appearance.setColorId(colors.get(safeIndex).getId());
        localStore.saveAppearance(appearance);
        return safeIndex;
    }

    public int selectEyeStyleIndex(int index) {
        int safeIndex = wrap(index, eyeStyles.size());
        PlayerAppearance appearance = loadAppearance();
        appearance.setEyeStyleId(eyeStyles.get(safeIndex).getId());
        localStore.saveAppearance(appearance);
        return safeIndex;
    }

    public int selectEyeColorIndex(int index) {
        int safeIndex = wrap(index, eyeColors.size());
        PlayerAppearance appearance = loadAppearance();
        appearance.setEyeColorId(eyeColors.get(safeIndex).getId());
        localStore.saveAppearance(appearance);
        return safeIndex;
    }

    public int changeSkin(int currentIndex, int direction) {
        return selectSkinIndex(currentIndex + direction);
    }

    public int changeColor(int currentIndex, int direction) {
        return selectColorIndex(currentIndex + direction);
    }

    public int changeEyeStyle(int currentIndex, int direction) {
        return selectEyeStyleIndex(currentIndex + direction);
    }

    public int changeEyeColor(int currentIndex, int direction) {
        return selectEyeColorIndex(currentIndex + direction);
    }

    public SkinOption skinAt(int index) {
        return skins.get(wrap(index, skins.size()));
    }

    public ColorOption colorAt(int index) {
        return colors.get(wrap(index, colors.size()));
    }

    public SkinOption eyeStyleAt(int index) {
        return eyeStyles.get(wrap(index, eyeStyles.size()));
    }

    public ColorOption eyeColorAt(int index) {
        return eyeColors.get(wrap(index, eyeColors.size()));
    }

    public Color colorValue(int index) {
        ColorOption option = colorAt(index);
        return new Color(option.getR(), option.getG(), option.getB(), option.getA());
    }

    public Color eyeColorValue(int index) {
        ColorOption option = eyeColorAt(index);
        return new Color(option.getR(), option.getG(), option.getB(), option.getA());
    }

    public int skinCount() {
        return skins.size();
    }

    public int colorCount() {
        return colors.size();
    }

    public int eyeStyleCount() {
        return eyeStyles.size();
    }

    public int eyeColorCount() {
        return eyeColors.size();
    }

    private int indexOfSkin(String skinId) {
        for (int i = 0; i < skins.size(); i++) {
            if (skins.get(i).getId().equals(skinId)) {
                return i;
            }
        }
        return 0;
    }

    private int indexOfColor(String colorId) {
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).getId().equals(colorId)) {
                return i;
            }
        }
        return 0;
    }

    private int indexOfEyeStyle(String eyeStyleId) {
        for (int i = 0; i < eyeStyles.size(); i++) {
            if (eyeStyles.get(i).getId().equals(eyeStyleId)) {
                return i;
            }
        }
        return 0;
    }

    private int indexOfEyeColor(String eyeColorId) {
        for (int i = 0; i < eyeColors.size(); i++) {
            if (eyeColors.get(i).getId().equals(eyeColorId)) {
                return i;
            }
        }
        return 1;
    }

    private int wrap(int index, int count) {
        int wrapped = index % count;
        return wrapped < 0 ? wrapped + count : wrapped;
    }
}
