package me.willkroboth.configcommands.nms;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.UnsupportedVersionException;
import me.willkroboth.configcommands.nms.v1_16_5.NMS1_16_5;
import me.willkroboth.configcommands.nms.v1_17.NMS1_17;
import me.willkroboth.configcommands.nms.v1_18.NMS1_18;
import me.willkroboth.configcommands.nms.v1_18_2.NMS1_18_2;
import me.willkroboth.configcommands.nms.v1_19.NMS_1_19;
import me.willkroboth.configcommands.nms.v1_19_1.NMS1_19_1;
import me.willkroboth.configcommands.nms.v1_19_3.NMS1_19_3;
import org.bukkit.Bukkit;

/**
 * A class for loading the correct version of {@link NMS}.
 */
public interface VersionHandler {
    /**
     * @return The implementation of {@link NMS} for the current version.
     * @throws UnsupportedVersionException if the detected version is not supported.
     */
    static NMS loadNMS() {
        boolean useLatest = ConfigCommandsHandler.getConfigFile().getBoolean("useLatestNMS", false);

        String version;
        if (useLatest) {
            ConfigCommandsHandler.logDebug("Defaulting to latest NMS");
            version = "1.19.3";
        } else {
            String bukkit = Bukkit.getServer().toString();
            version = bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1);
        }
        ConfigCommandsHandler.logDebug("Loading NMS for version %s", version);
        return switch (version) {
            case "1.19.3" -> new NMS1_19_3();
            case "1.19.1", "1.19.2" -> new NMS1_19_1();
            case "1.19" -> new NMS_1_19();
            case "1.18.2" -> new NMS1_18_2();
            case "1.18", "1.18.1" -> new NMS1_18();
            case "1.17", "1.17.1" -> new NMS1_17();
            case "1.16.5" -> new NMS1_16_5();
            default -> throw new UnsupportedVersionException(version);
        };
    }
}
