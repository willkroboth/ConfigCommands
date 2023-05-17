package me.willkroboth.configcommands.registeredcommands;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.helperclasses.DebuggableState;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A class that holds information about a compiled function that is being run.
 * Methods used for modifying the internal variables return the current
 * InterpreterState object to make it easy to modify multiple variables at the same time.
 */
public class InterpreterState implements DebuggableState {
    // Just some silly casting
    @SuppressWarnings("unchecked")
    private static <T> void setArgument(InternalArgument<T> argument, Object object) {
        argument.setValue((T) object);
    }

    /**
     * Creates a new {@link InterpreterState} with internal variables set to the default values, those being:
     * <pre>
     * {@link InterpreterState#lines}{@code = new ArrayList<>();}
     * {@link InterpreterState#line}{@code = null;}
     * {@link InterpreterState#argumentClasses}{@code = new HashMap<>();}
     * {@link InterpreterState#argumentVariables}{@code = new LinkedHashMap<>();}
     * {@link InterpreterState#tagMap}{@code = new LinkedHashMap<>();}
     * {@link InterpreterState#index}{@code = 0;}
     * {@link InterpreterState#localDebug}{@code = new SharedDebugValue(ConfigCommandsHandler.getGlobalDebugValue(), false);}
     * </pre>
     */
    public InterpreterState() {
        lines = new ArrayList<>();
        line = null;
        argumentClasses = new HashMap<>();
        argumentVariables = new LinkedHashMap<>();
        tagMap = new LinkedHashMap<>();
        index = 0;
        localDebug = new SharedDebugValue(ConfigCommandsHandler.getGlobalDebugValue(), false);
    }

    /**
     * @return A copy of this {@link InterpreterState} object with all internal variables copied.
     */
    public InterpreterState copy() {
        return new InterpreterState(this);
    }

    private InterpreterState(InterpreterState toCopy) {
        this.lines = toCopy.lines;
        this.line = toCopy.line;
        this.argumentClasses = toCopy.argumentClasses;
        this.argumentVariables = new HashMap<>(toCopy.argumentVariables);
        this.tagMap = toCopy.tagMap;
        this.index = toCopy.index;
        this.localDebug = toCopy.localDebug;
    }

    /**
     * A list of {@link FunctionLine} objects that represents each line in the function being interpreted
     */
    private final List<FunctionLine> lines;

    /**
     * Adds a command to the {@link InterpreterState#lines} list. If {@link InterpreterState#line}
     * is currently null and {@link InterpreterState#index} is within the bounds of
     * {@link InterpreterState#lines}, then {@link InterpreterState#line} will be set to
     * {@link InterpreterState#lines}{@code .get(}{@link InterpreterState#index}{@code )}.
     *
     * @param line The {@link FunctionLine} to add.
     * @return This current InterpreterState.
     */
    public InterpreterState addLine(FunctionLine line) {
        lines.add(line);
        if (this.line == null && lines.size() > index) this.line = lines.get(index);
        return this;
    }

    /**
     * Adds multiple commands to the {@link InterpreterState#lines} list. If {@link InterpreterState#line}
     * is currently null and {@link InterpreterState#lines} is now long enough to have an entry at
     * {@link InterpreterState#index}, then {@link InterpreterState#line} will be set to
     * {@link InterpreterState#lines}{@code .get(}{@link InterpreterState#index}{@code )}.
     *
     * @param lines The List of {@link FunctionLine} objects to add.
     * @return This InterpreterState object.
     */
    public InterpreterState addLines(List<FunctionLine> lines) {
        this.lines.addAll(lines);
        if (line == null && this.lines.size() > index) line = this.lines.get(index);
        return this;
    }

    /**
     * @return The List of {@link FunctionLine}s currently
     */
    public List<FunctionLine> getLines() {
        return lines;
    }

    /**
     * The {@link FunctionLine} at entry {@link InterpreterState#index} in the {@link InterpreterState#lines} list.
     * May be null if {@link InterpreterState#index} is outside the bounds of {@link InterpreterState#lines}.
     */
    private FunctionLine line;

