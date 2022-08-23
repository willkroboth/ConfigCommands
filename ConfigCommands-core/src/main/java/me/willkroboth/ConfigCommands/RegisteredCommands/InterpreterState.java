package me.willkroboth.ConfigCommands.RegisteredCommands;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.HelperClasses.DebuggableState;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines.FunctionLine;

import java.util.*;

public class InterpreterState implements DebuggableState {
    public InterpreterState() {
        lines = new ArrayList<>();
        line = null;
        argumentClasses = new HashMap<>();
        argumentVariables = new LinkedHashMap<>();
        tagMap = new LinkedHashMap<>();
        index = 0;
        localDebug = false;
    }

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

    private final List<FunctionLine> lines;

    public InterpreterState addLine(FunctionLine line) {
        lines.add(line);
        if (this.line == null && lines.size() > index) this.line = lines.get(index);
        return this;
    }

    public InterpreterState addLines(List<FunctionLine> lines) {
        this.lines.addAll(lines);
        if (line == null && this.lines.size() > index) line = this.lines.get(index);
        return this;
    }

    public List<FunctionLine> getLines() {
        return lines;
    }

    private FunctionLine line;

    public boolean hasLine() {
        return line != null;
    }

    public FunctionLine getLine() {
        return line;
    }

    private final Map<String, InternalArgument> argumentVariables;
    private Map<String, Class<? extends InternalArgument>> argumentClasses;

    public InterpreterState setArgumentClasses(Map<String, Class<? extends InternalArgument>> argumentClasses){
        this.argumentClasses = argumentClasses;
        return this;
    }

    public InterpreterState setUpVariablesMap(Object[] args) {
        int i = 0;
        int numDefaultArgs = CommandTreeBuilder.getDefaultArgs().size();
        for (Map.Entry<String, Class<? extends InternalArgument>> toAdd : argumentClasses.entrySet()) {
            argumentVariables.put(toAdd.getKey(), InternalArgument.getInternalArgument(toAdd.getValue()));

            ConfigCommandsHandler.logDebug(localDebug, "Added argument %s with class %s", toAdd.getKey(), toAdd.getValue().getSimpleName());

            if (i >= numDefaultArgs && i - numDefaultArgs < args.length) {
                argumentVariables.get(toAdd.getKey()).setValue(args[i - numDefaultArgs]);
                ConfigCommandsHandler.logDebug(localDebug, "%s set to %s", toAdd.getKey(), args[i - numDefaultArgs]);
            }
            i++;
        }
        return this;
    }

    public InterpreterState addArgument(String name, InternalArgument object) {
        argumentVariables.put(name, object);
        return this;
    }

    public InterpreterState addArguments(Map<String, InternalArgument> arguments) {
        argumentVariables.putAll(arguments);
        return this;
    }

    public InterpreterState setVariable(String name, InternalArgument value) {
        argumentVariables.get(name).setValue(value);
        return this;
    }

    public InterpreterState setVariable(String name, Object value) {
        argumentVariables.get(name).setValue(value);
        return this;
    }

    public Map<String, InternalArgument> getArgumentVariables() {
        return argumentVariables;
    }

    public boolean hasVariable(String name) {
        return argumentVariables.containsKey(name);
    }

    public InternalArgument getVariable(String name) {
        return argumentVariables.get(name);
    }

    private final Map<String, Integer> tagMap;

    public InterpreterState addTag(String name, int index) {
        tagMap.put(name, index);
        return this;
    }

    public InterpreterState addTags(Map<String, Integer> tags) {
        tagMap.putAll(tags);
        return this;
    }

    public Map<String, Integer> getTagMap() {
        return tagMap;
    }

    public boolean hasTag(String tag) {
        return tagMap.containsKey(tag);
    }

    public Integer getTag(String tag) {
        return tagMap.get(tag);
    }

    private int index;

    public InterpreterState updateIndex(int index) {
        this.index = index;
        if (0 <= index && index < lines.size()) {
            line = lines.get(index);
        } else {
            line = null;
        }
        return this;
    }

    public InterpreterState increaseIndex(int amount) {
        updateIndex(index + amount);
        return this;
    }

    public int getIndex() {
        return index;
    }

    public int nextIndex() {
        return index + 1;
    }

    private boolean localDebug;

    public InterpreterState setDebug(boolean debug) {
        localDebug = debug;
        return this;
    }

    public boolean isDebug() {
        return localDebug;
    }
}
