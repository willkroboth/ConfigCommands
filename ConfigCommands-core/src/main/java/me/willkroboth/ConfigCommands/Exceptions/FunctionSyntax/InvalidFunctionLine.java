package me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax;

import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;

public class InvalidFunctionLine extends RegistrationException {
    public InvalidFunctionLine(String name, String arg, String reason) {
        super("Invalid " + name + " command: \"" + arg + "\". " + reason);
    }
}
