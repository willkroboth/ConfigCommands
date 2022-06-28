package me.willkroboth.ConfigCommands.OpSenders;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class PlayerOpSender extends CraftPlayer implements OpSender {
    // listener created through return ((CraftPlayer)sender).getHandle().createCommandSourceStack();
    private final Player p;

    public PlayerOpSender(CraftPlayer p) {
        super((CraftServer) p.getServer(), new ServerPlayerOpWrapper(p.getHandle()));
        ((ServerPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    private static class ServerPlayerOpWrapper extends ServerPlayer {
        private PlayerOpSender source = null;

        public ServerPlayerOpWrapper(ServerPlayer p) {
            // parameters found by tracing down normal constructor
            super(p.server, (ServerLevel) p.level, p.getGameProfile());
        }

        public void setSource(PlayerOpSender source) {
            if (this.source == null)
                this.source = source;
            else
                throw new UnsupportedOperationException("Only the parent PlayerOpSender can set the source!");
        }

        // override command source stack to use OpPlayer as source and permission level 4
        public CommandSourceStack createCommandSourceStack() {
            return OpSender.modifyStack(super.createCommandSourceStack(), source);
        }
    }

    public Player.Spigot spigot() {
        return new Player.Spigot();
    }

    // make sure certain methods work correctly
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause){
        return p.teleport(location, cause);
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
        return p;
    }

    private String lastMessage = "";

    public String getResult() {
        return lastMessage;
    }

    public void setLastMessage(String message) {
        lastMessage = message;
    }
}
