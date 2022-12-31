package me.willkroboth.configcommands.systemcommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.ExecutorType;
import me.willkroboth.configcommands.ConfigCommands;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A class that handles registering all the subcommands for {@code /configcommands}.
 * Subclasses of this class handle defining those subcommands.
 */
public abstract class SystemCommandHandler {
    private static final List<SystemCommandHandler> commands = new ArrayList<>();

    /**
     * @return A List of the active SystemCommandHandler subclasses
     */
    protected static List<SystemCommandHandler> getCommands() {
        return commands;
    }

    /**
     * Builds and registers the full {@code /configcommands} command. This
     * includes registering any {@link EventHandler} methods a subcommand may define.
     *
     * @param plugin The {@link ConfigCommands} plugin instance, used to register events for commands.
     */
    public static void setUpCommands(ConfigCommands plugin) {
        commands.addAll(List.of(
                new HelpCommandHandler(),
                new FunctionsCommandHandler(),
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

    /**
     * Builds the default permission node for a subcommand.
     *
     * @param branch The name of the branch
     * @return The default permission node for this branch, {@code configcommands.system.[branch]}
     */
    protected static String buildPermission(String branch) {
        return "configcommands.system." + branch;
    }

    // Assumes class name is formatted as [name]CommandHandler

    /**
     * Gets the name of this command. The default implementation of this method
     * assumes the class is called {@code [name]CommandHandler}.
     *
     * @return The name of this command
     */
    protected String getName() {
        String className = getClass().getSimpleName();
        return className.substring(0, className.indexOf("CommandHandler")).toLowerCase(Locale.ROOT);
    }

    /**
     * Creates a CommandAPI {@link ArgumentTree} for this command. The default
     * tree returned by this method can be built upon using
     * {@link ArgumentTree#then(ArgumentTree)} and {@link ArgumentTree#executes(CommandExecutor, ExecutorType...)}.
     *
     * @return The CommandAPI {@link ArgumentTree} for this subcommand.
     * The tree returned by the default implementation is defined as
     * {@code new LiteralArgument(getName()).withPermission(getPermission())}.
     */
    protected ArgumentTree getArgumentTree() {
        return new LiteralArgument(getName()).withPermission(getPermission());
    }

    /**
     * @return The help messages to display when using {@code configcommands help} for this command.
     */
    protected abstract String[] getHelpMessages();

    /**
     * @return The permission node used by this command.
     */
    protected String getPermission() {
        return buildPermission(getName());
    }
}
