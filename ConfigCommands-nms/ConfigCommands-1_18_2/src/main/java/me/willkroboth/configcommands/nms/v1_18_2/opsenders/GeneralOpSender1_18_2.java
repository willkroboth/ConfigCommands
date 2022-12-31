package me.willkroboth.configcommands.nms.v1_18_2.opsenders;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R2.block.impl.CraftCommand;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftBlockCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An OpSender that works for any {@link CommandSender} on Minecraft 1.18.2.
 */
public class GeneralOpSender1_18_2 extends CraftBlockCommandSender implements OpSender1_18_2 {
    private final CommandSender sender;
    private final CommandSourceStack listener;
    private final CraftBlock fakeBlock;

    /**
     * Creates a new {@link GeneralOpSender1_18_2}.
     *
     * @param sender The {@link CommandSender} this {@link GeneralOpSender1_18_2} is wrapping.
     */
    public GeneralOpSender1_18_2(CommandSender sender) {
        super(null, null);

        this.sender = sender;
        this.listener = buildListener(sender);
        fakeBlock = new FakeCraftBlock();
    }

    private CommandSourceStack buildListener(CommandSender sender) {
        CommandSourceStack stack;
        if (sender instanceof Entity entity) {
            Location location = entity.getBukkitEntity().getLocation();

            Vec3 position = new Vec3(location.getX(), location.getY(), location.getZ());
            Vec2 rotation = new Vec2(location.getYaw(), location.getPitch());
            ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
            stack = new CommandSourceStack(this, position, rotation, world, 4,
                    entity.getScoreboardName(), entity.getCustomName(), world.getServer(), entity);
        } else {
            ServerLevel world = ((CraftWorld) sender.getServer().getWorlds().get(0)).getHandle();
            String name = sender.getName();
            stack = new CommandSourceStack(this, Vec3.ZERO, Vec2.ZERO, world, 4,
                    name, new TextComponent(name), world.getServer(), null);
        }
        return OpSender1_18_2.modifyStack(stack, this);
    }

    // Make sure no errors happen because this looks like a command block
    private static class FakeCraftBlock extends CraftBlock{
        public FakeCraftBlock(){
            super(((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle(), BlockPos.ZERO);
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

    // Overriding getWrapper() to provide the custom CommandSourceStack
    @Override
    public CommandSourceStack getWrapper() {
        return listener;
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
