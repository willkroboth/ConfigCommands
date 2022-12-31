package me.willkroboth.configcommands.nms.v1_19_1.opsenders;

import me.willkroboth.configcommands.nms.v1_19_common.opsenders.OpSender1_19_common;
import me.willkroboth.configcommands.nms.v1_19_common.opsenders.PlayerOpSender1_19_common;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * A {@link Player} OpSender for Minecraft 1.19.1 and 1.19.2.
 */
public class PlayerOpSender1_19_1 extends PlayerOpSender1_19_common implements OpSender1_19_1 {
    /**
     * Creates a new {@link PlayerOpSender1_19_1}.
     *
     * @param p The {@link CraftPlayer} this {@link PlayerOpSender1_19_1} is wrapping.
     */
    public PlayerOpSender1_19_1(CraftPlayer p) {
        super(p, new ServerPlayerOpWrapper(p.getHandle()));
    }

    /**
     * The implementation of {@link PlayerOpSender1_19_common.ServerPlayerOpWrapper} for 1.19.1 and 1.19.2.
     */
    protected static class ServerPlayerOpWrapper extends PlayerOpSender1_19_common.ServerPlayerOpWrapper{
        /**
         * Creates a new {@link PlayerOpSender1_19_common.ServerPlayerOpWrapper} wrapping the given {@link ServerPlayer}.
         * @param p The {@link ServerPlayer} this {@link PlayerOpSender1_19_common.ServerPlayerOpWrapper} is wrapping.
         */
        public ServerPlayerOpWrapper(ServerPlayer p) {
            // getGameProfile() is mapped to fy()
            // getProfilePublicKey() is mapped to fz()
            super(p, p.getGameProfile(), p.getProfilePublicKey());
        }

        // createCommandSourceStack() is mapped to cT
        // override command source stack to use OpPlayer as source and permission level 4
        @Override
        public CommandSourceStack createCommandSourceStack() {
            return OpSender1_19_common.modifyStack(super.createCommandSourceStack(), source);
        }
    }
}
