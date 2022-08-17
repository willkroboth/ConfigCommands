package me.willkroboth.ConfigCommands.Exceptions;

public class ParseException extends RegistrationException {
    public ParseException(String section, String reason) {
        super("Parse error in \"" + section + "\". " + reason);
    }
}
