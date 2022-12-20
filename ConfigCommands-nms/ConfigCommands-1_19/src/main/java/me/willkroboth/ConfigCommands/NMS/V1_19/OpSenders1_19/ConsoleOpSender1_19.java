package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.ConsoleOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;

public class ConsoleOpSender1_19 extends ConsoleOpSender1_19_common implements OpSender1_19 {
    public ConsoleOpSender1_19(CraftConsoleCommandSender c) {
        super(c);
    }
}
