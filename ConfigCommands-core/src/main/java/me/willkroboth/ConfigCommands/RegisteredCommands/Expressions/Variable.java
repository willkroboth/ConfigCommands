package me.willkroboth.ConfigCommands.RegisteredCommands.Expressions;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.Map;

class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Class<? extends InternalArgument> getEvaluationType(Map<String, Class<? extends InternalArgument>> argumentClasses) {
        return argumentClasses.get(name);
    }

    @Override
    public InternalArgument evaluate(Map<String, InternalArgument> argumentVariables, boolean localDebug) throws CommandRunException {
        if (localDebug) {
            ConfigCommandsHandler.logNormal("Evaluating Variable");
            ConfigCommandsHandler.logNormal("Variable name is: %s", name);
            ConfigCommandsHandler.logNormal("Class %s with value %s ", argumentVariables.get(name).getClass().getSimpleName(), argumentVariables.get(name).forCommand());
        }
        return argumentVariables.get(name);
    }
}
