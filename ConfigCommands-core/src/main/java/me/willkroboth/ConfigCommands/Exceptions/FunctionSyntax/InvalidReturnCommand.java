package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

public class InvalidReturnCommand extends InvalidFunctionLine {
    public InvalidReturnCommand(String arg, String reason) {
        super("return", arg, reason);
    }
}
