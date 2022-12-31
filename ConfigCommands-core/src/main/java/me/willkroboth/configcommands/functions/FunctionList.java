package me.willkroboth.configcommands.functions;

import me.willkroboth.configcommands.internalarguments.InternalArgument;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of generic {@link FunctionBuilder} objects. This class extends
 * {@link ArrayList} and so can be used as a {@code List<FunctionBuilder>}, but
 * it also contains various methods for searching for functions by name and
 * input parameters.
 *
 * @param <T> The subclass of {@link FunctionBuilder} this List holds
 */
public class FunctionList<T extends FunctionBuilder<T>> extends ArrayList<T> {
    /**
     * Checks if this FunctionList contains a function that can be called using the given name.
     *
     * @param name The name to search for.
     * @return True if this FunctionList contains a {@link FunctionBuilder} whose
     * name equals the given name or whose aliases contain the given name, and false otherwise.
     */
    public boolean hasName(String name) {
        for (T function : this) {
            if (function.getName().equals(name)) return true;
            if (function.getAliases().contains(name)) return true;
        }
        return false;
    }

    /**
     * Finds the {@link FunctionBuilder} in this list that can be called using the given name.
     *
     * @param name The name to search for.
     * @return A {@link FunctionBuilder} whose name equals the given name or whose aliases
     * contain the given name, or null if no such function is found.
     */
    @Nullable
    public T getByName(String name) {
        for (T function : this) {
            if (function.getName().equals(name)) return function;
            if (function.getAliases().contains(name)) return function;
        }
        return null;
    }

    /**
     * @return An array of Strings for every name and alias in this list.
     */
    public String[] getNames() {
        List<String> out = new ArrayList<>();
        for (T function : this) {
            out.add(function.getName());
            out.addAll(function.getAliases());
        }
        return out.toArray(String[]::new);
    }

    /**
     * Checks if this FunctionList contains a function that can be called using the given name and parameters.
     *
     * @param name           The name to look for.
     * @param parameterTypes A list of {@link InternalArgument} class objects that represent the parameters to look for.
     * @return True if a {@link FunctionBuilder} exists in this list whose name equals
     * the given name or whose aliases contain the given name and which can be called using
     * the given parameters, and false otherwise.
     */
    public boolean hasFunction(String name, List<Class<? extends InternalArgument>> parameterTypes) {
        for (T function : this) {
            if (function.getName().equals(name) || function.getAliases().contains(name)) {
                if (function.getParameters().size() == 0 && parameterTypes.size() == 0) return true;

                parameterLoop:
                for (Parameter[] parameters : function.getParameters()) {
                    if (parameters.length != parameterTypes.size()) continue;
                    int i = 0;
                    for (Parameter parameter : parameters) {
                        if (!parameter.getType().isAssignableFrom(parameterTypes.get(i))) continue parameterLoop;
                        i++;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the {@link FunctionBuilder} in this list that can be called using the given name and parameters.
     *
     * @param name           The name to look for.
     * @param parameterTypes A list of {@link InternalArgument} class objects that represent the parameters to look for.
     * @return A {@link FunctionBuilder} whose name equals the given name or whose
     * aliases contain the given name and which can be called using the given
     * parameters, or null if no such function is found.
     */
    @Nullable
    public T getFunction(String name, List<Class<? extends InternalArgument>> parameterTypes) {
        for (T function : this) {
            if (function.getName().equals(name) || function.getAliases().contains(name)) {
                if (function.getParameters().size() == 0 && parameterTypes.size() == 0) return function;

                parameterLoop:
                for (Parameter[] parameters : function.getParameters()) {
                    if (parameters.length != parameterTypes.size()) continue;
                    int i = 0;
                    for (Parameter parameter : parameters) {
                        if (!parameter.getType().isAssignableFrom(parameterTypes.get(i))) continue parameterLoop;
                        i++;
                    }
                    return function;
                }
            }
        }
        return null;
    }
}
