package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.ProxyOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.ProxiedNativeCommandSender;

/**
 * A {@link org.bukkit.command.ProxiedCommandSender} OpSender for Minecraft 1.19.
 */
public class ProxyOpSender1_19 extends ProxyOpSender1_19_common implements OpSender1_19 {
    /**
     * Creates a new {@link ProxyOpSender1_19}.
     *
     * @param p The {@link ProxiedNativeCommandSender} this {@link ProxyOpSender1_19} is wrapping.
     */
    public ProxyOpSender1_19(ProxiedNativeCommandSender p) {
        super(p);
    }

    /**
     * Creates a new {@link ProxyOpSender1_19}.
     *
     * @param p The {@link NativeProxyCommandSender} this {@link ProxyOpSender1_19} is wrapping.
     */
    public ProxyOpSender1_19(NativeProxyCommandSender p) {
        super(p);
    }
}
