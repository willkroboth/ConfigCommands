package me.willkroboth.ConfigCommands.NMS;

import me.willkroboth.ConfigCommands.Exceptions.UnsupportedVersionException;
import me.willkroboth.ConfigCommands.NMS.V1_16_5.NMS1_16_5;
import me.willkroboth.ConfigCommands.NMS.V1_19.NMS1_19;

public interface VersionHandler {
    static NMS getVersion(String version) {
        return switch (version){
            case "1.19" -> new NMS1_19();
            case "1.16.5" -> new NMS1_16_5();
            default -> throw new UnsupportedVersionException(version);
        };
    }
}
