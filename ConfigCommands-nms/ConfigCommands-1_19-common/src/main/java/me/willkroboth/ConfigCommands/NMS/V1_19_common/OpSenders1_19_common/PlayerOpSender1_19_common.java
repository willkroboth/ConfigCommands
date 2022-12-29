package me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common;

import com.mojang.authlib.GameProfile;
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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A common {@link Player} OpSender for Minecraft 1.19, 1.19.1, and 1.19.2.
 */
public abstract class PlayerOpSender1_19_common extends CraftPlayer implements OpSender1_19_common {
    // listener created through return ((CraftPlayer)sender).getHandle().createCommandSourceStack();
    private final Player p;

    /**
     * Creates a new {@link PlayerOpSender1_19_common}.
     *
     * @param p The {@link CraftPlayer} this {@link PlayerOpSender1_19_common} is wrapping.
     * @param handle The {@link ServerPlayerOpWrapper} that is the entity handle for this {@link CraftPlayer}.
     */
    public PlayerOpSender1_19_common(CraftPlayer p, ServerPlayerOpWrapper handle) {
        super((CraftServer) p.getServer(), handle);
        ((ServerPlayerOpWrapper) getHandle()).setSource(this);
        this.p = p;
    }

    /**
     * A wrapper for a {@link ServerPlayer} that creates an {@link me.willkroboth.ConfigCommands.NMS.OpSender}
     * appropriate {@link net.minecraft.commands.CommandSourceStack}. This class is not applicable to all
     * target versions because of mapping nonsense.
     */
    protected abstract static class ServerPlayerOpWrapper extends ServerPlayer {
        /**
         * The {@link PlayerOpSender1_19_common} who is the source of the {@link net.minecraft.commands.CommandSourceStack}
         * this {@link ServerPlayerOpWrapper} should return.
         */
        protected PlayerOpSender1_19_common source = null;

        /**
         * Creates a new {@link ServerPlayerOpWrapper} wrapping the given {@link ServerPlayer}.
         *
         * @param p The {@link ServerPlayer} this {@link ServerPlayerOpWrapper} is wrapping.
         * @param gp The {@link GameProfile} of the given {@link ServerPlayer}. This cannot be accessed here because
         *           the relevant method is mapped to different names in the target versions.
         * @param ppk The {@link ProfilePublicKey} of the given {@link ServerPlayer}. This cannot be accessed here
         *            because the relevant method is mapped to different names in the target versions.
         */
        public ServerPlayerOpWrapper(ServerPlayer p, GameProfile gp, ProfilePublicKey ppk) {
            // GameProfile and ProfilePublicKey need to be passed in because the methods to access them are mapped to different names
            super(p.server, (ServerLevel) p.level, gp, ppk);
        }

        /**
         * Sets the source of this {@link ServerPlayerOpWrapper}.
         *
         * @param source The {@link PlayerOpSender1_19_common} that is using this {@link ServerPlayerOpWrapper}.
         */
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
        OpSender1_19_common.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_19_common.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_19_common.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19_common.super.sendMessage(uuid, strings);
    }

    @Override
    public boolean isOp() {
        return OpSender1_19_common.super.isOp();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return OpSender1_19_common.super.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return OpSender1_19_common.super.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return OpSender1_19_common.super.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return OpSender1_19_common.super.hasPermission(perm);
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
