package me.willkroboth.configcommands.exceptions;

import me.willkroboth.configcommands.functions.InstanceFunction;
import me.willkroboth.configcommands.functions.StaticFunction;
import me.willkroboth.configcommands.registeredcommands.CommandExecutorBuilder;

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
