package me.willkroboth.configcommands.nms.v1_18;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_18.opsenders.OpSender1_18;
import org.bukkit.command.CommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.18 and 1.18.1
 */
public class NMS1_18 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_18.makeOpSender(sender);
    }
}
