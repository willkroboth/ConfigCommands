package me.willkroboth.ConfigCommands.Exceptions;

public class IncorrectArgumentKey extends RegistrationException {
    public IncorrectArgumentKey(String arg, String key, String reason) {
        super("Command has invalid argument: " + arg + " with key \"" + key + "\". " + reason);
    }
}
