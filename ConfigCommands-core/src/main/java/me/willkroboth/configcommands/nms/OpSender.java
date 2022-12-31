package me.willkroboth.configcommands.nms;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

/**
 * An interface that wraps a {@link CommandSender} as an "OpSender". OpSenders should:
 * <ul>
 *     <li>Appear as the class they represent in instanceof calls ({@link Player}, {@link ConsoleCommandSender}, etc.)</li>
 *     <li>Be able to run vanilla commands
 *     <ul>
 *         <li>Vanilla wrapper must represent data of sender, use the wrapping OpSender as source, and have permissionLevel 4</li>
 *         <li>Parameters of CommandListenerWrapper are: {@code source, worldPosition, rotation, level(Dimension?), permissionLevel, textName, displayName, server, entity}</li>
 *         <li>See {@code org.bukkit.craftbukkit.[version].command.VanillaCommandWrapper#getListener()} for the methods used to get the wrapper for each class</li>
 *     </ul></li>
 *     <li>Not broadcast commands to console</li>
 *     <li>Act with operator status</li>
 *     <li>Remember the last message sent to them, which carries the final result of a command</li>
 * </ul>
 */
public interface OpSender extends CommandSender {
    /**
     * Wraps a {@link CommandSender} into an {@link OpSender}.
     *
     * @param sender The {@link CommandSender} to wrap.
     * @return An {@link OpSender} wrapping the given {@link CommandSender}.
     */
    static OpSender makeOpSender(CommandSender sender) {
        return ConfigCommandsHandler.getNMS().makeOpSender(sender);
    }

    /**
     * @return The {@link CommandSender} this {@link OpSender} is wrapping
     */
    CommandSender getSender();

    /**
     * @return The last message sent to this {@link OpSender}.
     */
    String getResult();

    /**
     * Sets the last message sent to this {@link OpSender}.
     *
     * @param message The message sent.
     */
    void setLastMessage(String message);

    // overriding CommandSender
    @Override
    default void sendMessage(@NotNull String s) {
        setLastMessage(s);
    }

    @Override
    default void sendMessage(String[] strings) {
        setLastMessage(Arrays.toString(strings));
    }

    @Override
    default void sendMessage(UUID uuid, @NotNull String s) {
        setLastMessage(s);
    }

    @Override
    default void sendMessage(UUID uuid, String[] strings) {
        setLastMessage(Arrays.toString(strings));
    }

    // Overriding ServerOperator
    @Override
    default boolean isOp() {
        return true;
    }

    // Overriding Permissible
    @Override
    default boolean isPermissionSet(@NotNull String name) {
        return true;
    }

    @Override
    default boolean isPermissionSet(@NotNull Permission perm) {
        return true;
    }

    @Override
    default boolean hasPermission(@NotNull String name) {
        return true;
    }

    @Override
    default boolean hasPermission(@NotNull Permission perm) {
        return true;
    }
}
