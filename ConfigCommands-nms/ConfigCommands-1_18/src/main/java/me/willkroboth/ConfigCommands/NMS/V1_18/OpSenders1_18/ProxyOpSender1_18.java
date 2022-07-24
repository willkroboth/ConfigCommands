package me.willkroboth.ConfigCommands.NMS.V1_18.OpSenders1_18;

import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.command.ProxiedNativeCommandSender;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class ProxyOpSender1_18 extends ProxiedNativeCommandSender implements OpSender1_18 {
    private final ProxiedNativeCommandSender sender;
    public ProxyOpSender1_18(ProxiedNativeCommandSender p) {
        super(p.getHandle(), p.getCaller(), p.getCallee());
        sender = p;
    }

    public ProxyOpSender1_18(NativeProxyCommandSender p) {
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
        return OpSender1_18.modifyStack(super.getHandle(), this);
    }

    public Spigot spigot() {
        return new Spigot();
    }

    // Make sure OpSender's methods are used
    public void sendMessage(String s) {
        OpSender1_18.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender1_18.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender1_18.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_18.super.sendMessage(uuid, strings);
    }

    public boolean isOp() {
        return OpSender1_18.super.isOp();
    }

    public boolean isPermissionSet(String name) {
        return OpSender1_18.super.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return OpSender1_18.super.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return OpSender1_18.super.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return OpSender1_18.super.hasPermission(perm);
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
