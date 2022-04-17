package me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions;

public class InvalidReturnCommand extends InvalidExpressionCommand {
    public InvalidReturnCommand(String arg, String reason) {
        super("return", arg, reason);
    }
}
