package me.willkroboth.ConfigCommands.HelperClasses;

public abstract class IndentedStringHandler {
    protected int indentation = 0;
    protected String indentationString;

    public IndentedStringHandler(String indentationString) {
        this.indentationString = indentationString;
    }

    public void setIndentation(int i) {
        indentation = i;
    }

    public int getIndentation() {
        return indentation;
    }

    public void increaseIndentation() {
        indentation++;
    }

    public void decreaseIndentation() {
        indentation--;
    }

    protected String formatMessage(String message, Object... args) {
        return indentationString.repeat(indentation) + String.format(message, args);
    }
}
