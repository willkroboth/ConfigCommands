package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.OpSender1_19_common;
import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.PlayerOpSender1_19_common;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;

public class PlayerOpSender1_19 extends PlayerOpSender1_19_common implements OpSender1_19 {
    public PlayerOpSender1_19(CraftPlayer p) {
        super(p, new ServerPlayerOpWrapper(p.getHandle()));
    }

    protected static class ServerPlayerOpWrapper extends PlayerOpSender1_19_common.ServerPlayerOpWrapper{
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
