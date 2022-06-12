package me.willkroboth.ConfigCommands.HelperClasses.GuidedCommands;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandStep {
    void perform(CommandSender sender, String message, CommandContext context);
}
