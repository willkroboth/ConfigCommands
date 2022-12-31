package me.willkroboth.configcommands.helperclasses;

import org.bukkit.command.CommandSender;

/**
 * A class that sends indented messages to a {@link CommandSender}.
 * This class extends {@link IndentedStringHandler} with {@code "  "} (two spaces)
 * as its {@link IndentedStringHandler#indentationString}.
 */
public class IndentedCommandSenderMessenger extends IndentedStringHandler {
    private final CommandSender sender;

    /**
     * Creates a new {@link IndentedCommandSenderMessenger} to send indented messages to the given {@link CommandSender}.
     *
     * @param sender The {@link CommandSender} that this {@link IndentedCommandSenderMessenger} should send messages to.
     */
    public IndentedCommandSenderMessenger(CommandSender sender) {
        super("  ");
        this.sender = sender;
    }

    /**
     * Sends one or multiple messages to the {@link CommandSender}, indenting each message by the set {@link IndentedStringHandler#indentation}.
     *
     * @param messages A variadic array of Strings to sen to the {@link CommandSender}.
     */
    public void sendMessage(String... messages) {
        for (String message : messages) {
            sender.sendMessage(formatMessage(message));
        }
    }

    /**
     * @return The {@link CommandSender} this {@link IndentedCommandSenderMessenger} is sending messages to.
     */
    public CommandSender getSender() {
        return sender;
    }
}
