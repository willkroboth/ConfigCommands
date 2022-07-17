# Relevant Issue:
#19

# Goal:
Make ConfigCommands compatible with minecraft server versions 1.13 to 1.19 (the versions supported by CommandAPI). The introduction of [OpSenders](https://github.com/willkroboth/ConfigCommands/tree/main/src/main/java/me/willkroboth/ConfigCommands/OpSenders) means that classes from the `org.bukkit.craftbukkit` and `net.minecraft.server` are now being accessed. The classes in these packages may change through the different Minecraft versions, so a plugin developed for one version dose not work for another. 
