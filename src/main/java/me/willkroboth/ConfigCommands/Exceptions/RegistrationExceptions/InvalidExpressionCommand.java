package me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions;

public class InvalidExpressionCommand extends RegistrationException {
    public InvalidExpressionCommand(String expression, String arg, String reason) {
        super("Invalid " + expression + " command: \"" + arg + "\". " + reason);
    }
}
