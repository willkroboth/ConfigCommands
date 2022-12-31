package me.willkroboth.configcommands.nms.v1_19_1.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.ConsoleOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;

/**
 * A {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.19.1 and 1.19.2.
 */
public class ConsoleOpSender1_19_1 extends ConsoleOpSender1_19_common implements OpSender1_19_1 {

    /**
     * Creates a new {@link ConsoleOpSender1_19_1}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_19_1} is wrapping.
     */
    public ConsoleOpSender1_19_1(CraftConsoleCommandSender c) {
        super(c);
    }
}
