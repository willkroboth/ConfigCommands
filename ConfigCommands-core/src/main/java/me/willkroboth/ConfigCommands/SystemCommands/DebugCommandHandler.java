package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class DebugCommandHandler {
    private static final ArgumentTree argumentTree = new LiteralArgument("debug")
            .withPermission("configcommands.system.debug")
            .executes(DebugCommandHandler::sendGlobalDebugMode)
            .then(new LiteralArgument("enable")
                    .executes(DebugCommandHandler.setGlobalDebug(true))
            ).then(new LiteralArgument("disable")
                    .executes(DebugCommandHandler.setGlobalDebug(false))
            ).then(new LiteralArgument("local")
                    .then(new StringArgument("command")
                            .replaceSuggestions(ArgumentSuggestions.strings(DebugCommandHandler.getKeys()))
                            .executes(DebugCommandHandler::sendLocalDebugMode)
                            .then(new LiteralArgument("enable")
                                    .executes(DebugCommandHandler.setLocalDebug(true))
                            ).then(new LiteralArgument("disable")
                                    .executes(DebugCommandHandler.setLocalDebug(false))
                            )
                    )
            );

    public static ArgumentTree getArgumentTree() {
        return argumentTree;
    }

    private static final String[] helpMessages = new String[]{
            "Allows setting and viewing the values currently set for global and local debug",
            "Usage:",
            "\tSee value of global debug: /configcommands debug",
            "\tTurn on global debug: /configcommands debug enable",
            "\tSee echo command's local debug: /configcommands debug local echo",
            "\tSet echo command's local debug: /configcommands debug local echo disable"
    };

    public static String[] getHelpMessages() {
        return helpMessages;
    }

    public static void sendGlobalDebugMode(CommandSender sender, Object[] ignored) {
        ConfigCommandsHandler.reloadConfigFile();
        boolean debugMode = ConfigCommandsHandler.getConfigFile().getBoolean("debug", false);
        sender.sendMessage("Global debug is currently " + (debugMode ? "enabled" : "disabled"));
    }

    public static CommandExecutor setGlobalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.reloadConfigFile();
            ConfigCommandsHandler.getConfigFile().set("debug", value);
            ConfigCommandsHandler.saveConfigFile();

            sender.sendMessage("Global debug is currently " + (value ? "enabled" : "disabled"));
        };
    }

    public static String[] getKeys() {
        ConfigCommandsHandler.reloadConfigFile();
        ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
        if (commands == null || commands.getKeys(false).size() == 0) return new String[0];
        return commands.getKeys(false).toArray(new String[0]);
    }

    public static void sendLocalDebugMode(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
        String key = (String) args[0];

        ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
        if (commands == null || !commands.getKeys(false).contains(key))
            throw CommandAPI.fail("Command \"" + key + "\" dose not exist!");
        boolean debugMode = commands.getConfigurationSection(key).getBoolean("debug", false);

        sender.sendMessage("Debug for \"" + key + "\" is currently " + (debugMode ? "enabled" : "disabled"));
    }

    public static CommandExecutor setLocalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.reloadConfigFile();
            String key = (String) args[0];

            ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
            if (commands == null || !commands.getKeys(false).contains(key))
                throw CommandAPI.fail("Command \"" + key + "\" dose not exist!");

            commands.getConfigurationSection(key).set("debug", value);
            ConfigCommandsHandler.saveConfigFile();

            sender.sendMessage("Debug for \"" + key + "\" is currently " + (value ? "enabled" : "disabled"));
        };
    }
}
