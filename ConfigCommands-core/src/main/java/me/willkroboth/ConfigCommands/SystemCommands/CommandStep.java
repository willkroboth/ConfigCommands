package me.willkroboth.ConfigCommands.SystemCommands;

import org.bukkit.command.CommandSender;

/**
 * An {@link FunctionalInterface} that represents a step in a guided command
 * menu, such as {@code /configcommands build} or {@code /configcommand functions}.
 * See {@link CommandStep#perform(CommandSender, String, CommandContext)}.
 */
@FunctionalInterface
public interface CommandStep {
    /**
     * Performs this CommandStep
     *
     * @param sender The {@link CommandSender} who triggered this CommandStep
     * @param message The message sent to be handled
     * @param context The {@link CommandContext} for this step
     */
    void perform(CommandSender sender, String message, CommandContext context);
}
