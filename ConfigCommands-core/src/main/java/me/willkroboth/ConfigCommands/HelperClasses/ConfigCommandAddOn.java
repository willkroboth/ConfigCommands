package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ConfigCommandAddOn extends JavaPlugin {
    public ConfigCommandAddOn() {
        super();
        ConfigCommandsHandler.registerAddOn(this);
    }

    public void registerInternalArguments() {
        switch (getRegisterMode()) {
            case 0 -> InternalArgument.registerFullPackage(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case 1 -> InternalArgument.registerPackageOfInternalArguments(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case 2 -> InternalArgument.registerPackageOfFunctionAdders(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
        }
    }

    // makes it easier to choose how internal arguments are registered
    protected int getRegisterMode() { return 0; }

    protected abstract String getPackageName();


    public String toString() { return this.getName(); }
}
