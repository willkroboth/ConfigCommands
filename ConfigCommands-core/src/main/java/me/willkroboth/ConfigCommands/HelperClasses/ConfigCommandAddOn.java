package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ConfigCommandAddOn extends JavaPlugin {
    public ConfigCommandAddOn() {
        super();
        ConfigCommandsHandler.registerAddOn(this);
    }

    public enum RegisterMode{
        All,
        INTERNAL_ARGUMENTS,
        FUNCTION_ADDERS,
        NONE,
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
        }
    }

    // makes it easier to choose how internal arguments are registered
    protected RegisterMode getRegisterMode() { return RegisterMode.All; }

    protected abstract String getPackageName();


    public String toString() { return this.getName(); }
}
