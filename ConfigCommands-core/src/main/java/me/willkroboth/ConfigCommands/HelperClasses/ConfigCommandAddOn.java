package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.FunctionAdder;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expressions.Expression;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// TODO: Make static methods on InternalArgument schedule registering InternalArguments
//  figure out if that means ConfigCommandAddOn exists or needs to be changed

/**
 * A class that handles loading new {@link InternalArgument} and {@link FunctionAdder} objects to add
 * to the base ConfigCommands system.
 */
public abstract class ConfigCommandAddOn extends JavaPlugin {

    // AddOn management
    private static final Map<String, ConfigCommandAddOn> addOns = new HashMap<>();

    /**
     * Registers a {@link ConfigCommandAddOn} with the ConfigCommands system, so it can be loaded at the proper time.
     *
     * @param addOn The {@link ConfigCommandAddOn} to register.
     */
    public static void includeAddOn(ConfigCommandAddOn addOn) {
        addOns.put(addOn.getName().toLowerCase(), addOn);
    }

    /**
     * @return A map from name to {@link ConfigCommandAddOn} instance for all the registered {@link ConfigCommandAddOn} objects.
     */
    public static Map<String, ConfigCommandAddOn> getAddOns() {
        return addOns;
    }

    /**
     * Gets a {@link ConfigCommandAddOn} instance from the map of registered {@link ConfigCommandAddOn} objects.
     *
     * @param name The name of the {@link ConfigCommandAddOn} to get
     * @return The {@link ConfigCommandAddOn} instance for the given name, or null if this {@link ConfigCommandAddOn}
     * dose not exist or was not registered with {@link ConfigCommandAddOn#includeAddOn(ConfigCommandAddOn)}.
     */
    @Nullable
    public static ConfigCommandAddOn getAddOn(String name) {
        return addOns.get(name.toLowerCase());
    }

    /**
     * Registers all the {@link InternalArgument} and {@link FunctionAdder} objects from all the registered
     * {@link ConfigCommandAddOn} objects.
     */
    public static void registerAllInternalArguments() {
        ConfigCommandsHandler.logNormal("");
        ConfigCommandsHandler.logNormal("Registering InternalArguments");

        // register InternalArguments from addOns
        for (ConfigCommandAddOn addOn : addOns.values()) {
            ConfigCommandsHandler.logNormal("Loading addOn %s", addOn);
            addOn.registerInternalArguments();
        }

        // display registrations
        if (ConfigCommandsHandler.isDebugMode()) {
            ConfigCommandsHandler.logNormal(
                    "All recognized InternalArguments:\n\t%s",
                    InternalArgument.getRegisteredInternalArguments().toString().replace(", ", ",\n\t")
            );
            ConfigCommandsHandler.logNormal(
                    "Static class map:\n\t%s",
                    Expression.getStaticClassMap().toString().replace(", ", ",\n\t")
            );
        }
    }

    // AddOn configuration

    /**
     * This default constructor, used when Bukkit initializes this class as a {@link JavaPlugin}, automatically
     * registers this {@link ConfigCommandAddOn} with {@link ConfigCommandAddOn#includeAddOn(ConfigCommandAddOn)}.
     */
    public ConfigCommandAddOn() {
        super();
        includeAddOn(this);
    }

    /**
     * An enum used to select one of four default behaviors to perform when
     * {@link ConfigCommandAddOn#registerInternalArguments()} is called. These are:
     * <ul>
     *     <li>{@link RegisterMode#All}</li>
     *     <li>{@link RegisterMode#INTERNAL_ARGUMENTS}</li>
     *     <li>{@link RegisterMode#FUNCTION_ADDERS}</li>
     *     <li>{@link RegisterMode#NONE}</li>
     * </ul>
     */
    public enum RegisterMode {
        /**
         * Registers both {@link InternalArgument} and {@link FunctionAdder} objects using
         * {@link InternalArgument#registerFullPackage(String, String, ClassLoader, boolean, Logger)}.
         */
        All,
        /**
         * Registers only {@link InternalArgument} objects using
         * {@link InternalArgument#registerPackageOfInternalArguments(String, String, ClassLoader, boolean, Logger)}.
         */
        INTERNAL_ARGUMENTS,
        /**
         * Registers only {@link FunctionAdder} objects using
         * {@link InternalArgument#registerPackageOfFunctionAdders(String, String, ClassLoader, boolean, Logger)}.
         */
        FUNCTION_ADDERS,
        /**
         * Registers nothing.
         */
        NONE
    }

    /**
     * Registers {@link InternalArgument} and {@link FunctionAdder} objects for this {@link ConfigCommandAddOn}.
     * The default implementation of this method in {@link ConfigCommandAddOn} uses
     * {@link ConfigCommandAddOn#getRegisterMode()} and {@link ConfigCommandAddOn#getPackageName()} to determine
     * how to load these objects.
     */
    public void registerInternalArguments() {
        switch (getRegisterMode()) {
            case All -> InternalArgument.registerFullPackage(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case INTERNAL_ARGUMENTS -> InternalArgument.registerPackageOfInternalArguments(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case FUNCTION_ADDERS -> InternalArgument.registerPackageOfFunctionAdders(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case NONE -> getLogger().info("No InternalArguments or FunctionAdders to register");
        }
    }

    /**
     * @return The {@link RegisterMode} to use for the default implementation of
     * {@link ConfigCommandAddOn#registerInternalArguments()}. The default value
     * for this method is {@link RegisterMode#All}.
     */
    protected RegisterMode getRegisterMode() {
        return RegisterMode.All;
    }

    /**
     * @return A String that holds the name of the package where the {@link InternalArgument} and {@link FunctionAdder}
     * java files can be found. This can be the String after the {@code package} keyword and before the semicolon on the
     * first line in one of these files, or any enclosing package.
     */
    protected abstract String getPackageName();

    @Override
    public @NotNull String toString() {
        return this.getName();
    }
}
