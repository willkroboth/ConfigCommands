package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.BlockOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;

public class BlockOpSender1_19 extends BlockOpSender1_19_common implements OpSender1_19 {
    public BlockOpSender1_19(CraftBlockCommandSender b) {
        super(b);
    }
}
