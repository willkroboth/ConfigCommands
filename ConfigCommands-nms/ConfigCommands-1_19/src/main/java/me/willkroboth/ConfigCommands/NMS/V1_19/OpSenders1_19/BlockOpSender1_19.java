package me.willkroboth.ConfigCommands.NMS.V1_19.OpSenders1_19;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftBlockCommandSender;

import java.util.UUID;

public class BlockOpSender1_19 extends CraftBlockCommandSender implements OpSender1_19 {
    // listener created through return ((CraftBlockCommandSender)sender).getWrapper();
    private final CommandSourceStack block;
    private final Block tile;
    private final CraftBlockCommandSender sender;

    public BlockOpSender1_19(CraftBlockCommandSender b) {
        super(b.getWrapper(), null);
        block = OpSender1_19.modifyStack(b.getWrapper(), this);
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
        OpSender1_19.super.sendMessage(s);
    }

    public void sendMessage(String[] strings) {
        OpSender1_19.super.sendMessage(strings);
    }

    public void sendMessage(UUID uuid, String s) {
        OpSender1_19.super.sendMessage(uuid, s);
    }

    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19.super.sendMessage(uuid, strings);
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
