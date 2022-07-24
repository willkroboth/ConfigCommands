package me.willkroboth.ConfigCommands.NMS.V1_17.OpSenders1_17;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftMinecartCommand;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class MinecartOpSender1_17 extends CraftMinecartCommand implements OpSender1_17 {
    // listener created through return ((CraftMinecartCommand)sender).getHandle().getCommandBlock().createCommandSourceStack();
    private final CraftMinecartCommand sender;

    public MinecartOpSender1_17(CraftMinecartCommand m) {
        super((CraftServer) m.getServer(), new MinecartCommandBlockOpWrapper(m.getHandle()));
        ((MinecartCommandBlockOpWrapper) getHandle()).setSource(this);
        sender = m;
    }

    private static class MinecartCommandBlockOpWrapper extends MinecartCommandBlock {
        private final MinecartCommandBaseOpWrapper commandBlock;

        public MinecartCommandBlockOpWrapper(MinecartCommandBlock m) {
            super(EntityType.COMMAND_BLOCK_MINECART, m.level);
            commandBlock = new MinecartCommandBaseOpWrapper();
        }

        public void setSource(MinecartOpSender1_17 source) {
            commandBlock.setSource(source);
        }

        private class MinecartCommandBaseOpWrapper extends MinecartCommandBase {
            MinecartOpSender1_17 source = null;

            public void setSource(MinecartOpSender1_17 source) {
                if (this.source == null)
                    this.source = source;
                else
                    throw new UnsupportedOperationException("Only the parent MinecartOpSender can set the source!");
            }

            public CommandSourceStack createCommandSourceStack() {
                return OpSender1_17.modifyStack(super.createCommandSourceStack(), source);
            }
        }

        public BaseCommandBlock getCommandBlock() {
            return commandBlock;
        }
    }

    public Entity.Spigot spigot() {
        return new Entity.Spigot();
    }

    // Make sure OpSender's sendMessage methods are used
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
