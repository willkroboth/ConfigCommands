package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class PlayerOpSender1_16_5 extends CraftPlayer implements OpSender1_16_5 {
    // listener created through return ((CraftPlayer)sender).getHandle().getCommandListener();
    private final Player p;

    public PlayerOpSender1_16_5(CraftPlayer p) {
        super((CraftServer) p.getServer(), new EntityPlayerOpWrapper(p.getHandle()));
        ((EntityPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    private static class EntityPlayerOpWrapper extends EntityPlayer {
        private PlayerOpSender1_16_5 source = null;

        public EntityPlayerOpWrapper(EntityPlayer p) {
            // parameters found by tracing down normal constructor
            super(p.server, (WorldServer) p.world, p.getProfile(), p.playerInteractManager);
        }

        public void setSource(PlayerOpSender1_16_5 source) {
            if (this.source == null)
                this.source = source;
            else
                throw new UnsupportedOperationException("Only the parent PlayerOpSender can set the source!");
        }

        public CommandListenerWrapper getCommandListener() {
            return OpSender1_16_5.modifyStack(super.getCommandListener(), source);
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
