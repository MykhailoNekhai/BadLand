package ua.uni.presentation.screen.menu.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class FontQuality {
    private static final float VIRTUAL_W = 1280f;

    private FontQuality() {}

    public static float screenScale() {
        return Math.max(1f, (float) Gdx.graphics.getWidth() / VIRTUAL_W);
    }

    /**
     * Apply to FreeTypeFontParameter before generateFont().
     * Scales size/borderWidth for HiDPI and enables Linear filtering + Full hinting.
     * After generating the font, call {@link #fixScale(BitmapFont)} on it.
     */
    public static void apply(FreeTypeFontGenerator.FreeTypeFontParameter p) {
        float scale = screenScale();
        p.size = Math.round(p.size * scale);
        p.borderWidth *= scale;
        p.minFilter = TextureFilter.Linear;
        p.magFilter = TextureFilter.Linear;
        p.hinting = FreeTypeFontGenerator.Hinting.Full;
        p.gamma = 1.4f;
    }

    /** Sets font scale back to virtual coordinates after HiDPI generation. */
    public static void fixScale(BitmapFont font) {
        font.getData().setScale(1f / screenScale());
    }
}
