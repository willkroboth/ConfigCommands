package me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common;

import me.willkroboth.ConfigCommands.NMS.OpSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;

public interface OpSender1_19_common extends OpSender, CommandSource {
    static CommandSourceStack modifyStack(CommandSourceStack source, OpSender1_19_common sender) {
        return source.withSource(sender).withPermission(4);
    }

    // CommandSource methods
    default void sendSystemMessage(Component component){
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
