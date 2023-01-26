package me.willkroboth.configcommands.nms.v1_19_3.opsenders;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.configcommands.nms.OpSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R2.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_19_R2.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_19_R2.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;

/**
 * An interface for {@link OpSender} in Minecraft 1.19.3.
 */
public interface OpSender1_19_3 extends OpSender, CommandSource {
    /**
     * Wraps a {@link CommandSender} into an {@link OpSender1_19_3}.
     *
     * @param sender The {@link CommandSender} to wrap.
     * @return An {@link OpSender1_19_3} wrapping the given {@link CommandSender}.
     */
    static OpSender1_19_3 makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender1_19_3 o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender1_19_3(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender1_19_3(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender1_19_3(m);
        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender1_19_3(c);
            return ConsoleOpSender1_19_3.getInstance();
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender1_19_3(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender1_19_3(p);
        return new GeneralOpSender1_19_3(sender);
    }


    /**
     * Modifies a {@link CommandSourceStack} to use the given {@link OpSender1_19_3} as its
     * source and have a permission level of 4, the highest in vanilla.
     *
     * @param source The original {@link CommandSourceStack}.
     * @param sender The {@link OpSender1_19_3} which is the new source of new {@link CommandSourceStack}.
     * @return A {@link CommandSourceStack} with all values copied, but with the source set to the given
     * {@link OpSender1_19_3} and the permission level set to 4.
     */
    static CommandSourceStack modifyStack(CommandSourceStack source, OpSender1_19_3 sender) {
        return source.withSource(sender).withPermission(4);
    }

    // CommandSource methods
    @Override
    default void sendSystemMessage(Component component) {
        setLastMessage(component.getString());
    }

    // send success and failure messages
    @Override
    default boolean acceptsSuccess() {
        return true;
    }

    @Override
    default boolean acceptsFailure() {
        return true;
    }

    // do not broadcast commands to console
    @Override
    default boolean shouldInformAdmins() {
        return false;
    }

    @Override
    default CommandSender getBukkitSender(CommandSourceStack commandSourceStack) {
        return this;
    }
}
