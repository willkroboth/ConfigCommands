package me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.command.ProxiedNativeCommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A common {@link org.bukkit.command.ProxiedCommandSender} OpSender for Minecraft 1.19, 1.19.1, and 1.19.2.
 */
public abstract class ProxyOpSender1_19_common extends ProxiedNativeCommandSender implements OpSender1_19_common {
    private final ProxiedNativeCommandSender sender;

    /**
     * Creates a new {@link ProxyOpSender1_19_common}.
     *
     * @param p The {@link ProxiedNativeCommandSender} this {@link ProxyOpSender1_19_common} is wrapping.
     */
    public ProxyOpSender1_19_common(ProxiedNativeCommandSender p) {
        super(p.getHandle(), p.getCaller(), p.getCallee());
        sender = p;
    }

    /**
     * Creates a new {@link ProxyOpSender1_19_common}.
     *
     * @param p The {@link NativeProxyCommandSender} this {@link ProxyOpSender1_19_common} is wrapping.
     */
    public ProxyOpSender1_19_common(NativeProxyCommandSender p) {
        super(buildStack(p), p.getCaller(), p.getCallee());
        sender = new ProxiedNativeCommandSender(getHandle(), getCaller(), getCallee());
    }

    private static CommandSourceStack buildStack(NativeProxyCommandSender proxy){
        Location location = proxy.getLocation();
        Vec3 position = new Vec3(location.getX(), location.getY(), location.getZ());
        Vec2 rotation = new Vec2(location.getYaw(), location.getPitch());

        ServerLevel world = ((CraftWorld) proxy.getWorld()).getHandle();
        String name = proxy.getName();
        CommandSender callee = proxy.getCallee();
        return new CommandSourceStack(null, position, rotation, world, 4, name,
                MutableComponent.create(new LiteralContents(name)), world.getServer(), callee instanceof Entity e ? e : null);
    }

    @Override
    public CommandSourceStack getHandle() {
        return OpSender1_19_common.modifyStack(super.getHandle(), this);
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
    }

    // Make sure OpSender's methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_19_common.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_19_common.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_19_common.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19_common.super.sendMessage(uuid, strings);
    }

    @Override
    public boolean isOp() {
        return OpSender1_19_common.super.isOp();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return OpSender1_19_common.super.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return OpSender1_19_common.super.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return OpSender1_19_common.super.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return OpSender1_19_common.super.hasPermission(perm);
    }

    // OpSender methods
    @Override
    public CommandSender getSender() {
        return sender;
    }

    private String lastMessage = "";

    @Override
    public String getResult() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(String message) {
        lastMessage = message;
    }
}
