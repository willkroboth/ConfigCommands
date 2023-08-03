package me.willkroboth.configcommands.nms.v1_19_3.opsenders;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.map.MapPalette;
import org.bukkit.potion.Potion;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.command.CraftConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.19.3.
 */
public class ConsoleOpSender1_19_3 extends CraftConsoleCommandSender implements OpSender1_19_3 {
    // listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
    private final CraftConsoleCommandSender c;
    private final CraftServer server;

    /**
     * Creates a new {@link ConsoleOpSender1_19_3}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_19_3} is wrapping.
     */
    public ConsoleOpSender1_19_3(CraftConsoleCommandSender c) {
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

            Field colorCacheField = ConfigCommandsHandler.getField(MapPalette.class, "mapColorCache");
            Object oldColorCache = colorCacheField.get(null);
            colorCacheField.set(null, null);

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
            colorCacheField.set(null, oldColorCache);

            // Re enable the logger
            logger.setLevel(oldLevel);
        } catch (IllegalAccessException | RuntimeException e) {
            ConfigCommandsHandler.logError("Could not create ConsoleOpSender!");
            throw new RuntimeException(e);
        }
    }

    private static class DedicatedServerOpWrapper extends DedicatedServer {
        private final ConsoleOpSender1_19_3 sender;

        public DedicatedServerOpWrapper(DedicatedServer server, ConsoleOpSender1_19_3 sender) {
            // constructor parameters found by tracing constructors
            super(
                    server.options,
                    server.worldLoader,
                    server.serverThread,
                    server.storageSource,
                    server.getPackRepository(),
                    new WorldStem(
                            server.resources.resourceManager(),
                            server.resources.managers(),
                            server.registries(),
                            server.getWorldData()
                    ),
                    server.settings,
                    server.fixerUpper,
                    new Services(
                            server.getSessionService(),
                            server.getServiceSignatureValidator(),
                            server.getProfileRepository(),
                            server.getProfileCache()
                    ),
                    server.progressListenerFactory
            );
            this.sender = sender;
        }

        public CommandSourceStack createCommandSourceStack() {
            return OpSender1_19_3.modifyStack(super.createCommandSourceStack(), sender);
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
        OpSender1_19_3.super.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings) {
        OpSender1_19_3.super.sendMessage(strings);
    }

    @Override
    public void sendMessage(UUID uuid, @NotNull String s) {
        OpSender1_19_3.super.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(UUID uuid, String[] strings) {
        OpSender1_19_3.super.sendMessage(uuid, strings);
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
