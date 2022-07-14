package me.willkroboth.ConfigCommands.Exceptions;

public class IncorrectArgumentKey extends RegistrationException {
    public IncorrectArgumentKey(String arg, String key) {
        super("Command has invalid argument: " + arg + " for key \"" + key + "\".");
    }

    public IncorrectArgumentKey(String arg, String key, String reason) {
        super("Command has invalid argument: " + arg + " for key \"" + key + "\". " + reason);
    }

    public IncorrectArgumentKey(String arg, boolean ignored, String reason){
        super("Command has invalid argument: " + arg + ". " + reason);
    }
}
