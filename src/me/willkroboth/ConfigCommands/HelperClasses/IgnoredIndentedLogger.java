package me.willkroboth.ConfigCommands.HelperClasses;

import java.util.logging.Logger;

public class IgnoredIndentedLogger extends IndentedLogger {
    public IgnoredIndentedLogger() {
        super(null);
    }

    public void setIndentation(int i) {
        super.setIndentation(i);
    }

    public int getIndentation() {
        return super.getIndentation();
    }

    public void increaseIndentation() {
        super.increaseIndentation();
    }

    public void decreaseIndentation() {
        super.decreaseIndentation();
    }

    public void info(String message) {
    }

    public void warn(boolean red, String message) {
    }

    public Logger getLogger() {
        return null;
    }
}
