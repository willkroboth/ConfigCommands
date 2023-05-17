package me.willkroboth.configcommands.functions;

import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;

/**
 * A class for building functions that operate on an instance of an {@link InternalArgument}
 * and are called within {@link Expression}s.
 */
public class InstanceFunction<Target> extends FunctionBuilder<InstanceExecution<Target, ?>, InstanceFunction<Target>> {

    // Set information

    /**
     * Creates a new {@link InstanceFunction} with the given name.
     *
     * @param name The name used to call this function.
     */
    protected InstanceFunction(String name) {
        super(name);
    }
}
