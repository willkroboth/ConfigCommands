package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

public class StaticFunction{
    private final InternalArgumentStaticFunction function;
    private final Class<? extends InternalArgument> returnType;

    public StaticFunction(InternalArgumentStaticFunction function, Class<? extends InternalArgument> returnType) {
        this.function = function;
        this.returnType = returnType;
    }

    public InternalArgumentStaticFunction getFunction(){
        return function;
    }

    public Class<? extends InternalArgument> getReturnType() {
        return returnType;
    }

    public InternalArgument run(List<InternalArgument> args) {
        return function.apply(args);
    }
}
