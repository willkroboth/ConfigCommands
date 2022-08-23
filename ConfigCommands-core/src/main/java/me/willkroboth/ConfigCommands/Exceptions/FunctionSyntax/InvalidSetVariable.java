package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

public class InvalidSetVariable extends InvalidFunctionLine {
    public InvalidSetVariable(String arg, String reason) {
        super("set", arg, reason);
    }
}
