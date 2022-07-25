package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.CommandTree;
import me.willkroboth.ConfigCommands.ConfigCommands;
import org.bukkit.plugin.PluginManager;

public class SystemCommandHandler {
    public static void setUpCommands(ConfigCommands plugin) {
        // register main commands
        new CommandTree("configcommands")
                .withHelp(
                        HelpCommandHandler.getShortDescription(),
                        HelpCommandHandler.getFullDescription()
                )
                .executes(HelpCommandHandler.getDefaultMessage())
                // help command
                .then(HelpCommandHandler.getArgumentTree())
                // functions command
                .then(FunctionCommandHandler.getArgumentTree())
                // build command
                .then(BuildCommandHandler.getArgumentTree())
                // debug command
                .then(DebugCommandHandler.getArgumentTree())
                // reload command
                .then(ReloadCommandHandler.getArgumentTree())
                .register();

        // register events
        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new FunctionCommandHandler(), plugin);
        manager.registerEvents(new BuildCommandHandler(), plugin);
    }
}
