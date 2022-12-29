package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalVoidArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expressions.Expression;

import java.util.List;

/**
 * A class for building functions that don't operate on an instance of an {@link InternalArgument}
 * and are called within {@link Expression}s.
 */
public class StaticFunction extends FunctionBuilder<StaticFunction> {
    // Executes

    /**
     * A {@link FunctionalInterface} that represents a static function that returns an {@link InternalArgument}.
     */
    @FunctionalInterface
    public interface InternalArgumentStaticFunction {
        /**
         * A functions that takes in some parameters and returns a {@link InternalArgument}.
         *
         * @param parameters A list of {@link InternalArgument} objects that are the parameters of this function.
         * @return The {@link InternalArgument} that is the result of this function.
         * @throws CommandRunException If there is an exception while performing this function.
         */
        InternalArgument apply(List<InternalArgument> parameters) throws CommandRunException;
    }

    /**
     * A {@link FunctionalInterface} that represents a static function that returns nothing.
     */
    @FunctionalInterface
    public interface InternalArgumentStaticFunctionVoidReturn {
        /**
         * A functions that takes in some parameters and returns nothing.
         *
         * @param parameters A list of {@link InternalArgument} objects that are the parameters of this function.
         * @throws CommandRunException If there is an exception while performing this function.
         */
        void apply(List<InternalArgument> parameters) throws CommandRunException;
    }

    private InternalArgumentStaticFunction executes;

    // Set information

    /**
     * Creates a new {@link StaticFunction} with the given name.
     *
     * @param name The name used to call this function.
     */
    public StaticFunction(String name) {
        super(name);
    }

    /**
     * Gives this {@link StaticFunction} a function to run when called in {@link Expression}s.
     *
     * @param executes An {@link InternalArgumentStaticFunction} for this function to run.
     * @return The current static function.
     */
    public StaticFunction executes(InternalArgumentStaticFunction executes) {
        this.executes = executes;

        return this;
    }

    /**
     * Gives this {@link StaticFunction} a function to run when called in {@link Expression}s.
     *
     * @param executes An {@link InternalArgumentStaticFunctionVoidReturn} for this function to run.
     * @return The current static function.
     */
    public StaticFunction executes(InternalArgumentStaticFunctionVoidReturn executes) {
        this.executes = (parameters) -> {
            executes.apply(parameters);
            return InternalVoidArgument.getInstance();
        };

        return this;
    }

    // Use information

    /**
     * Runs this function.
     *
     * @param parameters A list of {@link InternalArgument} objects that are the parameters of this function.
     * @return The {@link InternalArgument} that is the result of this function or {@link InternalVoidArgument} if
     * this function was defined not to return anything.
     */
    public InternalArgument run(List<InternalArgument> parameters) {
        return executes.apply(parameters);
    }
}
