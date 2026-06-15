package ua.uni.presentation.screen.menu.settings;

import ua.uni.core.config.GameSettings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LanguageButton {
    public static final String[] LANGUAGES = {"EN", "UK"};
    public static final String FONT_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
                    + " .,!?:;'\"()[]<>-_+/*=#@&%$→\n%"
                    + "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЬЮЯЄІЇҐ"
                    + "абвгдежзийклмнопрстуфхцчшщьюяєіїґ";

    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        Map<String, String> uk = new HashMap<>();

        put(en, uk, "SOUNDS", "SOUNDS", "ЗВУК");
        put(en, uk, "STATISTICS", "STATISTICS", "СТАТИСТИКА");
        put(en, uk, "ACHIEVEMENTS", "ACHIEVEMENTS", "ДОСЯГНЕННЯ");
        put(en, uk, "LANGUAGE", "LANGUAGE", "МОВА");
        put(en, uk, "CREDITS", "CREDITS", "АВТОРИ");
        put(en, uk, "KEY_BINDINGS", "KEY BINDINGS", "КЛАВІШІ");
        put(en, uk, "BACK", "BACK", "НАЗАД");
        put(en, uk, "LOG_OUT", "LOG OUT", "ВИЙТИ");
        put(en, uk, "READY", "READY", "ГОТОВО");
        put(en, uk, "CREDITS_TEAM", "TEAM", "КОМАНДА");
        put(en, uk, "SINGLE_PLAYER", "SINGLE PLAYER", "ОДИНОЧНА ГРА");
        put(en, uk, "COOP", "CO-OP", "КООП");
        put(en, uk, "OPTIONS", "OPTIONS", "ОПЦІЇ");
        put(en, uk, "EXIT", "EXIT", "ВИХІД");
        put(en, uk, "MOVE_LEFT", "MOVE LEFT", "ВЛІВО");
        put(en, uk, "MOVE_RIGHT", "MOVE RIGHT", "ВПРАВО");
        put(en, uk, "JUMP", "JUMP", "СТРИБОК");
        put(en, uk, "INTERACT", "INTERACT", "ДІЯ");
        put(en, uk, "MOVE_UP", "MOVE UP", "ВГОРУ");
        put(en, uk, "MOVE_DOWN", "MOVE DOWN", "ВНИЗ");
        put(en, uk, "PAUSE", "PAUSE", "ПАУЗА");
        put(en, uk, "CLICK_TO_TOGGLE", "CLICK TO TOGGLE", "КЛІК ЩОБ ЗМІНИТИ");
        put(en, uk, "COMING_SOON", "COMING SOON", "СКОРО");
        put(en, uk, "UNLOCKED", "UNLOCKED", "ВІДКРИТО");
        put(en, uk, "LOCKED", "LOCKED", "ЗАКРИТО");
        put(en, uk, "TOTAL_DEATHS", "TOTAL DEATHS", "СМЕРТЕЙ ВСЬОГО");
        put(en, uk, "ACHIEVEMENTS_PROGRESS", "ACHIEVEMENTS", "ДОСЯГНЕННЯ");
        put(en, uk, "DEVELOPED_BY", "DEVELOPED BY", "РОЗРОБЛЕНО");
        put(en, uk, "YEAR", "2026", "2026");
        put(en, uk, "LEVELS", "LEVELS", "РІВНІ");
        put(en, uk, "EMAIL", "Email", "Пошта");
        put(en, uk, "PASSWORD", "Password", "Пароль");
        put(en, uk, "NICKNAME_REGISTER", "Nickname (for register)", "Нікнейм (для реєстрації)");
        put(en, uk, "LOGIN", "LOGIN", "УВІЙТИ");
        put(en, uk, "REGISTER", "REGISTER", "РЕЄСТРАЦІЯ");
        put(en, uk, "RESET_PASSWORD", "RESET PASSWORD", "СКИНУТИ ПАРОЛЬ");
        put(en, uk, "RESEND_VERIFICATION", "RESEND VERIFICATION", "НАДІСЛАТИ ПІДТВЕРДЖЕННЯ");
        put(en, uk, "FILL_EMAIL_PASSWORD", "Fill email and password", "Заповніть пошту і пароль");
        put(en, uk, "VERIFY_EMAIL_FIRST", "Verify your email first. Check inbox.", "Спочатку підтвердьте пошту. Перевірте вхідні.");
        put(en, uk, "LOGIN_FAILED_FMT", "Login failed: %s", "Вхід не вдався: %s");
        put(en, uk, "FILL_REGISTER_FIELDS", "Fill all fields for register", "Заповніть усі поля для реєстрації");
        put(en, uk, "REGISTERED_VERIFY_LOGIN", "Registered. Verify email, then login.", "Зареєстровано. Підтвердьте пошту, потім увійдіть.");
        put(en, uk, "REGISTER_FAILED_FMT", "Register failed: %s", "Реєстрація не вдалась: %s");
        put(en, uk, "ENTER_EMAIL_FIRST", "Enter your email first", "Спочатку введіть пошту");
        put(en, uk, "PASSWORD_RESET_SENT", "Password reset email sent.", "Лист для скидання пароля надіслано.");
        put(en, uk, "RESET_FAILED_FMT", "Reset failed: %s", "Скидання не вдалося: %s");
        put(en, uk, "FILL_EMAIL_PASSWORD_FIRST", "Fill email and password first", "Спочатку заповніть пошту і пароль");
        put(en, uk, "VERIFICATION_SENT_AGAIN", "Verification email sent again.", "Лист підтвердження надіслано повторно.");
        put(en, uk, "RESEND_FAILED_FMT", "Resend failed: %s", "Повторне надсилання не вдалося: %s");
        put(en, uk, "ACHIEVEMENT_UNLOCKED", "ACHIEVEMENT UNLOCKED", "ДОСЯГНЕННЯ ВІДКРИТО");
        put(en, uk, "PRESS_ANY_KEY_SKIP", "Press any key to skip", "Натисніть будь-яку клавішу, щоб пропустити");
        put(en, uk, "PRESS_ENTER_ESC_RETURN", "Press ENTER or ESC to return.", "Натисніть ENTER або ESC, щоб повернутися.");
        put(en, uk, "COOP_LOBBY", "CO-OP LOBBY", "КООП-ЛОБІ");
        put(en, uk, "COOP_LOBBY_INFO", "Host selects level and starts. Everyone must be ready.", "Хост обирає рівень і стартує матч. Усі мають бути готові.");
        put(en, uk, "ENTER_MATCH_ID", "Enter match ID", "Введіть ID матчу");
        put(en, uk, "CREATE_MATCH", "CREATE MATCH", "СТВОРИТИ МАТЧ");
        put(en, uk, "JOIN_MATCH", "JOIN MATCH", "ПРИЄДНАТИСЬ");
        put(en, uk, "LEAVE_MATCH", "LEAVE MATCH", "ПОКИНУТИ МАТЧ");
        put(en, uk, "START", "START", "СТАРТ");
        put(en, uk, "LEVEL_PREV", "LEVEL -", "РІВЕНЬ -");
        put(en, uk, "LEVEL_NEXT", "LEVEL +", "РІВЕНЬ +");
        put(en, uk, "READY_ON", "READY: ON", "ГОТОВО: ТАК");
        put(en, uk, "READY_OFF", "READY: OFF", "ГОТОВО: НІ");
        put(en, uk, "CREATE_OR_JOIN_MATCH", "Create a room or join by match ID.", "Створіть кімнату або приєднайтесь за ID матчу.");
        put(en, uk, "MATCH_CREATED_WAITING", "Match created. Waiting for players.", "Матч створено. Очікування гравців.");
        put(en, uk, "ENTER_MATCH_ID_FIRST", "Enter a match ID first.", "Спочатку введіть ID матчу.");
        put(en, uk, "JOINED_MATCH_WAITING", "Joined match. Waiting for host lobby sync.", "До матчу приєднано. Очікування синхронізації від хоста.");
        put(en, uk, "JOIN_OR_CREATE_FIRST", "Join or create a match first.", "Спочатку створіть матч або приєднайтесь.");
        put(en, uk, "READY_SENT_TO_HOST", "Ready state sent to host.", "Статус готовності надіслано хосту.");
        put(en, uk, "ONLY_HOST_SELECT", "Only the host can select level.", "Лише хост може вибирати рівень.");
        put(en, uk, "HOST_SELECTED_LEVEL_FMT", "Host selected level %s.", "Хост вибрав рівень %s.");
        put(en, uk, "ONLY_HOST_START", "Only the host can start the match.", "Лише хост може почати матч.");
        put(en, uk, "NEED_AT_LEAST_TWO", "Need at least 2 players.", "Потрібно щонайменше 2 гравці.");
        put(en, uk, "EVERYONE_READY", "Everyone must be ready before start.", "Усі гравці мають бути готові перед стартом.");
        put(en, uk, "ROLE_FMT", "Role: %s", "Роль: %s");
        put(en, uk, "ROLE_NONE", "not in lobby", "не в лобі");
        put(en, uk, "ROLE_HOST", "HOST", "ХОСТ");
        put(en, uk, "ROLE_GUEST", "GUEST", "ГОСТЬ");
        put(en, uk, "SESSION_FMT", "Session: %s", "Сесія: %s");
        put(en, uk, "SESSION_DISCONNECTED", "disconnected", "відключено");
        put(en, uk, "MATCH_FMT", "Match: %s", "Матч: %s");
        put(en, uk, "MATCH_NONE", "none", "немає");
        put(en, uk, "LEVEL_FMT", "Level: %s%s", "Рівень: %s%s");
        put(en, uk, "LEVEL_HOST_SELECTS", " (host selects)", " (обирає хост)");
        put(en, uk, "LEVEL_HOST_CONTROLS", " (host controls)", " (контролює хост)");
        put(en, uk, "PLAYERS_HEADER", "Players:\n", "Гравці:\n");
        put(en, uk, "WAITING_FOR_LOBBY", "Waiting for lobby...", "Очікування лобі...");
        put(en, uk, "PLAYER_HOST_PREFIX", "[HOST] ", "[ХОСТ] ");
        put(en, uk, "PLAYER_GUEST_PREFIX", "[GUEST] ", "[ГОСТЬ] ");
        put(en, uk, "PLAYER_READY", "READY", "ГОТОВИЙ");
        put(en, uk, "PLAYER_NOT_READY", "NOT READY", "НЕ ГОТОВИЙ");
        put(en, uk, "DISCONNECTED_FROM_LOBBY", "Disconnected from lobby.", "Відключено від лобі.");
        put(en, uk, "SOCKET_ERROR_FMT", "Socket error: %s", "Помилка сокета: %s");
        put(en, uk, "LOBBY_UPDATED", "Lobby updated.", "Лобі оновлено.");
        put(en, uk, "READY_CHANGED_FMT", "%s changed ready state.", "%s змінив статус готовності.");
        put(en, uk, "HOST_DISCONNECTED", "Host disconnected. Lobby closed.", "Хост відключився. Лобі закрито.");
        put(en, uk, "LOBBY_PRESENCE_UPDATED", "Lobby presence updated.", "Склад лобі оновлено.");
        put(en, uk, "CLOSED_COOP_LOBBY", "Closed coop lobby.", "Кооп-лобі закрито.");
        put(en, uk, "LEFT_LOBBY", "Left lobby.", "Ви покинули лобі.");
        put(en, uk, "COOP_LEVEL_TITLE_FMT", "CO-OP LEVEL %s", "КООП-РІВЕНЬ %s");
        put(en, uk, "COOP_LEVEL_PLACEHOLDER", "Placeholder co-op level.\nENTER = finish, K = local death, ESC = abort match", "Тимчасовий кооп-рівень.\nENTER = завершити, K = локальна смерть, ESC = скасувати матч");
        put(en, uk, "PLAYER_LEFT_MATCH", "A player left the match.", "Гравець покинув матч.");
        put(en, uk, "YOU_ARE_DEAD", "You are dead. Waiting for teammate...", "Ви загинули. Очікування напарника...");
        put(en, uk, "VICTORY", "Victory", "Перемога");
        put(en, uk, "COOP_LEVEL_COMPLETED_FMT", "Co-op level %s completed.", "Кооп-рівень %s завершено.");
        put(en, uk, "GAME_OVER", "Game Over", "Кінець гри");
        put(en, uk, "ALL_PLAYERS_DIED", "All players died. Match ended.", "Усі гравці загинули. Матч завершено.");
        put(en, uk, "CONNECTION_ERROR", "Connection Error", "Помилка з'єднання");
        put(en, uk, "CONNECTION_LOST_COOP", "Connection lost during co-op level.", "Зв'язок під час кооп-рівня втрачено.");
        put(en, uk, "PLAYER_DISCONNECTED", "A player disconnected.", "Один із гравців відключився.");
        put(en, uk, "MATCH_ENDED", "Match Ended", "Матч завершено");
        put(en, uk, "COOP_MATCH_FINISHED", "Co-op match finished.", "Кооп-матч завершено.");
        put(en, uk, "TEAMMATE_DIED", "Teammate died. If you die too, the match ends.", "Напарник загинув. Якщо загинете і ви, матч завершиться.");
        put(en, uk, "TEAMMATE_ALIVE", "Teammate is alive.", "Напарник живий.");
        put(en, uk, "PLAYER_DISCONNECTED_LEVEL", "A player disconnected from the level.", "Гравець відключився від рівня.");
        put(en, uk, "CONTINUE", "CONTINUE", "ПРОДОВЖИТИ");
        put(en, uk, "RESTART", "RESTART", "ПЕРЕЗАПУСТИТИ");
        put(en, uk, "CHECKPOINT", "CHECKPOINT", "ЧЕКПОЙНТ");
        put(en, uk, "SETTING", "SETTINGS", "НАЛАШТУВАННЯ");
        put(en, uk, "EXIT_TO_MENU", "EXIT TO MENU", "ВИЙТИ В МЕНЮ");
        put(en, uk, "ACCOUNT", "ACCOUNT", "АКАУНТ");
        put(en, uk, "SECURITY", "SECURITY", "БЕЗПЕКА");
        put(en, uk, "CUSTOMIZE", "CUSTOMIZE", "КАСТОМІЗАЦІЯ");
        put(en, uk, "CHANGE_AVATAR", "CHANGE AVATAR", "ЗМІНИТИ АВАТАР");
        put(en, uk, "CHANGE_NICKNAME", "CHANGE NICKNAME", "ЗМІНИТИ НІКНЕЙМ");
        put(en, uk, "NICKNAME", "NICKNAME", "НІКНЕЙМ");
        put(en, uk, "CURRENT_NICKNAME", "CURRENT NICKNAME", "ПОТОЧНИЙ НІКНЕЙМ");
        put(en, uk, "ID", "ID", "ID");
        put(en, uk, "DEATHS", "DEATHS", "СМЕРТІ");
        put(en, uk, "WINS", "WINS", "ВИГРАШІ");
        put(en, uk, "LOSSES", "LOSSES", "ПРОГРАШІ");
        put(en, uk, "PLAY_TIME", "PLAY TIME", "ЧАС ГРИ");
        put(en, uk, "SCORE", "SCORE", "РАХУНОК");
        put(en, uk, "SOON", "SOON", "СКОРО");
        put(en, uk, "CHANGE_EMAIL", "CHANGE EMAIL", "ЗМІНИТИ ПОШТУ");
        put(en, uk, "SESSION_EMAIL_HINT", "Firebase actions will use your current account email.", "Firebase-дії використовуватимуть поточну пошту акаунта.");
        put(en, uk, "LOGIN_FIRST_FIREBASE", "Login first to enable Firebase actions.", "Спочатку увійдіть, щоб увімкнути Firebase-дії.");
        put(en, uk, "SKINS", "SKINS", "СКІНИ");
        put(en, uk, "TRAILS", "TRAILS", "СЛІДИ");
        put(en, uk, "COSMETICS", "COSMETICS", "КОСМЕТИКА");
        put(en, uk, "AVATAR_CHANGE", "AVATAR CHANGE", "ЗМІНА АВАТАРА");
        put(en, uk, "CHOOSE_PHOTO", "CHOOSE PHOTO", "ОБРАТИ ФОТО");
        put(en, uk, "PASTE_PHOTO_PATH", "Paste photo path", "Вставте шлях до фото");
        put(en, uk, "APPLY_PHOTO_PATH", "APPLY PHOTO PATH", "ЗАСТОСУВАТИ ШЛЯХ ДО ФОТО");
        put(en, uk, "UPLOAD_TARGET", "UPLOAD TARGET", "ЦІЛЬ ЗАВАНТАЖЕННЯ");
        put(en, uk, "FIREBASE_STORAGE", "FIREBASE STORAGE", "СХОВИЩЕ FIREBASE");
        put(en, uk, "LOCAL_JSON_PROFILE", "LOCAL JSON PROFILE", "ЛОКАЛЬНИЙ JSON ПРОФІЛЬ");
        put(en, uk, "AVATAR_LOCAL_JSON_HINT", "Avatar path is stored locally in a JSON profile file.", "Шлях до аватара зберігається локально у JSON-файлі профілю.");
        put(en, uk, "LOCAL_PILOT", "LOCAL PILOT", "ЛОКАЛЬНИЙ ПІЛОТ");
        put(en, uk, "LOCAL_PROFILE", "LOCAL PROFILE", "ЛОКАЛЬНИЙ ПРОФІЛЬ");
        put(en, uk, "LOCAL", "LOCAL", "ЛОКАЛЬНО");
        put(en, uk, "UNKNOWN", "UNKNOWN", "НЕВІДОМО");
        put(en, uk, "MINUTES_FMT", "%dm", "%dхв");
        put(en, uk, "HOURS_MINUTES_FMT", "%dh %dm", "%dг %dхв");
        put(en, uk, "SECONDS_FMT", "%ds", "%dс");
        put(en, uk, "RESET", "RESET", "СКИНУТИ");
        put(en, uk, "RESET_ACHIEVEMENTS", "RESET ACHIEVEMENTS", "СКИНУТИ ДОСЯГНЕННЯ");

        TRANSLATIONS.put("EN", en);
        TRANSLATIONS.put("UK", uk);
    }

    private LanguageButton() {}

    private static void put(Map<String, String> en, Map<String, String> uk, String key, String enValue, String ukValue) {
        en.put(key, enValue);
        uk.put(key, ukValue);
    }

    public static String t(String key) {
        Map<String, String> table = TRANSLATIONS.get(GameSettings.getLanguage());
        if (table == null) table = TRANSLATIONS.get("EN");
        String value = table.get(key);
        return value != null ? value : key;
    }

    public static String tf(String key, Object... args) {
        return String.format(Locale.ROOT, t(key), args);
    }

    public static String achievementTitle(String code, String fallback) {
        if (!"UK".equals(GameSettings.getLanguage())) return fallback;
        return switch (code) {
            case "LEVEL_01" -> "Перші крила";
            case "LEVEL_02" -> "У тернини";
            case "LEVEL_03" -> "Крізь імлу";
            case "LEVEL_04" -> "Темна течія";
            case "LEVEL_05" -> "Без шляху назад";
            case "LEVEL_06" -> "Глибше вниз";
            case "LEVEL_07" -> "Порожнистий шлях";
            case "LEVEL_08" -> "Де згасає світло";
            case "LEVEL_09" -> "Край тиші";
            case "LEVEL_10" -> "Згасаюче відлуння";
            case "LEVEL_11" -> "Після відлуння";
            case "LEVEL_12" -> "Скуті корінням";
            case "LEVEL_13" -> "Попелястий серпанок";
            case "LEVEL_14" -> "Холодна порожнеча";
            case "LEVEL_15" -> "Палаючий прохід";
            case "LEVEL_16" -> "Під пологом";
            case "LEVEL_17" -> "Крізь статику";
            case "LEVEL_18" -> "Останній жар";
            case "LEVEL_19" -> "Тихий спуск";
            case "LEVEL_20" -> "За межею темряви";
            case "DEATH_1_TOTAL" -> "Перше падіння";
            case "DEATH_5_TOTAL" -> "Зламані крила";
            case "DEATH_10_TOTAL" -> "Стертий темрявою";
            case "FIRST_TRY_ANY_LEVEL" -> "Один подих";
            case "PLAYTIME_10_MIN" -> "У мороці";
            case "PLAYTIME_30_MIN" -> "Загублений у дикості";
            case "PLAYTIME_60_MIN" -> "Єдиний з темрявою";
            case "ALL_LEVELS_COMPLETE" -> "Це кінець?";
            case "COOP_SESSION" -> "Ніколи не сам";
            case "WINS_1_TOTAL" -> "Перша перемога";
            case "WINS_5_TOTAL" -> "Шлях переможця";
            case "WINS_10_TOTAL" -> "Стійкий політ";
            case "WINS_15_TOTAL" -> "Підкорювач тіней";
            case "WINS_20_TOTAL" -> "Володар порожнечі";
            case "LOSSES_1_TOTAL" -> "Перша поразка";
            case "LOSSES_5_TOTAL" -> "Знову впав";
            case "LOSSES_10_TOTAL" -> "Позначений поразкою";
            case "LOSSES_15_TOTAL" -> "У безодню";
            case "LOSSES_20_TOTAL" -> "Ще не зламаний";
            case "SCORE_1000_TOTAL" -> "Перша іскра";
            case "SCORE_5000_TOTAL" -> "Збирач сяйва";
            case "SCORE_10000_TOTAL" -> "Збирач попелу";
            case "SCORE_15000_TOTAL" -> "Полум'я зростає";
            case "SCORE_20000_TOTAL" -> "Хранитель жару";
            case "SCORE_25000_TOTAL" -> "Тіньовий скарб";
            case "SCORE_30000_TOTAL" -> "Володар жарин";
            case "SCORE_50000_TOTAL" -> "Темна скарбниця";
            case "SCORE_100000_TOTAL" -> "Корона тіней";
            default -> fallback;
        };
    }

    public static String achievementMessage(String code, String fallback) {
        if (!"UK".equals(GameSettings.getLanguage())) return fallback;
        return switch (code) {
            case "LEVEL_01" -> "Пройдіть перший рівень.";
            case "LEVEL_02" -> "Виживіть і завершіть рівень 2.";
            case "LEVEL_03" -> "Проберіться крізь рівень 3.";
            case "LEVEL_04" -> "Дістаньтесь кінця рівня 4.";
            case "LEVEL_05" -> "Завершіть п'ятий рівень.";
            case "LEVEL_06" -> "Просуньтесь уперед і пройдіть рівень 6.";
            case "LEVEL_07" -> "Пройдіть небезпечний шлях сьомого рівня.";
            case "LEVEL_08" -> "Подолайте темряву восьмого рівня.";
            case "LEVEL_09" -> "Подолайте рівень 9.";
            case "LEVEL_10" -> "Перейдіть порожнисту темряву десятого рівня.";
            case "LEVEL_11" -> "Просуньтесь далі й пройдіть рівень 11.";
            case "LEVEL_12" -> "Переживіть небезпеки рівня 12.";
            case "LEVEL_13" -> "Подолайте випробування рівня 13.";
            case "LEVEL_14" -> "Дістаньтесь кінця рівня 14.";
            case "LEVEL_15" -> "Пройдіть рівень 15.";
            case "LEVEL_16" -> "Подолайте глибини рівня 16.";
            case "LEVEL_17" -> "Переживіть виклики рівня 17.";
            case "LEVEL_18" -> "Подолайте небезпеки рівня 18.";
            case "LEVEL_19" -> "Пройдіть рівень 19.";
            case "LEVEL_20" -> "Завершіть рівень 20.";
            case "DEATH_1_TOTAL" -> "Загиньте вперше.";
            case "DEATH_5_TOTAL" -> "Накопичте 5 смертей.";
            case "DEATH_10_TOTAL" -> "Накопичте 10 смертей.";
            case "FIRST_TRY_ANY_LEVEL" -> "Пройдіть будь-який рівень з першої спроби.";
            case "PLAYTIME_10_MIN" -> "Проведіть у грі 10 хвилин.";
            case "PLAYTIME_30_MIN" -> "Проведіть у грі 30 хвилин.";
            case "PLAYTIME_60_MIN" -> "Проведіть у грі одну годину.";
            case "ALL_LEVELS_COMPLETE" -> "Пройдіть усі рівні.";
            case "COOP_SESSION" -> "Запустіть кооп-сесію.";
            case "WINS_1_TOTAL" -> "Переможіть уперше.";
            case "WINS_5_TOTAL" -> "Здобудьте 5 перемог загалом.";
            case "WINS_10_TOTAL" -> "Здобудьте 10 перемог загалом.";
            case "WINS_15_TOTAL" -> "Здобудьте 15 перемог загалом.";
            case "WINS_20_TOTAL" -> "Здобудьте 20 перемог загалом.";
            case "LOSSES_1_TOTAL" -> "Програйте вперше.";
            case "LOSSES_5_TOTAL" -> "Накопичте 5 поразок загалом.";
            case "LOSSES_10_TOTAL" -> "Накопичте 10 поразок загалом.";
            case "LOSSES_15_TOTAL" -> "Накопичте 15 поразок загалом.";
            case "LOSSES_20_TOTAL" -> "Накопичте 20 поразок загалом.";
            case "SCORE_1000_TOTAL" -> "Наберіть сумарно 1,000 очок.";
            case "SCORE_5000_TOTAL" -> "Наберіть сумарно 5,000 очок.";
            case "SCORE_10000_TOTAL" -> "Наберіть сумарно 10,000 очок.";
            case "SCORE_15000_TOTAL" -> "Наберіть сумарно 15,000 очок.";
            case "SCORE_20000_TOTAL" -> "Наберіть сумарно 20,000 очок.";
            case "SCORE_25000_TOTAL" -> "Наберіть сумарно 25,000 очок.";
            case "SCORE_30000_TOTAL" -> "Наберіть сумарно 30,000 очок.";
            case "SCORE_50000_TOTAL" -> "Наберіть сумарно 50,000 очок.";
            case "SCORE_100000_TOTAL" -> "Наберіть сумарно 100,000 очок.";
            default -> fallback;
        };
    }
}
