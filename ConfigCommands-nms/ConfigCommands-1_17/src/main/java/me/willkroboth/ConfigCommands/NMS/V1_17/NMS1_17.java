package me.willkroboth.ConfigCommands.NMS.V1_17;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_17.OpSenders1_17.OpSender1_17;
import org.bukkit.command.CommandSender;

public class NMS1_17 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_17.makeOpSender(sender);
    }
}
