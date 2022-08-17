package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.ICommandListener;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftBlockCommandSender;
//import org.bukkit.craftbukkit.v1_16_R3.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

import java.util.UUID;

public interface OpSender1_16_5 extends OpSender, ICommandListener {
    static OpSender1_16_5 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_16_5 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_16_5(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_16_5(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_16_5(m);
        // not working at the moment, see file
//        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender1_16_5(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_16_5(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_16_5(p);
        return new GeneralOpSender1_16_5(sender);
    }

    static CommandListenerWrapper modifyStack(CommandListenerWrapper source, OpSender1_16_5 sender) {
        // source, worldPosition, rotation, level(Dimension?), permissionLevel, textName, displayName, server, entity
        return new CommandListenerWrapper(sender, source.getPosition(), source.i(), source.getWorld(), 4, source.getName(), source.getScoreboardDisplayName(), source.getServer(), source.getEntity());
    }

    // ICommandListener methods
    default void sendMessage(IChatBaseComponent iChatBaseComponent, UUID uuid) {
        setLastMessage(iChatBaseComponent.getString());
    }

    // send success and failure messages
    default boolean shouldSendSuccess() {
        return true;
    }

    default boolean shouldSendFailure() {
        return true;
    }

    // do not broadcast commands to console
    default boolean shouldBroadcastCommands() {
        return false;
    }

    default CommandSender getBukkitSender(CommandListenerWrapper commandSourceStack) {
        return this;
    }
}
