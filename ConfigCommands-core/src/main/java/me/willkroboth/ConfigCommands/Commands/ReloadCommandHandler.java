package me.willkroboth.ConfigCommands.Commands;

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

    public static String[] getCommandNames() {
        return commands.keySet().toArray(new String[0]);
    }

    public static int reloadCommand(CommandSender sender, Object[] args) {
        String name = (String) args[0];
        if(!commands.containsKey(name)){
            sender.sendMessage("Command: \"" + name + "\" was not created by ConfigCommands.");
            return 0;
        }
        ConfigCommandsHandler.reloadConfigFile();
        FileConfiguration config = ConfigCommandsHandler.getConfigFile();
        ConfigurationSection commandSection = config.getConfigurationSection("commands");

        if (commandSection == null || commandSection.getKeys(false).size() == 0) {
            sender.sendMessage("No commands found in config.yml");
            return 0;
        }

        String key = nameToKey.get(name);
        ConfigurationSection command = commandSection.getConfigurationSection(key);
        if (command == null) {
            sender.sendMessage("No data was found for the command");
            return 0;
        }

        List<String> commandsToRun = command.getStringList("commands");
        if (commandsToRun.size() == 0) {
            sender.sendMessage(key + " has no commands. Skipping.");
            return 0;
        }

        try {
            commands.get(name).refreshExecutor(commandsToRun);
        } catch (RegistrationException e) {
            sender.sendMessage("Could not apply new commands: " + e.getMessage());
            return 0;
        }

        sender.sendMessage("Command successfully updated!");
        return 1;
    }
}
