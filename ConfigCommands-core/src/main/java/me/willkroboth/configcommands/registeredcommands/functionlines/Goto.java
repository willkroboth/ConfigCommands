package me.willkroboth.configcommands.registeredcommands.functionlines;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.functionsyntax.InvalidGotoCommand;
import me.willkroboth.configcommands.exceptions.ParseException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalIntegerArgument;
import me.willkroboth.configcommands.internalarguments.InternalStringArgument;
import me.willkroboth.configcommands.registeredcommands.CompilerState;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;
import me.willkroboth.configcommands.registeredcommands.InterpreterState;

class Goto extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidGotoCommand, ParseException {
        if (!compilerState.getCommand().startsWith("goto "))
            throw new InvalidGotoCommand(compilerState.getCommand(), "Invalid format. Must start with \"goto \"");

        String indexString = compilerState.getCommand().substring(5);
        ConfigCommandsHandler.logDebug(compilerState, "Goto trimmed off to get: %s", indexString);

        ConfigCommandsHandler.logDebug(compilerState, "indexExpression is: %s", indexString);
        Expression<?> indexExpression =
                Expression.parseExpression(indexString, compilerState.getArgumentClasses(), compilerState.isDebug());
        ConfigCommandsHandler.logDebug(compilerState, "indexExpression parsed to: %s", indexExpression);
        Class<? extends InternalArgument<?>> returnType = indexExpression.getEvaluationType(compilerState.getArgumentClasses());

        if (returnType.isAssignableFrom(InternalIntegerArgument.class)) {
            ConfigCommandsHandler.logDebug(compilerState, "indexExpression correctly returns an integer");
        } else if (returnType.isAssignableFrom(InternalStringArgument.class)) {
            ConfigCommandsHandler.logDebug(compilerState, "indexExpression correctly returns a string");
        } else {
            throw new InvalidGotoCommand(compilerState.getCommand(), "Invalid indexExpression. Must return InternalIntegerArgument or InternalStringArgument, but instead returns " + returnType.getSimpleName());
        }

        return new Goto(indexExpression);
    }

    private final Expression<?> indexExpression;

    private Goto(Expression<?> indexExpression) {
        this.indexExpression = indexExpression;
    }

    @Override
    public String toString() {
        return "goto " + indexExpression;
    }

    @Override
    public int run(InterpreterState interpreterState) throws CommandRunException {
        ConfigCommandsHandler.logDebug(interpreterState, "IndexExpression is: %s", indexExpression);
        ConfigCommandsHandler.increaseIndentation();
        Object value = indexExpression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug()).getValue();

        int returnIndex;
        if (value instanceof Integer i) {
            returnIndex = i;
        } else {
            String target = (String) value;
            if (!interpreterState.hasTag(target))
                throw new CommandRunException("Tried to jump to tag \"" + target + "\" which was not found.");
            returnIndex = interpreterState.getTag(target);
        }
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(interpreterState, "Next command index is %s", returnIndex);

        return returnIndex;
    }
}
