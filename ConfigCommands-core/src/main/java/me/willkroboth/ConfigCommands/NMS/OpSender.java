package me.willkroboth.ConfigCommands.NMS;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.UUID;

// OpSenders should:
//  Appear as the class they represent in instanceof calls
//  Be able to run vanilla commands
//      See org.bukkit.craftbukkit.[version].command.VanillaCommandWrapper#getListener() for what methods are used
//      Wrapper must represent data of entity, use `this` as source, and have permissionLevel 4
//      Parameters of CommandListenerWrapper are as follows:
//          source, worldPosition, rotation, level(Dimension?), permissionLevel, textName, displayName, server, entity
//  Not broadcast commands to console
//  Act with operator status
//  Keep a message history for returning
public interface OpSender extends CommandSender {
    static OpSender makeOpSender(CommandSender sender) {
        return ConfigCommandsHandler.getNMS().makeOpSender(sender);
    }

    CommandSender getSender();

    // provide result message
    String getResult();

    // store result message for CommandSender methods
    void setLastMessage(String message);

    // overriding CommandSender
    default void sendMessage(String s) {
        setLastMessage(s);
    }

    default void sendMessage(String[] strings) {
        setLastMessage(Arrays.toString(strings));
    }

    default void sendMessage(UUID uuid, String s) {
        setLastMessage(s);
    }

    default void sendMessage(UUID uuid, String[] strings) {
        setLastMessage(Arrays.toString(strings));
    }

    // overriding ServerOperator
    default boolean isOp() {
        return true;
    }

    // permissions
    default boolean isPermissionSet(String name) {
        return true;
    }

    default boolean isPermissionSet(Permission perm) {
        return true;
    }

    default boolean hasPermission(String name) {
        return true;
    }

    default boolean hasPermission(Permission perm) {
        return true;
    }
}
