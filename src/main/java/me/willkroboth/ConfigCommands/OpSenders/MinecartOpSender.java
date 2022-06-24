package me.willkroboth.ConfigCommands.OpSenders;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMinecartCommand;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class MinecartOpSender extends CraftMinecartCommand implements OpSender {
    // listener created through return ((CraftMinecartCommand)sender).getHandle().getCommandBlock().createCommandSourceStack();
    // Note: this works slightly differently on paper servers because this paper patch:
    // https://github.com/PaperMC/Paper/blob/9ad94dcbc4a43a542e685bcfed7418c00fdbcc29/patches/server/0785-VanillaCommandWrapper-didnt-account-for-entity-sende.patch
    // changes the code in VanillaCommandWrapper to use this instead when creating the listener:
    // if (sender instanceof org.bukkit.craftbukkit.entity.CraftEntity craftEntity) {
    //      return craftEntity.getHandle().createCommandSourceStack();
    private final CraftMinecartCommand sender;

    public MinecartOpSender(CraftMinecartCommand m) {
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

        public void setSource(MinecartOpSender source) {
            commandBlock.setSource(source);
        }

        private class MinecartCommandBaseOpWrapper extends MinecartCommandBase {
            MinecartOpSender source = null;

            public void setSource(MinecartOpSender source) {
                if (this.source == null)
                    this.source = source;
                else
                    throw new UnsupportedOperationException("Only the parent MinecartOpSender can set the source!");
            }

            // for spigot servers
            public CommandSourceStack createCommandSourceStack() {
                return OpSender.modifyStack(super.createCommandSourceStack(), source);
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

    public Entity.Spigot spigot() {
        return new Entity.Spigot();
    }

    // Make sure OpSender's sendMessage methods are used
    public void sendMessage(String s) {
        OpSender.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender.super.sendMessage(uuid, strings);
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
