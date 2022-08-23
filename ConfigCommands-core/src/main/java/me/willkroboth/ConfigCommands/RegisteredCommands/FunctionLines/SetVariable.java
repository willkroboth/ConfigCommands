package me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax.InvalidSetVariable;
import me.willkroboth.ConfigCommands.Exceptions.ParseException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.CompilerState;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expression;
import me.willkroboth.ConfigCommands.RegisteredCommands.InterpreterState;

import java.util.Arrays;
import java.util.List;

class SetVariable extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidSetVariable, ParseException {
        String[] parts = compilerState.getCommand().split(" = ");
        if (parts.length != 2)
            throw new InvalidSetVariable(compilerState.getCommand(), "Invalid format. Must contain only one \" = \".");

        ConfigCommandsHandler.logDebug(compilerState, "Set split into: %s", Arrays.toString(parts));

        String variable = parts[0];
        if (!(variable.charAt(0) == '<' && variable.charAt(variable.length() - 1) == '>')) {
            throw new InvalidSetVariable(compilerState.getCommand(), "Invalid variable: " + variable + ". Must be wrapped by < >.");
        }
        ConfigCommandsHandler.logDebug(compilerState, "Variable is " + variable);

        String rawExpression = parts[1];
        Class<? extends InternalArgument> returnType;
        boolean usingExpression = !rawExpression.startsWith("/");
        Object value;
        if (usingExpression) {
            // expression is code
            ConfigCommandsHandler.logDebug(compilerState, "Parsing \"%s\" as expression.", rawExpression);
            Expression expression = Expression.parseExpression(rawExpression, compilerState.getArgumentClasses(), compilerState.isDebug());
            returnType = expression.getEvaluationType(compilerState.getArgumentClasses());

            value = expression;
        } else {
            // expression is command
            ConfigCommandsHandler.logDebug(compilerState, "Expression looks like a command.");
            ConfigCommandsHandler.logDebug(compilerState, "Parsing \"%s\" as command.", rawExpression);

            // parse command
            List<String> commandSections = RunCommand.getCommandSections(rawExpression, compilerState);
            returnType = InternalStringArgument.class;

            value = commandSections;
        }

        if (compilerState.hasVariable(variable)) {
            Class<? extends InternalArgument> currentType = compilerState.getVariable(variable);
            if (!returnType.isAssignableFrom(currentType)) {
                throw new InvalidSetVariable(compilerState.getCommand(), "Wrong type. Set variable(" + variable +
                        ") was previously made as " + currentType.getSimpleName() +
                        ", but is now being set as " + returnType.getSimpleName());
            }
            ConfigCommandsHandler.logDebug(compilerState, "%s already found in argument keys, and the return type matches.", variable);
        } else {
            compilerState.addArgument(variable, returnType);
            ConfigCommandsHandler.logDebug("%s not found in arguments keys, so it was created to match return type.", variable);
        }

        return new SetVariable(variable, usingExpression, value);
    }

    private final String variableName;
    private final boolean usingExpression;
    private final Object value;

    private SetVariable(String variableName, boolean usingExpression, Object value) {
        this.variableName = variableName;
        this.usingExpression = usingExpression;
        this.value = value;
    }

    @Override
    public String toString() {
        return variableName + " = " + value.toString();
    }

    @Override
    public int run(InterpreterState interpreterState) {
        ConfigCommandsHandler.logDebug(interpreterState, "Variable is %s", variableName);
        ConfigCommandsHandler.logDebug(interpreterState, "expressionType is %s", usingExpression ? "expression" : "command");

        if (usingExpression) {
            Expression expression = (Expression) value;
            ConfigCommandsHandler.logDebug(interpreterState, "Expression is %s", expression);

            ConfigCommandsHandler.increaseIndentation();
            InternalArgument result = expression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug());
            ConfigCommandsHandler.decreaseIndentation();

            ConfigCommandsHandler.logDebug(interpreterState, "Variable will be set to: %s", result.getValue());
            interpreterState.setVariable(variableName, result);
        } else {
            @SuppressWarnings("unchecked")
            String stringResult = RunCommand.runCommandGetResult((List<String>) value, interpreterState);

            ConfigCommandsHandler.logDebug(interpreterState, "Variable will be set to: %s", stringResult);
            interpreterState.setVariable(variableName, stringResult);
        }

        return interpreterState.nextIndex();
    }
}
