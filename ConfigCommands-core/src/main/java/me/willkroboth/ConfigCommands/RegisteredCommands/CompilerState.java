package me.willkroboth.ConfigCommands.RegisteredCommands;

import me.willkroboth.ConfigCommands.HelperClasses.DebuggableState;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.*;

public class CompilerState implements DebuggableState {
    public CompilerState() {
        commands = new ArrayList<>();
        command = null;
        argumentClasses = new LinkedHashMap<>();
        tagMap = new HashMap<>();
        index = 0;
        localDebug = false;
    }

    private final List<String> commands;

    public CompilerState addCommand(String command) {
        commands.add(command);
        if (this.command == null && commands.size() > index) this.command = commands.get(index);
        return this;
    }

    public CompilerState addCommands(List<String> commands) {
        this.commands.addAll(commands);
        if (this.command == null && this.commands.size() > index) this.command = this.commands.get(index);
        return this;
    }

    public List<String> getCommands() {
        return commands;
    }

    private String command;

    public boolean hasCommand() {
        return command != null;
    }

    public String getCommand() {
        return command;
    }

    private final Map<String, Class<? extends InternalArgument>> argumentClasses;

    public CompilerState addArgument(String name, Class<? extends InternalArgument> clazz) {
        argumentClasses.put(name, clazz);
        return this;
    }

    public CompilerState addArguments(Map<String, Class<? extends InternalArgument>> arguments) {
        argumentClasses.putAll(arguments);
        return this;
    }

    public Map<String, Class<? extends InternalArgument>> getArgumentClasses() {
        return argumentClasses;
    }

    public boolean hasVariable(String name) {
        return argumentClasses.containsKey(name);
    }

    public Class<? extends InternalArgument> getVariable(String name) {
        return argumentClasses.get(name);
    }

    private final Map<String, Integer> tagMap;

    public CompilerState addTag(String name, int index) {
        tagMap.put(name, index);
        return this;
    }

    public CompilerState addTags(Map<String, Integer> tags) {
        tagMap.putAll(tags);
        return this;
    }

    public Map<String, Integer> getTagMap() {
        return tagMap;
    }

    private int index;

    public CompilerState updateIndex(int index) {
        this.index = index;
        if (0 <= index && index < commands.size()) {
            command = commands.get(index);
        } else {
            command = null;
        }
        return this;
    }

    public CompilerState increaseIndex(int amount) {
        updateIndex(index + amount);
        return this;
    }

    public int getIndex() {
        return index;
    }

    private boolean localDebug;

    public CompilerState setDebug(boolean debug) {
        localDebug = debug;
        return this;
    }

    public boolean isDebug() {
        return localDebug;
    }
}
