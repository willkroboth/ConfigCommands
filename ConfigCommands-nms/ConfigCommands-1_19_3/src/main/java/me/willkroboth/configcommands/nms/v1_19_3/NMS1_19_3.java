package me.willkroboth.configcommands.nms.v1_19_3;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_19_3.opsenders.OpSender1_19_3;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.19.3
 */
public class NMS1_19_3 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19_3.makeOpSender(sender);
    }
}