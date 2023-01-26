package me.willkroboth.configcommands.nms.v1_19_1;

import me.willkroboth.configcommands.nms.NMS;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_19_1.opsenders.ConsoleOpSender1_19_1;
import me.willkroboth.configcommands.nms.v1_19_1.opsenders.OpSender1_19_1;
import me.willkroboth.configcommands.nms.v1_19_common.NMS1_19_common;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * {@link NMS} implementation for Minecraft version 1.19.1 and 1.19.2
 */
public class NMS1_19_1 extends NMS1_19_common {
    @Override
    public OpSender makeOpSender(CommandSender sender) {
        return OpSender1_19_1.makeOpSender(sender);
    }

    @Override
    public void initializeConsoleOpSender(ConsoleCommandSender source) {
        ConsoleOpSender1_19_1.initializeInstance(source);
    }
}
