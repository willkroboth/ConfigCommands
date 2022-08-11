package me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftMinecartCommand;

import java.util.UUID;

public abstract class MinecartOpSender1_19Common extends CraftMinecartCommand implements OpSender1_19_common {
    // listener created through return ((CraftMinecartCommand)sender).getHandle().getCommandBlock().createCommandSourceStack();
    // Note: this works slightly differently on paper servers because this paper patch:
    // https://github.com/PaperMC/Paper/blob/9ad94dcbc4a43a542e685bcfed7418c00fdbcc29/patches/server/0785-VanillaCommandWrapper-didnt-account-for-entity-sende.patch
    // changes the code in VanillaCommandWrapper to use this instead when creating the listener:
    // if (sender instanceof org.bukkit.craftbukkit.entity.CraftEntity craftEntity) {
    //      return craftEntity.getHandle().createCommandSourceStack();
    private final CraftMinecartCommand sender;

    public MinecartOpSender1_19Common(CraftMinecartCommand m) {
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

        public void setSource(MinecartOpSender1_19Common source) {
            commandBlock.setSource(source);
        }

        private class MinecartCommandBaseOpWrapper extends MinecartCommandBase {
            MinecartOpSender1_19Common source = null;

            public void setSource(MinecartOpSender1_19Common source) {
                if (this.source == null)
                    this.source = source;
                else
                    throw new UnsupportedOperationException("Only the parent MinecartOpSender can set the source!");
            }

            // for spigot servers
            public CommandSourceStack createCommandSourceStack() {
                return OpSender1_19_common.modifyStack(super.createCommandSourceStack(), source);
            }
        }
        
        public BaseCommandBlock getCommandBlock() {
            return commandBlock;
        }

        // forward paper servers to same path as spigot servers
        public CommandSourceStack createCommandSourceStack() {
            return commandBlock.createCommandSourceStack();
        }
    }

    // Make sure OpSender's sendMessage methods are used
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
