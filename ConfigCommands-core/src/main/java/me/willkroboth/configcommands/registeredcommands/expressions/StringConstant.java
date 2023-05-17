package me.willkroboth.configcommands.registeredcommands.expressions;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalStringArgument;

import java.util.Map;

class StringConstant extends Expression<String> {
    private final InternalStringArgument value;

    public StringConstant(String value) {
        this.value = new InternalStringArgument(value);
    }

    @Override
    public String toString() {
        return "\"" + value.getValue() + "\"";
    }

    @Override
    public Class<? extends InternalArgument<String>> getEvaluationType(Map<String, Class<? extends InternalArgument<?>>> argumentClasses) {
        return InternalStringArgument.class;
    }

    @Override
    public InternalArgument<String> evaluate(Map<String, InternalArgument<?>> argumentVariables, boolean localDebug) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Evaluating Constant");
        ConfigCommandsHandler.logDebug(localDebug, "Constant is %s", this);
        return value;
    }
}
