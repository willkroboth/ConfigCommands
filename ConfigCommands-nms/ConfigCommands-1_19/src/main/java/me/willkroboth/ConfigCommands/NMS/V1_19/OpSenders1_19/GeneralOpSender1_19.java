package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.GeneralOpSender1_19_common;
import org.bukkit.command.CommandSender;

/**
 * An OpSender that works for any {@link CommandSender} on Minecraft 1.19.
 */
public class GeneralOpSender1_19 extends GeneralOpSender1_19_common implements OpSender1_19 {
    /**
     * Creates a new {@link GeneralOpSender1_19}.
     *
     * @param sender The {@link CommandSender} this {@link GeneralOpSender1_19} is wrapping.
     */
    public GeneralOpSender1_19(CommandSender sender) {
        super(sender);
    }
}
