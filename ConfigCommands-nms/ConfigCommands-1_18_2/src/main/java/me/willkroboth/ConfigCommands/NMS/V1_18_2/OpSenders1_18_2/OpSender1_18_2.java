package me.willkroboth.ConfigCommands.NMS.V1_18_2.OpSenders1_18_2;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftBlockCommandSender;
//import org.bukkit.craftbukkit.v1_18_R2.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;

import java.util.UUID;

public interface OpSender1_18_2 extends OpSender, CommandSource {
    static OpSender makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_18_2 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_18_2(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_18_2(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_18_2(m);
        // not working at the moment, see file
//        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender1_18_2(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_18_2(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_18_2(p);
        return new GeneralOpSender1_18_2(sender);
    }

    static CommandSourceStack modifyStack(CommandSourceStack source, OpSender1_18_2 sender) {
        return source.withSource(sender).withPermission(4);
    }

    // ICommandListener methods
    default void sendMessage(Component component, UUID uuid) {
        setLastMessage(component.getString());
    }

    // send success and failure messages
    default boolean acceptsSuccess() {
        return true;
    }

    default boolean acceptsFailure() {
        return true;
    }

    // do not broadcast commands to console
    default boolean shouldInformAdmins() {
        return false;
    }

    default CommandSender getBukkitSender(CommandSourceStack commandSourceStack) {
        return this;
    }
}
