package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

public class Function extends AbstractFunction<Function> {
    // Executes
    @FunctionalInterface
    public interface InternalArgumentFunction {
        InternalArgument apply(InternalArgument target, List<InternalArgument> parameters);
    }

    private InternalArgumentFunction executes;

    // Set information
    public Function(String name) {
        super(name);
    }

    public Function executes(InternalArgumentFunction executes) {
        this.executes = executes;

        return this;
    }

    // Use information
    public InternalArgument run(InternalArgument target, List<InternalArgument> parameters) {
        return executes.apply(target, parameters);
    }
}
