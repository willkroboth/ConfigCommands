package me.willkroboth.configcommands.nms.v1_16_5.opsenders;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.block.impl.CraftCommand;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftBlockCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An OpSender that works for any {@link CommandSender} on Minecraft 1.16.5.
 */
public class GeneralOpSender1_16_5 extends CraftBlockCommandSender implements OpSender1_16_5 {
    private final CommandSender sender;
    private final CommandListenerWrapper listener;
    private final CraftBlock fakeBlock;

    /**
     * Creates a new {@link GeneralOpSender1_16_5}.
     *
     * @param sender The {@link CommandSender} this {@link GeneralOpSender1_16_5} is wrapping.
     */
    public GeneralOpSender1_16_5(CommandSender sender) {
        super(null, null);

        this.sender = sender;
        this.listener = buildListener(sender);
        fakeBlock = new FakeCraftBlock();
    }

    private CommandListenerWrapper buildListener(CommandSender sender) {
        CommandListenerWrapper stack;
        if (sender instanceof Entity entity) {
            Location location = entity.getBukkitEntity().getLocation();

            Vec3D position = new Vec3D(location.getX(), location.getY(), location.getZ());
            Vec2F rotation = new Vec2F(location.getYaw(), location.getPitch());
            WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
            stack = new CommandListenerWrapper(this, position, rotation, world, 4,
                    entity.getName(), entity.getCustomName(), world.getServer().getServer(), entity);
        } else {
            WorldServer world = ((CraftWorld) sender.getServer().getWorlds().get(0)).getHandle();
            String name = sender.getName();
            stack = new CommandListenerWrapper(this, Vec3D.ORIGIN, Vec2F.a, world, 4,
                    name, new ChatComponentText(name), world.getServer().getServer(), null);
        }
        return OpSender1_16_5.modifyStack(stack, this);
    }

    // Make sure no errors happen because this looks like a command block
    private static class FakeCraftBlock extends CraftBlock {
        public FakeCraftBlock() {
            super(((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle(), BlockPosition.ZERO);
        }

        public @NotNull BlockData getBlockData() {
            return new CraftCommand(null);
        }
    }

    @Override
    public @NotNull Block getBlock() {
        return fakeBlock;
    }

    @Override
    public @NotNull String getName() {
        return sender.getName();
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
    }

    // Overriding getWrapper() to provide the custom CommandListenerWrapper
    @Override
    public CommandListenerWrapper getWrapper() {
        return listener;
    }

    // Make sure OpSender's sendMessage methods are used
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
