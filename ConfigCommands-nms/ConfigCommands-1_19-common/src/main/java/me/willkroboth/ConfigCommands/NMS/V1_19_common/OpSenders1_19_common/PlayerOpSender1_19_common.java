package me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common;

import com.mojang.authlib.GameProfile;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public abstract class PlayerOpSender1_19_common extends CraftPlayer implements OpSender1_19_common {
    // listener created through return ((CraftPlayer)sender).getHandle().createCommandSourceStack();
    private final Player p;

    public PlayerOpSender1_19_common(CraftPlayer p, ServerPlayerOpWrapper handle) {
        super((CraftServer) p.getServer(), handle);
        ((ServerPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    protected abstract static class ServerPlayerOpWrapper extends ServerPlayer {
        protected PlayerOpSender1_19_common source = null;

        public ServerPlayerOpWrapper(ServerPlayer p, GameProfile gp, ProfilePublicKey ppk) {
            // GameProfile and ProfilePublicKey need to be passed in because the methods to access them are mapped to different names
            super(p.server, (ServerLevel) p.level, gp, ppk);
        }

        public void setSource(PlayerOpSender1_19_common source) {
            if (this.source == null)
                this.source = source;
            else
                throw new UnsupportedOperationException("Only the parent PlayerOpSender can set the source!");
        }

//        // method override doesn't actually work here because it is mapped to different values
//        // override command source stack to use OpPlayer as source and permission level 4
//        public CommandSourceStack createCommandSourceStack() {
//            return OpSender1_19_common.modifyStack(super.createCommandSourceStack(), source);
//        }
    }

    // make sure certain methods work correctly
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause){
        return p.teleport(location, cause);
    }

    // Make sure OpSender's methods are used
    public void sendMessage(String s) {
        OpSender1_19_common.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender1_19_common.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender1_19_common.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19_common.super.sendMessage(uuid, strings);
    }

    public boolean isOp() {
        return OpSender1_19_common.super.isOp();
    }

    public boolean isPermissionSet(String name) {
        return OpSender1_19_common.super.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return OpSender1_19_common.super.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return OpSender1_19_common.super.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return OpSender1_19_common.super.hasPermission(perm);
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
