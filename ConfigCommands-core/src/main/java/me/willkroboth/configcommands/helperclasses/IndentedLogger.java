package me.willkroboth.configcommands.helperclasses;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that wraps a Java {@link Logger} to send indented and formatted messages.
 * This class extends {@link IndentedStringHandler} with {@code "\t"} as its
 * {@link IndentedStringHandler#indentationString}.
 */
public class IndentedLogger extends IndentedStringHandler {
    private final Logger logger;

    /**
     * Creates a new {@link IndentedLogger} that formats messages and uses the given Java {@link Logger} to display them.
     *
     * @param l The Java {@link Logger} that is used for sending messages.
     */
    public IndentedLogger(Logger l) {
        super("\t");
        logger = l;
    }

    /**
     * Logs a formatted message ({@link IndentedStringHandler#formatMessage(String, Object...)}) using {@link Logger#info(String)}.
     *
     * @param message The message to send.
     * @param args    A variadic array of Objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public void info(String message, Object... args) {
        logger.info(formatMessage(message, args));
    }

    /**
     * Logs a formatted message ({@link IndentedStringHandler#formatMessage(String, Object...)})
     * using {@link Logger#info(String)} if debug is enabled.
     *
     * @param message The message to send.
     * @param args    A variadic array of Objects to insert into the message using {@link String#format(String, Object...)}.
     * @param debug   True if the message should be logged, and false otherwise.
     */
    public void logDebug(boolean debug, String message, Object... args) {
        if (debug) logger.info(formatMessage(message, args));
    }

    /**
     * Logs a formatted message ({@link IndentedStringHandler#formatMessage(String, Object...)})
     * using {@link Logger#log(Level, String)}, with the level set to {@link Level#WARNING}.
     *
     * @param message The message to send as a warning.
     * @param args    A variadic array of Objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public void warn(String message, Object... args) {
        logger.log(Level.WARNING, formatMessage(message, args));
    }

    /**
     * Logs a formatted message ({@link IndentedStringHandler#formatMessage(String, Object...)})
     * using {@link Logger#log(Level, String)}, with the level set to {@link Level#SEVERE}.
     *
     * @param message The message to send as an error.
     * @param args    A variadic array of Objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public void error(String message, Object... args) {
        logger.log(Level.SEVERE, formatMessage(message, args));
    }

    /**
     * @return The {@link Logger} this {@link IndentedLogger} is using to send its messages.
     */
    public Logger getLogger() {
        return logger;
    }
}
