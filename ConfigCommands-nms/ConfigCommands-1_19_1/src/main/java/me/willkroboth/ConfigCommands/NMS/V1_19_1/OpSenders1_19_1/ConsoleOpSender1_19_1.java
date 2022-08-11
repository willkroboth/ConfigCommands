//// this class is currently not working
//// In order to provide the correct listener, a new CraftServer instance must be created
//// However, the CraftServer constructor automatically tries to set itself as the singleton server instance
//// This instance cannot be overridden, so an error is thrown
//// The current process also seems to mess with other parts of the server, causing a crash when players exist
//
//package me.willkroboth.ConfigCommands.NMS.V1_19_1.OpSenders1_19_1;
//
//import me.willkroboth.ConfigCommands.NMS.V1_19_common.OpSenders1_19_common.ConsoleOpSender1_19_common;
//import org.bukkit.craftbukkit.v1_19_R1.command.CraftConsoleCommandSender;
//
//public class ConsoleOpSender1_19_1 extends ConsoleOpSender1_19_common implements OpSender1_19_1 {
//    public ConsoleOpSender1_19_1(CraftConsoleCommandSender c) {
//        super(c);
//    }
//}
