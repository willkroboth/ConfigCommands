package me.willkroboth.configcommands.registeredcommands;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.helperclasses.DebuggableState;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A class that holds information about a function that is being compiled.
 * Methods used for modifying the internal variables return the current
 * CompilerState object to make it easy to modify multiple variables at the same time.
 * This class is useful when keeping track of the items needed to create a {@link InterpreterState} object.
 */
public class CompilerState implements DebuggableState {
    /**
     * Creates a new {@link CompilerState} with internal variables set to the default values, those being:
     * <pre>
     * {@link CompilerState#commands}{@code  = new ArrayList<>();}
     * {@link CompilerState#command}{@code  = null;}
     * {@link CompilerState#argumentClasses}{@code = new LinkedHashMap<>();}
     * {@link CompilerState#tagMap}{@code = new HashMap<>();}
     * {@link CompilerState#index}{@code = 0;}
     * {@link CompilerState#localDebug}{@code = new }{@link SharedDebugValue}{@code (ConfigCommandsHandler.getGlobalDebugValue(), false);}
     * </pre>
     */
    public CompilerState() {
        commands = new ArrayList<>();
        command = null;
        argumentClasses = new LinkedHashMap<>();
        tagMap = new HashMap<>();
        index = 0;
        localDebug = new SharedDebugValue(ConfigCommandsHandler.getGlobalDebugValue(), false);
    }

    /**
     * A list of Strings that represent the commands to be parsed for
     * this function, listed in the order they should be run.
     */
    private final List<String> commands;

    /**
     * Adds a command to the {@link CompilerState#commands} list. If {@link CompilerState#command}
     * is currently null and {@link CompilerState#index} is within the bounds of
     * {@link CompilerState#commands}, then {@link CompilerState#command} will be set to
     * {@link CompilerState#commands}{@code .get(}{@link CompilerState#index}{@code )}.
     *
     * @param command The String to add.
     * @return This current CompilerState.
     */
    public CompilerState addCommand(String command) {
        commands.add(command);
        if (this.command == null && commands.size() > index) this.command = commands.get(index);
        return this;
    }

    /**
     * Adds multiple commands to the {@link CompilerState#commands} list. If {@link CompilerState#command}
     * is currently null and {@link CompilerState#commands} is now long enough to have an entry at
     * {@link CompilerState#index}, then {@link CompilerState#command} will be set to
     * {@link CompilerState#commands}{@code .get(}{@link CompilerState#index}{@code )}.
     *
     * @param commands The List of Strings to add.
     * @return This CompilerState object.
     */
    public CompilerState addCommands(List<String> commands) {
        this.commands.addAll(commands);
        if (this.command == null && this.commands.size() > index) this.command = this.commands.get(index);
        return this;
    }

    /**
     * @return The List of Strings currently stored in {@link CompilerState#commands}.
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * The String at entry {@link CompilerState#index} in the {@link CompilerState#commands} list.
     * May be null if {@link CompilerState#index} is outside the bounds of {@link CompilerState#commands}.
     */
    private String command;

    /**
     * Checks whether a command is currently selected.
     *
     * @return False if {@link CompilerState#command} is null, and true otherwise.
     */
    public boolean hasCommand() {
        return command != null;
    }

    /**
     * Gets the command currently selected. Will be null when
     * {@link CompilerState#hasCommand()} returns false, and non-null otherwise.
     *
     * @return The value of {@link CompilerState#command}.
     */
    @Nullable
    public String getCommand() {
        return command;
    }

    /**
     * A map from names to the {@link InternalArgument} class objects for
     * each argument variable currently available.
     */
    private final Map<String, Class<? extends InternalArgument>> argumentClasses;

    /**
     * Adds an argument to the {@link CompilerState#argumentClasses} map.
     *
     * @param name  The name of the new argument.
     * @param clazz The {@link InternalArgument} class object that represents the type of the argument.
     * @return This CompilerState object.
     */
    public CompilerState addArgument(String name, Class<? extends InternalArgument> clazz) {
        argumentClasses.put(name, clazz);
        return this;
    }

