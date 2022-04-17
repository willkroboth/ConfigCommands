package me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions;

public class InvalidIfCommand extends InvalidExpressionCommand {
    public InvalidIfCommand(String arg, String reason) {
        super("if", arg, reason);
    }
}
