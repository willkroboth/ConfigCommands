package me.willkroboth.configcommands.nms.v1_19.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.BlockOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;

/**
 * A {@link org.bukkit.command.BlockCommandSender} OpSender for Minecraft 1.19.
 */
public class BlockOpSender1_19 extends BlockOpSender1_19_common implements OpSender1_19 {
    /**
     * Creates a new {@link BlockOpSender1_19}.
     *
     * @param b The {@link CraftBlockCommandSender} this {@link BlockOpSender1_19} is wrapping.
     */
    public BlockOpSender1_19(CraftBlockCommandSender b) {
        super(b);
    }
}
