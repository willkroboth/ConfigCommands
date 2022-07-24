package me.willkroboth.ConfigCommands.HelperClasses;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IndentedLogger {
    private final Logger logger;
    private int indentation = 0;

    public IndentedLogger(Logger l) {
        logger = l;
    }

    public void setIndentation(int i) {
        indentation = i;
    }

    public int getIndentation() {
        return indentation;
    }

    public void increaseIndentation() {
        indentation++;
    }

    public void decreaseIndentation() {
        indentation--;
    }

    private String formatMessage(String message, Object... args) {
        return "\t".repeat(indentation) + String.format(message, args);
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
