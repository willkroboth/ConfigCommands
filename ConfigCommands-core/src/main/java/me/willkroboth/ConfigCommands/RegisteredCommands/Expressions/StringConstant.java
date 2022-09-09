package me.willkroboth.ConfigCommands.RegisteredCommands.Expressions;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;

import java.util.Map;

class StringConstant extends Expression {
    private final InternalStringArgument value;

    public StringConstant(String value) {
        this.value = new InternalStringArgument(value);
    }

    public String toString() {
        return "\"" + value.getValue() + "\"";
    }

    public Class<? extends InternalArgument> getEvaluationType(Map<String, Class<? extends InternalArgument>> argumentClasses) {
        return InternalStringArgument.class;
    }

    public InternalArgument evaluate(Map<String, InternalArgument> argumentVariables, boolean localDebug) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Evaluating Constant");
        ConfigCommandsHandler.logDebug(localDebug, "Constant is %s", this);
        return value;
    }
}
