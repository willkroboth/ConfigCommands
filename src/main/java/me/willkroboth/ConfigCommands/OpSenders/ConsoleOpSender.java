// this class is currently not working
// In order to provide the correct listener, a new CraftServer instance must be created
// However, the CraftServer constructor automatically tries to set itself as the singleton server instance
// This instance cannot be overridden, so an error is thrown
// The current process also seems to mess with other parts of the server, causing a crash when players exist
//
//package me.willkroboth.ConfigCommands.OpSenders;
//
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.server.Services;
//import net.minecraft.server.WorldStem;
//import net.minecraft.server.dedicated.DedicatedPlayerList;
//import net.minecraft.server.dedicated.DedicatedServer;
//import org.bukkit.Server;
//import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
//import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
//
//import java.util.UUID;
//
//public class ConsoleOpSender extends CraftConsoleCommandSender implements OpSender {
////     listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
//    private final CraftConsoleCommandSender c;
//    private final CraftServer server;
//
//    public ConsoleOpSender(CraftConsoleCommandSender c) {
//        this.c = c;
//        CraftServer craftServer = (CraftServer) c.getServer();
//        DedicatedPlayerList players = craftServer.getHandle();
//        server = new CraftServer(new DedicatedServerOpWrapper(craftServer.getServer(), this),
//                new DedicatedPlayerList(
//                        players.getServer(),
//                        players.getServer().registryHolder,
//                        players.playerIo
//                )
//        );
//    }
//
//    private static class DedicatedServerOpWrapper extends DedicatedServer {
//        private final ConsoleOpSender sender;
//
//        public DedicatedServerOpWrapper(DedicatedServer server, ConsoleOpSender sender) {
//            // constructor parameters found by tracing constructors
//            super(
//                    server.options,
//                    server.datapackconfiguration,
//                    server.registryreadops,
//                    server.serverThread,
//                    server.storageSource,
//                    server.getPackRepository(),
//                    new WorldStem(
//                            server.resources.resourceManager(),
//                            server.resources.managers(),
//                            server.registryHolder,
//                            server.getWorldData()
//                    ),
//                    server.settings,
//                    server.fixerUpper,
//                    new Services(
//                            server.getSessionService(),
//                            server.getServiceSignatureValidator(),
//                            server.getProfileRepository(),
//                            server.getProfileCache()
//                    ),
//                    server.progressListenerFactory
//            );
//            this.sender = sender;
//        }
//
//        public CommandSourceStack createCommandSourceStack() {
//            return OpSender.modifyStack(super.createCommandSourceStack(), sender);
//        }
//    }
//
//    public Server getServer() {
//        return server;
//    }
//
//    public Spigot spigot() {
//        return new Spigot();
//    }
//
//    // Make sure OpSender's sendMessage methods are used
//    public void sendMessage(String s) {
//        OpSender.super.sendMessage(s);
//    }
//
//    public void sendMessage(String[] strings) {
//        OpSender.super.sendMessage(strings);
//    }
//
//    public void sendMessage(UUID uuid, String s) {
//        OpSender.super.sendMessage(uuid, s);
//    }
//
//    public void sendMessage(UUID uuid, String[] strings) {
//        OpSender.super.sendMessage(uuid, strings);
//    }
//
//    // OpSender methods
//    public CommandSender getSender() {
//        return c;
//    }
//
//    private String lastMessage = "";
//
//    public String getResult() {
//        return lastMessage;
//    }
//
//    public void setLastMessage(String message) {
//        lastMessage = message;
//    }
//}
