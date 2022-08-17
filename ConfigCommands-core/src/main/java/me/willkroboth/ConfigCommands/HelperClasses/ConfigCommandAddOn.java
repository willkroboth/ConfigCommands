package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigCommandAddOn extends JavaPlugin {

    // AddOn management
    private static final Map<String, ConfigCommandAddOn> addOns = new HashMap<>();

    public static void includeAddOn(ConfigCommandAddOn addOn) {
        addOns.put(addOn.getName().toLowerCase(), addOn);
    }

    public static Map<String, ConfigCommandAddOn> getAddOns() {
        return addOns;
    }

    public static ConfigCommandAddOn getAddOn(String name) {
        return addOns.getOrDefault(name.toLowerCase(), null);
    }

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
                    AllInternalArguments.getFlat().toString().replace(", ", ",\n\t")
            );
            ConfigCommandsHandler.logNormal(
                    "Static class map:\n\t%s",
                    Expression.getClassMap().toString().replace(", ", ",\n\t")
            );
        }
    }

    // AddOn configuration
    public ConfigCommandAddOn() {
        super();
        includeAddOn(this);
    }

    public enum RegisterMode{
        All,
        INTERNAL_ARGUMENTS,
        FUNCTION_ADDERS,
        NONE
    }

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

    // makes it easier to choose how internal arguments are registered
    protected RegisterMode getRegisterMode() { return RegisterMode.All; }

    protected abstract String getPackageName();


    public String toString() { return this.getName(); }
}
