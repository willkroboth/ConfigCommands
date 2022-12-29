package me.willkroboth.ConfigCommands.NMS.V1_19_1;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_19_1.OpSenders1_19_1.OpSender1_19_1;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.NMS1_19_common;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.19.1 and 1.19.2
 */
public class NMS1_19_1 extends NMS1_19_common {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19_1.makeOpSender(sender);
    }
}
