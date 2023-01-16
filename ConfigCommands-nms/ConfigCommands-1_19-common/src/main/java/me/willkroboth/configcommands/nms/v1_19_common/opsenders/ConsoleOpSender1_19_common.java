package me.willkroboth.configcommands.nms.v1_19_common.opsenders;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
import org.bukkit.map.MapPalette;
import org.bukkit.potion.Potion;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * A common {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.19, 1.19.1, and 1.19.2.
 */
public abstract class ConsoleOpSender1_19_common extends CraftConsoleCommandSender implements OpSender1_19_common {
    // listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
    private final CraftConsoleCommandSender c;
    private final CraftServer server;

    /**
     * Creates a new {@link ConsoleOpSender1_19_common}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_19_common} is wrapping.
     */
    public ConsoleOpSender1_19_common(CraftConsoleCommandSender c) {
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

            server = new CraftServer(new DedicatedServerOpWrapper(craftServer.getServer(), this), players);

            // Reset singleton to the correct old values
            serverField.set(null, oldServer);
            brewerField.set(null, oldBrewer);
            colorCacheField.set(null, oldColorCache);
        } catch (IllegalAccessException | RuntimeException e) {
            ConfigCommandsHandler.logError("Could not create ConsoleOpSender!");
            throw new RuntimeException(e);
        }
    }

    private static class DedicatedServerOpWrapper extends DedicatedServer {
        private final ConsoleOpSender1_19_common sender;

        public DedicatedServerOpWrapper(DedicatedServer server, ConsoleOpSender1_19_common sender) {
            // constructor parameters found by tracing constructors
            super(
                    server.options,
                    server.datapackconfiguration,
                    server.registryreadops,
                    server.serverThread,
                    server.storageSource,
                    server.getPackRepository(),
                    new WorldStem(
                            server.resources.resourceManager(),
                            server.resources.managers(),
                            server.registryHolder,
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
            return OpSender1_19_common.modifyStack(super.createCommandSourceStack(), sender);
        }
    }

    @Override
    public @NotNull Spigot spigot() {
        return new Spigot();
    }

    @Override
    public @NotNull Server getServer() {
        return server;
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