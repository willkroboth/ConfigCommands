package me.willkroboth.configcommands.nms.v1_19_4;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_19_4.opsenders.OpSender1_19_4;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.19.4
 */
public class NMS1_19_4 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19_4.makeOpSender(sender);
    }
}