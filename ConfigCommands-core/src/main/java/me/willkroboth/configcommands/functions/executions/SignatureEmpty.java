package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.List;

public class SignatureEmpty<E extends Execution<?, ?>> implements Signature<E> {
    @Override
    public E findExecution(List<Class<? extends InternalArgument<?>>> parameterTypes) {
        return null;
    }

    @Override
    public void printExecutions(IndentedCommandSenderMessenger messenger) {
        messenger.sendMessage("No executions were set for this function! Not runnable.");
    }
}
