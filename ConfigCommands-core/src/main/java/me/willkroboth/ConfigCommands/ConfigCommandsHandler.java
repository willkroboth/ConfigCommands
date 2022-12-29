package me.willkroboth.ConfigCommands;

import me.willkroboth.ConfigCommands.HelperClasses.*;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.VersionHandler;
import me.willkroboth.ConfigCommands.RegisteredCommands.CommandTreeBuilder;
import me.willkroboth.ConfigCommands.SystemCommands.SystemCommandHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class for handling the ConfigCommands plugin
 */
public class ConfigCommandsHandler {
    // plugin instance
    private static ConfigCommands plugin;

    // debug mode
    private static GlobalDebugValue debugMode;

    /**
     * @return True if debug mode is enabled globally for ConfigCommands, and false otherwise.
     */
    public static boolean isDebugMode() {
        return debugMode.isDebug();
    }

    /**
     * @return The {@link GlobalDebugValue} object being used by ConfigCommands, useful for
     * creating {@link SharedDebugValue} objects that should be linked to ConfigCommands.
     */
    public static GlobalDebugValue getGlobalDebugValue() {
        return debugMode;
    }

    /**
     * Turns the global debug for ConfigCommands on or off.
     *
     * @param debug True to turn global debug on, and false otherwise.
     */
    public static void setGlobalDebug(boolean debug) {
        debugMode.setDebug(debug);
    }

    // Config file

    /**
     * @return The {@link FileConfiguration} object that holds ConfigCommand's config file.
     */
    public static FileConfiguration getConfigFile() {
        return plugin.getConfig();
    }

    /**
     * Triggers {@link JavaPlugin#saveConfig()} to save any changes made to ConfigCommand's config file.
     */
    public static void saveConfigFile() {
        plugin.saveConfig();
    }

    /**
     * Triggers {@link JavaPlugin#reloadConfig()} to update the cached config file with any values changed from disk.
     */
    public static void reloadConfigFile() {
        plugin.reloadConfig();
    }

    // NMS
    private static NMS nms;

    /**
     * @return The current instance of {@link NMS} in use.
     */
    public static NMS getNMS() {
        return nms;
    }

    // logging
    private static IndentedLogger logger;

    /**
     * Increases the indentation being used for ConfigCommand's {@link IndentedLogger} by one.
     */
    public static void increaseIndentation() {
        logger.increaseIndentation();
    }

    /**
     * Decreases the indentation being used for ConfigCommand's {@link IndentedLogger} by one.
     */
    public static void decreaseIndentation() {
        logger.decreaseIndentation();
    }

    /**
     * @return The indentation currently set for ConfigCommand's {@link IndentedLogger}.
     */
    public static int getIndentation() {
        return logger.getIndentation();
    }

    /**
     * Sets the indentation of ConfigCommand's {@link IndentedLogger} to the given value.
     *
     * @param indentation The new indentation level.
     */
    public static void setIndentation(int indentation) {
        logger.setIndentation(indentation);
    }

    /**
     * Logs a message with ConfigCommand's {@link IndentedLogger} using {@link IndentedLogger#info(String, Object...)}.
     *
     * @param message The String for the message.
     * @param objects An array of objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public static void logNormal(String message, Object... objects) {
        logger.info(message, objects);
    }

    /**
     * Logs a message with ConfigCommand's {@link IndentedLogger} using
     * {@link IndentedLogger#logDebug(boolean, String, Object...)}, with the debug
     * value given by {@link ConfigCommandsHandler#isDebugMode()}.
     *
     * @param message The String for the message.
     * @param objects An array of objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public static void logDebug(String message, Object... objects) {
        logger.logDebug(debugMode.isDebug(), message, objects);
    }

    /**
     * Logs a message with ConfigCommand's {@link IndentedLogger} using
     * {@link IndentedLogger#logDebug(boolean, String, Object...)}, with the debug value given.
     *
     * @param debugMode The debug value to pass to {@link IndentedLogger#logDebug(boolean, String, Object...)}.
     * @param message   The String for the message.
     * @param objects   An array of objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public static void logDebug(boolean debugMode, String message, Object... objects) {
        logger.logDebug(debugMode, message, objects);
    }

    /**
     * Logs a message with ConfigCommand's {@link IndentedLogger} using
     * {@link IndentedLogger#logDebug(boolean, String, Object...)}, with the debug
     * value given by {@link DebuggableState#isDebug()}.
     *
     * @param debugState The {@link DebuggableState} object to use to determine if the message is shown or not.
     * @param message    The String for the message.
     * @param objects    An array of objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public static void logDebug(DebuggableState debugState, String message, Object... objects) {
        logger.logDebug(debugState.isDebug(), message, objects);
    }

    /**
     * Logs a message with ConfigCommand's {@link IndentedLogger} using {@link IndentedLogger#warn(String, Object...)}.
     *
     * @param message The String for the message.
     * @param objects An array of objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public static void logWarning(String message, Object... objects) {
        logger.warn(message, objects);
    }

    /**
     * Logs a message with ConfigCommand's {@link IndentedLogger} using {@link IndentedLogger#error(String, Object...)}.
     *
     * @param message The String for the message.
     * @param objects An array of objects to insert into the message using {@link String#format(String, Object...)}.
     */
    public static void logError(String message, Object... objects) {
        logger.error(message, objects);
    }

    // Reflection
    private static final Map<ClassKey, Field> fieldCache = new HashMap<>();

    /**
     * Retrieves a {@link Field} with the given name from the given class using {@link Class#getDeclaredField(String)}.
     * If the class-name combination was previously requested, then the given {@link Field} is retrieved from a cache.
     * Before the {@link Field} is added to the cache and returned, it is also made accessible using
     * {@link Field#setAccessible(boolean)}.
     *
     * @param clazz The class that holds the target {@link Field}.
     * @param name  The name of the target {@link Field}.
     * @return The field inside the given class with the given name.
     */
    public static Field getField(Class<?> clazz, String name) {
        return fieldCache.computeIfAbsent(new ClassKey(clazz, name), (key) -> {
            try {
                Field field = key.clazz.getDeclaredField(key.name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                logError("Could not get field \"%s\" from class %s", key.name, key.clazz);
                throw new RuntimeException(e);
            }
        });
    }

    private record ClassKey(Class<?> clazz, String name) {
    }

    // Enable tasks

    /**
     * Enables the ConfigCommands plugin.
     *
     * @param plugin The {@link ConfigCommands} plugin object.
     */
    public static void enable(ConfigCommands plugin) {
        ConfigCommandsHandler.plugin = plugin;

        logger = new IndentedLogger(plugin.getLogger());

        plugin.saveDefaultConfig();
        debugMode = new GlobalDebugValue(getConfigFile().getBoolean("debug", false));
        logDebug("Debug mode on! More information will be shown.");

        nms = VersionHandler.loadNMS();

        ConfigCommandAddOn.registerAllInternalArguments();

        InternalArgument.createFunctionMaps();

        CommandTreeBuilder.registerCommandsFromConfig(getConfigFile().getConfigurationSection("commands"), debugMode);

        SystemCommandHandler.setUpCommands(plugin);

        logNormal("Done!");
    }
}


