package me.willkroboth.configcommands.systemcommands;

import org.bukkit.command.CommandSender;

/**
 * A record that stores information about a step in a guided command menu,
 * such as {@code /configcommands build} and {@code /configcommands functions}
 *
 * @param previousContext The CommandContext that came before this one. May be null if this is the first step.
 * @param previousChoice  Any object that was "chosen" in the previous step.
 * @param nextStep        The {@link CommandStep} to run next
 */
public record CommandContext(CommandContext previousContext, Object previousChoice, CommandStep nextStep) {
    /**
     * Performs the recorded {@link CommandContext#nextStep} with the given {@link CommandSender} and message.
     *
     * @param sender  The {@link CommandSender} performing this step
     * @param message The message to process
     */
    public void doNextStep(CommandSender sender, String message) {
        nextStep.perform(sender, message, this);
    }
}
