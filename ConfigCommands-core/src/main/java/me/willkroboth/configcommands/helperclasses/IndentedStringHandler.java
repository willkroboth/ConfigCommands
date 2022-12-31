package me.willkroboth.configcommands.helperclasses;

/**
 * A class that handles indenting and formatting a String.
 */
public abstract class IndentedStringHandler {
    /**
     * The current indentation level.
     */
    protected int indentation = 0;
    /**
     * The String used as indentation.
     */
    protected String indentationString;

    /**
     * Sets up a new {@link IndentedStringHandler} that uses the given string as its indentation.
     *
     * @param indentationString The String that gets repeated to represent indentation.
     */
    public IndentedStringHandler(String indentationString) {
        this.indentationString = indentationString;
    }

    /**
     * @param i The new value for the indentation.
     */
    public void setIndentation(int i) {
        indentation = i;
    }

    /**
     * @return The current indentation level.
     */
    public int getIndentation() {
        return indentation;
    }

    /**
     * Increases the current indentation level by one.
     */
    public void increaseIndentation() {
        indentation++;
    }

    /**
     * Decreases the current indentation level by one.
     */
    public void decreaseIndentation() {
        indentation--;
    }

    /**
     * Formats a message using the given Objects array and applies the indentation level.
     *
     * @param message The message to format.
     * @param args    A variadic array of Objects to insert into the message using {@link String#format(String, Object...)}.
     * @return The formatted string, starting with {@link IndentedStringHandler#indentation} instances of the
     * {@link IndentedStringHandler#indentationString}, then the message formatted with the given objects.
     */
    protected String formatMessage(String message, Object... args) {
        return indentationString.repeat(indentation) + String.format(message, args);
    }
}
