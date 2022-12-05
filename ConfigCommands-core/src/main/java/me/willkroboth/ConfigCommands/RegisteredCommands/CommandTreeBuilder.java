package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.CommandTree;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.HelperClasses.GlobalDebugValue;
import me.willkroboth.ConfigCommands.HelperClasses.SharedDebugValue;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalCommandSenderArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;
import me.willkroboth.ConfigCommands.SystemCommands.DebugCommandHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class CommandTreeBuilder extends CommandTree {
    public static void registerCommandsFromConfig(ConfigurationSection commands, GlobalDebugValue globalDebug) {
        ConfigCommandsHandler.logNormal("");
        if (commands == null) {
            ConfigCommandsHandler.logWarning("The configuration section for the commands was not found! Skipping");
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
            if (globalDebug.isDebug()) {
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
            if (globalDebug.isDebug()) {
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

    public CommandTreeBuilder(String name, ConfigurationSection command, GlobalDebugValue globalDebug) throws RegistrationException {
        //set name
        super(name);

        // set debug variable
        boolean localDebug = command.getBoolean("debug", false);
        ConfigCommandsHandler.logDebug(localDebug && !globalDebug.isDebug(), "Debug turned on for %s", name);
        SharedDebugValue sharedDebug = new SharedDebugValue(globalDebug, localDebug);
        DebugCommandHandler.registerSharedDebug(name, sharedDebug);

        // set help
        String shortDescription = command.getString("shortDescription");
        if (shortDescription != null) {
            super.withShortDescription(shortDescription);
        } else {
            ConfigCommandsHandler.logWarning("%s has no shortDescription. It will default to \"A Mojang provided command\".", name);
        }
        ConfigCommandsHandler.logDebug(sharedDebug, "%s has shortDescription %s", name, shortDescription);

        String fullDescription = command.getString("fullDescription");
        if (fullDescription != null) {
            super.withFullDescription(fullDescription);
        } else {
            ConfigCommandsHandler.logWarning("%s has no fullDescription. It will default to \"A Mojang provided command\".", name);
        }
        ConfigCommandsHandler.logDebug(sharedDebug, "%s has fullDescription %s", name, fullDescription);

        // set permission
        String permission = command.getString("permission");
        if (permission == null) {
            permission = buildDefaultPermission(name);
            ConfigCommandsHandler.logWarning("%s has no permission. It will default to \"%s\".", name, permission);
        }
        super.withPermission(permission);
        ConfigCommandsHandler.logDebug(sharedDebug, "%s has permission \"%s\"", name, permission);

        // set aliases
        List<String> aliases = command.getStringList("aliases");
        ConfigCommandsHandler.logDebug(sharedDebug, "%s has %s alias(es): %s", name, aliases.size(), aliases);
        super.withAliases(aliases.toArray(new String[0]));

        ConfigCommandsHandler.logNormal("Building CommandTree...");

        Map<String, Class<? extends InternalArgument>> argumentClasses = getDefaultArgs();
        if (sharedDebug.isDebug()) {
            ConfigCommandsHandler.logNormal("Default arguments available:");
            ConfigCommandsHandler.increaseIndentation();
            for (Map.Entry<String, Class<? extends InternalArgument>> arg : argumentClasses.entrySet()) {
                ConfigCommandsHandler.logNormal("%s: %s", arg.getKey(), arg.getValue().getSimpleName());
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
        List<String> argumentPath = new ArrayList<>(List.of(name));

        // set executor
        super.setExecutor(new CommandAPIExecutorBuilder(command, argumentPath, argumentClasses, sharedDebug));

        // set then
        ConfigurationSection then = command.getConfigurationSection("then");
        if (then == null || then.getKeys(false).size() == 0) {
            ConfigCommandsHandler.logDebug(sharedDebug, "No branches");
        } else {

            ConfigCommandsHandler.logDebug(sharedDebug, "Adding branches");
            ConfigCommandsHandler.increaseIndentation();
            for (String branchName : then.getKeys(false)) {
                ConfigCommandsHandler.logDebug(sharedDebug, "Adding branch %s", branchName);
                ConfigCommandsHandler.increaseIndentation();
                super.then(new ArgumentTreeBuilder(branchName, new LinkedHashMap<>(argumentClasses), then.getConfigurationSection(branchName), sharedDebug, argumentPath));
                ConfigCommandsHandler.decreaseIndentation();
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
    }
}
