package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;
//import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

public interface OpSender1_19 extends OpSender1_19_common {
    static OpSender1_19 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_19 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_19(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_19(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_19(m);
//        // not working at the moment, see file
//        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender1_19(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_19(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_19(p);
        return new GeneralOpSender1_19(sender);
    }
}
