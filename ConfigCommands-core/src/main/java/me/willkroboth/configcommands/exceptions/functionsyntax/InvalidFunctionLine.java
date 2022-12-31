package me.willkroboth.configcommands.exceptions.functionsyntax;

import me.willkroboth.configcommands.exceptions.RegistrationException;
import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;

/**
 * An exception that gets thrown when there is a problem with the String representation
 * of a {@link FunctionLine} that means it cannot be parsed.
 */
public class InvalidFunctionLine extends RegistrationException {
    /**
     * Creates a new {@link InvalidFunctionLine} with the message:
     * <pre>{@code "Invalid " + name + " command: \"" + arg + "\". " + reason}</pre>
     *
     * @param name   The name of the command being processed.
     * @param arg    The full command being processed.
     * @param reason The reason why this exception was thrown.
     */
    public InvalidFunctionLine(String name, String arg, String reason) {
        super("Invalid " + name + " command: \"" + arg + "\". " + reason);
    }
}
