//// this class is currently not working
//// In order to provide the correct listener, a new CraftServer instance must be created
//// However, the CraftServer constructor automatically tries to set itself as the singleton server instance
//// This instance cannot be overridden, so an error is thrown
//// The current process also seems to mess with other parts of the server, causing a crash when players exist
//
//package me.willkroboth.ConfigCommands.NMS.V1_18.OpSenders1_18;
//
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.server.dedicated.DedicatedPlayerList;
//import net.minecraft.server.dedicated.DedicatedServer;
//import org.bukkit.Server;
//import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
//import org.bukkit.craftbukkit.v1_18_R1.command.CraftConsoleCommandSender;
//
//import java.util.UUID;
//
//public class ConsoleOpSender1_18 extends CraftConsoleCommandSender implements OpSender1_18 {
////     listener created through ((CraftServer)sender.getServer()).getServer().createCommandSourceStack();
//    private final CraftConsoleCommandSender c;
//    private final CraftServer server;
//
//    public ConsoleOpSender1_18(CraftConsoleCommandSender c) {
//        this.c = c;
//        CraftServer craftServer = (CraftServer) c.getServer();
//        DedicatedPlayerList players = craftServer.getHandle();
//        server = new CraftServer(new DedicatedServerOpWrapper(craftServer.getServer(), this), players);
//    }
//
//    private static class DedicatedServerOpWrapper extends DedicatedServer {
//        private final ConsoleOpSender1_18 sender;
//
//        public DedicatedServerOpWrapper(DedicatedServer server, ConsoleOpSender1_18 sender) {
//            // constructor parameters found by tracing constructors
//            super(
//                    server.options,
//                    server.datapackconfiguration,
//                    server.serverThread,
//                    server.registryHolder,
//                    server.storageSource,
//                    server.getPackRepository(),
//                    server.resources,
//                    server.getWorldData(),
//                    server.settings,
//                    server.fixerUpper,
//                    server.getSessionService(),
//                    server.getProfileRepository(),
//                    server.getProfileCache(),
//                    server.progressListenerFactory
//            );
//            this.sender = sender;
//        }
//
//        public CommandSourceStack createCommandSourceStack() {
//            return OpSender1_18.modifyStack(super.createCommandSourceStack(), sender);
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
//        OpSender1_18.super.sendMessage(s);
//    }
//
//    public void sendMessage(String[] strings) {
//        OpSender1_18.super.sendMessage(strings);
//    }
//
//    public void sendMessage(UUID uuid, String s) {
//        OpSender1_18.super.sendMessage(uuid, s);
//    }
//
//    public void sendMessage(UUID uuid, String[] strings) {
//        OpSender1_18.super.sendMessage(uuid, strings);
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
