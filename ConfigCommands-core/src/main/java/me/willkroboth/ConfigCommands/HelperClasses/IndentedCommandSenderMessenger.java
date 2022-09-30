package me.willkroboth.ConfigCommands.HelperClasses;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class IndentedCommandSenderMessenger extends IndentedStringHandler {
    private final CommandSender sender;

    public IndentedCommandSenderMessenger(CommandSender sender) {
        super("  ");
        this.sender = sender;
    }

    public void sendMessage(String message) {
        sender.sendMessage(formatMessage(message));
    }

    public void sendMessage(String... messages) {
        Arrays.stream(messages).map(this::formatMessage).forEach(sender::sendMessage);
    }

    public CommandSender getSender() {
        return sender;
    }
}
