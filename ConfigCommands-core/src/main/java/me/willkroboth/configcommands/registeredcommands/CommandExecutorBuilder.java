package me.willkroboth.configcommands.registeredcommands;

import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.ExecutorType;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.RegistrationException;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;
import org.bukkit.command.CommandSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * A class that builds a CommandAPI {@link CommandExecutor} based on a list of functions to run.
 * See {@link CommandExecutorBuilder#CommandExecutorBuilder(List, Map, ExecutorType, SharedDebugValue)}
 */
public class CommandExecutorBuilder implements CommandExecutor {
    private final InterpreterState defaultState;
    private final Stack<InterpreterState> interpreterStateStack;
    private InterpreterState currentState;

    private final ExecutorType type;

    /**
     * Creates a CommandAPI {@link CommandExecutor} based on a list of functions to run.
     *
     * @param executes        A List of Strings that define what happens when this executor is run. The format of these
     *                        lines is defined by {@link FunctionLine#parseExecutes(List, Map, SharedDebugValue)}.
     * @param argumentClasses A Map linking the name to the {@link InternalArgument}
     *                        class for each of the accessible arguments, including default arguments
     * @param type            A CommandAPI {@link ExecutorType} to use for this executor. See
     *                        <a href="https://commandapi.jorel.dev/8.6.0/normalexecutors.html#multiple-command-executors-with-the-same-implementation">
     *                        multiple command executors with the same implementation</a> in the CommandAPI documentation
     *                        for more information about this class.
     * @param localDebug      The {@link SharedDebugValue} being used for this command.
     * @throws RegistrationException If there is an error while parsing the functions
     */
    public CommandExecutorBuilder(List<String> executes, Map<String, Class<? extends InternalArgument>> argumentClasses,
                                  ExecutorType type, SharedDebugValue localDebug) throws RegistrationException {
        defaultState = FunctionLine.parseExecutes(executes, new LinkedHashMap<>(argumentClasses), localDebug);
        interpreterStateStack = new Stack<>();

        this.type = type;
    }

    @Override
    public ExecutorType getType() {
        return type;
    }

    @Override
    public void run(CommandSender sender, CommandArguments args) {
        interpreterStateStack.push(currentState);
        currentState = defaultState.copy();

        int startIndentation = ConfigCommandsHandler.getIndentation();
        try {
            ConfigCommandsHandler.logDebug(currentState, "Running ConfigCommand with args: %s", Arrays.deepToString(args.args()));

            currentState.setUpVariablesMap(args.args());

            // setup default args
            currentState.setVariable("<sender>", sender);
            ConfigCommandsHandler.logDebug(currentState, "<sender> set to %s", currentState.getVariable("<sender>").getValue());

            if (currentState.isDebug()) {
                ConfigCommandsHandler.logNormal("Code lines are:");
                ConfigCommandsHandler.increaseIndentation();
                for (int lineIndex = 0; lineIndex < currentState.getLines().size(); lineIndex++) {
                    ConfigCommandsHandler.logNormal("%s: %s", lineIndex, currentState.getLines().get(lineIndex));
                }
                ConfigCommandsHandler.decreaseIndentation();
            }

            while (currentState.hasLine()) {
                currentState.setVariable("<lineIndex>", currentState.getIndex());
                ConfigCommandsHandler.logDebug(currentState, "<lineIndex> set to %s", currentState.getIndex());

                FunctionLine line = currentState.getLine();
                ConfigCommandsHandler.logDebug(currentState, "Executing %s", line);
                ConfigCommandsHandler.increaseIndentation();
                currentState.setIndex(line.run(currentState));
                ConfigCommandsHandler.decreaseIndentation();
            }
        } catch (Throwable e) {
            if (currentState.isDebug()) {
                ConfigCommandsHandler.logNormal("Error occurred while running the command:");
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                ConfigCommandsHandler.logNormal(stackTrace.toString());
            }
            sender.sendMessage("Error occurred while running the command: " + e.getMessage());
        } finally {
            // always reset
            ConfigCommandsHandler.setIndentation(startIndentation);

            currentState = interpreterStateStack.pop();
        }
    }
}
