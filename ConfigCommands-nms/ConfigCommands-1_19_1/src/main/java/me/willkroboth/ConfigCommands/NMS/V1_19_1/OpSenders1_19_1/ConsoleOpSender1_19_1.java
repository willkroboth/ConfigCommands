package me.willkroboth.ConfigCommands.NMS.V1_19_1.OpSenders1_19_1;

import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.ConsoleOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;

public class ConsoleOpSender1_19_1 extends ConsoleOpSender1_19_common implements OpSender1_19_1 {
    public ConsoleOpSender1_19_1(CraftConsoleCommandSender c) {
        super(c);
    }
}