    /**
     * Checks whether a line is currently selected.
     *
     * @return False if {@link InterpreterState#line} is null, and true otherwise.
     */
    public boolean hasLine() {
        return line != null;
    }

    /**
     * Gets the line currently selected. Will be null when
     * {@link InterpreterState#hasLine()} returns false, and non-null otherwise.
     *
     * @return The value of {@link InterpreterState#line}.
     */
    @Nullable
    public FunctionLine getLine() {
        return line;
    }

    /**
     * A map from name to {@link InternalArgument} object for each variable available in this function.
     */
    private final Map<String, InternalArgument<?>> argumentVariables;
    /**
     * A map from name to {@link InternalArgument} class for each variable available in this function.
     */
    private Map<String, Class<? extends InternalArgument<?>>> argumentClasses;

    /**
     * Sets the {@link InterpreterState#argumentClasses} variable.
     *
     * @param argumentClasses A map from name to {@link InternalArgument} class for each variable available in this function.
     * @return This InterpreterState object.
     */
    public InterpreterState setArgumentClasses(Map<String, Class<? extends InternalArgument<?>>> argumentClasses) {
        this.argumentClasses = argumentClasses;
        return this;
    }

    /**
     * Sets up the {@link InterpreterState#argumentVariables} map with the given Objects array from the CommandAPI.
     *
     * @param args The Objects array from the CommandAPI.
     * @return This InterpreterState object.
     */
    public InterpreterState setUpVariablesMap(Object[] args) {
        int i = 0;
        int numDefaultArgs = CommandTreeBuilder.getDefaultArgs().size();
        for (Map.Entry<String, Class<? extends InternalArgument<?>>> toAdd : argumentClasses.entrySet()) {
            argumentVariables.put(toAdd.getKey(), InternalArgument.getInternalArgument(toAdd.getValue()));

            ConfigCommandsHandler.logDebug(localDebug, "Added argument %s with class %s", toAdd.getKey(), toAdd.getValue().getSimpleName());

            if (i >= numDefaultArgs && i - numDefaultArgs < args.length) {
                setArgument(argumentVariables.get(toAdd.getKey()), args[i - numDefaultArgs]);
                ConfigCommandsHandler.logDebug(localDebug, "%s set to %s", toAdd.getKey(), args[i - numDefaultArgs]);
            }
            i++;
        }
        return this;
    }

    /**
     * Adds a single variable to the {@link InterpreterState#argumentVariables} map.
     *
     * @param name   The name of the variable
     * @param object The {@link InternalArgument} that holds the value for the variable.
     * @return This InterpreterState object.
     */
    public InterpreterState addArgument(String name, InternalArgument<?> object) {
        argumentVariables.put(name, object);
        return this;
    }

    /**
     * Adds multiple variables to the {@link InterpreterState#argumentVariables} map.
     *
     * @param arguments A map from name to {@link InterpreterState} object for each variable to add to the map.
     * @return This InterpreterState object.
     */
    public InterpreterState addArguments(Map<String, InternalArgument<?>> arguments) {
        argumentVariables.putAll(arguments);
        return this;
    }

    /**
     * Sets a variable in the {@link InterpreterState#argumentVariables} map to a new value.
     *
     * @param name  The name of the variable to change.
     * @param value An {@link InternalArgument} holding the value of the new value for the variable.
     * @return This InterpreterState object.
     */
    public InterpreterState setVariable(String name, InternalArgument<?> value) {
        setArgument(argumentVariables.get(name), value.getValue());
        return this;
    }

    /**
     * Sets a variable in the {@link InterpreterState#argumentVariables} map to a new value.
     *
     * @param name  The name of the variable to change.
     * @param value The new value for the variable.
     * @return This InterpreterState object.
     */
    public InterpreterState setVariable(String name, Object value) {
        setArgument(argumentVariables.get(name), value);
        return this;
    }

    /**
     * @return The {@link InterpreterState#argumentVariables} map.
     */
    public Map<String, InternalArgument<?>> getArgumentVariables() {
        return argumentVariables;
    }

