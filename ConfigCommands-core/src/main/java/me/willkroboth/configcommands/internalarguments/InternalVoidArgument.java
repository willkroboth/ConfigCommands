package me.willkroboth.configcommands.internalarguments;

import me.willkroboth.configcommands.functions.InstanceFunction;
import me.willkroboth.configcommands.functions.InstanceFunctionList;
import me.willkroboth.configcommands.functions.StaticFunction;
import me.willkroboth.configcommands.functions.StaticFunctionList;

/**
 * A special {@link InternalArgument} that gets returned from functions that don't return anything.
 */
public class InternalVoidArgument extends InternalArgument {
    // singleton class is best for this because it never stores a value
    private static final InternalVoidArgument instance = new InternalVoidArgument();

    // InternalArgument class needs to be able to initialize object when registering
    // Otherwise, the singleton instance should be used

    /**
     * Creates a new {@link InternalVoidArgument}.
     */
    protected InternalVoidArgument() {
    }

    /**
     * @return The singleton instance of {@link InternalVoidArgument}.
     */
    public static InternalVoidArgument getInstance() {
        return instance;
    }

    @Override
    public InstanceFunctionList getInstanceFunctions() {
        return functions(new InstanceFunction[0]);
    }

    @Override
    public StaticFunctionList getStaticFunctions() {
        return functions(new StaticFunction[0]);
    }

    // not used
    @Override
    public void setValue(Object arg) {
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(InternalArgument arg) {
    }

    @Override
    public String forCommand() {
        return "";
    }
}
