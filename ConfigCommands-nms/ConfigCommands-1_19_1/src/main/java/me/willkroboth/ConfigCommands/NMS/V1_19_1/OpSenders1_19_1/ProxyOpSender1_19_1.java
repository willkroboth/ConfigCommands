package me.willkroboth.ConfigCommands.NMS.V1_19_1.OpSenders1_19_1;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.ProxyOpSender1_19_common;
import org.bukkit.craftbukkit.v1_19_R1.command.ProxiedNativeCommandSender;

/**
 * A {@link org.bukkit.command.ProxiedCommandSender} OpSender for Minecraft 1.19.1 and 1.19.2.
 */
public class ProxyOpSender1_19_1 extends ProxyOpSender1_19_common implements OpSender1_19_1 {
    /**
     * Creates a new {@link ProxyOpSender1_19_1}.
     *
     * @param p The {@link ProxiedNativeCommandSender} this {@link ProxyOpSender1_19_1} is wrapping.
     */
    public ProxyOpSender1_19_1(ProxiedNativeCommandSender p) {
        super(p);
    }

    /**
     * Creates a new {@link ProxyOpSender1_19_1}.
     *
     * @param p The {@link NativeProxyCommandSender} this {@link ProxyOpSender1_19_1} is wrapping.
     */
    public ProxyOpSender1_19_1(NativeProxyCommandSender p) {
        super(p);
    }
}
