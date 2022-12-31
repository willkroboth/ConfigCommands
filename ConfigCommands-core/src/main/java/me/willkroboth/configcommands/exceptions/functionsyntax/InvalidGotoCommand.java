package me.willkroboth.configcommands.exceptions.functionsyntax;

import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;

/**
 * An exception thrown when there is a problem with the String representation
 * of a {@link FunctionLine} starting with the String {@code "goto"}.
 */
public class InvalidGotoCommand extends InvalidFunctionLine {
    /**
     * Creates a new {@link InvalidGotoCommand} with the message:
     * <pre>{@code "Invalid goto command: \"" + arg + "\". " + reason}</pre>
     *
     * @param arg    The full command being processed.
     * @param reason The reason why this exception was thrown.
     */
    public InvalidGotoCommand(String arg, String reason) {
        super("goto", arg, reason);
    }
}
