package me.willkroboth.ConfigCommands.NMS.V1_18;

import me.willkroboth.ConfigCommands.NMS.NMS;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_18.OpSenders1_18.OpSender1_18;
import org.bukkit.command.CommandSender;

public class NMS1_18 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_18.makeOpSender(sender);
    }
}
