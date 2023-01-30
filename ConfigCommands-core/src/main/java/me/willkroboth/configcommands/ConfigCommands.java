package me.willkroboth.configcommands;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The JavaPlugin entry point for loading ConfigCommands on Bukkit
 */
public class ConfigCommands extends JavaPlugin {
    // Load
    @Override
    public void onLoad() {
        ConfigCommandsHandler.onLoad(this);
    }

    // Enable
    @Override
    public void onEnable() {
        ConfigCommandsHandler.enable();
    }
}
