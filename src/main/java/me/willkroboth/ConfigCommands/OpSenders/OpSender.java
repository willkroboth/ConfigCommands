package me.willkroboth.ConfigCommands.OpSenders;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftBlockCommandSender;
//import org.bukkit.craftbukkit.v1_18_R2.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.UUID;

// OpSenders should:
//  Appear as the class they represent in instanceof calls
//  Be able to run vanilla commands
//      See org.bukkit.craftbukkit.v1_18_R1.command.VanillaCommandWrapper.class.getListener() for what methods are used
//      Wrapper must represent data of entity, use `this` as source, and have permissionLevel 4
//      Parameters of CommandListenerWrapper are as follows:
//          source, worldPosition, rotation, level(Dimension?), permissionLevel, textName, displayName, server, entity
//  Not broadcast commands to console
//  Act with operator status
//  Keep a message history for returning
public interface OpSender extends CommandSender, CommandSource {
    static OpSender makeOpSender(CommandSender sender) {
        if (sender instanceof OpSender o)
            return o;
        if (sender instanceof CraftPlayer p)
            return new PlayerOpSender(p);
        if (sender instanceof CraftBlockCommandSender b)
            return new BlockOpSender(b);
        if (sender instanceof CraftMinecartCommand m)
            return new MinecartOpSender(m);
        // not working at the moment, see file
//        if (sender instanceof CraftConsoleCommandSender c)
//            return new ConsoleOpSender(c);
        if (sender instanceof ProxiedNativeCommandSender p)
            return new ProxyOpSender(p);
        if (sender instanceof NativeProxyCommandSender p)
            return new ProxyOpSender(p);
        return new GeneralOpSender(sender);
    }

    static CommandSourceStack modifyStack(CommandSourceStack source, OpSender sender) {
        return source.withSource(sender).withPermission(4);
    }

    CommandSender getSender();

    // provide result message
    String getResult();

    // store result message for CommandSender methods
    void setLastMessage(String message);

    // overriding CommandSender
    default void sendMessage(String s) {
        setLastMessage(s);
    }

    default void sendMessage(String[] strings) {
        setLastMessage(Arrays.toString(strings));
    }

    default void sendMessage(UUID uuid, String s) {
        setLastMessage(s);
    }

    default void sendMessage(UUID uuid, String[] strings) {
        setLastMessage(Arrays.toString(strings));
    }

    // overriding ServerOperator
    default boolean isOp() {
        return true;
    }

    // permissions
    default boolean isPermissionSet(String name) {
        return true;
    }

    default boolean isPermissionSet(Permission perm) {
        return true;
    }

    default boolean hasPermission(String name) {
        return true;
    }

    default boolean hasPermission(Permission perm) {
        return true;
    }

    // CommandSource methods
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
