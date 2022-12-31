package me.willkroboth.configcommands.helperclasses;

/**
 * A class that holds a debug value for a local context. A reference to the same instance of this class
 * can be shared in multiple places, and updating the internal debug value will affect all those places.
 */
public class SharedDebugValue implements DebuggableState {
    private final GlobalDebugValue globalDebug;
    private boolean localDebug;

    /**
     * Creates a new {@link SharedDebugValue} with a reference to a {@link GlobalDebugValue} and
     * an initial internal boolean state for the local debug.
     *
     * @param globalDebug The {@link GlobalDebugValue} this {@link SharedDebugValue} should use.
     * @param localDebug  The initial value for the local debug of this {@link SharedDebugValue}.
     */
    public SharedDebugValue(GlobalDebugValue globalDebug, boolean localDebug) {
        this.globalDebug = globalDebug;
        this.localDebug = localDebug;
    }

    /**
     * Sets the internal local debug value to the given boolean.
     *
     * @param localDebug The new local state for this {@link SharedDebugValue}.
     */
    public void setLocalDebug(boolean localDebug) {
        this.localDebug = localDebug;
    }

    /**
     * @return True if the local debug or the {@link GlobalDebugValue} is set, and false otherwise.
     */
    @Override
    public boolean isDebug() {
        return localDebug || globalDebug.isDebug();
    }
}
