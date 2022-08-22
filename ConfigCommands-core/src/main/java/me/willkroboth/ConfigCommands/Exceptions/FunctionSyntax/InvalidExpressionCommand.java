package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;

public class InvalidExpressionCommand extends RegistrationException {
    public InvalidExpressionCommand(String expression, String arg, String reason) {
        super("Invalid " + expression + " command: \"" + arg + "\". " + reason);
    }
}
