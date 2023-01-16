package me.willkroboth.configcommands.nms.v1_18_2.opsenders;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftBlockCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A {@link org.bukkit.command.BlockCommandSender} OpSender for Minecraft 1.18.2.
 */
public class BlockOpSender1_18_2 extends CraftBlockCommandSender implements OpSender1_18_2 {
    // listener created through return ((CraftBlockCommandSender)sender).getWrapper();
    private final CommandSourceStack block;
    private final Block tile;
    private final CraftBlockCommandSender sender;

    /**
     * Creates a new {@link BlockOpSender1_18_2}.
     *
     * @param b The {@link CraftBlockCommandSender} this {@link BlockOpSender1_18_2} is wrapping.
     */
    public BlockOpSender1_18_2(CraftBlockCommandSender b) {
        super(b.getWrapper(), null);
        block = OpSender1_18_2.modifyStack(b.getWrapper(), this);
        tile = b.getBlock();
        sender = b;
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
    }

    // Make sure CraftBlockCommandSender's methods don't give a NPE b/c parameters were given to be null
    @Override
    public @NotNull Block getBlock() {
        return tile;
    }

    @Override
    public @NotNull String getName() {
        return block.getTextName();
    }

    @Override
    public CommandSourceStack getWrapper() {
        return block;
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