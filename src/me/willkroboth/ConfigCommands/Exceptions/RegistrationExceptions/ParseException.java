package me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions;

public class ParseException extends RegistrationException {
    public ParseException(String section, String reason) {
        super("Parse error in \"" + section + "\". " + reason);
    }
}
