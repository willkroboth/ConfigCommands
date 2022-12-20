package me.willkroboth.ConfigCommands.NMS.V1_16_5.OpSenders1_16_5;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.DedicatedPlayerList;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftConsoleCommandSender;
import org.bukkit.potion.Potion;

import java.lang.reflect.Field;
import java.util.UUID;

public class ConsoleOpSender1_16_5 extends CraftConsoleCommandSender implements OpSender1_16_5 {
    // listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
    private final CraftConsoleCommandSender c;
    private final CraftServer server;

    public ConsoleOpSender1_16_5(CraftConsoleCommandSender c) {
        this.c = c;
        CraftServer craftServer = (CraftServer) c.getServer();
        DedicatedPlayerList players = craftServer.getHandle();
        try {
            // The CraftServer constructor dose a bunch of setup stuff b/c it's supposed to be a singleton
            // That includes setting other singleton fields, which get mad if you try and set them twice
            // Just convince them this is the first time, then set it back to the original

            // Set singleton fields to null
            Field serverField = ConfigCommandsHandler.getField(Bukkit.class, "server");
            Object oldServer = serverField.get(null);
            serverField.set(null, null);

            Field brewerField = ConfigCommandsHandler.getField(Potion.class, "brewer");
            Object oldBrewer = serverField.get(null);
            brewerField.set(null, null);

            // TODO: Figure out how to remove logged messages triggered by constructor (all versions)
            server = new CraftServer(new DedicatedServerOpWrapper(craftServer.getServer(), this), players);

            // Reset singleton to the correct old values
            serverField.set(null, oldServer);
            brewerField.set(null, oldBrewer);
        } catch (IllegalAccessException | RuntimeException e) {
            ConfigCommandsHandler.logError("Could not create ConsoleOpSender!");
            throw new RuntimeException(e);
        }
    }

    private static class DedicatedServerOpWrapper extends DedicatedServer {
        private final ConsoleOpSender1_16_5 sender;

        public DedicatedServerOpWrapper(DedicatedServer server, ConsoleOpSender1_16_5 sender) {
            // constructor parameters found by tracing constructors
            super(
                    server.options,
                    server.datapackconfiguration,
                    server.serverThread,
                    server.customRegistry,
                    server.convertable,
                    server.getResourcePackRepository(),
                    server.dataPackResources,
                    server.getSaveData(),
                    server.propertyManager,
                    server.dataConverterManager,
                    server.getMinecraftSessionService(),
                    server.getGameProfileRepository(),
                    server.getUserCache(),
                    server.worldLoadListenerFactory
            );
            this.sender = sender;
        }

        public CommandListenerWrapper getServerCommandListener() {
            return OpSender1_16_5.modifyStack(super.getServerCommandListener(), sender);
        }
    }

    public Server getServer() {
        return server;
    }

    public Spigot spigot() {
        return new Spigot();
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
        return c;
    }

    private String lastMessage = "";

    public String getResult() {
        return lastMessage;
    }

    public void setLastMessage(String message) {
        lastMessage = message;
    }
}
