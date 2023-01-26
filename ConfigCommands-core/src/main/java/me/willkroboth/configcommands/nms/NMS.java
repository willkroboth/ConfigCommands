package me.willkroboth.configcommands.nms;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * An interface for using version-specific features. An appropriate implementation of this class
 * will be loaded by {@link VersionHandler} which implements these methods in a version-specific
 * context.
 */
public interface NMS {
    /**
     * Wraps a {@link CommandSender} into an {@link OpSender}.
     *
     * @param sender The {@link CommandSender} to wrap.
     * @return An {@link OpSender} wrapping the given {@link CommandSender}.
     */
    OpSender makeOpSender(CommandSender sender);

    /**
     * Initializes the shared ConsoleOpSender
     * @param source The base {@link ConsoleCommandSender} to wrap
     */
    void initializeConsoleOpSender(ConsoleCommandSender source);
}
