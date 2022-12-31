package me.willkroboth.configcommands.nms.v1_19;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_19.opsenders.OpSender1_19;
import me.willkroboth.configcommands.nms.v1_19_common.NMS1_19_common;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.19
 */
public class NMS_1_19 extends NMS1_19_common {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19.makeOpSender(sender);
    }
}
