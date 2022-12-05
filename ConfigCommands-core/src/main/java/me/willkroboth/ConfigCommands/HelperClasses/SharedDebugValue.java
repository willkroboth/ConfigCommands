package me.willkroboth.ConfigCommands.HelperClasses;

public class SharedDebugValue implements DebuggableState {
    private final GlobalDebugValue globalDebug;
    private boolean localDebug;

    public SharedDebugValue(GlobalDebugValue globalDebug, boolean localDebug) {
        this.globalDebug = globalDebug;
        this.localDebug = localDebug;
    }

    public void setLocalDebug(boolean localDebug) {
        this.localDebug = localDebug;
    }

    @Override
    public boolean isDebug() {
        return localDebug || globalDebug.isDebug();
    }
}
