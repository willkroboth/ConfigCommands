package me.willkroboth.configcommands.nms.v1_19_1.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.GeneralOpSender1_19_common;
import org.bukkit.command.CommandSender;

/**
 * An OpSender that works for any {@link CommandSender} on Minecraft 1.19.1 and 1.19.2.
 */
public class GeneralOpSender1_19_1 extends GeneralOpSender1_19_common implements OpSender1_19_1 {
    /**
     * Creates a new {@link GeneralOpSender1_19_1}.
     *
     * @param sender The {@link CommandSender} this {@link GeneralOpSender1_19_1} is wrapping.
     */
    public GeneralOpSender1_19_1(CommandSender sender) {
        super(sender);
    }
}
