package me.willkroboth.configcommands.exceptions;

import me.willkroboth.configcommands.registeredcommands.expressions.Expression;

/**
 * An exception thrown when there is a problem with the String representation of
 * an {@link Expression} that means it cannot be parsed.
 */
public class ParseException extends RegistrationException {
    /**
     * Creates a new {@link ParseException} with the message:
     * <pre>{@code "Parse error in \"" + section + "\". " + reason}</pre>
     *
     * @param section The part of the input {@link Expression} String that had a problem.
     * @param reason  The reason why this exception was thrown.
     */
    public ParseException(String section, String reason) {
        super("Parse error in \"" + section + "\". " + reason);
    }
}
