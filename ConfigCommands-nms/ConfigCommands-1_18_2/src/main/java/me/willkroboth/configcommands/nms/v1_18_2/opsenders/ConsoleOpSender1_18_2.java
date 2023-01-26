package me.willkroboth.configcommands.nms.v1_18_2.opsenders;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.command.CraftConsoleCommandSender;
import org.bukkit.potion.Potion;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * A {@link org.bukkit.command.ConsoleCommandSender} OpSender for Minecraft 1.18.2.
 */
public class ConsoleOpSender1_18_2 extends CraftConsoleCommandSender implements OpSender1_18_2 {
    private static ConsoleOpSender1_18_2 instance;

    public static void initializeInstance(ConsoleCommandSender source) {
        instance = new ConsoleOpSender1_18_2((CraftConsoleCommandSender) source);
    }

    public static ConsoleOpSender1_18_2 getInstance() {
        return instance;
    }

    // listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
    private final CraftConsoleCommandSender c;
    private final CraftServer server;

    /**
     * Creates a new {@link ConsoleOpSender1_18_2}.
     *
     * @param c The {@link CraftConsoleCommandSender} this {@link ConsoleOpSender1_18_2} is wrapping.
     */
    public ConsoleOpSender1_18_2(CraftConsoleCommandSender c) {
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
        private final ConsoleOpSender1_18_2 sender;

        public DedicatedServerOpWrapper(DedicatedServer server, ConsoleOpSender1_18_2 sender) {
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
                    server.getSessionService(),
                    server.getProfileRepository(),
                    server.getProfileCache(),
                    server.progressListenerFactory
            );
            this.sender = sender;
        }

        public CommandSourceStack createCommandSourceStack() {
            return OpSender1_18_2.modifyStack(super.createCommandSourceStack(), sender);
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
