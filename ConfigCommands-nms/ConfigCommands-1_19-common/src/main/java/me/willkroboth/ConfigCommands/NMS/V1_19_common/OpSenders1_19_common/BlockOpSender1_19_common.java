package me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;

import java.util.UUID;

public abstract class BlockOpSender1_19_common extends CraftBlockCommandSender implements OpSender1_19_common {
    // listener created through return ((CraftBlockCommandSender)sender).getWrapper();
    private final CommandSourceStack block;
    private final Block tile;
    private final CraftBlockCommandSender sender;

    public BlockOpSender1_19_common(CraftBlockCommandSender b) {
        super(b.getWrapper(), null);
        block = OpSender1_19_common.modifyStack(b.getWrapper(), this);
        tile = b.getBlock();
        sender = b;
    }

    // Make sure CraftBlockCommandSender's methods don't give a NPE b/c parameters were given to be null
    public Block getBlock() {
        return tile;
    }

    public String getName() {
        return block.getTextName();
    }

    public CommandSourceStack getWrapper() {
        return block;
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
