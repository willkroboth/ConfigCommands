package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

public class Function{
    private final InternalArgumentFunction function;
    private final Class<? extends InternalArgument> returnType;

    public Function(InternalArgumentFunction function, Class<? extends InternalArgument> returnType) {
        this.function = function;
        this.returnType = returnType;
    }

    public InternalArgumentFunction getFunction(){
        return function;
    }

    public Class<? extends InternalArgument> getReturnType() {
        return returnType;
    }

    public InternalArgument run(InternalArgument target, List<InternalArgument> args) { return function.apply(target, args); }
}
