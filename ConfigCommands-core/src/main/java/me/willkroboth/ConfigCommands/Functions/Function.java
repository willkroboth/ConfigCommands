package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalVoidArgument;

import java.util.List;

public class Function extends AbstractFunction<Function> {
    // Executes
    @FunctionalInterface
    public interface InternalArgumentFunction {
        InternalArgument apply(InternalArgument target, List<InternalArgument> parameters) throws CommandRunException;
    }

    @FunctionalInterface
    public interface InternalArgumentFunctionVoidReturn {
        void apply(InternalArgument target, List<InternalArgument> parameters) throws CommandRunException;
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

    public Function executes(InternalArgumentFunctionVoidReturn executes) {
        this.executes = (target, parameters) -> {
            executes.apply(target, parameters);
            return InternalVoidArgument.getInstance();
        };

        return this;
    }

    // Use information
    public InternalArgument run(InternalArgument target, List<InternalArgument> parameters) {
        return executes.apply(target, parameters);
    }
}
