package me.willkroboth.configcommands.nms.v1_19_common.opsenders;

import me.willkroboth.configcommands.nms.OpSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;

/**
 * An common interface for {@link OpSender} in Minecraft 1.19, 1.19.1, and 1.19.2.
 */
public interface OpSender1_19_common extends OpSender, CommandSource {
    /**
     * Modifies a {@link CommandSourceStack} to use the given {@link OpSender1_19_common} as its
     * source and have a permission level of 4, the highest in vanilla.
     *
     * @param source The original {@link CommandSourceStack}.
     * @param sender The {@link OpSender1_19_common} which is the new source of new {@link CommandSourceStack}.
     * @return A {@link CommandSourceStack} with all values copied, but with the source set to the given
     * {@link OpSender1_19_common} and the permission level set to 4.
     */
    static CommandSourceStack modifyStack(CommandSourceStack source, OpSender1_19_common sender) {
        return source.withSource(sender).withPermission(4);
    }

    // CommandSource methods
    @Override
    default void sendSystemMessage(Component component){
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
