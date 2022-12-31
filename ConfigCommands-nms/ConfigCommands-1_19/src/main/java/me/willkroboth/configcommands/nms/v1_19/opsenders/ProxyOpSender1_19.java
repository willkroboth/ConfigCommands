package me.willkroboth.configcommands.nms.v1_19.opsenders;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.configcommands.nms.v1_19_common.opsenders.ProxyOpSender1_19_common;
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
