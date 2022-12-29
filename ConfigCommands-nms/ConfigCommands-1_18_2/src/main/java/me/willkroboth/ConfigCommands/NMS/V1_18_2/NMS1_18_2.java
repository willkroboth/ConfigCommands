package me.willkroboth.ConfigCommands.NMS.V1_18_2;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_18_2.OpSenders1_18_2.OpSender1_18_2;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.18.2
 */
public class NMS1_18_2 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_18_2.makeOpSender(sender);
    }
}
