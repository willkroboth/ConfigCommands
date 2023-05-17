package me.willkroboth.configcommands.registeredcommands.expressions;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.Map;

class Variable<Return> extends Expression<Return> {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Class<? extends InternalArgument<Return>> getEvaluationType(Map<String, Class<? extends InternalArgument<?>>> argumentClasses) {
        return (Class<? extends InternalArgument<Return>>) argumentClasses.get(name);
    }

    @Override
    public InternalArgument<Return> evaluate(Map<String, InternalArgument<?>> argumentVariables, boolean localDebug) throws CommandRunException {
        if (localDebug) {
            ConfigCommandsHandler.logNormal("Evaluating Variable");
            ConfigCommandsHandler.logNormal("Variable name is: %s", name);
            ConfigCommandsHandler.logNormal("Class %s with value %s ", argumentVariables.get(name).getClass().getSimpleName(), argumentVariables.get(name).forCommand());
        }
        return (InternalArgument<Return>) argumentVariables.get(name);
    }
}
