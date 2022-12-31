package me.willkroboth.configcommands.nms.v1_19.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.MinecartOpSender1_19Common;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMinecartCommand;

/**
 * A {@link org.bukkit.entity.minecart.CommandMinecart} OpSender for Minecraft 1.19.
 */
public class MinecartOpSender1_19 extends MinecartOpSender1_19Common implements OpSender1_19 {
    /**
     * Creates a new {@link MinecartOpSender1_19}.
     *
     * @param m The {@link CraftMinecartCommand} this {@link MinecartOpSender1_19} is wrapping.
     */
    public MinecartOpSender1_19(CraftMinecartCommand m) {
        super(m);
    }
}
