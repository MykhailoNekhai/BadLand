package ua.uni.language;

import java.util.HashMap;
import java.util.Map;

import ua.uni.config.GameSettings;

public final class language {
    public static final String[] LANGUAGES = {"EN", "UK"};
    public static final String FONT_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
                    + " .,!?:;'\"()[]<>-_+/*=#@&%$→"
                    + "АБВГДЕЖЗИЙКЛМНОП"
                    + "РСТУФХЦЧШЩЬЮЯ"
                    + "ЄІЇҐ";

    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("SOUNDS", "SOUNDS");
        en.put("STATISTICS", "STATISTICS");
        en.put("ACHIEVEMENTS", "ACHIEVEMENTS");
        en.put("LANGUAGE", "LANGUAGE");
        en.put("CREDITS", "CREDITS");
        en.put("KEY_BINDINGS", "KEY BINDINGS");
        en.put("BACK", "BACK");
        en.put("LOG_OUT", "LOG OUT");
        en.put("READY", "READY");
        en.put("CREDITS_TEAM", "SHADOW FLIGHT TEAM");
        en.put("SINGLE_PLAYER", "SINGLE PLAYER");
        en.put("COOP", "COOP");
        en.put("OPTIONS", "OPTIONS");
        en.put("EXIT", "EXIT");
        en.put("MOVE_LEFT", "MOVE LEFT");
        en.put("MOVE_RIGHT", "MOVE RIGHT");
        en.put("JUMP", "JUMP");
        en.put("INTERACT", "INTERACT");
        en.put("PAUSE", "PAUSE");
        en.put("SPRINT", "SPRINT");
        en.put("ATTACK", "ATTACK");
        en.put("INVENTORY", "INVENTORY");
        en.put("CLICK_TO_TOGGLE", "CLICK TO TOGGLE");
        en.put("COMING_SOON", "COMING SOON");
        en.put("UNLOCKED", "UNLOCKED");
        en.put("LOCKED", "LOCKED");
        TRANSLATIONS.put("EN", en);

        Map<String, String> uk = new HashMap<>();
        uk.put("SOUNDS", "ЗВУК");
        uk.put("STATISTICS", "СТАТИСТИКА");
        uk.put("ACHIEVEMENTS", "ДОСЯГНЕННЯ");
        uk.put("LANGUAGE", "МОВА");
        uk.put("CREDITS", "АВТОРИ");
        uk.put("KEY_BINDINGS", "КЛАВІШІ");
        uk.put("BACK", "НАЗАД");
        uk.put("LOG_OUT", "ВИЙТИ");
        uk.put("READY", "ГОТОВО");
        uk.put("CREDITS_TEAM", "КОМАНДА SHADOW FLIGHT");
        uk.put("SINGLE_PLAYER", "ОДИНОЧНА ГРА");
        uk.put("COOP", "КООП");
        uk.put("OPTIONS", "ОПЦІЇ");
        uk.put("EXIT", "ВИХІД");
        uk.put("MOVE_LEFT", "ВЛІВО");
        uk.put("MOVE_RIGHT", "ВПРАВО");
        uk.put("JUMP", "СТРИБОК");
        uk.put("INTERACT", "ДІЯ");
        uk.put("PAUSE", "ПАУЗА");
        uk.put("SPRINT", "СПРИНТ");
        uk.put("ATTACK", "АТАКА");
        uk.put("INVENTORY", "ІНВЕНТАР");
        uk.put("CLICK_TO_TOGGLE", "КЛІК ЩОБ ЗМІНИТИ");
        uk.put("COMING_SOON", "СКОРО");
        uk.put("UNLOCKED", "ВІДКРИТО");
        uk.put("LOCKED", "ЗАКРИТО");
        TRANSLATIONS.put("UK", uk);
    }

    private language() {}

    public static String t(String key) {
        Map<String, String> table = TRANSLATIONS.get(GameSettings.getLanguage());
        if (table == null) table = TRANSLATIONS.get("EN");
        String value = table.get(key);
        return value != null ? value : key;
    }
}
