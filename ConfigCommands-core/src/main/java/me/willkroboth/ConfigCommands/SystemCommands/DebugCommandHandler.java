package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.HelperClasses.SharedDebugValue;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class DebugCommandHandler extends SystemCommandHandler {
    // command configuration
    protected ArgumentTree getArgumentTree() {
        return super.getArgumentTree()
                .executes(DebugCommandHandler::sendGlobalDebugMode)
                .then(new LiteralArgument("enable")
                        .executes(setGlobalDebug(true))
                ).then(new LiteralArgument("disable")
                        .executes(setGlobalDebug(false))
                ).then(new LiteralArgument("local")
                        .then(new MultiLiteralArgument(nameToSharedDebug.keySet().toArray(String[]::new))
                                .executes(DebugCommandHandler::sendLocalDebugMode)
                                .then(new LiteralArgument("enable")
                                        .executes(setLocalDebug(true))
                                ).then(new LiteralArgument("disable")
                                        .executes(setLocalDebug(false))
                                )
                        )
                );
    }

    private static final String[] helpMessages = new String[]{
            "Allows setting and viewing the values currently set for global and local debug",
            "Usage:",
            "\tSee value of global debug: /configcommands debug",
            "\tTurn on global debug: /configcommands debug enable",
            "\tSee echo command's local debug: /configcommands debug local echo",
            "\tSet echo command's local debug: /configcommands debug local echo disable"
    };

    protected String[] getHelpMessages() {
        return helpMessages;
    }

    // command functions

    private static final Map<String, SharedDebugValue> nameToSharedDebug = new HashMap<>();
    // accessed by CommandTreeBuilder
    public static void registerSharedDebug(String name, SharedDebugValue sharedDebug) {
        nameToSharedDebug.put(name, sharedDebug);
    }

    private static void sendGlobalDebugMode(CommandSender sender, Object[] ignored) {
        boolean debugMode = ConfigCommandsHandler.isDebugMode();
        sender.sendMessage("Global debug is currently " + (debugMode ? "enabled" : "disabled"));
    }

    private static CommandExecutor setGlobalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.getConfigFile().set("debug", value);
            ConfigCommandsHandler.saveConfigFile();

            sender.sendMessage("Global debug is currently " + (value ? "enabled" : "disabled"));
            ConfigCommandsHandler.setGlobalDebug(value);
        };
    }

    private static void sendLocalDebugMode(CommandSender sender, Object[] args) {
        String key = (String) args[0];
        boolean debugMode = nameToSharedDebug.get(key).isDebug();
        sender.sendMessage("Debug for \"" + key + "\" is currently " + (debugMode ? "enabled" : "disabled"));
    }

    private static CommandExecutor setLocalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.reloadConfigFile();
            String key = (String) args[0];

            ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
            if (commands == null || !commands.getKeys(false).contains(key))
                throw CommandAPI.failWithString("Command \"" + key + "\" dose not exist!");

            commands.getConfigurationSection(key).set("debug", value);
            ConfigCommandsHandler.saveConfigFile();

            sender.sendMessage("Debug for \"" + key + "\" is currently " + (value ? "enabled" : "disabled"));
            nameToSharedDebug.get(key).setLocalDebug(value);
        };
    }
}
