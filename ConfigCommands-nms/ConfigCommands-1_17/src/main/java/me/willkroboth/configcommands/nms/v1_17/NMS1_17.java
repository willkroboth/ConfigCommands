package me.willkroboth.configcommands.nms.v1_17;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_17.opsenders.ConsoleOpSender1_17;
import me.willkroboth.configcommands.nms.v1_17.opsenders.OpSender1_17;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.17 and 1.17.1
 */
public class NMS1_17 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_17.makeOpSender(sender);
    }

    @Override
    public void initializeConsoleOpSender(ConsoleCommandSender source) {
        ConsoleOpSender1_17.initializeInstance(source);
    }
}
