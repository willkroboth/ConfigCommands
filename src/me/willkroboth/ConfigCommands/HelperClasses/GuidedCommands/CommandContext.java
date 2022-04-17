package me.willkroboth.ConfigCommands.HelperClasses.GuidedCommands;

import org.bukkit.command.CommandSender;

public class CommandContext {
    private final CommandContext previousContext;
    private final Object previousChoice;
    private final CommandStep nextStep;

    public CommandContext(CommandContext previousContext, Object previousChoice, CommandStep nextStep){
        this.previousContext = previousContext;
        this.previousChoice = previousChoice;
        this.nextStep = nextStep;
    }

    public CommandContext getPreviousContext(){
        return previousContext;
    }

    public void doNextStep(CommandSender sender, String message){
        nextStep.perform(sender, message, this);
    }

    public Object getPreviousChoice() {
        return previousChoice;
    }
}
