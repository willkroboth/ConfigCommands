package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

import net.minecraft.server.v1_16_R3.CommandBlockListenerAbstract;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.EntityMinecartCommandBlock;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMinecartCommand;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class MinecartOpSender1_16_5 extends CraftMinecartCommand implements OpSender1_16_5 {
    // listener created through return ((CraftMinecartCommand)sender).getHandle().getCommandBlock().getWrapper();
    private final CraftMinecartCommand sender;

    public MinecartOpSender1_16_5(CraftMinecartCommand m) {
        super((CraftServer) m.getServer(), new MinecartCommandBlockOpWrapper(m.getHandle()));
        ((MinecartCommandBlockOpWrapper) getHandle()).setSource(this);
        sender = m;
    }

    private static class MinecartCommandBlockOpWrapper extends EntityMinecartCommandBlock {
        private final MinecartCommandBaseOpWrapper commandBlock;

        public MinecartCommandBlockOpWrapper(EntityMinecartCommandBlock m) {
            super(EntityTypes.COMMAND_BLOCK_MINECART, m.world);
            commandBlock = new MinecartCommandBaseOpWrapper();
        }

        public void setSource(MinecartOpSender1_16_5 source) {
            commandBlock.setSource(source);
        }

        private class MinecartCommandBaseOpWrapper extends a {
            MinecartOpSender1_16_5 source = null;

            public void setSource(MinecartOpSender1_16_5 source) {
                if (this.source == null)
                    this.source = source;
                else
                    throw new UnsupportedOperationException("Only the parent MinecartOpSender can set the source!");
            }

            public CommandListenerWrapper getWrapper() {
                return OpSender1_16_5.modifyStack(super.getWrapper(), source);
            }
        }

        public CommandBlockListenerAbstract getCommandBlock() {
            return commandBlock;
        }
    }

    public Entity.Spigot spigot() {
        return new Entity.Spigot();
    }

    // Make sure OpSender's sendMessage methods are used
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

    // OpSender methods
    public CommandSender getSender() {
        return sender;
    }

    private String lastMessage = "";

    public String getResult() {
        return lastMessage;
    }

    public void setLastMessage(String message) {
        lastMessage = message;
    }
}
