package me.willkroboth.ConfigCommands.Exceptions;

public class CommandRunException extends RuntimeException {
    public CommandRunException(String message) {
        super(message);
    }

    public CommandRunException(Throwable throwable) {
        super(throwable);
    }
}
