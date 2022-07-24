package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftBlockCommandSender;

import java.util.UUID;

public class BlockOpSender1_16_5 extends CraftBlockCommandSender implements OpSender1_16_5 {
    // listener created through return ((CraftBlockCommandSender)sender).getWrapper();
    private final CommandListenerWrapper block;
    private final Block tile;
    private final CraftBlockCommandSender sender;

    public BlockOpSender1_16_5(CraftBlockCommandSender b) {
        super(b.getWrapper(), null);
        block = OpSender1_16_5.modifyStack(b.getWrapper(), this);
        tile = b.getBlock();
        sender = b;
    }

    public Spigot spigot() {
        return new Spigot();
    }

    // Make sure CraftBlockCommandSender's methods don't give a NPE b/c parameters were given to be null
    public Block getBlock() {
        return tile;
    }

    public String getName() {
        return block.getName();
    }

    public CommandListenerWrapper getWrapper() {
        return block;
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
