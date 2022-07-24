package me.willkroboth.ConfigCommands;

import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;

public class ConfigCommands extends ConfigCommandAddOn {
    // ConfigCommands' information as an AddOn
    protected String getPackageName() {
        return "me.willkroboth.ConfigCommands.InternalArguments";
    }

    protected RegisterMode getRegisterMode() {
        return RegisterMode.INTERNAL_ARGUMENTS;
    }

    // Enable
    @Override
    public void onEnable() {
        ConfigCommandsHandler.enable(this);
    }
}
