package me.willkroboth.ConfigCommands.NMS;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import org.bukkit.command.CommandSender;

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
}
