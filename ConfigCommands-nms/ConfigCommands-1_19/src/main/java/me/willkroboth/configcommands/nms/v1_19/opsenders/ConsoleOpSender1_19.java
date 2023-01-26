package me.willkroboth.configcommands.nms.v1_19.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.ConsoleOpSender1_19_common;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;

/**
 * A {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.19.
 */
public class ConsoleOpSender1_19 extends ConsoleOpSender1_19_common implements OpSender1_19 {
    private static ConsoleOpSender1_19 instance;

    public static void initializeInstance(ConsoleCommandSender source) {
        instance = new ConsoleOpSender1_19((CraftConsoleCommandSender) source);
    }

    public static ConsoleOpSender1_19 getInstance() {
        return instance;
    }

    /**
     * Creates a new {@link ConsoleOpSender1_19}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_19} is wrapping.
     */
    public ConsoleOpSender1_19(CraftConsoleCommandSender c) {
        super(c);
    }
}
