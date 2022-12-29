package me.willkroboth.ConfigCommands.Exceptions;

import me.willkroboth.ConfigCommands.Functions.InstanceFunction;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.RegisteredCommands.CommandExecutorBuilder;

/**
 * An exception thrown when an {@link InstanceFunction} or {@link StaticFunction} is
 * run by a {@link CommandExecutorBuilder} and encounters some exception.
 */
public class CommandRunException extends RuntimeException {
    /**
     * @param message A message explaining why this exception was thrown
     */
    public CommandRunException(String message) {
        super(message);
    }

    /**
     * @param throwable A {@link Throwable} that explains why this exception was thrown
     */
    public CommandRunException(Throwable throwable) {
        super(throwable);
    }
}
