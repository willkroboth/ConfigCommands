package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.executors.ExecutorType;
import dev.jorel.commandapi.executors.IExecutorNormal;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines.FunctionLine;
import org.bukkit.command.CommandSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class CommandExecutorBuilder implements IExecutorNormal<CommandSender> {
    private final InterpreterState defaultState;
    private final Stack<InterpreterState> interpreterStateStack;
    private InterpreterState currentState;

    private final ExecutorType type;

    public CommandExecutorBuilder(List<String> executes, Map<String, Class<? extends InternalArgument>> argumentClasses,
                                  ExecutorType type, boolean localDebug) throws RegistrationException {
        defaultState = FunctionLine.parseExecutes(executes, new LinkedHashMap<>(argumentClasses), localDebug);
        interpreterStateStack = new Stack<>();

        this.type = type;
    }

    @Override
    public ExecutorType getType() {
        return type;
    }

    @Override
    public void run(CommandSender sender, Object[] args) {
        interpreterStateStack.push(currentState);
        currentState = defaultState.copy();

        int startIndentation = ConfigCommandsHandler.getIndentation();
        try {
            ConfigCommandsHandler.logDebug(currentState, "Running ConfigCommand with args: %s", Arrays.deepToString(args));

            currentState.setUpVariablesMap(args);

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
                currentState.updateIndex(line.run(currentState));
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
