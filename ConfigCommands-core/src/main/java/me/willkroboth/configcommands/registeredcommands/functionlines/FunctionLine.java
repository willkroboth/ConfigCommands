package me.willkroboth.configcommands.registeredcommands.functionlines;

import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.RegistrationException;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.registeredcommands.CompilerState;
import me.willkroboth.configcommands.registeredcommands.InterpreterState;

import java.util.List;
import java.util.Map;

/**
 * A class that represents a single line in a function.
 */
public abstract class FunctionLine {
    /**
     * Compiles a List of Strings into an {@link InterpreterState} object that can be used
     * to execute the corresponding {@link FunctionLine} objects that corresponded to each line.
     *
     * @param executes        A List of Strings that each represent in order each {@link FunctionLine} in the total function.
     * @param argumentClasses A map from name to {@link InternalArgument} class object that represent each of the variables
     *                        available within this function.
     * @param localDebug      An {@link SharedDebugValue} that determines if debug messages in this method will appear, as well
     *                        as if debug messages within the returned {@link InterpreterState} object will appear.
     * @return An {@link InterpreterState} object that can be used to execute the function represented by the inputs.
     * @throws RegistrationException If a line in the executes list cannot be turned into a {@link FunctionLine}.
     */
    public static InterpreterState parseExecutes(List<String> executes, Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                 SharedDebugValue localDebug) throws RegistrationException {
        CompilerState compilerState = new CompilerState().addCommands(executes).addArguments(argumentClasses).setDebug(localDebug);
        InterpreterState out = new InterpreterState().setDebug(localDebug);
        for (String command : executes) {
            ConfigCommandsHandler.logDebug(localDebug, "Parsing command: %s", command);

            ConfigCommandsHandler.increaseIndentation();

            out.addLine(parseByCharacter.getOrDefault(
                    command.charAt(0),
                    (s) -> {
                        throw new RegistrationException("Command not recognized, invalid format: \"" + command + "\"");
                    }
            ).parse(compilerState));

            ConfigCommandsHandler.decreaseIndentation();
            compilerState.increaseIndex(1);
        }
        out.addTags(compilerState.getTagMap()).setArgumentClasses(compilerState.getArgumentClasses());
        return out;
    }

    @FunctionalInterface
    private interface ParseRule {
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

    /**
     * @return A representation of this {@link FunctionLine} as a String.
     */
    public abstract String toString();

    /**
     * Runs this {@link FunctionLine}
     *
     * @param interpreterState An {@link InterpreterState} object that represents the current state of the function
     *                         this {@link FunctionLine} is a part of.
     * @return An int representing the next line that should be run according to the rules of this {@link FunctionLine}.
     */
    public abstract int run(InterpreterState interpreterState) throws CommandRunException;
}
