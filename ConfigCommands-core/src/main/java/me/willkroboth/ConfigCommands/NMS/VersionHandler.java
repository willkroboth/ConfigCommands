package me.willkroboth.ConfigCommands.NMS;

public interface VersionHandler {
    static NMS loadNMS() {
        throw new RuntimeException("You have the wrong VersionHandler class loaded");
    }
}