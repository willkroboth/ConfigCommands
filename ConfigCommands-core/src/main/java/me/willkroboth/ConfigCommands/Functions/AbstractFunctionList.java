package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;

public class AbstractFunctionList<T extends AbstractFunction<T>> extends ArrayList<T> {
    public boolean hasName(String name) {
        for (T function : this) {
            if (function.getName().equals(name)) return true;
            if (function.getAliases().contains(name)) return true;
        }
        return false;
    }

    public T getFromName(String name) {
        for (T function : this) {
            if (function.getName().equals(name)) return function;
            if (function.getAliases().contains(name)) return function;
        }
        return null;
    }

    public String[] getNames() {
        List<String> out = new ArrayList<>();
        for (T function : this) {
            out.add(function.getName());
            out.addAll(function.getAliases());
        }
        return out.toArray(String[]::new);
    }

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
