package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;

import java.util.Arrays;

public class HelpCommandHandler {
    private static final String[] helpMessages = new String[]{
            "Gives information for the different ConfigCommands sections",
            "Usage:",
            "\t/configcommands help <section>"
    };

    public static String[] getHelpMessages(){
        return helpMessages;
    }
    private static final ArgumentTree argumentTree =
            helpBranch("help", HelpCommandHandler.getHelpMessages()).withPermission("configcommands.help")
            .then(helpBranch("build", BuildCommandHandler.getHelpMessages()))
            .then(helpBranch("debug", DebugCommandHandler.getHelpMessages()))
            .then(helpBranch("functions", FunctionCommandHandler.getHelpMessages()))
            .then(helpBranch("reload", ReloadCommandHandler.getHelpMessages()));

    private static LiteralArgument helpBranch(String name, String... messages) {
        return (LiteralArgument) new LiteralArgument(name).executes(sendMessages(messages));
    }

    private static CommandExecutor sendMessages(String... messages) {
        return (sender, args) -> Arrays.stream(messages).forEach(sender::sendMessage);
    }

    public static ArgumentTree getArgumentTree() {
        return argumentTree;
    }

    public static String getShortDescription() {
        return "A command for interacting with the ConfigCommands system";
    }

    public static String getFullDescription() {
        return "Different systems are accessed using their keywords. \n" +
                "For help with a specific section, use /configcommands help <section>";
    }

    public static CommandExecutor getDefaultMessage() {
        return sendMessages(
                "A command for interacting with the ConfigCommands system",
                "For help using this command for a specific section, use /configcommands help <section>"
        );
    }
}
