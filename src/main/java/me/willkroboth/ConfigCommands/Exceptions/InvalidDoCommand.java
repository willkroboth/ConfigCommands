package me.willkroboth.ConfigCommands.Exceptions;

public class InvalidDoCommand extends InvalidExpressionCommand {
    public InvalidDoCommand(String arg, String reason) {
        super("do", arg, reason);
    }
}
