package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class DebugCommandHandler {
    public static void sendGlobalDebugMode(CommandSender sender, Object[] ignored) {
        ConfigCommandsHandler.reloadConfigFile();
        boolean debugMode = ConfigCommandsHandler.getConfigFile().getBoolean("debugMode", false);
        sender.sendMessage("Global debug is currently " + (debugMode ? "enabled" : "disabled"));
    }

    public static CommandExecutor setGlobalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.reloadConfigFile();
            ConfigCommandsHandler.getConfigFile().set("debugMode", value);
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
        if (commands == null || !commands.getKeys(false).contains(key)) throw CommandAPI.fail("Command \"" + key + "\" dose not exist!");
        boolean debugMode = commands.getConfigurationSection(key).getBoolean("debug", false);

        sender.sendMessage("Debug for \"" + key + "\" is currently " + (debugMode ? "enabled" : "disabled"));
    }

    public static CommandExecutor setLocalDebug(boolean value) {
        return (sender, args) -> {
            ConfigCommandsHandler.reloadConfigFile();
            String key = (String) args[0];

            ConfigurationSection commands = ConfigCommandsHandler.getConfigFile().getConfigurationSection("commands");
            if (commands == null || !commands.getKeys(false).contains(key)) throw CommandAPI.fail("Command \"" + key + "\" dose not exist!");

            commands.getConfigurationSection(key).set("debugMode", value);
            sender.sendMessage("Debug for \"" + key + "\" is currently " + (value ? "enabled" : "disabled"));
        };
    }
}
