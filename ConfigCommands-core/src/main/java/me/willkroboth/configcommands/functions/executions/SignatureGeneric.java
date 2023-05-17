package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.List;

public class SignatureGeneric<Base, E extends Execution<?, ?>> implements Signature<E> {
    @FunctionalInterface
    public interface ExecutionGenerator<Base, E extends Execution<?, ?>> {
        Signature<E> get(Class<? extends InternalArgument<Base>> clazz);
    }

    private final Class<? extends InternalArgument<Base>> baseClass;
    private final ExecutionGenerator<Base, E> generator;

    public SignatureGeneric(Class<? extends InternalArgument<Base>> baseClass, ExecutionGenerator<Base, E> generator) {
        this.baseClass = baseClass;
        this.generator = generator;
    }

    // Cast is safe since we literally check `baseClass.isAssignableFrom(clazz)`
    @SuppressWarnings("unchecked")
    @Override
    public E findExecution(List<Class<? extends InternalArgument<?>>> parameterTypes) {
        for(Class<? extends InternalArgument<?>> clazz : InternalArgument.getRegisteredInternalArguments()) {
            if(baseClass.isAssignableFrom(clazz)) {
                E execution = generator.get((Class<? extends InternalArgument<Base>>) clazz).findExecution(parameterTypes);
                if(execution != null) return execution;
            }
        }
        return null;
    }

    @Override
    public void printExecutions(IndentedCommandSenderMessenger messenger) {
        generator.get(baseClass).printExecutions(messenger);
    }
}
