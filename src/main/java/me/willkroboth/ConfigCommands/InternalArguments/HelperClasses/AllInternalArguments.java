package me.willkroboth.ConfigCommands.InternalArguments.HelperClasses;

import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.ArgList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.NestedArgList;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

public final class AllInternalArguments{
    private static final NestedArgList allClasses = new NestedArgList();
    private static final ArgList flatClasses = new ArgList();

    public static void addToAllClasses(Class<? extends InternalArgument> clazz){
        ArgList single = new ArgList();
        single.add(clazz);
        allClasses.add(single);
        flatClasses.add(clazz);
    }

    public static NestedArgList get(){ return allClasses; }

    public static ArgList getFlat(){ return flatClasses; }
}
