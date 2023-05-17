package me.willkroboth.configcommands.registeredcommands.expressions;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class InstanceFunctionCall<Target, Return> extends Expression<Return> {
    private final Expression<Target> targetExpression;
    private final InstanceExecution<Target, Return> function;
    private final List<? extends Expression<?>> parameterExpressions;

    public InstanceFunctionCall(Expression<Target> targetExpression, InstanceExecution<Target, Return> function, List<? extends Expression<?>> parameterExpressions) {
        this.targetExpression = targetExpression;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    @Override
    public String toString() {
        return "(" + targetExpression.toString() + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    @Override
    public Class<? extends InternalArgument<Return>> getEvaluationType(Map<String, Class<? extends InternalArgument<?>>> argumentClasses) {
        return function.getReturnClass();
    }

    @Override
    public InternalArgument<Return> evaluate(Map<String, InternalArgument<?>> argumentVariables, boolean localDebug) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Evaluating FunctionCall");

        ConfigCommandsHandler.logDebug(localDebug, "Target expression is: %s", targetExpression);
        ConfigCommandsHandler.increaseIndentation();
        InternalArgument<Target> target = targetExpression.evaluate(argumentVariables, localDebug);
        ConfigCommandsHandler.decreaseIndentation();

        ConfigCommandsHandler.logDebug(localDebug, "Function is: " + function);

        List<InternalArgument<?>> parameters = new ArrayList<>();
        for (Expression<?> parameterExpression : parameterExpressions) {
            ConfigCommandsHandler.logDebug(localDebug, "Parameter expression is: %s", parameterExpression);
            ConfigCommandsHandler.increaseIndentation();
            parameters.add(parameterExpression.evaluate(argumentVariables, localDebug));
            ConfigCommandsHandler.decreaseIndentation();
        }

        ConfigCommandsHandler.logDebug(localDebug, "Running function");
        ConfigCommandsHandler.increaseIndentation();
        InternalArgument<Return> result = function.getRun().run(target, parameters);
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Result is %s with value %s", result, result.getValue());
        return result;
    }
}
