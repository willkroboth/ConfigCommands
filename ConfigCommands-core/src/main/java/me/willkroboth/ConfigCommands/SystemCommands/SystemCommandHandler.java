package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.willkroboth.ConfigCommands.ConfigCommands;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SystemCommandHandler {
    private static final List<SystemCommandHandler> commands = new ArrayList<>();

    protected static List<SystemCommandHandler> getCommands() {
        return commands;
    }

    public static void setUpCommands(ConfigCommands plugin) {
        commands.addAll(List.of(
                new HelpCommandHandler(),
                new FunctionCommandHandler(),
                new BuildCommandHandler(),
                new ReloadCommandHandler(),
                new DebugCommandHandler()
        ));

        ConfigCommandsHandler.logDebug("Creating command /configcommands");

        // register system commands
        CommandTree configcommands = new CommandTree("configcommands")
                .withRequirement(sender -> {
                    for (SystemCommandHandler command : commands) {
                        if (sender.hasPermission(command.getPermission())) return true;
                    }
                    return false;
                })
                .withHelp(
                        HelpCommandHandler.getShortDescription(),
                        HelpCommandHandler.getFullDescription()
                )
                .executes(HelpCommandHandler.getDefaultMessage());

        // add commands to tree and register events for commands
        ConfigCommandsHandler.increaseIndentation();
        PluginManager manager = plugin.getServer().getPluginManager();
        for (SystemCommandHandler command : commands) {
            ConfigCommandsHandler.logDebug("Adding branch %s", command.getName());
            ConfigCommandsHandler.increaseIndentation();

            configcommands.then(command.getArgumentTree());

            if (command instanceof Listener l) {
                ConfigCommandsHandler.logDebug("Registering events");
                manager.registerEvents(l, plugin);
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
        ConfigCommandsHandler.decreaseIndentation();

        configcommands.register();
    }

    // command configuration
    protected static String buildPermission(String branch) {
        return "configcommands.system." + branch;
    }

    // Assumes class name is formatted as [name]CommandHandler
    protected String getName() {
        String className = getClass().getSimpleName();
        return className.substring(0, className.indexOf("CommandHandler")).toLowerCase(Locale.ROOT);
    }

    protected ArgumentTree getArgumentTree() {
        return new LiteralArgument(getName()).withPermission(getPermission());
    }

    protected abstract String[] getHelpMessages();

    protected String getPermission() {
        return buildPermission(getName());
    }
}
