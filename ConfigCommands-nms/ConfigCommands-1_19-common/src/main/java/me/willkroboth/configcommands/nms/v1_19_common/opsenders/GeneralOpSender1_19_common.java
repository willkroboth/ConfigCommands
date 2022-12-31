package me.willkroboth.configcommands.nms.v1_19_common.opsenders;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.impl.CraftCommand;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An common OpSender that works for any {@link CommandSender} on Minecraft 1.19, 1.19.1, and 1.19.2.
 */
public abstract class GeneralOpSender1_19_common extends CraftBlockCommandSender implements OpSender1_19_common {
    private final CommandSender sender;
    private final CommandSourceStack stack;
    private final CraftBlock fakeBlock;

    /**
     * Creates a new {@link GeneralOpSender1_19_common}.
     *
     * @param sender The {@link CommandSender} this {@link GeneralOpSender1_19_common} is wrapping.
     */
    public GeneralOpSender1_19_common(CommandSender sender) {
        super(null, null);

        this.sender = sender;
        this.stack = buildStack(sender);
        fakeBlock = new FakeCraftBlock();
    }

    private CommandSourceStack buildStack(CommandSender sender) {
        CommandSourceStack stack;
        if (sender instanceof Entity entity) {
            Location location = entity.getBukkitEntity().getLocation();

            Vec3 position = new Vec3(location.getX(), location.getY(), location.getZ());
            Vec2 rotation = new Vec2(location.getYaw(), location.getPitch());
            ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();
            stack = new CommandSourceStack(this, position, rotation, world, 4,
                    sender.getName(), entity.getDisplayName(), world.getServer(), entity);
        } else {
            ServerLevel world = ((CraftWorld) sender.getServer().getWorlds().get(0)).getHandle();
            String name = sender.getName();
            stack = new CommandSourceStack(this, Vec3.ZERO, Vec2.ZERO, world, 4,
                    name, MutableComponent.create(new LiteralContents(name)), world.getServer(), null);
        }
        return OpSender1_19_common.modifyStack(stack, this);
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
        return stack;
    }

    // Make sure OpSender's sendMessage methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_19_common.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_19_common.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_19_common.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19_common.super.sendMessage(uuid, strings);
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
