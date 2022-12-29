package me.willkroboth.ConfigCommands.NMS.V1_16_5;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5.OpSender1_16_5;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.16.5
 */
public class NMS1_16_5 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_16_5.makeOpSender(sender);
    }
}
