package me.willkroboth.configcommands.exceptions.functionsyntax;

import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;

/**
 * An exception thrown when there is a problem with the String representation
 * of a {@link FunctionLine} starting with the String {@code "if"}.
 */
public class InvalidIfCommand extends InvalidFunctionLine {
    /**
     * Creates a new {@link InvalidIfCommand} with the message:
     * <pre>{@code "Invalid if command: \"" + arg + "\". " + reason}</pre>
     *
     * @param arg    The full command being processed.
     * @param reason The reason why this exception was thrown.
     */
    public InvalidIfCommand(String arg, String reason) {
        super("if", arg, reason);
    }
}
