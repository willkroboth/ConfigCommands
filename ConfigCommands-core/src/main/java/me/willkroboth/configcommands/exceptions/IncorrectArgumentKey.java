package me.willkroboth.configcommands.exceptions;

/**
 * An exception thrown when a command has an argument that is incorrectly configured with an invalid key.
 * See {@link IncorrectArgumentKey#IncorrectArgumentKey(String, String, String)}.
 */
public class IncorrectArgumentKey extends RegistrationException {
    /**
     * Creates a new {@link IncorrectArgumentKey} with the message:
     * <p>
     * <b>{@code "Command has invalid argument: " + arg + " with key \"" + key + "\". " + reason}</b>
     *
     * @param arg    The name of the argument that is incorrectly configured.
     * @param key    The key that has a bad value.
     * @param reason The reason the key is incorrect.
     */
    public IncorrectArgumentKey(String arg, String key, String reason) {
        super("Command has invalid argument: " + arg + " with key \"" + key + "\". " + reason);
    }
}
