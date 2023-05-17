package me.willkroboth.configcommands.internalarguments;

import me.willkroboth.configcommands.functions.FunctionCreator;
import me.willkroboth.configcommands.functions.InstanceFunctionList;
import me.willkroboth.configcommands.functions.SafeFunctionCreator;
import me.willkroboth.configcommands.functions.StaticFunctionList;
import org.jetbrains.annotations.Nullable;

/**
 * A class used to add functions to an existing {@link InternalArgument}.
 * This class extends {@link FunctionAdder} to make it easier to build these functions.
 */
public abstract class FunctionAdder<T> extends SafeFunctionCreator<T> {
    /**
     * @return The {@link InternalArgument} class object of the {@link InternalArgument} this {@link FunctionAdder}
     * is adding functions to.
     */
    public abstract Class<? extends InternalArgument<T>> getClassToAddTo();

    @Override
    public Class<? extends InternalArgument<T>> myClass() {
        return getClassToAddTo();
    }

    /**
     * @return An {@link InstanceFunctionList} to add to the {@link InternalArgument}'s existing instance functions.
     * The default implementation of this method in {@link FunctionCreator} returns null to indicate that there are
     * no functions to add.
     */
    @Nullable
    public InstanceFunctionList<T> getAddedFunctions() {
        return null;
    }

    /**
     * @return An {@link StaticFunctionList} to add to the {@link InternalArgument}'s existing static functions.
     * The default implementation of this method in {@link FunctionCreator} returns null to indicate that there are
     * no functions to add.
     */
    @Nullable
    public StaticFunctionList getAddedStaticFunctions() {
        return null;
    }
}
