package me.willkroboth.ConfigCommands;

import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandBuilder;
import me.willkroboth.ConfigCommands.HelperClasses.Expression;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.VersionHandler;
import me.willkroboth.ConfigCommands.SystemCommands.ReloadCommandHandler;
import me.willkroboth.ConfigCommands.SystemCommands.SystemCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigCommandsHandler {
    // plugin instance
    private static ConfigCommands plugin;

    // debug mode
    private static boolean debugMode;

    public static boolean isDebugMode() {
        return debugMode;
    }

    // Add on management
    private static final List<ConfigCommandAddOn> addOns = new ArrayList<>();

    public static void registerAddOn(ConfigCommandAddOn addOn) {
        addOns.add(addOn);
    }

    public static List<ConfigCommandAddOn> getAddOns() {
        return addOns;
    }

    public static ConfigCommandAddOn getAddOn(String name) {
        Plugin plugin = ConfigCommandsHandler.plugin.getServer().getPluginManager().getPlugin(name);
        if (plugin instanceof ConfigCommandAddOn addOn) return addOn;
        return null;
    }

    // Config file
    public static FileConfiguration getConfigFile() {
        return plugin.getConfig();
    }

    public static void saveConfigFile() {
        plugin.saveConfig();
    }

    public static void reloadConfigFile() {
        plugin.reloadConfig();
    }

    // NMS
    private static NMS nms;

    public static NMS getNMS() {
        return nms;
    }

    // logging
    private static IndentedLogger logger;

    public static void increaseIndentation() {
        logger.increaseIndentation();
    }

    public static void decreaseIndentation() {
        logger.decreaseIndentation();
    }

    public static int getIndentation() {
        return logger.getIndentation();
    }

    public static void setIndentation(int indentation) {
        logger.setIndentation(indentation);
    }

    public static void logNormal(String message, Object... objects) {
        logger.info(message, objects);
    }

    public static void logDebug(String message, Object... objects) {
        logger.logDebug(debugMode, message, objects);
    }

    public static void logDebug(boolean debugMode, String message, Object... objects) {
        logger.logDebug(debugMode, message, objects);
    }

    public static void logWarning(String message, Object... objects) {
        logger.warn(message, objects);
    }

    public static void logError(String message, Object... objects) {
        logger.error(message, objects);
    }

    // Enable tasks
    public static void enable(ConfigCommands plugin) {
        ConfigCommandsHandler.plugin = plugin;

        setVariables();

        setupInternalArguments();

        registerCommandsFromConfig();

        SystemCommandHandler.setUpCommands(plugin);

        logNormal("Done!");
    }

    private static void setVariables() {
        String bukkit = Bukkit.getServer().toString();
        String version = bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1);
        nms = VersionHandler.getVersion(version);

        logger = new IndentedLogger(plugin.getLogger());

        plugin.saveDefaultConfig();
        debugMode = getConfigFile().getBoolean("debug", false);
        logDebug("Debug mode on! More information will be shown.");
    }

    private static void setupInternalArguments() {
        // register InternalArguments from addOns
        for (ConfigCommandAddOn addOn : addOns) {
            logNormal("Enabling addOn %s", addOn);
            addOn.registerInternalArguments();
        }

        // display registrations
        if (debugMode) {
            logNormal(
                    "All recognized InternalArguments:\n\t%s",
                    AllInternalArguments.getFlat().toString().replace(", ", ",\n\t")
            );
            logNormal(
                    "Static class map:\n\t%s",
                    Expression.getClassMap().toString().replace(", ", ",\n\t")
            );
        }

        // create function maps
        InternalArgument.createFunctionMaps();
    }

    private static void registerCommandsFromConfig() {
        // register commands
        ConfigurationSection commands = getConfigFile().getConfigurationSection("commands");

        if (commands == null || commands.getKeys(false).size() == 0) {
            logNormal("No commands given. Skipping");
        } else {
            List<String> failedCommands = new ArrayList<>();

            for (String key : commands.getKeys(false)) {
                logNormal("Loading command %s", key);

                // vital data needed for command to work
                ConfigurationSection command = commands.getConfigurationSection(key);
                if (command == null) {
                    logError("%s has no data. Skipping.", key);
                    failedCommands.add("(key) " + key + ": No data found!");
                    continue;
                }

                boolean localDebug = command.getBoolean("debug", false);
                logDebug(localDebug && !debugMode, "Debug turned on for %s", key);
                localDebug = debugMode || localDebug;

                String name = (String) command.get("name");
                if (name == null) {
                    logError("%s has no command name. Skipping.", key);
                    failedCommands.add("(key) " + key + ": No name found!");
                    continue;
                }

                logDebug(localDebug, "%s has name %s", key, name);

                List<String> commandsToRun = command.getStringList("commands");
                if (commandsToRun.size() == 0) {
                    logError("%s has no commands. Skipping.", key);
                    failedCommands.add("(name) " + name + ": No commands found!");
                    continue;
                }

                logDebug(localDebug, "%s has %s command(s): %s", key, commandsToRun.size(), commandsToRun);

                // less important, but will warn user if they don't exist
                String shortDescription = command.getString("shortDescription");
                if (shortDescription == null) logWarning("%s has no shortDescription.", key);
                logDebug(localDebug, "%s has shortDescription: %s", key, shortDescription);

                String fullDescription = command.getString("fullDescription");
                if (fullDescription == null) logWarning("%s has no fullDescription.", key);
                logDebug(localDebug, "%s has fullDescription: %s", key, fullDescription);

                String permission = command.getString("permission");
                if (permission == null) {
                    permission = ConfigCommandBuilder.getDefaultPermission(name);
                    logWarning("%s has no permission. Using \"%s\".", key, permission);
                }
                logDebug(localDebug, "%s has permission %s", key, permission);

                // Don't need to warn user about these
                List<Map<?, ?>> args = command.getMapList("args");
                logDebug(localDebug, key + " has args: " + args);

                List<String> aliases = command.getStringList("aliases");
                logDebug(localDebug, "%s has %s alias(es): %s", key, aliases.size(), aliases);

                // register command
                logNormal("Loading %s with name: %s", key, name);
                increaseIndentation();
                try {
                    ReloadCommandHandler.addCommand(
                            new ConfigCommandBuilder(
                                    name, shortDescription, fullDescription, args,
                                    aliases, permission, commandsToRun, localDebug
                            ),
                            key
                    );
                } catch (RegistrationException e) {
                    logError("Registration error: \"%s\" Skipping registration", e.getMessage());
                    failedCommands.add("(name) " + name + ": Registration error: \"" + e.getMessage() + "\"");
                }
                setIndentation(0);
            }

            // inform user of failed commands
            if (failedCommands.size() == 0) {
                logNormal("All commands were successfully registered.");
                logNormal("Note: this does not mean they will work as you expect.");
                if (debugMode) {
                    logNormal("If a command does not work, check the console output to try to find the problem.");
                } else {
                    logNormal("If a command does not work, turn on debug mode, then check the console output to try to find the problem.");
                }
            } else {
                logNormal("%s command(s) failed while registering:", failedCommands.size());
                increaseIndentation();
                for (String message : failedCommands) {
                    logError(message);
                }
                decreaseIndentation();
                if (debugMode) {
                    logNormal("Scroll up to find more information.");
                } else {
                    logNormal("Turn on debug mode and scroll up to find more information.");
                }
            }
        }
    }
}


