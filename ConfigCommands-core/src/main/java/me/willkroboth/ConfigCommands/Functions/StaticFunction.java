package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalVoidArgument;

import java.util.List;

public class StaticFunction extends AbstractFunction<StaticFunction> {
    // Executes
    @FunctionalInterface
    public interface InternalArgumentStaticFunction {
        InternalArgument apply(List<InternalArgument> parameters) throws CommandRunException;
    }

    @FunctionalInterface
    public interface InternalArgumentStaticFunctionVoidReturn {
        void apply(List<InternalArgument> parameters) throws CommandRunException;
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

    public StaticFunction executes(InternalArgumentStaticFunctionVoidReturn executes) {
        this.executes = (parameters) -> {
            executes.apply(parameters);
            return InternalVoidArgument.getInstance();
        };

        return this;
    }

    // Use information
    public InternalArgument run(List<InternalArgument> parameters) {
        return executes.apply(parameters);
    }
}
