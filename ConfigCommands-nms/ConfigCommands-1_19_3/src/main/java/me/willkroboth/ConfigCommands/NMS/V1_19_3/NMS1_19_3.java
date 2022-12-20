package me.willkroboth.ConfigCommands.NMS.V1_19_3;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_19_3.OpSenders1_19_3.OpSender1_19_3;
import org.bukkit.command.CommandSender;

public class NMS1_19_3 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19_3.makeOpSender(sender);
    }
}