    /**
     * Adds multiple arguments to the {@link CompilerState#argumentClasses} map.
     *
     * @param arguments A map that links the names to the {@link InternalArgument}
     *                  class objects for each of the arguments that should be added.
     * @return This CompilerState object.
     */
    public CompilerState addArguments(Map<String, Class<? extends InternalArgument>> arguments) {
        argumentClasses.putAll(arguments);
        return this;
    }

    /**
     * @return The value of {@link CompilerState#argumentClasses}.
     */
    public Map<String, Class<? extends InternalArgument>> getArgumentClasses() {
        return argumentClasses;
    }

    /**
     * Checks if an argument exists in the {@link CompilerState#argumentClasses} map.
     *
     * @param name The name of the argument to look for.
     * @return True if {@link CompilerState#argumentClasses} contains name as one of its keys, and false otherwise.
     */
    public boolean hasVariable(String name) {
        return argumentClasses.containsKey(name);
    }

    /**
     * Gets the {@link InternalArgument} class object for the given
     * name in the {@link CompilerState#argumentClasses} map.
     *
     * @param name The name of the argument to look for.
     * @return The {@link InternalArgument} class object for the given
     * name, or null if {@link CompilerState#argumentClasses} does not
     * have a value for that key.
     */
    @Nullable
    public Class<? extends InternalArgument> getVariable(String name) {
        return argumentClasses.get(name);
    }

    /**
     * A map from name to line index for each of the tags available.
     */
    private final Map<String, Integer> tagMap;

    /**
     * Adds a tag to the {@link CompilerState#tagMap}.
     *
     * @param name  The name of the new tag.
     * @param index The line index for the new tag.
     * @return This CompilerState object.
     */
    public CompilerState addTag(String name, int index) {
        tagMap.put(name, index);
        return this;
    }

    /**
     * Adds multiple tags to the {@link CompilerState#tagMap}.
     *
     * @param tags A map from name to line index for each of the tags that should be added.
     * @return This CompilerState object.
     */
    public CompilerState addTags(Map<String, Integer> tags) {
        tagMap.putAll(tags);
        return this;
    }

    /**
     * @return The value of {@link CompilerState#tagMap}
     */
    public Map<String, Integer> getTagMap() {
        return tagMap;
    }

    /**
     * The selected index into {@link CompilerState#commands} for {@link CompilerState#command}
     */
    private int index;

    /**
     * Sets {@link CompilerState#index} to the given index. If {@link CompilerState#index}
     * is inside the bounds of {@link CompilerState#commands}, then
     * {@link CompilerState#command} will be set to {@link CompilerState#commands}{@code .get(}
     * {@link CompilerState#index}{@code )}.
     *
     * @param index The index to move to.
     * @return This CompilerState object.
     */
    public CompilerState setIndex(int index) {
        this.index = index;
        if (0 <= index && index < commands.size()) {
            command = commands.get(index);
        } else {
            command = null;
        }
        return this;
    }

    /**
     * Changes {@link CompilerState#index} by the given amount. {@link CompilerState#command}
     * is updated in the way described by {@link CompilerState#setIndex(int)}.
     *
     * @param amount The amount to change the current index by. If this is negative,
     *               {@link CompilerState#index} will decrease.
     * @return This CompilerState object.
     */
    public CompilerState increaseIndex(int amount) {
        setIndex(index + amount);
        return this;
    }

    /**
     * @return The value of {@link CompilerState#index}.
     */
    public int getIndex() {
        return index;
    }

    /**
     * The {@link SharedDebugValue} being used by this CompilerState.
     */
    private SharedDebugValue localDebug;

    /**
     * Sets {@link CompilerState#localDebug} to the given value.
     *
     * @param debug The new {@link SharedDebugValue} for this CompilerState.
     * @return This CompilerState object.
     */
    public CompilerState setDebug(SharedDebugValue debug) {
        localDebug = debug;
        return this;
    }

    /**
     * @return True if {@link CompilerState#localDebug} is active, and false otherwise.
     */
    @Override
    public boolean isDebug() {
        return localDebug.isDebug();
    }
}
