package me.willkroboth.ConfigCommands.HelperClasses;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.Arrays;
import java.util.UUID;

// When running vanilla commands, the code wants to get a CommandListenerWrapper.
//      (in org.bukkit.craftbukkit.v1_18_R1.command.VanillaCommandWrapper.class.getListener())
// When the sender is a CraftBlockCommandSender, it directly uses the getWrapper() method to do so.
// Therefore, this class extends CraftBlockCommandSender, so it can easily provide a wrapper
//      The custom wrapper allows me to set shouldBroadcastCommands to false so commands don't appear in the console
//      This also means the sender is automatically an operator that can run all commands
// The class also implements ICommandListener, so it can be used as one
public class OpSender extends CraftBlockCommandSender implements ICommandListener {
    private String lastMessage = "";

    private final CommandSender sender;
    private final CommandListenerWrapper commandListenerWrapper;
    public OpSender(CommandSender sender) {
        // no methods in the CraftBlockCommandSender that need these values are used
        super(null, null);

        this.sender = sender;
        this.commandListenerWrapper = buildCommandWrapper(sender);
    }

    public CommandSender getSender() {
        if(sender instanceof OpSender)
            return ((OpSender) sender).getSender();
        return sender;
    }

    private CommandListenerWrapper buildCommandWrapper(CommandSender sender){
        CommandListenerWrapper wrapper;
        // build commandListenerWrapper
        // source, worldPosition, rotation, level(Dimension?), permissionLevel, textName, displayName, server, entity
        // example copied from a net.minecraft.server.MinecraftServer method that returns a CommandListenerWrapper
        // CommandListenerWrapper(this, worldserver == null ? Vec3D.a : Vec3D.b(worldserver.w()), Vec2F.a,
        //                        worldserver, 4, "Server", new ChatComponentText("Server"), this, (Entity)null);
        if(sender instanceof OpSender opSender) wrapper = opSender.getWrapper();
        // copied from org.bukkit.craftbukkit.v1_18_R2.command.VanillaCommandWrapper#getListener() til next comment
        else if (sender instanceof Player) wrapper = ((CraftPlayer)sender).getHandle().cQ();
        else if (sender instanceof BlockCommandSender) wrapper = ((CraftBlockCommandSender)sender).getWrapper();
        else if (sender instanceof CommandMinecart) wrapper = ((CraftMinecartCommand)sender).getHandle().x().i();
        else if (sender instanceof RemoteConsoleCommandSender) wrapper = (((CraftServer) Bukkit.getServer()).getServer()).w.g();
        else if (sender instanceof ConsoleCommandSender) wrapper = ((CraftServer)sender.getServer()).getServer().aB();
        // custom cases
        else if (sender instanceof ProxiedCommandSender) { wrapper = buildCommandWrapper(((ProxiedCommandSender)sender).getCallee()); }
        else if(sender instanceof Entity entity){
            Location location = entity.getBukkitEntity().getLocation();
            Vec3D position = new Vec3D(location.getX(), location.getY(), location.getZ());
            Vec2F rotation = new Vec2F(location.getYaw(), location.getPitch());
            WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
            String name = sender.getName();
            wrapper = new CommandListenerWrapper(this, position, rotation, world, 4,
                    name, new ChatComponentText(name), world.n(), entity);
        }
        else {
            WorldServer world = ((CraftWorld) sender.getServer().getWorlds().get(0)).getHandle();
            String name = sender.getName();
            wrapper = new CommandListenerWrapper(this, Vec3D.a, Vec2F.a, world, 4,
                    name, new ChatComponentText(name), world.n(), null);
        }
        // edit wrapper to use this as listener and permission level 4
        return wrapper.a(this).a(4);
    }

    // override getName to provide sender's name and not a nullPointerException
    public String getName() { return sender.getName(); }

    @Override
    public Spigot spigot() {
        return new Spigot();
    }

    // Overriding getWrapper() to provide a custom CommandListenerWrapper
    public CommandListenerWrapper getWrapper() { return commandListenerWrapper; }

    // provide result message
    public String getResult(){ return lastMessage; }

    // store result message for CommandSender methods
    public void sendMessage(String s) { lastMessage = s; }

    public void sendMessage(String[] strings) { lastMessage = Arrays.toString(strings); }

    public void sendMessage(UUID uuid, String s) { lastMessage = s; }

    public void sendMessage(UUID uuid, String[] strings) { lastMessage = Arrays.toString(strings); }

    // ICommandListener methods
    public void sendMessage(IChatBaseComponent iChatBaseComponent, UUID ignored) { lastMessage = iChatBaseComponent.getString(); }

    // send success and failure messages
    public boolean shouldSendSuccess() { return true; }

    public boolean shouldSendFailure() { return true; }

    // do not broadcast commands to console
    public boolean shouldBroadcastCommands() { return false; }

    // methods obfuscated in ICommandListener class
    public void a(IChatBaseComponent iChatBaseComponent, UUID uuid) { sendMessage(iChatBaseComponent, uuid); }

    public boolean i_() { return shouldSendSuccess(); }

    public boolean j_() { return shouldSendFailure(); }

    public boolean G_() { return shouldBroadcastCommands(); }

    // return self
    public CommandSender getBukkitSender(CommandListenerWrapper commandListenerWrapper) { return this; }
}