package me.willkroboth.configcommands.registeredcommands.functionlines;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.functionsyntax.InvalidIfCommand;
import me.willkroboth.configcommands.exceptions.ParseException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalBooleanArgument;
import me.willkroboth.configcommands.internalarguments.InternalIntegerArgument;
import me.willkroboth.configcommands.internalarguments.InternalStringArgument;
import me.willkroboth.configcommands.registeredcommands.CompilerState;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;
import me.willkroboth.configcommands.registeredcommands.InterpreterState;

import java.util.Arrays;

class BranchIf extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidIfCommand, ParseException {
        if (!compilerState.getCommand().startsWith("if "))
            throw new InvalidIfCommand(compilerState.getCommand(), "Invalid format. Must start with \"if \"");

        String command = compilerState.getCommand().substring(3);
        ConfigCommandsHandler.logDebug(compilerState, "If trimmed off to get: %s", command);

        String[] parts = command.split(" goto ");
        if (parts.length != 2) throw new InvalidIfCommand(command, "Invalid format. Must contain \" goto \" once.");
        ConfigCommandsHandler.logDebug(compilerState, "If split into: %s", Arrays.toString(parts));

        String booleanString = parts[0];
        ConfigCommandsHandler.logDebug(compilerState, "booleanExpression is: %s", booleanString);
        Expression booleanExpression =
                Expression.parseExpression(booleanString, compilerState.getArgumentClasses(), compilerState.isDebug());
        ConfigCommandsHandler.logDebug(compilerState, "booleanExpression parsed to: %s", booleanExpression);
        Class<? extends InternalArgument> returnType = booleanExpression.getEvaluationType(compilerState.getArgumentClasses());
        if (!returnType.isAssignableFrom(InternalBooleanArgument.class))
            throw new InvalidIfCommand(command, "Invalid booleanExpression. Must return InternalBooleanArgument, but instead returns " + returnType.getSimpleName());
        ConfigCommandsHandler.logDebug(compilerState, "booleanExpression correctly returns a boolean");

        String indexString = parts[1];
        ConfigCommandsHandler.logDebug("indexExpression is: %s", indexString);
        Expression indexExpression =
                Expression.parseExpression(indexString, compilerState.getArgumentClasses(), compilerState.isDebug());
        ConfigCommandsHandler.logDebug(compilerState, "indexExpression parsed to: %s", indexExpression);
        returnType = indexExpression.getEvaluationType(compilerState.getArgumentClasses());

        if (returnType.isAssignableFrom(InternalIntegerArgument.class)) {
            ConfigCommandsHandler.logDebug(compilerState, "indexExpression correctly returns an integer");
        } else if (returnType.isAssignableFrom(InternalStringArgument.class)) {
            ConfigCommandsHandler.logDebug(compilerState, "indexExpression correctly returns a string");
        } else {
            throw new InvalidIfCommand(command, "Invalid indexExpression. Must return InternalIntegerArgument or InternalStringArgument, but instead returns " + returnType.getSimpleName());
        }

        return new BranchIf(booleanExpression, indexExpression);
    }

    private final Expression booleanExpression;
    private final Expression indexExpression;

    private BranchIf(Expression booleanExpression, Expression indexExpression) {
        this.booleanExpression = booleanExpression;
        this.indexExpression = indexExpression;
    }

    @Override
    public String toString() {
        return "if(" + booleanExpression + ") goto (" + indexExpression + ")";
    }

    @Override
    public int run(InterpreterState interpreterState) {
        ConfigCommandsHandler.logDebug(interpreterState, "BooleanExpression is: %s", booleanExpression);
        ConfigCommandsHandler.logDebug(interpreterState, "IndexExpression is: %s", indexExpression);

        ConfigCommandsHandler.logDebug(interpreterState, "Evaluating BooleanExpression");
        ConfigCommandsHandler.increaseIndentation();
        boolean result = (boolean)
                booleanExpression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug()).getValue();
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(interpreterState, "Boolean evaluated to %s", result);

        int returnIndex;
        if (result) {
            ConfigCommandsHandler.logDebug(interpreterState, "Evaluating IndexExpression");
            ConfigCommandsHandler.increaseIndentation();
            Object value =
                    indexExpression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug()).getValue();
            if (value instanceof Integer i) {
                returnIndex = i;
            } else {
                String target = (String) value;
                if (!interpreterState.hasTag(target))
                    throw new CommandRunException("Tried to jump to tag \"" + target + "\" which was not found.");
                returnIndex = interpreterState.getTag(target);
            }
            ConfigCommandsHandler.decreaseIndentation();
        } else {
            returnIndex = interpreterState.nextIndex();
        }
        ConfigCommandsHandler.logDebug(interpreterState, "Next command index is %s", returnIndex);

        return returnIndex;
    }
}
