package me.willkroboth.ConfigCommands.Commands;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandStep {
    void perform(CommandSender sender, String message, CommandContext context);
}
