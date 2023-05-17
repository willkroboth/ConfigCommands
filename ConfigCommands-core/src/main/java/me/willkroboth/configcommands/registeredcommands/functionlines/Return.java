package me.willkroboth.configcommands.registeredcommands.functionlines;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.functionsyntax.InvalidReturnCommand;
import me.willkroboth.configcommands.exceptions.ParseException;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalCommandSenderArgument;
import me.willkroboth.configcommands.internalarguments.InternalStringArgument;
import me.willkroboth.configcommands.registeredcommands.CompilerState;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;
import me.willkroboth.configcommands.registeredcommands.InterpreterState;

import java.util.Collections;
import java.util.List;

class Return extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidReturnCommand, ParseException {
        if (!compilerState.getCommand().startsWith("return "))
            throw new InvalidReturnCommand(compilerState.getCommand(), "Invalid format. Must start with \"return \"");

        String returnString = compilerState.getCommand().substring(7);
        ConfigCommandsHandler.logDebug(compilerState, "Return trimmed off to get: %s", returnString);

        ConfigCommandsHandler.logDebug(compilerState, "returnExpression is: %s", returnString);
        Expression<?> returnExpression =
                Expression.parseExpression(returnString, compilerState.getArgumentClasses(), compilerState.isDebug());
        ConfigCommandsHandler.logDebug(compilerState, "returnExpression parsed to: %s", returnExpression);

        return new Return(returnExpression);
    }

    private final Expression<?> returnExpression;

    private Return(Expression<?> returnExpression) {
        this.returnExpression = returnExpression;
    }

    @Override
    public String toString() {
        return "return " + returnExpression;
    }

    @Override
    public int run(InterpreterState interpreterState) throws CommandRunException {
        ConfigCommandsHandler.logDebug(interpreterState, "Expression is " + returnExpression);
        ConfigCommandsHandler.increaseIndentation();
        String returnValue = returnExpression.evaluate(interpreterState.getArgumentVariables(), interpreterState.isDebug()).getValue().toString();
        ConfigCommandsHandler.decreaseIndentation();

        ConfigCommandsHandler.logDebug(interpreterState, "Return value is \"" + returnValue + "\"");
        List<InternalArgument<?>> messageParameter = Collections.singletonList(new InternalStringArgument(returnValue));
        InternalCommandSenderArgument sender = (InternalCommandSenderArgument) interpreterState.getVariable("<sender>");

        sender.getInstanceExecution("sendMessage", Collections.singletonList(InternalStringArgument.class)).getRun().run(sender, messageParameter);

        return -1;
    }
}
