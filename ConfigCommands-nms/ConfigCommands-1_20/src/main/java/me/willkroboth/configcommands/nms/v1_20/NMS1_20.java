package me.willkroboth.configcommands.nms.v1_20;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_20.opsenders.ConsoleOpSender1_20;
import me.willkroboth.configcommands.nms.v1_20.opsenders.OpSender1_20;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.20 and 1.20.1
 */
public class NMS1_20 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_20.makeOpSender(sender);
    }

    @Override
    public void initializeConsoleOpSender(ConsoleCommandSender source) {
        ConsoleOpSender1_20.initializeInstance(source);
    }
}
