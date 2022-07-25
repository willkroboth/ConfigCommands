package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;

public class HelpCommandHandler {
    private static final ArgumentTree argumentTree = new LiteralArgument("help")
            .withPermission("configcommands.help")
            .executes(sendMessage("Gives help information for the different ConfigCommands sections. To get help with a specific system, use /configcommands help [keyword]"))
            .then(new LiteralArgument("build")
                    .executes(sendMessage("Opens a menu that guides users through creating a new command. Enables creating, editing, and deleting commands in-game."))
            )
            .then(new LiteralArgument("debug")
                    .executes(sendMessage("Allows setting and viewing the values currently set for global and local debugs"))
            )
            .then(new LiteralArgument("functions")
                    .executes(sendMessage("Displays information about the available ConfigCommands functions. Using just /configcommands help brings up a guided menu that narrows in on the function you need help with. You can also use tab-complete suggestions to explore the functions the same way."))
            )
            .then(new LiteralArgument("reload")
                    .executes(sendMessage("Reloads a command's code from the config.yml, allowing its behavior to change without restarting the server."))
            );

    public static ArgumentTree getArgumentTree() {
        return argumentTree;
    }

    private static CommandExecutor sendMessage(String message) {
        return (sender, args) -> sender.sendMessage(message);
    }
}
