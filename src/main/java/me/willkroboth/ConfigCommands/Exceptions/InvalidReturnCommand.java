package me.willkroboth.ConfigCommands.Exceptions;

public class InvalidReturnCommand extends InvalidExpressionCommand {
    public InvalidReturnCommand(String arg, String reason) {
        super("return", arg, reason);
    }
}
