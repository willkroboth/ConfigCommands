package me.willkroboth.configcommands.nms.v1_19_1.opsenders;

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
 * An interface for {@link OpSender} in Minecraft 1.19.1 and 1.19.2.
 */
public interface OpSender1_19_1 extends OpSender1_19_common {
    /**
     * Wraps a {@link CommandSender} into an {@link OpSender1_19_1}.
     *
     * @param sender The {@link CommandSender} to wrap.
     * @return An {@link OpSender1_19_1} wrapping the given {@link CommandSender}.
     */
    static OpSender1_19_1 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_19_1 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_19_1(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_19_1(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_19_1(m);
        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender1_19_1(c);
            return ConsoleOpSender1_19_1.getInstance();
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_19_1(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_19_1(p);
        return new GeneralOpSender1_19_1(sender);
    }
}
