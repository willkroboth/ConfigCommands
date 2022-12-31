package me.willkroboth.configcommands;

import me.willkroboth.configcommands.helperclasses.ConfigCommandAddOn;

/**
 * The JavaPlugin entry point for loading ConfigCommands on Bukkit
 */
public class ConfigCommands extends ConfigCommandAddOn {
    // ConfigCommands' information as an AddOn
    @Override
    protected String getPackageName() {
        return "me.willkroboth.ConfigCommands.InternalArguments";
    }

    @Override
    protected RegisterMode getRegisterMode() {
        return RegisterMode.INTERNAL_ARGUMENTS;
    }

    // Enable
    @Override
    public void onEnable() {
        ConfigCommandsHandler.enable(this);
    }
}
