package me.willkroboth.ConfigCommands.NMS;

public interface VersionHandler {
    static NMS getVersion(String ignored) {
        throw new RuntimeException("You have the wrong VersionHandler class loaded");
    }
}