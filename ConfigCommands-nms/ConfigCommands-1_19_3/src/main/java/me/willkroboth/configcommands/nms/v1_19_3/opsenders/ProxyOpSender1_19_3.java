package me.willkroboth.configcommands.nms.v1_19_3.opsenders;

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
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.command.ProxiedNativeCommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A {@link org.bukkit.command.ProxiedCommandSender} OpSender for Minecraft 1.19.3.
 */
public class ProxyOpSender1_19_3 extends ProxiedNativeCommandSender implements OpSender1_19_3 {
    private final ProxiedNativeCommandSender sender;

    /**
     * Creates a new {@link ProxyOpSender1_19_3}.
     *
     * @param p The {@link ProxiedNativeCommandSender} this {@link ProxyOpSender1_19_3} is wrapping.
     */
    public ProxyOpSender1_19_3(ProxiedNativeCommandSender p) {
        super(p.getHandle(), p.getCaller(), p.getCallee());
        sender = p;
    }

    /**
     * Creates a new {@link ProxyOpSender1_19_3}.
     *
     * @param p The {@link NativeProxyCommandSender} this {@link ProxyOpSender1_19_3} is wrapping.
     */
    public ProxyOpSender1_19_3(NativeProxyCommandSender p) {
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
        return OpSender1_19_3.modifyStack(super.getHandle(), this);
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
    }

    // Make sure OpSender's methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_19_3.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_19_3.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_19_3.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19_3.super.sendMessage(uuid, strings);
    }

    @Override
    public boolean isOp() {
        return OpSender1_19_3.super.isOp();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return OpSender1_19_3.super.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return OpSender1_19_3.super.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return OpSender1_19_3.super.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return OpSender1_19_3.super.hasPermission(perm);
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
