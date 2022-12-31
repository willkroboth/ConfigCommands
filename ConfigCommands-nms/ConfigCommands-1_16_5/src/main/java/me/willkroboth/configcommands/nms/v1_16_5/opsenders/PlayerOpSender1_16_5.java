package me.willkroboth.configcommands.nms.v1_16_5.opsenders;

import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A {@link Player} OpSender for Minecraft 1.16.5.
 */
public class PlayerOpSender1_16_5 extends CraftPlayer implements OpSender1_16_5 {
    // listener created through return ((CraftPlayer)sender).getHandle().getCommandListener();
    private final Player p;

    /**
     * Creates a new {@link PlayerOpSender1_16_5}.
     *
     * @param p The {@link CraftPlayer} this {@link PlayerOpSender1_16_5} is wrapping.
     */
    public PlayerOpSender1_16_5(CraftPlayer p) {
        super((CraftServer) p.getServer(), new EntityPlayerOpWrapper(p.getHandle()));
        ((EntityPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    private static class EntityPlayerOpWrapper extends EntityPlayer {
        private PlayerOpSender1_16_5 source = null;

        public EntityPlayerOpWrapper(EntityPlayer p) {
            // parameters found by tracing down normal constructor
            // copying the playerInteractManager directly seems to set it to null after running a command
            super(p.server, (WorldServer) p.world, p.getProfile(), new PlayerInteractManager((WorldServer) p.world));
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

    @Override
    public Player.@NotNull Spigot spigot() {
        return new Player.Spigot();
    }

    // make sure certain methods work correctly
    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        return p.teleport(location, cause);
    }

    // Make sure OpSender's methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_16_5.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_16_5.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_16_5.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_16_5.super.sendMessage(uuid, strings);
    }

    @Override
    public boolean isOp() {
        return OpSender1_16_5.super.isOp();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return OpSender1_16_5.super.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return OpSender1_16_5.super.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return OpSender1_16_5.super.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return OpSender1_16_5.super.hasPermission(perm);
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
