# Related Issue(s)
https://github.com/willkroboth/ConfigCommands/issues/6

# Goal
Create versions of OpSender that extend the classes CraftPlayer, CraftBlockCommandSender, CraftMinecartCommand, DedicatedServer, CraftServer, and ProxiedNativeCommandSender.

An OpSender should accomplish the following things:
- Appear as the class they represent in instanceof calls
- Be able to run vanilla commands
- Not broadcast commands to console
- Act with operator status
- Keep a message history for returning

Note that to be able to run vanilla commands, the sender needs to generate a valid output when passed through the [getListener](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/command/VanillaCommandWrapper.java#66) method of [VanillaCommandWrapper](https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/command/VanillaCommandWrapper.java):
```java
public static CommandListenerWrapper getListener(CommandSender sender) {
    if (sender instanceof Player) {
        return ((CraftPlayer) sender).getHandle().createCommandSourceStack();
    }
    if (sender instanceof BlockCommandSender) {
        return ((CraftBlockCommandSender) sender).getWrapper();
    }
    if (sender instanceof CommandMinecart) {
        return ((EntityMinecartCommandBlock) ((CraftMinecartCommand) sender).getHandle()).getCommandBlock().createCommandSourceStack();
    }
    if (sender instanceof RemoteConsoleCommandSender) {
        return ((DedicatedServer) MinecraftServer.getServer()).rconConsoleSource.createCommandSourceStack();
    }
    if (sender instanceof ConsoleCommandSender) {
        return ((CraftServer) sender.getServer()).getServer().createCommandSourceStack();
    }
    if (sender instanceof ProxiedCommandSender) {
        return ((ProxiedNativeCommandSender) sender).getHandle();
    }

    throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
}
```
