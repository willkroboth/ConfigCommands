package me.willkroboth.configcommands.registeredcommands.functionlines;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.functionsyntax.InvalidRunExpression;
import me.willkroboth.configcommands.exceptions.ParseException;
import me.willkroboth.configcommands.registeredcommands.CompilerState;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;
import me.willkroboth.configcommands.registeredcommands.InterpreterState;

class RunExpression extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidRunExpression, ParseException {
        if (!compilerState.getCommand().startsWith("do "))
            throw new InvalidRunExpression(compilerState.getCommand(), "Invalid format. Must start with \"do \".");

        String command = compilerState.getCommand().substring(3);
        ConfigCommandsHandler.logDebug(compilerState, "Do trimmed off to get: %s", command);

        Expression<?> expression = Expression.parseExpression(command, compilerState.getArgumentClasses(), compilerState.isDebug());

        return new RunExpression(expression);
    }

    private final Expression<?> expression;

    private RunExpression(Expression<?> expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "do " + expression;
    }

    @Override
    public int run(InterpreterState interpreterState) throws CommandRunException {
        ConfigCommandsHandler.logDebug(interpreterState, "Expression is %s", expression);

        ConfigCommandsHandler.increaseIndentation();
        expression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug());
        ConfigCommandsHandler.decreaseIndentation();

        return interpreterState.nextIndex();
    }
}
