package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

import me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines.FunctionLine;

/**
 * An exception thrown when there is a problem with the String representation
 * of a {@link FunctionLine} starting with the String {@code "<"}, indicating the start
 * of a variable name that will be set.
 */
public class InvalidSetVariable extends InvalidFunctionLine {
    /**
     * Creates a new {@link InvalidSetVariable} with the message:
     * <pre>{@code "Invalid set command: \"" + arg + "\". " + reason}</pre>
     *
     * @param arg    The full command being processed.
     * @param reason The reason why this exception was thrown.
     */
    public InvalidSetVariable(String arg, String reason) {
        super("set", arg, reason);
    }
}
