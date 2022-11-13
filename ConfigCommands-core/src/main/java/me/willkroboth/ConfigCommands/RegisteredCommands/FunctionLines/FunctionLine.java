package me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.CompilerState;
import me.willkroboth.ConfigCommands.RegisteredCommands.InterpreterState;

import java.util.List;
import java.util.Map;

public abstract class FunctionLine {
    public static InterpreterState parseExecutes(List<String> executes, Map<String, Class<? extends InternalArgument>> argumentClasses, boolean localDebug) throws RegistrationException {
        CompilerState compilerState = new CompilerState().addCommands(executes).addArguments(argumentClasses).setDebug(localDebug);
        InterpreterState out = new InterpreterState().setDebug(localDebug);
        for (String command : executes) {
            ConfigCommandsHandler.logDebug(localDebug, "Parsing command: %s", command);

            ConfigCommandsHandler.increaseIndentation();

            out.addLine(parseByCharacter.getOrDefault(
                    command.charAt(0),
                    (s) -> {throw new RegistrationException("Command not recognized, invalid format: \"" + command + "\"");}
            ).parse(compilerState));

            ConfigCommandsHandler.decreaseIndentation();
            compilerState.increaseIndex(1);
        }
        out.addTags(compilerState.getTagMap()).setArgumentClasses(compilerState.getArgumentClasses());
        return out;
    }

    @FunctionalInterface
    private interface ParseRule{
        FunctionLine parse(CompilerState state) throws RegistrationException;
    }

    // Commands:
    // - Run command, indicated by /
    // - define and set variables, indicated by <name> =
    // - run functions, indicated by do
    // - define branch target tag, indicated by tag
    // - conditional branch, indicated by if
    // - branch, indicated by goto
    // - return value, indicated by return
    private static final Map<Character, ParseRule> parseByCharacter = Map.of(
            '/', RunCommand::parse,
            '<', SetVariable::parse,
            'd', RunExpression::parse,
            't', Tag::parse,
            'i', BranchIf::parse,
            'g', Goto::parse,
            'r', Return::parse
    );

    public abstract String toString();

    public abstract int run(InterpreterState interpreterState);
}
