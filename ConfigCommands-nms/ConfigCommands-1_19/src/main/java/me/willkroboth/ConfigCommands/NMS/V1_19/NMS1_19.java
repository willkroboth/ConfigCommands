package me.willkroboth.ConfigCommands.NMS.V1_19;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19.OpSender1_19;
import org.bukkit.command.CommandSender;

public class NMS1_19 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19.makeOpSender(sender);
    }
}
