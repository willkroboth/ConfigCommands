package me.willkroboth.configcommands.functions;

import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalVoidArgument;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;

import java.util.List;

/**
 * A class for building functions that operate on an instance of an {@link InternalArgument}
 * and are called within {@link Expression}s.
 */
public class InstanceFunction extends FunctionBuilder<InstanceFunction> {
    // Executes

    /**
     * A {@link FunctionalInterface} that represents an instance function that returns an {@link InternalArgument}.
     */
    @FunctionalInterface
    public interface InternalArgumentInstanceFunction {
        /**
         * A functions that operates on a target instance of an {@link InternalArgument}, taking in some parameters,
         * and returns a {@link InternalArgument}.
         *
         * @param target     The instance of an {@link InternalArgument} this function is operating on.
         * @param parameters A list of {@link InternalArgument} objects that are the parameters of this function.
         * @return The {@link InternalArgument} that is the result of this function.
         * @throws CommandRunException If there is an exception while performing this function.
         */
        InternalArgument apply(InternalArgument target, List<InternalArgument> parameters) throws CommandRunException;
    }

    /**
     * A {@link FunctionalInterface} that represents an instance function that returns nothing.
     */
    @FunctionalInterface
    public interface InternalArgumentInstanceFunctionVoidReturn {
        /**
         * A functions that operates on a target instance of an {@link InternalArgument}, taking in some parameters,
         * and returns nothing.
         *
         * @param target     The instance of an {@link InternalArgument} this function is operating on.
         * @param parameters A list of {@link InternalArgument} objects that are the parameters of this function.
         * @throws CommandRunException If there is an exception while performing this function.
         */
        void apply(InternalArgument target, List<InternalArgument> parameters) throws CommandRunException;
    }

    private InternalArgumentInstanceFunction executes;

    // Set information

    /**
     * Creates a new {@link InstanceFunction} with the given name.
     *
     * @param name The name used to call this function.
     */
    public InstanceFunction(String name) {
        super(name);
    }

    /**
     * Gives this {@link InstanceFunction} a function to run when called in {@link Expression}s.
     *
     * @param executes An {@link InternalArgumentInstanceFunction} for this function to run.
     * @return The current instance function.
     */
    public InstanceFunction executes(InternalArgumentInstanceFunction executes) {
        this.executes = executes;

        return this;
    }

    /**
     * Gives this {@link InstanceFunction} a function to run when called in {@link Expression}s.
     *
     * @param executes An {@link InternalArgumentInstanceFunctionVoidReturn} for this function to run.
     * @return The current instance function.
     */
    public InstanceFunction executes(InternalArgumentInstanceFunctionVoidReturn executes) {
        this.executes = (target, parameters) -> {
            executes.apply(target, parameters);
            return InternalVoidArgument.getInstance();
        };

        return this;
    }

    // Use information

    /**
     * Runs this function.
     *
     * @param target     The instance of an {@link InternalArgument} this function is operating on.
     * @param parameters A list of {@link InternalArgument} objects that are the parameters of this function.
     * @return The {@link InternalArgument} that is the result of this function or {@link InternalVoidArgument} if
     * this function was defined not to return anything.
     */
    public InternalArgument run(InternalArgument target, List<InternalArgument> parameters) {
        return executes.apply(target, parameters);
    }
}
