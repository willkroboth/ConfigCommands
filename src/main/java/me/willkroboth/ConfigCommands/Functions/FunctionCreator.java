package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.*;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.*;

public interface FunctionCreator {
    //methods to make declaring functions easier
    default FunctionList merge(FunctionList... lists){
        FunctionList out = new FunctionList();
        List<Definition> taken = new ArrayList<>();

        for(FunctionList list:lists){
            for(FunctionEntry entry: list) {
                if(!taken.contains(entry.getKey())) {
                    out.add(entry);
                    taken.add(entry.getKey());
                }
            }
        }

        return out;
    }

    default StaticFunctionList staticMerge(StaticFunctionList... lists){
        StaticFunctionList out = new StaticFunctionList();
        List<Definition> taken = new ArrayList<>();

        for(StaticFunctionList list:lists){
            for(StaticFunctionEntry entry: list) {
                if(!taken.contains(entry.getKey())) {
                    out.add(entry);
                    taken.add(entry.getKey());
                }
            }
        }

        return out;
    }

    default FunctionEntry entry(Definition definition, Function function){
        return new FunctionEntry(definition, function);
    }

    default StaticFunctionEntry staticEntry(Definition definition, StaticFunction function){
        return new StaticFunctionEntry(definition, function);
    }

    default FunctionList entries(FunctionEntry... entriesList){
        FunctionList out = new FunctionList();
        out.addAll(List.of(entriesList));
        return out;
    }

    default StaticFunctionList staticEntries(StaticFunctionEntry... entriesList){
        StaticFunctionList out = new StaticFunctionList();
        out.addAll(List.of(entriesList));
        return out;
    }

    // providing hardcoded args() for 0 to 3 to avoid warnings in many cases
    default ArgList args(){
        return new ArgList();
    }

    default ArgList args(Class<? extends InternalArgument> arg1){
        ArgList out = new ArgList();
        out.add(arg1);
        return out;
    }

    default ArgList args(Class<? extends InternalArgument> arg1, Class<? extends InternalArgument> arg2){
        ArgList out = new ArgList();
        out.addAll(List.of(arg1, arg2));
        return out;
    }

    default ArgList args(Class<? extends InternalArgument> arg1, Class<? extends InternalArgument> arg2,
                         Class<? extends InternalArgument> arg3){
        ArgList out = new ArgList();
        out.addAll(List.of(arg1, arg2, arg3));
        return out;
    }

    default ArgList args(Class<? extends InternalArgument>... args){
        ArgList out = new ArgList();
        out.addAll(List.of(args));
        return out;
    }

    default NestedArgList args(ArgList... args){
        return new NestedArgList(args);
    }

    default List<String> strings(String... strings){
        return List.of(strings);
    }

    default FunctionList expandDefinition(List<String> names, NestedArgList args, Function function){
        FunctionList functions = new FunctionList();
        for(String name:names){
            for(ArgList arg:args){
                functions.add(entry(new Definition(name, arg), function));
            }
        }
        return functions;
    }

    default StaticFunctionList staticExpandDefinition(List<String> names, NestedArgList args, StaticFunction function){
        StaticFunctionList functions = new StaticFunctionList();
        for(String name:names){
            for(ArgList arg:args){
                functions.add(staticEntry(new Definition(name, arg), function));
            }
        }
        return functions;
    }

    Class<? extends InternalArgument> myClass();
}
