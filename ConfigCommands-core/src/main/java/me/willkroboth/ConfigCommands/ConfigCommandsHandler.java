package me.willkroboth.ConfigCommands;

import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;
import me.willkroboth.ConfigCommands.NMS.NMS;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommandsHandler {
    // plugin instance
    private static ConfigCommandsPlugin plugin;

    public static void setPlugin(ConfigCommandsPlugin plugin){
        if(ConfigCommandsHandler.plugin != null){
            throw new UnsupportedOperationException("The ConfigCommands plugin instance can only be set once!");
        }
        ConfigCommandsHandler.plugin = plugin;
    }

    // debug mode
    private static boolean debugMode;

    public static void setDebugMode(boolean debugMode){
        ConfigCommandsHandler.debugMode = debugMode;
    }

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
    public static void setNMS(NMS nms){
        if(ConfigCommandsHandler.nms != null){
            throw new UnsupportedOperationException("The NMS instance can only be set once!");
        }

        ConfigCommandsHandler.nms = nms;
    }

    public static NMS getNMS(){
        return nms;
    }
}


