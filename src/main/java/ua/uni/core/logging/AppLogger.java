package ua.uni.core.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AppLogger {
    private AppLogger() {
    }

    private static Logger logger(String tag) {
        return LogManager.getLogger(tag);
    }

    public static void info(String tag, String message) {
        logger(tag).info(message);
    }

    public static void error(String tag, String message, Throwable throwable) {
        logger(tag).error(message, throwable);
    }
}
