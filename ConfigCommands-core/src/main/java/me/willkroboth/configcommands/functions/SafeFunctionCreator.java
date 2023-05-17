package me.willkroboth.configcommands.functions;

public abstract class SafeFunctionCreator<T> implements FunctionCreator<T> {
    @SafeVarargs
    @Override
    public final InstanceFunctionList<T> functions(InstanceFunction<T>... functions) {
        return FunctionCreator.super.functions(functions);
    }

    @SafeVarargs
    @Override
    public final InstanceFunctionList<T> merge(InstanceFunctionList<T>... lists) {
        return FunctionCreator.super.merge(lists);
    }
}
