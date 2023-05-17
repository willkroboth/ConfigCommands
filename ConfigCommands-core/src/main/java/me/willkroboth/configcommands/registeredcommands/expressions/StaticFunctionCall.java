package me.willkroboth.configcommands.registeredcommands.expressions;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.functions.executions.StaticExecution;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class StaticFunctionCall<Return> extends Expression<Return> {
    private final String className;
    private final StaticExecution<Return> function;
    private final List<? extends Expression<?>> parameterExpressions;

    StaticFunctionCall(String className, StaticExecution<Return> function, List<? extends Expression<?>> parameterExpressions) {
        this.className = className;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    @Override
    public String toString() {
        return "(" + className + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    @Override
    public Class<? extends InternalArgument<Return>> getEvaluationType(Map<String, Class<? extends InternalArgument<?>>> argumentClasses) {
        return function.getReturnClass();
    }

    @Override
    public InternalArgument<Return> evaluate(Map<String, InternalArgument<?>> argumentVariables, boolean localDebug) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Evaluating StaticFunctionCall");

        ConfigCommandsHandler.logDebug(localDebug, "Static class is: %s", className);

        ConfigCommandsHandler.logDebug(localDebug, "Function is: %s", function);

        List<InternalArgument<?>> parameters = new ArrayList<>();
        for (Expression<?> parameterExpression : parameterExpressions) {
            ConfigCommandsHandler.logDebug(localDebug, "Parameter expression is: %s", parameterExpression);
            ConfigCommandsHandler.increaseIndentation();
            parameters.add(parameterExpression.evaluate(argumentVariables, localDebug));
            ConfigCommandsHandler.decreaseIndentation();
        }

        ConfigCommandsHandler.logDebug(localDebug, "Running function");
        ConfigCommandsHandler.increaseIndentation();
        InternalArgument<Return> result = function.getRun().run(parameters);
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Result is %s with value %s", result, result.getValue());
        return result;
    }
}
