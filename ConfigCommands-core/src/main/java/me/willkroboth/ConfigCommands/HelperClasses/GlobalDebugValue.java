package me.willkroboth.ConfigCommands.HelperClasses;

public class GlobalDebugValue implements DebuggableState {
    private boolean debug;

    public GlobalDebugValue(boolean debug) {
        this.debug = debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }
}
