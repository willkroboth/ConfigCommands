package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.List;

public interface Signature<E extends Execution<?, ?>> {
    E findExecution(List<Class<? extends InternalArgument<?>>> parameterTypes);

    void printExecutions(IndentedCommandSenderMessenger messenger);
}
