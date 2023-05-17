package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.functions.Parameter;
import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;

public class SignatureParameterized<E extends Execution<?, ?>> implements Signature<E> {
    private final List<Class<? extends InternalArgument<?>>> parameterTypes;
    private final E execution;

    public SignatureParameterized(List<Class<? extends InternalArgument<?>>> parameterTypes, E execution) {
        this.parameterTypes = parameterTypes;
        this.execution = execution;
    }

    public static <E extends Execution<?, ?>> SignatureParameterized<E> of(E execution) {
        List<Class<? extends InternalArgument<?>>> parameterTypes = new ArrayList<>(execution.getParameters().length);
        for(Parameter<?> parameter : execution.getParameters()) {
            parameterTypes.add(parameter.getType());
        }
        return new SignatureParameterized<>(parameterTypes, execution);
    }

    @Override
    public E findExecution(List<Class<? extends InternalArgument<?>>> parameterTypes) {
        if(this.parameterTypes.size() != parameterTypes.size()) return null;
        for(int i = 0; i < parameterTypes.size(); i++) {
            if(!this.parameterTypes.get(i).isAssignableFrom(parameterTypes.get(i))) return null;
        }
        return execution;
    }

    @Override
    public void printExecutions(IndentedCommandSenderMessenger messenger) {
        execution.sendInformation(messenger);
    }
}
