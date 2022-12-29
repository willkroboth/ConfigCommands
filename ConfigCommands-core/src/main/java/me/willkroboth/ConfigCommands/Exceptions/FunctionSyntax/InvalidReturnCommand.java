package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

import me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines.FunctionLine;

/**
 * An exception thrown when there is a problem with the String representation
 * of a {@link FunctionLine} starting with the String {@code "return"}.
 */
public class InvalidReturnCommand extends InvalidFunctionLine {
    /**
     * Creates a new {@link InvalidReturnCommand} with the message:
     * <pre>{@code "Invalid return command: \"" + arg + "\". " + reason}</pre>
     *
     * @param arg    The full command being processed.
     * @param reason The reason why this exception was thrown.
     */
    public InvalidReturnCommand(String arg, String reason) {
        super("return", arg, reason);
    }
}
