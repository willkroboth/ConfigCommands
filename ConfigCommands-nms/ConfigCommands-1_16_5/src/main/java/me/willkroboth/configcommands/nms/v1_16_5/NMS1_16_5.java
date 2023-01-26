package me.willkroboth.configcommands.nms.v1_16_5;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_16_5.opsenders.ConsoleOpSender1_16_5;
import me.willkroboth.configcommands.nms.v1_16_5.opsenders.OpSender1_16_5;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.16.5
 */
public class NMS1_16_5 implements NMS {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_16_5.makeOpSender(sender);
    }

    @Override
    public void initializeConsoleOpSender(ConsoleCommandSender source) {
        ConsoleOpSender1_16_5.initializeInstance(source);
    }
}
