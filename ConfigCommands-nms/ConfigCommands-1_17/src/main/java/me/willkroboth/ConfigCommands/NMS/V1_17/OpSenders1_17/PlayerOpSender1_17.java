package me.willkroboth.ConfigCommands.NMS.V1_17.OpSenders1_17;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class PlayerOpSender1_17 extends CraftPlayer implements OpSender1_17 {
    // listener created through return ((CraftPlayer)sender).getHandle().createCommandSourceStack();
    private final Player p;

    public PlayerOpSender1_17(CraftPlayer p) {
        super((CraftServer) p.getServer(), new ServerPlayerOpWrapper(p.getHandle()));
        ((ServerPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    private static class ServerPlayerOpWrapper extends ServerPlayer {
        private PlayerOpSender1_17 source = null;

        public ServerPlayerOpWrapper(ServerPlayer p) {
            // parameters found by tracing down normal constructor
            super(p.server, (ServerLevel) p.level, p.getGameProfile());
        }

        public void setSource(PlayerOpSender1_17 source) {
            if (this.source == null)
                this.source = source;
            else
                throw new UnsupportedOperationException("Only the parent PlayerOpSender can set the source!");
        }

        @Override
        public CommandSourceStack createCommandSourceStack() {
            return OpSender1_17.modifyStack(super.createCommandSourceStack(), source);
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
        OpSender1_17.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender1_17.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender1_17.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_17.super.sendMessage(uuid, strings);
    }

    public boolean isOp() {
        return OpSender1_17.super.isOp();
    }

    public boolean isPermissionSet(String name) {
        return OpSender1_17.super.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return OpSender1_17.super.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return OpSender1_17.super.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return OpSender1_17.super.hasPermission(perm);
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
