package me.willkroboth.ConfigCommands.NMS.V1_19;

import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19.OpSender1_19;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.NMS1_19_common;
import org.bukkit.command.CommandSender;

public class NMS_1_19 extends NMS1_19_common {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19.makeOpSender(sender);
    }
}
