package me.willkroboth.ConfigCommands.Exceptions;

/**
 * An exception that is thrown when ConfigCommands is loaded on an unsupported Minecraft version.
 */
public class UnsupportedVersionException extends RuntimeException{
    /**
     * Creates a new {@link UnsupportedVersionException}.
     *
     * @param version The detected Minecraft version that was decidedly not supported.
     */
    public UnsupportedVersionException(String version){
        super("This version of Minecraft is unsupported: " + version);
    }
}
