package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

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

import java.util.UUID;

public class GeneralOpSender1_16_5 extends CraftBlockCommandSender implements OpSender1_16_5 {
    private final CommandSender sender;
    private final CommandListenerWrapper listener;
    private final CraftBlock fakeBlock;

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
    private static class FakeCraftBlock extends CraftBlock{
        public FakeCraftBlock(){
            super(((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle(), BlockPosition.ZERO);
        }

        public BlockData getBlockData() {
            return new CraftCommand(null);
        }
    }

    public Block getBlock() {
        return fakeBlock;
    }

    public String getName() {
        return sender.getName();
    }

    public Spigot spigot() {
        return new Spigot();
    }

    // Overriding getWrapper() to provide the custom CommandListenerWrapper
    public CommandListenerWrapper getWrapper() {
        return listener;
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
