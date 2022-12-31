package me.willkroboth.configcommands.internalarguments;

import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.configcommands.exceptions.IncorrectArgumentKey;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.registeredcommands.ArgumentTreeBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * An interface that can be attached to a {@link InternalArgument} to indicate it can
 * be added as an argument of a command, and to add the functionality to make that possible.
 */
public interface CommandArgument {
    /**
     * The String to use when declaring an {@link ArgumentTreeBuilder} will use this class as its argument type
     * (See {@link ArgumentTreeBuilder#ArgumentTreeBuilder(String, Map, ConfigurationSection, SharedDebugValue, List)} tree type).
     *
     * @return By default, this returns the value of {@link CommandArgument#getName()}
     */
    default String getTypeTag() {
        return getName();
    }

    /**
     * Asserts an argumentInfo object has a certain class and casts it to that type.
     *
     * @param argumentInfo The argumentInfo object
     * @param clazz        The class the argumentInfo object should have
     * @param arg          The name of the argument, used when creating the exception's message.
     * @param <T>          The type to cast argumentInfo to
     * @return argumentInfo cast to the given class
     * @throws IncorrectArgumentKey If the argumentInfo object is not of the specified class
     */
    default <T> T assertArgumentInfoClass(@NotNull Object argumentInfo, Class<? extends T> clazz, String arg) throws IncorrectArgumentKey {
        if (clazz.isAssignableFrom(argumentInfo.getClass())) return clazz.cast(argumentInfo);
        throw new IncorrectArgumentKey(arg, "argumentInfo", "Expected argumentInfo to be a " + clazz.getSimpleName());
    }

    /**
     * Creates a CommandAPI {@link Argument} based on the given argumentInfo.
     *
     * @param name         The nodeName for the new {@link Argument}
     * @param argumentInfo The argumentInfo object. This object usually comes calling {@link ConfigurationSection#get(String)}.
     *                     This may be null if the {@code argumentInfo} tag is missing. After checking this isn't null,
     *                     {@link CommandArgument#assertArgumentInfoClass(Object, Class, String)} can be used to convert
     *                     this object into the class needed for the implementation of this method.
     * @param localDebug   Whether debug is enabled for this argument or not.
     * @return A new {@link Argument} to add to the command for this argument.
     * @throws IncorrectArgumentKey If the value on a key for this argument is incorrect.
     */
    Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey;

    /**
     * Handles editing an argumentInfo object in the config file when using {@code /configcommands build}.
     *
     * @param sender       The {@link CommandSender} editing the information.
     * @param message      The message to process.
     * @param argument     The {@link ConfigurationSection} that holds all the information for the argument.
     *                     This is useful if argumentInfo object is null and a new section for the argumentInfo
     *                     needs to be created.
     * @param argumentInfo The current argumentInfo object, gotten by running {@code argument.get("argumentInfo")}.
     * @return true if the user is done editing the argumentInfo, and false if more steps need to happen.
     */
    boolean editArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, @Nullable Object argumentInfo);

    /**
     * Turns an argumentInfo object into an array of messages to send to a {@link CommandSender}.
     * This is used when displaying information about a command while using {@code /configcommands build}.
     *
     * @param argumentInfo The argument info object.
     * @return An array of Strings to send.
     */
    String[] formatArgumentInfo(Object argumentInfo);

    // Assuming the implementing class is an InternalArgument, these methods are automatically implemented

    /**
     * Gets the name of this argument. Assuming this class is being implemented by a subclass of
     * {@link InternalArgument}, this method is automatically implemented by {@link InternalArgument#getName()}
     *
     * @return The name of this argument
     */
    String getName();

    /**
     * Gets the class of this argument. Assuming this class is being implemented by a subclass of
     * {@link InternalArgument}, this method is automatically implemented by {@link InternalArgument#myClass()}
     *
     * @return The class of this argument.
     */
    Class<? extends InternalArgument> myClass();
}
