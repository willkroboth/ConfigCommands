package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

public class InvalidRunExpression extends InvalidFunctionLine {
    public InvalidRunExpression(String arg, String reason) {
        super("do", arg, reason);
    }
}
