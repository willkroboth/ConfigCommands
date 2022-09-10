package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandArgument {
    default String getTypeTag(){
        return getName();
    }

    default <T> T assertArgumentInfoClass(@NotNull Object argumentInfo, Class<? extends T> clazz, String arg) throws IncorrectArgumentKey {
        if (clazz.isAssignableFrom(argumentInfo.getClass())) return clazz.cast(argumentInfo);
        throw new IncorrectArgumentKey(arg, "argumentInfo", "Expected argumentInfo to have class " + clazz.getSimpleName());
    }

    Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey;

    // Assuming the implementing class is an InternalArgument, these methods are automatically overridden
    String getName();

    Class<? extends InternalArgument> myClass();
}
