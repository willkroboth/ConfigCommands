package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandArgument {
    default String getTypeTag() {
        return getName();
    }

    // Dealing with argumentInfo object
    default <T> T assertArgumentInfoClass(@NotNull Object argumentInfo, Class<? extends T> clazz, String arg) throws IncorrectArgumentKey {
        if (clazz.isAssignableFrom(argumentInfo.getClass())) return clazz.cast(argumentInfo);
        throw new IncorrectArgumentKey(arg, "argumentInfo", "Expected argumentInfo to be a " + clazz.getSimpleName());
    }

    Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey;

    boolean editArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, @Nullable Object argumentInfo);

    String[] formatArgumentInfo(Object argumentInfo);

    // Assuming the implementing class is an InternalArgument, these methods are automatically overridden
    String getName();

    Class<? extends InternalArgument> myClass();
}