    /**
     * Checks if a variable with a certain name exists in this {@link InterpreterState}.
     *
     * @param name The name of the variable to look for.
     * @return True if the {@link InterpreterState#argumentVariables} map has name as one of its keys, and false otherwise.
     */
    public boolean hasVariable(String name) {
        return argumentVariables.containsKey(name);
    }

    /**
     * Gets the {@link InternalArgument} value held in the requested variable.
     *
     * @param name The name of the variable to look for.
     * @return The {@link InternalArgument} value mapped to in the {@link InterpreterState#argumentVariables} map.
     */
    public InternalArgument<?> getVariable(String name) {
        return argumentVariables.get(name);
    }

    /**
     * A map from name to line index for each of the tags available.
     */
    private final Map<String, Integer> tagMap;

    /**
     * Adds a tag to the {@link InterpreterState#tagMap}.
     *
     * @param name  The name of the new tag.
     * @param index The line index for the new tag.
     * @return This InterpreterState object.
     */
    public InterpreterState addTag(String name, int index) {
        tagMap.put(name, index);
        return this;
    }

    /**
     * Adds multiple tags to the {@link InterpreterState#tagMap}.
     *
     * @param tags A map from name to line index for each of the tags that should be added.
     * @return This InterpreterState object.
     */
    public InterpreterState addTags(Map<String, Integer> tags) {
        tagMap.putAll(tags);
        return this;
    }

    /**
     * @return The value of {@link InterpreterState#tagMap}
     */
    public Map<String, Integer> getTagMap() {
        return tagMap;
    }

    /**
     * Checks if the given String can be jumped to as a tag.
     *
     * @param tag The name of the tag to look for.
     * @return True if the {@link InterpreterState#tagMap} has the given tag as a key, and false otherwise.
     */
    public boolean hasTag(String tag) {
        return tagMap.containsKey(tag);
    }

    /**
     * Gets the index corresponding to the given tag.
     *
     * @param tag The name of the tag to look for.
     * @return The index the tag is located at.
     */
    public int getTag(String tag) {
        return tagMap.get(tag);
    }

    /**
     * The selected index into {@link InterpreterState#lines} for {@link InterpreterState#line}
     */
    private int index;

    /**
     * Sets {@link InterpreterState#index} to the given index. If {@link InterpreterState#index}
     * is inside the bounds of {@link InterpreterState#lines}, then
     * {@link InterpreterState#line} will be set to {@link InterpreterState#lines}{@code .get(}
     * {@link InterpreterState#index}{@code )}.
     *
     * @param index The index to move to.
     * @return This CompilerState object.
     */
    public InterpreterState setIndex(int index) {
        this.index = index;
        if (0 <= index && index < lines.size()) {
            line = lines.get(index);
        } else {
            line = null;
        }
        return this;
    }

    /**
     * Changes {@link InterpreterState#index} by the given amount. {@link InterpreterState#line}
     * is updated in the way described by {@link InterpreterState#setIndex(int)}.
     *
     * @param amount The amount to change the current index by. If this is negative,
     *               {@link InterpreterState#index} will decrease.
     * @return This CompilerState object.
     */
    public InterpreterState increaseIndex(int amount) {
        setIndex(index + amount);
        return this;
    }

    /**
     * @return The value of {@link InterpreterState#index}.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return The value of {@link InterpreterState#index} plus one, the next index.
     */
    public int nextIndex() {
        return index + 1;
    }

    /**
     * The {@link SharedDebugValue} being used by this InterpreterState.
     */
    private SharedDebugValue localDebug;

    /**
     * Sets {@link InterpreterState#localDebug} to the given value.
     *
     * @param debug The new {@link SharedDebugValue} for this InterpreterState.
     * @return This CompilerState object.
     */
    public InterpreterState setDebug(SharedDebugValue debug) {
        localDebug = debug;
        return this;
    }

    @Override
    public boolean isDebug() {
        return localDebug.isDebug();
    }
}
