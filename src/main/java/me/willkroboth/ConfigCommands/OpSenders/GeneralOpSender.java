package me.willkroboth.ConfigCommands.OpSenders;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;

import java.util.UUID;

public class GeneralOpSender extends CraftBlockCommandSender implements OpSender {
    private final CommandSender sender;
    private final CommandSourceStack stack;

    public GeneralOpSender(CommandSender sender) {
        // no methods in the CraftBlockCommandSender that need these values are used
        super(null, null);

        this.sender = sender;
        this.stack = buildStack(sender);
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
        return OpSender.modifyStack(stack, this);
    }

    // override getName to provide sender's name and not a nullPointerException
    public String getName() {
        return sender.getName();
    }

    public Spigot spigot() {
        return new Spigot();
    }

    // Overriding getWrapper() to provide the custom CommandSourceStack
    public CommandSourceStack getWrapper() {
        return stack;
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
