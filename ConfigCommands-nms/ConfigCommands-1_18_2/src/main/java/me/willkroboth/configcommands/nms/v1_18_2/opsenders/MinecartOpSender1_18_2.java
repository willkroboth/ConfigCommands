package me.willkroboth.configcommands.nms.v1_18_2.opsenders;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMinecartCommand;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A {@link org.bukkit.entity.minecart.CommandMinecart} OpSender for Minecraft 1.18.2.
 */
public class MinecartOpSender1_18_2 extends CraftMinecartCommand implements OpSender1_18_2 {
    // listener created through return ((CraftMinecartCommand)sender).getHandle().getCommandBlock().createCommandSourceStack();
    private final CraftMinecartCommand sender;

    /**
     * Creates a new {@link MinecartOpSender1_18_2}.
     *
     * @param m The {@link CraftMinecartCommand} this {@link MinecartOpSender1_18_2} is wrapping.
     */
    public MinecartOpSender1_18_2(CraftMinecartCommand m) {
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

        public void setSource(MinecartOpSender1_18_2 source) {
            commandBlock.setSource(source);
        }

        private class MinecartCommandBaseOpWrapper extends MinecartCommandBase {
            MinecartOpSender1_18_2 source = null;

            public void setSource(MinecartOpSender1_18_2 source) {
                if (this.source == null)
                    this.source = source;
                else
                    throw new UnsupportedOperationException("Only the parent MinecartOpSender can set the source!");
            }

            public CommandSourceStack createCommandSourceStack() {
                return OpSender1_18_2.modifyStack(super.createCommandSourceStack(), source);
            }
        }

        public BaseCommandBlock getCommandBlock() {
            return commandBlock;
        }
    }

    @Override
    public Entity.@NotNull Spigot spigot() {
        return new Entity.Spigot();
    }

    // Make sure OpSender's sendMessage methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_18_2.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_18_2.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_18_2.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_18_2.super.sendMessage(uuid, strings);
    }

    // OpSender methods
    @Override
    public CommandSender getSender() {
        return sender;
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
