package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

public class StaticFunction extends AbstractFunction<StaticFunction> {
    // Executes
    @FunctionalInterface
    public interface InternalArgumentStaticFunction {
        InternalArgument apply(List<InternalArgument> parameters);
    }
    private InternalArgumentStaticFunction executes;

    // Set information
    public StaticFunction(String name) {
        super(name);
    }

    public StaticFunction executes(InternalArgumentStaticFunction executes) {
        this.executes = executes;

        return this;
    }

    // Use information
    public InternalArgument run(List<InternalArgument> parameters) {
        return executes.apply(parameters);
    }
}
