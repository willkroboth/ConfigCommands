package me.willkroboth.configcommands.registeredcommands.expressions;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class InstanceFunctionCall extends Expression {
    private final Expression targetExpression;
    private final String function;
    private final List<? extends Expression> parameterExpressions;

    public InstanceFunctionCall(Expression targetExpression, String function, List<? extends Expression> parameterExpressions) {
        this.targetExpression = targetExpression;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    @Override
    public String toString() {
        return "(" + targetExpression.toString() + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    @Override
    public Class<? extends InternalArgument> getEvaluationType(Map<String, Class<? extends InternalArgument>> argumentClasses) {
        InternalArgument target = InternalArgument.getInternalArgument(targetExpression.getEvaluationType(argumentClasses));

        List<Class<? extends InternalArgument>> parameters = new ArrayList<>();
        for (Expression parameterExpression : parameterExpressions) {
            parameters.add(parameterExpression.getEvaluationType(argumentClasses));
        }

        return target.getReturnTypeForInstanceFunction(function, parameters);
    }

    @Override
    public InternalArgument evaluate(Map<String, InternalArgument> argumentVariables, boolean localDebug) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Evaluating FunctionCall");

        ConfigCommandsHandler.logDebug(localDebug, "Target expression is: %s", targetExpression);
        ConfigCommandsHandler.increaseIndentation();
        InternalArgument target = targetExpression.evaluate(argumentVariables, localDebug);
        ConfigCommandsHandler.decreaseIndentation();

        ConfigCommandsHandler.logDebug(localDebug, "Function is: " + function);

        List<InternalArgument> parameters = new ArrayList<>();
        for (Expression parameterExpression : parameterExpressions) {
            ConfigCommandsHandler.logDebug(localDebug, "Parameter expression is: %s", parameterExpression);
            ConfigCommandsHandler.increaseIndentation();
            parameters.add(parameterExpression.evaluate(argumentVariables, localDebug));
            ConfigCommandsHandler.decreaseIndentation();
        }

        ConfigCommandsHandler.logDebug(localDebug, "Running function");
        ConfigCommandsHandler.increaseIndentation();
        InternalArgument result = target.runInstanceFunction(function, parameters);
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Result is %s with value %s", result, result.getValue());
        return result;
    }
}
