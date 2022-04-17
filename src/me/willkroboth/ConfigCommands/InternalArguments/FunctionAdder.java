package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.FunctionCreator;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;

public abstract class FunctionAdder implements FunctionCreator {
    public abstract Class<? extends InternalArgument> getClassToAddTo();

    public Class<? extends InternalArgument> myClass() { return getClassToAddTo(); }

    public FunctionList getAddedFunctions(){ return null; }

    public StaticFunctionList getAddedStaticFunctions(){ return null; }
}
