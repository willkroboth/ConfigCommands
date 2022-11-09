package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.Functions.StaticFunctionList;

public class InternalVoidArgument extends InternalArgument {
    // singleton class is best for this because it never stores a value
    private static final InternalVoidArgument instance = new InternalVoidArgument();

    // InternalArgument class needs to be able to initialize object when registering
    // Otherwise, the singleton instance should be used
    protected InternalVoidArgument() {
    }

    public static InternalVoidArgument getInstance() {
        return instance;
    }

    public String getTypeTag() {
        return null;
    }

    // not used
    public void setValue(Object arg) {
    }

    public Object getValue() {
        return null;
    }

    public void setValue(InternalArgument arg) {
    }

    public String forCommand() {
        return "";
    }

    public FunctionList getFunctions() {
        return functions(new Function[0]);
    }

    public StaticFunctionList getStaticFunctions() {
        return functions(new StaticFunction[0]);
    }
}
