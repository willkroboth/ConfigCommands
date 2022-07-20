package me.willkroboth.ConfigCommands.Exceptions;

public class UnsupportedVersionException extends RuntimeException{
    public UnsupportedVersionException(String version){
        super("This version of Minecraft is unsupported: " + version);
    }
}
