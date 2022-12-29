package me.willkroboth.ConfigCommands.HelperClasses;

/**
 * A class that holds a debug value for a global context. A reference to the same instance of this class
 * can be shared in multiple places, and updating the internal debug value will affect all those places.
 */
public class GlobalDebugValue implements DebuggableState {
    private boolean debug;

    /**
     * Creates a new {@link GlobalDebugValue} with an initial state set to the given boolean.
     *
     * @param debug The initial state for this {@link GlobalDebugValue}.
     */
    public GlobalDebugValue(boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the internal debug value to the given boolean.
     *
     * @param debug The new state for this {@link GlobalDebugValue}.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }
}
