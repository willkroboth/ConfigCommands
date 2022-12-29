package me.willkroboth.ConfigCommands.RegisteredCommands.Expressions;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class StaticFunctionCall extends Expression {
    private final InternalArgument targetClass;
    private final String function;
    private final List<? extends Expression> parameterExpressions;

    StaticFunctionCall(InternalArgument targetClass, String function, List<? extends Expression> parameterExpressions) {
        this.targetClass = targetClass;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    @Override
    public String toString() {
        return "(" + targetClass.toString() + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    @Override
    public Class<? extends InternalArgument> getEvaluationType(Map<String, Class<? extends InternalArgument>> argumentClasses) {
        List<Class<? extends InternalArgument>> parameters = new ArrayList<>();
        for (Expression parameterExpression : parameterExpressions) {
            parameters.add(parameterExpression.getEvaluationType(argumentClasses));
        }

        return targetClass.getReturnTypeForStaticFunction(function, parameters);
    }

    @Override
    public InternalArgument evaluate(Map<String, InternalArgument> argumentVariables, boolean localDebug) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Evaluating StaticFunctionCall");

        ConfigCommandsHandler.logDebug(localDebug, "Target class is: %s", targetClass);

        ConfigCommandsHandler.logDebug(localDebug, "Function is: %s", function);

        List<InternalArgument> parameters = new ArrayList<>();
        for (Expression parameterExpression : parameterExpressions) {
            ConfigCommandsHandler.logDebug(localDebug, "Parameter expression is: %s", parameterExpression);
            ConfigCommandsHandler.increaseIndentation();
            parameters.add(parameterExpression.evaluate(argumentVariables, localDebug));
            ConfigCommandsHandler.decreaseIndentation();
        }

        ConfigCommandsHandler.logDebug(localDebug, "Running function");
        ConfigCommandsHandler.increaseIndentation();
        InternalArgument result = targetClass.runStaticFunction(function, parameters);
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Result is %s with value %s", result, result.getValue());
        return result;
    }
}
