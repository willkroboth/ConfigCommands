package me.willkroboth.ConfigCommands.Functions.NonGenericVarargs;

import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;

import java.util.Map;

public class StaticFunctionEntry implements Map.Entry<Definition, StaticFunction>{
    private final Definition key;
    private StaticFunction value;

    public StaticFunctionEntry(Definition key, StaticFunction value) {
        this.key = key;
        this.value = value;
    }

    public Definition getKey() {
        return key;
    }

    public StaticFunction getValue() {
        return value;
    }

    public StaticFunction setValue(StaticFunction value) {
        StaticFunction old = this.value;
        this.value = value;
        return old;
    }

    public String toString() {
        return key + " -> " + value;
    }
}
