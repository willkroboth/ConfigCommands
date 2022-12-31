package me.willkroboth.configcommands.helperclasses;

/**
 * An interface containing just the {@link DebuggableState#isDebug()} method
 */
public interface DebuggableState {
    /**
     * @return True if the debug state is active, and false otherwise
     */
    boolean isDebug();
}
