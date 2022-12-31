package me.willkroboth.configcommands.nms;

/**
 * A class for loading the correct version of {@link NMS}. The class declared here is not actually
 * used to load NMS, as it cannot access those classes and so throws an error. Instead, this class
 * is replaced by one with an identical signature in ConfigCommands-plugin that properly loads NMS.
 */
public interface VersionHandler {
    /**
     * @return The implementation of {@link NMS} for the current version.
     */
    static NMS loadNMS() {
        throw new RuntimeException("You have the wrong VersionHandler class loaded");
    }
}