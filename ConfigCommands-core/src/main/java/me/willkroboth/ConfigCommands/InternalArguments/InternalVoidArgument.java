package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;

public class InternalVoidArgument extends InternalArgument {
    // singleton class is best for this because it never stores a value
    private static final InternalVoidArgument instance = new InternalVoidArgument();

    // InternalArgument class needs to be able to initialize object when registering
    // Otherwise, the singleton instance should be used
    protected InternalVoidArgument(){ super(null); }

    public static InternalVoidArgument getInstance(){ return instance; }

    public String getTypeTag() { return null; }

    public FunctionList getFunctions() {
        return entries();
    }

    public StaticFunctionList getStaticFunctions() {
        return staticEntries();
    }

    // not used
    public void setValue(Object arg) { }

    public Object getValue() { return null; }

    public void setValue(InternalArgument arg) { }

    public String forCommand() { return ""; }
}
