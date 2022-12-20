package me.willkroboth.ConfigCommands.NMS.V1_17.OpSenders1_17;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;

import java.util.UUID;

public interface OpSender1_17 extends OpSender, CommandSource {
    static OpSender1_17 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_17 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_17(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_17(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_17(m);
        if (sender instanceof CraftConsoleCommandSender c)
            return new ConsoleOpSender1_17(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_17(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_17(p);
        return new GeneralOpSender1_17(sender);
    }

    static CommandSourceStack modifyStack(CommandSourceStack source, OpSender1_17 sender) {
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
