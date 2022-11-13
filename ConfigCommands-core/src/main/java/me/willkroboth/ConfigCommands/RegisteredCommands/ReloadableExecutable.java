package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.command.CommandSender;

public interface ReloadableExecutable {
    void reloadExecution(CommandSender sender) throws WrapperCommandSyntaxException;
}
