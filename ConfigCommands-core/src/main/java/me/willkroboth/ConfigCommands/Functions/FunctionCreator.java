package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

public interface FunctionCreator {
    default FunctionList functions(Function... functions) {
        FunctionList out = new FunctionList();
        out.addAll(List.of(functions));
        return out;
    }
    default StaticFunctionList functions(StaticFunction... functions) {
        StaticFunctionList out = new StaticFunctionList();
        out.addAll(List.of(functions));
        return out;
    }

    default FunctionList merge(FunctionList... lists) {
        FunctionList out = new FunctionList();
        for (FunctionList functions : lists) {
            out.addAll(functions);
        }
        return out;
    }

    default StaticFunctionList merge(StaticFunctionList... lists) {
        StaticFunctionList out = new StaticFunctionList();
        for (StaticFunctionList functions : lists) {
            out.addAll(functions);
        }
        return out;
    }

    Class<? extends InternalArgument> myClass();
}
