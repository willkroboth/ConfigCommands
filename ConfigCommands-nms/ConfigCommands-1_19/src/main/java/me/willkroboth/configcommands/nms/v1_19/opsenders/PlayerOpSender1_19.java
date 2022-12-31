package me.willkroboth.configcommands.nms.v1_19.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.OpSender1_19_common;
import me.willkroboth.configcommands.nms.v1_19_common.opsenders.PlayerOpSender1_19_common;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * A {@link Player} OpSender for Minecraft 1.19.
 */
public class PlayerOpSender1_19 extends PlayerOpSender1_19_common implements OpSender1_19 {
    /**
     * Creates a new {@link PlayerOpSender1_19}.
     *
     * @param p The {@link CraftPlayer} this {@link PlayerOpSender1_19} is wrapping.
     */
    public PlayerOpSender1_19(CraftPlayer p) {
        super(p, new ServerPlayerOpWrapper(p.getHandle()));
    }

    /**
     * The implementation of {@link PlayerOpSender1_19_common.ServerPlayerOpWrapper} for 1.19.
     */
    protected static class ServerPlayerOpWrapper extends PlayerOpSender1_19_common.ServerPlayerOpWrapper{
        /**
         * Creates a new {@link PlayerOpSender1_19_common.ServerPlayerOpWrapper} wrapping the given {@link ServerPlayer}.
         * @param p The {@link ServerPlayer} this {@link PlayerOpSender1_19_common.ServerPlayerOpWrapper} is wrapping.
         */
        public ServerPlayerOpWrapper(ServerPlayer p) {
            // getGameProfile() is mapped to fz()
            // getProfilePublicKey() is mapped to fA()
            super(p, p.getGameProfile(), p.getProfilePublicKey());
        }

        // createCommandSourceStack() is mapped to cU
        // override command source stack to use OpPlayer as source and permission level 4
        @Override
        public CommandSourceStack createCommandSourceStack() {
            return OpSender1_19_common.modifyStack(super.createCommandSourceStack(), source);
        }
    }
}
