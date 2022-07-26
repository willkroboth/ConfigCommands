package me.willkroboth.ConfigCommands;

import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandBuilder;
import me.willkroboth.ConfigCommands.HelperClasses.Expression;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.VersionHandler;
import me.willkroboth.ConfigCommands.SystemCommands.SystemCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

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

        ConfigCommandBuilder.registerCommandsFromConfig(getConfigFile().getConfigurationSection("commands"), debugMode);

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
}


