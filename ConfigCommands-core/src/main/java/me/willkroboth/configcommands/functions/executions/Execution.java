package me.willkroboth.configcommands.functions.executions;

import me.willkroboth.configcommands.functions.Parameter;
import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

public abstract class Execution<Run, Return> {
    private final Run run;
    private final Parameter<?>[] parameters;
    private final Class<? extends InternalArgument<Return>> returnClass;
    private final String returnMessage;

    public Execution(Run run, Class<? extends InternalArgument<Return>> returnClass, String returnMessage, Parameter<?>... parameters) {
        this.run = run;
        this.parameters = parameters;
        this.returnClass = returnClass;
        this.returnMessage = returnMessage;
    }

    public Run getRun() {
        return run;
    }

    public Parameter<?>[] getParameters() {
        return parameters;
    }

    public Class<? extends InternalArgument<Return>> getReturnClass() {
        return returnClass;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    @Override
    public String toString() {
        // TODO: this should probably be the name of the function, but we don't have that
        return "function";
    }

    public void sendInformation(IndentedCommandSenderMessenger messenger) {
        if (parameters.length == 0) {
            messenger.sendMessage("No parameters");
        } else {
            messenger.sendMessage("Parameters:");

            messenger.increaseIndentation();
            for (Parameter<?> parameter : parameters) {
                messenger.sendMessage("- " + parameter);
            }
            messenger.decreaseIndentation();
        }

        messenger.increaseIndentation();
        if (returnMessage == null) {
            messenger.sendMessage("Returns: " + InternalArgument.getNameForType(returnClass));
        } else {
            messenger.sendMessage("Returns: " + InternalArgument.getNameForType(returnClass) + " - " + returnMessage);
        }
        messenger.decreaseIndentation();
    }
}
