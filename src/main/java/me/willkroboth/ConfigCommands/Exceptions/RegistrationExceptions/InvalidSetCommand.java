package me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions;

public class InvalidSetCommand extends InvalidExpressionCommand {
    public InvalidSetCommand(String arg, String reason) {
        super("set", arg, reason);
    }
}
