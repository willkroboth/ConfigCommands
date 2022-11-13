package me.willkroboth.ConfigCommands.RegisteredCommands.Expressions;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.ArgList;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FunctionCall extends Expression {
    private final Expression targetExpression;
    private final String function;
    private final List<? extends Expression> parameterExpressions;

    public FunctionCall(Expression targetExpression, String function, List<? extends Expression> parameterExpressions) {
        this.targetExpression = targetExpression;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    public String toString() {
        return "(" + targetExpression.toString() + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    public Class<? extends InternalArgument> getEvaluationType(Map<String, Class<? extends InternalArgument>> argumentClasses) {
        InternalArgument target = InternalArgument.getInternalArgument(targetExpression.getEvaluationType(argumentClasses));

        ArgList parameters = new ArgList();
        for (Expression parameterExpression : parameterExpressions) {
            parameters.add(parameterExpression.getEvaluationType(argumentClasses));
        }

        return target.getReturnTypeForFunction(function, parameters);
    }

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
        InternalArgument result = target.runFunction(function, parameters);
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Result is %s with value %s", result, result.getValue());
        return result;
    }
}
