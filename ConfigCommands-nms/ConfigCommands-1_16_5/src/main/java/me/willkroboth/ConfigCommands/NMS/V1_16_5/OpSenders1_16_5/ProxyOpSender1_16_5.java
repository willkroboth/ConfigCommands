package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.command.ProxiedNativeCommandSender;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class ProxyOpSender1_16_5 extends ProxiedNativeCommandSender implements OpSender1_16_5 {
    private final ProxiedNativeCommandSender sender;
    public ProxyOpSender1_16_5(ProxiedNativeCommandSender p) {
        super(p.getHandle(), p.getCaller(), p.getCallee());
        sender = p;
    }

    public ProxyOpSender1_16_5(NativeProxyCommandSender p) {
        super(buildStack(p), p.getCaller(), p.getCallee());
        sender = new ProxiedNativeCommandSender(getHandle(), getCaller(), getCallee());
    }

    private static CommandListenerWrapper buildStack(NativeProxyCommandSender proxy){
        Location location = proxy.getLocation();
        Vec3D position = new Vec3D(location.getX(), location.getY(), location.getZ());
        Vec2F rotation = new Vec2F(location.getYaw(), location.getPitch());

        WorldServer world = ((CraftWorld) proxy.getWorld()).getHandle();
        String name = proxy.getName();
        CommandSender callee = proxy.getCallee();
        return new CommandListenerWrapper(null, position, rotation, world, 4, name,
                new ChatComponentText(name), world.getServer().getServer(), callee instanceof Entity e ? e : null);
    }

    public CommandListenerWrapper getHandle() {
        return OpSender1_16_5.modifyStack(super.getHandle(), this);
    }

    public Spigot spigot() {
        return new Spigot();
    }

    // Make sure OpSender's methods are used
    public void sendMessage(String s) {
        OpSender1_16_5.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender1_16_5.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender1_16_5.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_16_5.super.sendMessage(uuid, strings);
    }

    public boolean isOp() {
        return OpSender1_16_5.super.isOp();
    }

    public boolean isPermissionSet(String name) {
        return OpSender1_16_5.super.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return OpSender1_16_5.super.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return OpSender1_16_5.super.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return OpSender1_16_5.super.hasPermission(perm);
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
