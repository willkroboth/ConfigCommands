package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommandHandler {
    private static final ArgumentTree argumentTree = new LiteralArgument("reload")
            .withPermission("configcommands.reload")
            .then(new StringArgument("command")
                    .replaceSuggestions(ArgumentSuggestions.strings(ReloadCommandHandler::getCommandNames))
                    .executes(ReloadCommandHandler::reloadCommand)
            );

    public static ArgumentTree getArgumentTree() {
        return argumentTree;
    }

    private static final String[] helpMessages = new String[]{
            "Reloads a command's code from the config.yml, allowing its behavior to change without restarting the server.",
            "Usage:",
            "\t/configcommands reload <command>"
    };

    public static String[] getHelpMessages() {
        return helpMessages;
    }

    private static final Map<String, ConfigCommandBuilder> commands = new HashMap<>();
    private static final Map<String, String> nameToKey = new HashMap<>();
    private static final Map<String, String> keyToName = new HashMap<>();

    public static void addCommand(ConfigCommandBuilder configCommandBuilder, String key) {
        commands.put(configCommandBuilder.getName(), configCommandBuilder);
        nameToKey.put(configCommandBuilder.getName(), key);
        keyToName.put(key, configCommandBuilder.getName());
    }

    public static void updateKey(String oldKey, String newKey){
        String name = keyToName.get(oldKey);
        nameToKey.put(name, newKey);
        keyToName.remove(oldKey);
        keyToName.put(newKey, name);
    }

    public static String[] getCommandNames(SuggestionInfo suggestionInfo) {
        return commands.keySet().toArray(new String[0]);
    }

    public static void reloadCommand(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
        String name = (String) args[0];
        if(!commands.containsKey(name)){
           throw CommandAPI.fail("Command: \"" + name + "\" was not created by ConfigCommands.");
        }
        ConfigCommandsHandler.reloadConfigFile();
        FileConfiguration config = ConfigCommandsHandler.getConfigFile();
        ConfigurationSection commandSection = config.getConfigurationSection("commands");

        if (commandSection == null || commandSection.getKeys(false).size() == 0) {
            throw CommandAPI.fail("No commands found in config.yml");
        }

        String key = nameToKey.get(name);
        ConfigurationSection command = commandSection.getConfigurationSection(key);
        if (command == null) {
            throw CommandAPI.fail("No data was found for the command");
        }

        List<String> commandsToRun = command.getStringList("commands");
        if (commandsToRun.size() == 0) {
            throw CommandAPI.fail(key + " has no commands. Skipping.");
        }

        try {
            commands.get(name).refreshExecutor(commandsToRun);
        } catch (RegistrationException e) {
            throw CommandAPI.fail("Could not apply new commands: " + e.getMessage());
        }

        sender.sendMessage("Command successfully updated!");
    }
}
