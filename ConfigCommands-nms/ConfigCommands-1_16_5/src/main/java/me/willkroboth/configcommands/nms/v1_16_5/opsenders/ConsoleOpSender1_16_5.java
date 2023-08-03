package me.willkroboth.configcommands.nms.v1_16_5.opsenders;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;
import net.minecraft.server.v1_16_R3.DedicatedPlayerList;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.command.CraftConsoleCommandSender;
import org.bukkit.potion.Potion;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.16.5.
 */
public class ConsoleOpSender1_16_5 extends CraftConsoleCommandSender implements OpSender1_16_5 {
    // listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
    private final CraftConsoleCommandSender c;
    private final CraftServer server;

    /**
     * Creates a new {@link ConsoleOpSender1_16_5}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_16_5} is wrapping.
     */
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

            // When CraftServer's constructor calls Bukkit#setServer, it logs the running version
            // We don't want that, but luckily we can just disable the Logger
            // https://stackoverflow.com/a/50537708
            Logger logger = Logger.getLogger("Minecraft");
            Level oldLevel = logger.getLevel();
            logger.setLevel(Level.OFF);

            // Construct a new CraftServer
            server = new CraftServer(new DedicatedServerOpWrapper(craftServer.getServer(), this), players);

            // Reset singleton to the correct old values
            serverField.set(null, oldServer);
            brewerField.set(null, oldBrewer);

            // Re enable the logger
            logger.setLevel(oldLevel);
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

    @Override
    public @NotNull Server getServer() {
        return server;
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
    }

    // Make sure OpSender's sendMessage methods are used
    @Override
    public void sendMessage(@NotNull String s) {
        OpSender1_16_5.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_16_5.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_16_5.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_16_5.super.sendMessage(uuid, strings);
    }

    // OpSender methods
    @Override
    public CommandSender getSender() {
        return c;
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
