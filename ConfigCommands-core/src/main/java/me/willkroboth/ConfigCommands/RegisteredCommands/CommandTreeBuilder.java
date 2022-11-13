package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalCommandSenderArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;
import me.willkroboth.ConfigCommands.SystemCommands.ReloadCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class CommandTreeBuilder extends CommandTree implements ReloadableExecutable {
    public static void registerCommandsFromConfig(ConfigurationSection commands, boolean globalDebug) {
        ConfigCommandsHandler.logNormal("");
        if (commands == null) {
            ConfigCommandsHandler.logNormal("The configuration section for the commands was not found! Skipping");
            return;
        }

        ConfigCommandsHandler.logNormal("Registering commands from %s", commands.getCurrentPath());
        if (commands.getKeys(false).size() == 0) {
            ConfigCommandsHandler.logNormal("No commands found! Skipping");
            return;
        }

        List<String> failedCommands = new ArrayList<>();
        for (String key : commands.getKeys(false)) {
            ConfigCommandsHandler.logNormal("");
            ConfigCommandsHandler.logNormal("Loading command %s", key);

            int indentation = ConfigCommandsHandler.getIndentation();
            try {
                // vital data needed for command to work
                ConfigurationSection command = commands.getConfigurationSection(key);
                if (command == null) {
                    ConfigCommandsHandler.logError("%s has no data. Skipping.", key);
                    throw new RegistrationException("No data found for key " + key);
                }

                new CommandTreeBuilder(key, command, globalDebug).register();
            } catch (RegistrationException e) {
                ConfigCommandsHandler.logError("Error occurred while creating argument: %s", e.getMessage());
                failedCommands.add(e.getMessage());
            } finally {
                ConfigCommandsHandler.setIndentation(indentation);
            }
        }

        ConfigCommandsHandler.logNormal("");
        // inform user of failed commands
        if (failedCommands.size() == 0) {
            ConfigCommandsHandler.logNormal("All commands were successfully registered.");
            ConfigCommandsHandler.logNormal("Note: this does not mean they will work as you expect.");
            if (globalDebug) {
                ConfigCommandsHandler.logNormal("If a command does not work, check the console output to try to find the problem.");
            } else {
                ConfigCommandsHandler.logNormal("If a command does not work, turn on debug mode, then check the console output to try to find the problem.");
            }
        } else {
            ConfigCommandsHandler.logNormal("%s command(s) failed while registering:", failedCommands.size());
            ConfigCommandsHandler.increaseIndentation();
            for (String message : failedCommands) {
                ConfigCommandsHandler.logError(message);
            }
            ConfigCommandsHandler.decreaseIndentation();
            if (globalDebug) {
                ConfigCommandsHandler.logNormal("Scroll up to find more information.");
            } else {
                ConfigCommandsHandler.logNormal("Turn on debug mode and scroll up to find more information.");
            }
        }
    }

    public static String buildDefaultPermission(String name) {
        return "configcommands." + name.toLowerCase(Locale.ROOT);
    }

    public static Map<String, Class<? extends InternalArgument>> getDefaultArgs() {
        return new LinkedHashMap<>(Map.of(
                "<sender>", InternalCommandSenderArgument.class,
                "<lineIndex>", InternalIntegerArgument.class
        ));
    }

    public CommandTreeBuilder(String name, ConfigurationSection command, boolean globalDebug) throws RegistrationException {
        //set name
        super(name);
        this.name = name;

        // set debug variable
        boolean localDebug = command.getBoolean("debug", false);
        ConfigCommandsHandler.logDebug(localDebug && !globalDebug, "Debug turned on for %s", name);
        localDebug = globalDebug || localDebug;
        this.localDebug = localDebug;

        // set help
        String shortDescription = command.getString("shortDescription");
        if (shortDescription != null) {
            super.withShortDescription(shortDescription);
        } else {
            ConfigCommandsHandler.logWarning("%s has no shortDescription. It will default to \"A Mojang provided command\".", name);
        }
        ConfigCommandsHandler.logDebug(localDebug, "%s has shortDescription %s", name, shortDescription);

        String fullDescription = command.getString("fullDescription");
        if (fullDescription != null) {
            super.withFullDescription(fullDescription);
        } else {
            ConfigCommandsHandler.logWarning("%s has no fullDescription. It will default to \"A Mojang provided command\".", name);
        }
        ConfigCommandsHandler.logDebug(localDebug, "%s has fullDescription %s", name, fullDescription);

        // set permission
        String permission = command.getString("permission");
        if (permission == null) {
            permission = buildDefaultPermission(name);
            ConfigCommandsHandler.logWarning("%s has no permission. It will default to \"%s\".", name, permission);
        }
        super.withPermission(permission);
        ConfigCommandsHandler.logDebug(localDebug, "%s has permission \"%s\"", name, permission);

        // set aliases
        List<String> aliases = command.getStringList("aliases");
        ConfigCommandsHandler.logDebug(localDebug, "%s has %s alias(es): %s", name, aliases.size(), aliases);
        super.withAliases(aliases.toArray(new String[0]));

        ConfigCommandsHandler.logNormal("Building CommandTree...");

        argumentClasses = getDefaultArgs();
        if (localDebug) {
            ConfigCommandsHandler.logNormal("Default arguments available:");
            ConfigCommandsHandler.increaseIndentation();
            for (Map.Entry<String, Class<? extends InternalArgument>> arg : argumentClasses.entrySet()) {
                ConfigCommandsHandler.logNormal("%s: %s", arg.getKey(), arg.getValue().getSimpleName());
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
        List<String> argumentPath = new ArrayList<>(List.of(name));

        // set executes
        List<String> executes = command.getStringList("executes");
        if (executes.size() != 0) {
            ConfigCommandsHandler.logDebug(localDebug, "Adding executes");
            ConfigCommandsHandler.increaseIndentation();
            executor = new ExecutesBuilder(executes, argumentClasses, localDebug);
            super.executes(this::execute);
            ReloadCommandHandler.addCommand(argumentPath, this);
            ConfigCommandsHandler.decreaseIndentation();
        } else {
            ConfigCommandsHandler.logDebug(localDebug, "Not executable at this stage");
        }

        // set then
        ConfigurationSection then = command.getConfigurationSection("then");
        if (then == null || then.getKeys(false).size() == 0) {
            ConfigCommandsHandler.logDebug(localDebug, "No branches");
        } else {

            ConfigCommandsHandler.logDebug(localDebug, "Adding branches");
            ConfigCommandsHandler.increaseIndentation();
            for (String branchName : then.getKeys(false)) {
                ConfigCommandsHandler.logDebug(localDebug, "Adding branch %s", branchName);
                ConfigCommandsHandler.increaseIndentation();
                super.then(new ArgumentTreeBuilder(branchName, new LinkedHashMap<>(argumentClasses), then.getConfigurationSection(branchName), localDebug, argumentPath));
                ConfigCommandsHandler.decreaseIndentation();
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
    }

    private ExecutesBuilder executor;

    // Stored information for reloading executor
    private final String name;
    private final Map<String, Class<? extends InternalArgument>> argumentClasses;
    private final boolean localDebug;

    private void execute(CommandSender sender, Object[] args) {
        if (executor != null) executor.run(sender, args);
    }

    @Override
    public void reloadExecution(CommandSender sender) throws WrapperCommandSyntaxException {
        ConfigCommandsHandler.reloadConfigFile();
        FileConfiguration config = ConfigCommandsHandler.getConfigFile();
        ConfigurationSection commandSection = config.getConfigurationSection("commands");

        if (commandSection == null || commandSection.getKeys(false).size() == 0)
            throw CommandAPI.fail("No commands found in config.yml");

        ConfigurationSection command = commandSection.getConfigurationSection(name);
        if (command == null)
            throw CommandAPI.fail("No data was found for the command (Did you change it's name?)");

        List<String> executes = command.getStringList("executes");
        if (executes.size() == 0) {
            sender.sendMessage("No executes found, disabling command");
            executor = null;
            return;
        }

        try {
            executor = new ExecutesBuilder(executes, argumentClasses, localDebug);
        } catch (RegistrationException e) {
            throw CommandAPI.fail("Could not apply new commands: " + e.getMessage());
        }

        sender.sendMessage("Command successfully updated!");
    }
}
