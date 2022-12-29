package me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalCommandSenderArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.CompilerState;
import me.willkroboth.ConfigCommands.RegisteredCommands.InterpreterState;
import org.bukkit.command.CommandException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RunCommand extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) {
        List<String> commandSections = getCommandSections(compilerState.getCommand(), compilerState);

        return new RunCommand(commandSections);
    }

    public static List<String> getCommandSections(String command, CompilerState compilerState) {
        List<String> command_sections = new ArrayList<>();
        String[] keys = compilerState.getArgumentClasses().keySet().toArray(new String[0]);

        int previous_index = 1; //start at 1 to get rid of initial /
        intPair pair = get_first_index_and_length(command, keys);
        int index = pair.first;
        int length = pair.last;

        while (index != -1) {
            command_sections.add(command.substring(previous_index, index));
            command_sections.add(command.substring(index, index + length));

            previous_index = index + length;
            pair = get_first_index_and_length(command, keys, previous_index);
            index = pair.first;
            length = pair.last;
        }
        if (previous_index != command.length()) {
            command_sections.add(command.substring(previous_index));
        }

        ConfigCommandsHandler.logDebug(compilerState, "Command has been split into: %s", command_sections);
        return command_sections;
    }

    private static intPair get_first_index_and_length(String string, String[] keys) {
        return get_first_index_and_length(string, keys, 0);
    }

    private static intPair get_first_index_and_length(String string, String[] keys, int from_index) {
        int least = string.length();
        int length = -1;

        for (String key : keys) {
            int index = string.indexOf(key, from_index);
            if (index != -1 && index < least) {
                least = index;
                length = key.length();
            }
        }

        if (least == string.length()) return new intPair(-1, -1);
        return new intPair(least, length);
    }

    private record intPair(int first, int last) {
    }

    private final List<String> commandSections;

    private RunCommand(List<String> commandSections) {
        this.commandSections = commandSections;
    }

    @Override
    public String toString() {
        return "/" + commandSections;
    }

    @Override
    public int run(InterpreterState interpreterState) {
        runCommandGetResult(commandSections, interpreterState);

        return interpreterState.nextIndex();
    }

    public static String runCommandGetResult(List<String> commandSections, InterpreterState interpreterState) {
        ConfigCommandsHandler.logDebug(interpreterState, "Command has parts: %s", commandSections);

        StringBuilder command = new StringBuilder();
        boolean variable_section = false;
        for (String section : commandSections) {
            if (variable_section) {
                command.append(interpreterState.getVariable(section).forCommand());
            } else {
                command.append(section);
            }
            variable_section = !variable_section;
        }
        ConfigCommandsHandler.logDebug(interpreterState, "Command built as: %s", command);

        InternalCommandSenderArgument sender = (InternalCommandSenderArgument) interpreterState.getVariable("<sender>");
        List<InternalArgument> commandParameter = Collections.singletonList(new InternalStringArgument(command.toString()));
        try {
            ConfigCommandsHandler.increaseIndentation();
            String result = (String) sender.runInstanceFunction("dispatchCommand", commandParameter).getValue();
            ConfigCommandsHandler.decreaseIndentation();
            ConfigCommandsHandler.logDebug(interpreterState, "Command result: \"%s\"", result);
            return result;
        } catch (CommandException e) {
            List<InternalArgument> messageParameter = Collections.singletonList(new InternalStringArgument("Invalid command: " + command));
            sender.runInstanceFunction("sendMessage", messageParameter);
            throw e;
        }
    }
}
