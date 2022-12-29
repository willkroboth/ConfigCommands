package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.ConsoleOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;

/**
 * A {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.19.
 */
public class ConsoleOpSender1_19 extends ConsoleOpSender1_19_common implements OpSender1_19 {
    /**
     * Creates a new {@link ConsoleOpSender1_19}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_19} is wrapping.
     */
    public ConsoleOpSender1_19(CraftConsoleCommandSender c) {
        super(c);
    }
}
