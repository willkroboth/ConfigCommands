package me.willkroboth.ConfigCommands.NMS;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.UnsupportedVersionException;
import me.willkroboth.ConfigCommands.NMS.V1_16_5.NMS1_16_5;
import me.willkroboth.ConfigCommands.NMS.V1_17.NMS1_17;
import me.willkroboth.ConfigCommands.NMS.V1_18.NMS1_18;
import me.willkroboth.ConfigCommands.NMS.V1_18_2.NMS1_18_2;
import me.willkroboth.ConfigCommands.NMS.V1_19.NMS1_19;
import org.bukkit.Bukkit;

public interface VersionHandler {
    static NMS loadNMS() {
        boolean useLatest = ConfigCommandsHandler.getConfigFile().getBoolean("useLatestNMS", false);

        String version;
        if(useLatest) {
            ConfigCommandsHandler.logDebug("Defaulting to latestNMS");
            version = "1.19";
        } else {
            String bukkit = Bukkit.getServer().toString();
            version = bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1);
        }
        ConfigCommandsHandler.logDebug("Loading NMS for version: " + version);
        return switch (version){
            case "1.19" -> new NMS1_19();
            case "1.18.2" -> new NMS1_18_2();
            case "1.18", "1.18.1" -> new NMS1_18();
            case "1.17", "1.17.1" -> new NMS1_17();
            case "1.16.5" -> new NMS1_16_5();
            default -> throw new UnsupportedVersionException(version);
        };
    }
}
