package me.willkroboth.ConfigCommands.NMS.V1_19_1.OpSenders1_19_1;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

public interface OpSender1_19_1 extends OpSender1_19_common {
    static OpSender1_19_1 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_19_1 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_19_1(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_19_1(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_19_1(m);
//        // not working at the moment, see file
//        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender1_19_1(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_19_1(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_19_1(p);
        return new GeneralOpSender1_19_1(sender);
    }
}
