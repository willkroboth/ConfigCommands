package me.willkroboth.configcommands.nms.v1_19.opsenders;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.configcommands.nms.OpSender;
import me.willkroboth.configcommands.nms.v1_19_common.opsenders.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

/**
 * An interface for {@link OpSender} in Minecraft 1.19.
 */
public interface OpSender1_19 extends OpSender1_19_common {
    /**
     * Wraps a {@link CommandSender} into an {@link OpSender1_19}.
     *
     * @param sender The {@link CommandSender} to wrap.
     * @return An {@link OpSender1_19} wrapping the given {@link CommandSender}.
     */
    static OpSender1_19 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_19 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_19(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_19(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_19(m);
        if (sender instanceof CraftConsoleCommandSender c)
            return new ConsoleOpSender1_19(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_19(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_19(p);
        return new GeneralOpSender1_19(sender);
    }
}
