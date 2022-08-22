package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

public class InvalidGotoCommand extends InvalidExpressionCommand {
    public InvalidGotoCommand(String arg, String reason) {
        super("goto", arg, reason);
    }
}
