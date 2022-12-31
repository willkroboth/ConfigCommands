package me.willkroboth.configcommands.nms.v1_19_1.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.BlockOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;

/**
 * A {@link org.bukkit.command.BlockCommandSender} OpSender for Minecraft 1.19.1 and 1.19.2
 */
public class BlockOpSender1_19_1 extends BlockOpSender1_19_common implements OpSender1_19_1{
    /**
     * Creates a new {@link BlockOpSender1_19_1}.
     *
     * @param b The {@link CraftBlockCommandSender} this {@link BlockOpSender1_19_1} is wrapping.
     */
    public BlockOpSender1_19_1(CraftBlockCommandSender b) {
        super(b);
    }
}
