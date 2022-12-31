package me.willkroboth.configcommands.nms.v1_19_common.opsenders;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A common {@link org.bukkit.command.BlockCommandSender} OpSender for Minecraft 1.19, 1.19.1, and 1.19.2.
 */
public abstract class BlockOpSender1_19_common extends CraftBlockCommandSender implements OpSender1_19_common {
    // listener created through return ((CraftBlockCommandSender)sender).getWrapper();
    private final CommandSourceStack block;
    private final Block tile;
    private final CraftBlockCommandSender sender;

    /**
     * Creates a new {@link BlockOpSender1_19_common}.
     *
     * @param b The {@link CraftBlockCommandSender} this {@link BlockOpSender1_19_common} is wrapping.
     */
    public BlockOpSender1_19_common(CraftBlockCommandSender b) {
        super(b.getWrapper(), null);
        block = OpSender1_19_common.modifyStack(b.getWrapper(), this);
        tile = b.getBlock();
        sender = b;
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
    public @NotNull CommandSourceStack getWrapper() {
        return block;
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
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
