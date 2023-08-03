package me.willkroboth.configcommands.nms.v1_18_2;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_18_2.opsenders.OpSender1_18_2;
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
