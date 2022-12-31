package me.willkroboth.configcommands.exceptions;

import me.willkroboth.configcommands.registeredcommands.CommandTreeBuilder;

/**
 * An exception that is thrown when there is an error while building and registering
 * a {@link CommandTreeBuilder}.
 */
public class RegistrationException extends Exception {
    /**
     * Creates a new {@link RegistrationException}.
     *
     * @param errorMessage The reason why this exception was thrown.
     */
    public RegistrationException(String errorMessage) {
        super(errorMessage);
    }
}

