package ua.uni.presentation.screen.menu.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import ua.uni.presentation.screen.menu.settings.LanguageButton;

public final class MenuFontFactory {
    private static final String DEFAULT_FONT = "game-resourses/fonts/american_captain.ttf";

    public BitmapFont create(int size, Color color, float borderWidth, Color borderColor) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        parameter.borderWidth = borderWidth;
        parameter.borderColor = borderColor;
        parameter.characters = LanguageButton.FONT_CHARACTERS;
        return create(parameter);
    }

    public BitmapFont create(FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(DEFAULT_FONT));
        try {
            if (parameter.characters == null) {
                parameter.characters = LanguageButton.FONT_CHARACTERS;
            }
            FontQuality.apply(parameter);
            BitmapFont font = generator.generateFont(parameter);
            FontQuality.fixScale(font);
            return font;
        } finally {
            generator.dispose();
        }
    }
}
