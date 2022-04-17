package me.willkroboth.ConfigCommands.Functions.NonGenericVarargs;

import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;

import java.util.Map;

public class FunctionEntry implements Map.Entry<Definition, Function>{
    private final Definition key;
    private Function value;

    public FunctionEntry(Definition key, Function value) {
        this.key = key;
        this.value = value;
    }

    public Definition getKey() {
        return key;
    }

    public Function getValue() {
        return value;
    }

    public Function setValue(Function value) {
        Function old = this.value;
        this.value = value;
        return old;
    }

    public String toString() {
        return key + " -> " + value;
    }
}
