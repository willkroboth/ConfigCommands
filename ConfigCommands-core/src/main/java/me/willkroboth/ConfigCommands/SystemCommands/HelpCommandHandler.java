package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;

/**
 * A class that handles the {@code /configcommands help} command
 */
public class HelpCommandHandler extends SystemCommandHandler {
    // command configuration
    @Override
    protected ArgumentTree getArgumentTree() {
        ConfigCommandsHandler.logDebug("Setting up help topics");
        ConfigCommandsHandler.increaseIndentation();
        ArgumentTree base = super.getArgumentTree();

        ConfigCommandsHandler.logDebug("Adding help for branch \"help\"");
        base.executes(sendMessages(getHelpMessages()));

        for (SystemCommandHandler command : getCommands()) {
            if (command != this) {
                ConfigCommandsHandler.logDebug("Adding help for branch \"%s\"", command.getName());
                base.then(helpBranch(command.getName(), command.getHelpMessages()));
            }
        }
        ConfigCommandsHandler.decreaseIndentation();
        return base;
    }

    private static LiteralArgument helpBranch(String name, String... messages) {
        return (LiteralArgument) new LiteralArgument(name).executes(sendMessages(messages));
    }

    private static CommandExecutor sendMessages(String... messages) {
        return (sender, args) -> sender.sendMessage(messages);
    }

    private static final String[] helpMessages = new String[]{
            "Gives information for the different ConfigCommands sections",
            "Usage:",
            "\t/configcommands help <section>"
    };

    @Override
    protected String[] getHelpMessages() {
        return helpMessages;
    }

    // help messages for the master command

    /**
     * @return The short description for the master {@code /configcommands} command.
     */
    protected static String getShortDescription() {
        return "A command for interacting with the ConfigCommands system";
    }

    /**
     * @return The full description for the master {@code /configcommands} command.
     */
    protected static String getFullDescription() {
        return "Different systems are accessed using their keywords. \n" +
                "For help with a specific section, use /configcommands help <section>";
    }

    /**
     * @return The default message that gets sent when just {@code /configcommands} is executed.
     */
    protected static CommandExecutor getDefaultMessage() {
        return sendMessages(
                "A command for interacting with the ConfigCommands system",
                "For help using this command for a specific section, use /configcommands help <section>"
        );
    }
}
