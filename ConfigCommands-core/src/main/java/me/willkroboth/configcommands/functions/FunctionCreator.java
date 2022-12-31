package me.willkroboth.configcommands.functions;

import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.List;

/**
 * An interface that can add methods to a class that make it easier to create
 * {@link FunctionList} instances.
 * <p>
 * There are four methods in this interface:
 * <ul>
 *     <li>{@link FunctionCreator#functions(InstanceFunction...)}</li>
 *     <li>{@link FunctionCreator#functions(StaticFunction...)}</li>
 *     <li>{@link FunctionCreator#merge(InstanceFunctionList...)}</li>
 *     <li>{@link FunctionCreator#merge(StaticFunctionList...)}</li>
 * </ul>
 */
public interface FunctionCreator {
    /**
     * Collects multiple {@link InstanceFunction} objects into a {@link InstanceFunctionList}.
     *
     * @param functions A variadic array of {@link InstanceFunction} objects.
     * @return A new {@link InstanceFunctionList} that contains all the given {@link InstanceFunction} objects.
     */
    default InstanceFunctionList functions(InstanceFunction... functions) {
        InstanceFunctionList out = new InstanceFunctionList();
        out.addAll(List.of(functions));
        return out;
    }

    /**
     * Collects multiple {@link StaticFunction} objects into a {@link StaticFunctionList}.
     *
     * @param functions A variadic array of {@link StaticFunction} objects.
     * @return A new {@link StaticFunctionList} that contains all the given {@link StaticFunction} objects.
     */
    default StaticFunctionList functions(StaticFunction... functions) {
        StaticFunctionList out = new StaticFunctionList();
        out.addAll(List.of(functions));
        return out;
    }

    /**
     * Collects multiple {@link InstanceFunctionList} objects into a single {@link InstanceFunctionList}.
     *
     * @param lists A variadic array of {@link InstanceFunctionList} objects.
     * @return A new {@link InstanceFunctionList} tht contains all the {@link InstanceFunction} objects from the given lists.
     */
    default InstanceFunctionList merge(InstanceFunctionList... lists) {
        InstanceFunctionList out = new InstanceFunctionList();
        for (InstanceFunctionList functions : lists) {
            out.addAll(functions);
        }
        return out;
    }

    /**
     * Collects multiple {@link InstanceFunctionList} objects into a single {@link InstanceFunctionList}.
     *
     * @param lists A variadic array of {@link InstanceFunctionList} objects.
     * @return A new {@link InstanceFunctionList} tht contains all the {@link InstanceFunction} objects from the given lists.
     */
    default StaticFunctionList merge(StaticFunctionList... lists) {
        StaticFunctionList out = new StaticFunctionList();
        for (StaticFunctionList functions : lists) {
            out.addAll(functions);
        }
        return out;
    }

    /**
     * @return The {@link InternalArgument} object class that belongs to this {@link FunctionCreator}.
     */
    Class<? extends InternalArgument> myClass();
}
