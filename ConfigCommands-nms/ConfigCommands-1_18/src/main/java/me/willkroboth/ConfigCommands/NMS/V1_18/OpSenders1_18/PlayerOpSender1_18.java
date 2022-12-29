package me.willkroboth.ConfigCommands.NMS.V1_18.OpSenders1_18;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A {@link Player} OpSender for Minecraft 1.18 and 1.18.1.
 */
public class PlayerOpSender1_18 extends CraftPlayer implements OpSender1_18 {
    // listener created through return ((CraftPlayer)sender).getHandle().createCommandSourceStack();
    private final Player p;

    /**
     * Creates a new {@link PlayerOpSender1_18}.
     *
     * @param p The {@link CraftPlayer} this {@link PlayerOpSender1_18} is wrapping.
     */
    public PlayerOpSender1_18(CraftPlayer p) {
        super((CraftServer) p.getServer(), new ServerPlayerOpWrapper(p.getHandle()));
        ((ServerPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    private static class ServerPlayerOpWrapper extends ServerPlayer {
        private PlayerOpSender1_18 source = null;

        public ServerPlayerOpWrapper(ServerPlayer p) {
            // parameters found by tracing down normal constructor
            super(p.server, (ServerLevel) p.level, p.getGameProfile());
        }

        public void setSource(PlayerOpSender1_18 source) {
            if (this.source == null)
                this.source = source;
            else
                throw new UnsupportedOperationException("Only the parent PlayerOpSender can set the source!");
        }

        @Override
        public CommandSourceStack createCommandSourceStack() {
            return OpSender1_18.modifyStack(super.createCommandSourceStack(), source);
        }
    }

    @Override
    public Player.@NotNull Spigot spigot() {
        return new Player.Spigot();
    }

    // make sure certain methods work correctly
    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause){
        return p.teleport(location, cause);
    }

    // Make sure OpSender's methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_18.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_18.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_18.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_18.super.sendMessage(uuid, strings);
    }

    @Override
    public boolean isOp() {
        return OpSender1_18.super.isOp();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return OpSender1_18.super.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return OpSender1_18.super.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return OpSender1_18.super.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return OpSender1_18.super.hasPermission(perm);
    }

    // OpSender methods
    @Override
    public CommandSender getSender() {
        return p;
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
