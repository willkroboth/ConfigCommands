package me.willkroboth.ConfigCommands.HelperClasses;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IndentedLogger extends IndentedStringHandler{
    private final Logger logger;

    public IndentedLogger(Logger l) {
        super("\t");
        logger = l;
    }

    public void info(String message, Object... args) {
        logger.info(formatMessage(message, args));
    }

    public void logDebug(boolean debug, String message, Object... args) {
        if (debug) logger.info(formatMessage(message, args));
    }

    public void warn(String message, Object... args) {
        logger.log(Level.WARNING, formatMessage(message, args));
    }

    public void error(String message, Object... args) {
        logger.log(Level.SEVERE, formatMessage(message, args));
    }

    public Logger getLogger() {
        return logger;
    }
}
