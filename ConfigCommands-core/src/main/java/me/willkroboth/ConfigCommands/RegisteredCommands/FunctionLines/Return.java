package me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax.InvalidReturnCommand;
import me.willkroboth.ConfigCommands.Exceptions.ParseException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.CompilerState;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expressions.Expression;
import me.willkroboth.ConfigCommands.RegisteredCommands.InterpreterState;

import java.util.Collections;
import java.util.List;

class Return extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidReturnCommand, ParseException {
        if (!compilerState.getCommand().startsWith("return "))
            throw new InvalidReturnCommand(compilerState.getCommand(), "Invalid format. Must start with \"return \"");

        String returnString = compilerState.getCommand().substring(7);
        ConfigCommandsHandler.logDebug(compilerState, "Return trimmed off to get: %s", returnString);

        ConfigCommandsHandler.logDebug(compilerState, "returnExpression is: %s", returnString);
        Expression returnExpression =
                Expression.parseExpression(returnString, compilerState.getArgumentClasses(), compilerState.isDebug());
        ConfigCommandsHandler.logDebug(compilerState, "returnExpression parsed to: %s", returnExpression);

        return new Return(returnExpression);
    }

    private final Expression returnExpression;

    private Return(Expression returnExpression) {
        this.returnExpression = returnExpression;
    }

    @Override
    public String toString() {
        return "return " + returnExpression;
    }

    @Override
    public int run(InterpreterState interpreterState) {
        ConfigCommandsHandler.logDebug(interpreterState, "Expression is " + returnExpression);
        ConfigCommandsHandler.increaseIndentation();
        String returnValue =
                returnExpression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug()).getValue().toString();
        ConfigCommandsHandler.decreaseIndentation();

        ConfigCommandsHandler.logDebug(interpreterState, "Return value is \"" + returnValue + "\"");
        List<InternalArgument> messageParameter = Collections.singletonList(new InternalStringArgument(returnValue));
        interpreterState.getVariable("<sender>").runFunction("sendMessage", messageParameter);

        return -1;
    }
}
