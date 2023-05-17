package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;

public class SignatureMerge<E extends Execution<?, ?>> implements Signature<E> {
    private final List<Signature<E>> signatures = new ArrayList<>();

    // The cast totally works, I can't figure out why it complains
    // I also can't figure out why it needs to be Signature<? **extends** E>..., but that's just how it works
    // Very silly
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <E extends Execution<?, ?>> Signature<E> merge(Signature<E> original, Signature<? extends E>... toMerge) {
        SignatureMerge<E> merged = new SignatureMerge<>();

        addExecution(merged, original);

        for (Signature<? extends E> signature : toMerge) {
            addExecution(merged, (Signature<E>) signature);
        }

        if(merged.signatures.size() == 0) return new SignatureEmpty<>();
        if(merged.signatures.size() == 1) return merged.signatures.get(0);
        return merged;
    }

    private static <E extends Execution<?, ?>> void addExecution(SignatureMerge<E> merged, Signature<E> toMerge) {
        if(toMerge instanceof SignatureEmpty<E>) return;
        if(toMerge instanceof SignatureMerge<E> manyToMerge) {
            for (Signature<E> subToMerge : manyToMerge.signatures)  {
                addExecution(merged, subToMerge);
            }
        }
        merged.signatures.add(toMerge);
    }

    @Override
    public E findExecution(List<Class<? extends InternalArgument<?>>> parameterTypes) {
        for (Signature<E> signature : signatures) {
            E found = signature.findExecution(parameterTypes);
            if(found != null) return found;
        }
        return null;
    }

    @Override
    public void printExecutions(IndentedCommandSenderMessenger messenger) {
        messenger.sendMessage("Multiple input combinations available");
        messenger.increaseIndentation();
        for (Signature<E> signature : signatures) {
            signature.printExecutions(messenger);
        }
        messenger.decreaseIndentation();
    }
}
