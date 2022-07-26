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

public class ConfigCommandsHandler {
    // plugin instance
    private static ConfigCommands plugin;

    // debug mode
    private static boolean debugMode;

    public static boolean isDebugMode() {
        return debugMode;
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

        ConfigCommandAddOn.registerAllInternalArguments();

        InternalArgument.createFunctionMaps();

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
}


