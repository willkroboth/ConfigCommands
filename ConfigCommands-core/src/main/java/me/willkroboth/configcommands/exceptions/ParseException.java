package me.willkroboth.configcommands.exceptions;

import com.mojang.brigadier.StringReader;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;

/**
 * An exception thrown when there is a problem with the String representation of
 * an {@link Expression} that means it cannot be parsed.
 */
public class ParseException extends RegistrationException {
    private static String formatMessage(StringReader stringReader, String reason) {
        return "Error while parsing \"" + stringReader.getString() + "\" at position " + stringReader.getCursor() + ". " + reason;
    }

    /**
     * Creates a new {@link ParseException} with the message:
     * <pre>{@code "Error while parsing \"" + stringReader.getString() + "\" at position " + stringReader.getCursor() + ". " + reaso}</pre>
     *
     * @param stringReader The String being read when the problem was found.
     * @param reason  The reason why this exception was thrown.
     */
    public ParseException(StringReader stringReader, String reason) {
        super(formatMessage(stringReader, reason));
    }

    /**
     * Creates a new {@link ParseException} with the message:
     * <pre>{@code "Error while parsing \"" + stringReader.getString() + "\" at position " + stringReader.getCursor() + ". " + reaso}</pre>
     *
     * @param stringReader The String being read when the problem was found.
     * @param reason  The reason why this exception was thrown.
     * @param cause A {@link Throwable} that caused this exception
     */
    public ParseException(StringReader stringReader, String reason, Throwable cause) {
        super(formatMessage(stringReader, reason), cause);
    }
}
