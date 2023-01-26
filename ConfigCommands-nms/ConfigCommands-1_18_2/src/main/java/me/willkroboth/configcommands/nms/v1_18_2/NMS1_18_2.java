package me.willkroboth.configcommands.nms.v1_18_2;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_18_2.opsenders.ConsoleOpSender1_18_2;
import me.willkroboth.configcommands.nms.v1_18_2.opsenders.OpSender1_18_2;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.18.2
 */
public class NMS1_18_2 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_18_2.makeOpSender(sender);
    }

    @Override
    public void initializeConsoleOpSender(ConsoleCommandSender source) {
        ConsoleOpSender1_18_2.initializeInstance(source);
    }
}
