package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

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
                        .then(new StringArgument("command")
                                .replaceSuggestions(ArgumentSuggestions.strings(DebugCommandHandler::getKeys))
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
    private static void sendGlobalDebugMode(CommandSender sender, Object[] ignored) {
        ConfigCommandsHandler.reloadConfigFile();
        boolean debugMode = ConfigCommandsHandler.getConfigFile().getBoolean("debug", false);
        sender.sendMessage("Global debug is currently " + (debugMode ? "enabled" : "disabled"));
    }

    private static CommandExecutor setGlobalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.reloadConfigFile();
            ConfigCommandsHandler.getConfigFile().set("debug", value);
            ConfigCommandsHandler.saveConfigFile();

            sender.sendMessage("Global debug is currently " + (value ? "enabled" : "disabled"));
        };
    }

    private static String[] getKeys(SuggestionInfo info) {
        ConfigCommandsHandler.reloadConfigFile();
        ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
        if (commands == null || commands.getKeys(false).size() == 0) return new String[0];
        return commands.getKeys(false).toArray(new String[0]);
    }

    private static void sendLocalDebugMode(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
        String key = (String) args[0];

        ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
        if (commands == null || !commands.getKeys(false).contains(key))
            throw CommandAPI.failWithString("Command \"" + key + "\" dose not exist!");
        boolean debugMode = commands.getConfigurationSection(key).getBoolean("debug", false);

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
        };
    }
}
