package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.FunctionCreator;
import me.willkroboth.ConfigCommands.Functions.FunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunctionList;

public abstract class FunctionAdder implements FunctionCreator {
    public abstract Class<? extends InternalArgument> getClassToAddTo();

    @Override
    public Class<? extends InternalArgument> myClass() { return getClassToAddTo(); }

    public FunctionList getAddedFunctions(){ return null; }

    public StaticFunctionList getAddedStaticFunctions(){ return null; }
}
