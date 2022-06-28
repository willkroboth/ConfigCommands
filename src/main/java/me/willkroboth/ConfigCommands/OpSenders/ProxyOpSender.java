package me.willkroboth.ConfigCommands.OpSenders;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.command.ProxiedNativeCommandSender;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class ProxyOpSender extends ProxiedNativeCommandSender implements OpSender {
    private final ProxiedNativeCommandSender sender;
    public ProxyOpSender(ProxiedNativeCommandSender p) {
        super(p.getHandle(), p.getCaller(), p.getCallee());
        sender = p;
    }

    public ProxyOpSender(NativeProxyCommandSender p) {
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
                new TextComponent(name), world.getServer(), callee instanceof Entity e ? e : null);
    }

    public CommandSourceStack getHandle() {
        return OpSender.modifyStack(super.getHandle(), this);
    }

    public Spigot spigot() {
        return new Spigot();
    }

    // Make sure OpSender's methods are used
    public void sendMessage(String s) {
        OpSender.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender.super.sendMessage(uuid, strings);
    }

    public boolean isOp() {
        return OpSender.super.isOp();
    }

    public boolean isPermissionSet(String name) {
        return OpSender.super.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return OpSender.super.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return OpSender.super.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return OpSender.super.hasPermission(perm);
    }

    // OpSender methods
    public CommandSender getSender() {
        return sender;
    }

    private String lastMessage = "";

    public String getResult() {
        return lastMessage;
    }

    public void setLastMessage(String message) {
        lastMessage = message;
    }
}
