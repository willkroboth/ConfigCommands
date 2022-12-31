package me.willkroboth.configcommands.nms.v1_16_5.opsenders;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.configcommands.nms.OpSender;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.ICommandListener;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

import java.util.UUID;

/**
 * An interface for {@link OpSender} in Minecraft 1.16.5.
 */
public interface OpSender1_16_5 extends OpSender, ICommandListener {
    /**
     * Wraps a {@link CommandSender} into an {@link OpSender1_16_5}.
     *
     * @param sender The {@link CommandSender} to wrap.
     * @return An {@link OpSender1_16_5} wrapping the given {@link CommandSender}.
     */
    static OpSender1_16_5 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_16_5 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_16_5(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_16_5(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_16_5(m);
        if (sender instanceof CraftConsoleCommandSender c)
            return new ConsoleOpSender1_16_5(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_16_5(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_16_5(p);
        return new GeneralOpSender1_16_5(sender);
    }

    /**
     * Modifies a {@link CommandListenerWrapper} to use the given {@link OpSender1_16_5} as its
     * source and have a permission level of 4, the highest in vanilla.
     *
     * @param source The original {@link CommandListenerWrapper}.
     * @param sender The {@link OpSender1_16_5} which is the new source of new {@link CommandListenerWrapper}.
     * @return A {@link CommandListenerWrapper} with all values copied, but with the source set to the given
     * {@link OpSender1_16_5} and the permission level set to 4.
     */
    static CommandListenerWrapper modifyStack(CommandListenerWrapper source, OpSender1_16_5 sender) {
        // source, worldPosition, rotation, level(Dimension?), permissionLevel, textName, displayName, server, entity
        return new CommandListenerWrapper(sender, source.getPosition(), source.i(), source.getWorld(), 4, source.getName(), source.getScoreboardDisplayName(), source.getServer(), source.getEntity());
    }

    // ICommandListener methods
    @Override
    default void sendMessage(IChatBaseComponent iChatBaseComponent, UUID uuid) {
        setLastMessage(iChatBaseComponent.getString());
    }

    // Do not send success and failure messages
    @Override
    default boolean shouldSendSuccess() {
        return true;
    }

    @Override
    default boolean shouldSendFailure() {
        return true;
    }

    // Do not broadcast commands to console
    @Override
    default boolean shouldBroadcastCommands() {
        return false;
    }

    @Override
    default CommandSender getBukkitSender(CommandListenerWrapper commandSourceStack) {
        return this;
    }
}
