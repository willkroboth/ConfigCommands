package me.willkroboth.configcommands.exceptions.functionsyntax;

import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;

/**
 * An exception thrown when there is a problem with the String representation
 * of a {@link FunctionLine} starting with the String {@code "do"}.
 */
public class InvalidRunExpression extends InvalidFunctionLine {
    /**
     * Creates a new {@link InvalidRunExpression} with the message:
     * <pre>{@code "Invalid do command: \"" + arg + "\". " + reason}</pre>
     *
     * @param arg    The full command being processed.
     * @param reason The reason why this exception was thrown.
     */
    public InvalidRunExpression(String arg, String reason) {
        super("do", arg, reason);
    }
}
