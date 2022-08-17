package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalCommandSenderArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class ConfigCommandExecutor {
    private final String name;

    private final ArrayList<String> argument_keys;
    private final HashMap<String, InternalArgument> argument_variables;

    private final ArrayList<HashMap<String, Object>> commands;

    private final Map<String, Integer> tagMap;

    private final boolean localDebug;

    public ConfigCommandExecutor(String name, ArrayList<String> argument_keys, ArrayList<HashMap<String, Object>> commands,
                                 Map<String, Integer> tagMap, boolean localDebug) {
        this.name = name;

        this.argument_keys = argument_keys;
        this.argument_variables = new HashMap<>();

        this.commands = commands;

        this.tagMap = tagMap;

        this.localDebug = localDebug;
    }

    public ConfigCommandExecutor(String name, ArrayList<String> argument_keys, HashMap<String, InternalArgument> argument_variables,
                                 ArrayList<HashMap<String, Object>> commands, Map<String, Integer> tagMap, boolean localDebug) {
        this.name = name;

        this.argument_keys = argument_keys;
        this.argument_variables = argument_variables;

        this.commands = commands;
        this.tagMap = tagMap;

        this.localDebug = localDebug;
    }

    public ConfigCommandExecutor copy(HashMap<String, Class<? extends InternalArgument>> argument_variable_classes) {
        HashMap<String, InternalArgument> newArgumentVariables = new HashMap<>();
        for (String argName : argument_variable_classes.keySet()) {
            InternalArgument newArg = InternalArgument.getInternalArgument(argument_variable_classes.get(argName));
            newArgumentVariables.put(argName, newArg);
        }

        return new ConfigCommandExecutor(name, argument_keys, newArgumentVariables, commands, tagMap, localDebug);
    }

    public void execute(CommandSender sender, Object[] args) {
        int startIndentation = ConfigCommandsHandler.getIndentation();
        try {
            ConfigCommandsHandler.logDebug(localDebug, "Running ConfigCommand %s with args: %s", name, Arrays.deepToString(args));

            // setup default args
            argument_variables.get("<sender>").setValue(sender);
            ConfigCommandsHandler.logDebug(localDebug, "<sender> set to %s", argument_variables.get("<sender>").getValue());

            int numDefaultArgs = ConfigCommandBuilder.getDefaultArgs().size();
            // setup variable hashmap
            for (int i = 0; i < args.length; i++) {
                argument_variables.get(argument_keys.get(i + numDefaultArgs)).setValue(args[i]);
                ConfigCommandsHandler.logDebug(localDebug, argument_keys.get(i + numDefaultArgs) + " set to " + args[i].toString());
            }

            //run commands
            if (localDebug) {
                ConfigCommandsHandler.logNormal("Commands are:");
                ConfigCommandsHandler.increaseIndentation();
                for (int commandIndex = 0; commandIndex < this.commands.size(); commandIndex++) {
                    ConfigCommandsHandler.logNormal("%s: %s", commandIndex, this.commands.get(commandIndex));
                }
                ConfigCommandsHandler.decreaseIndentation();
            }

            for (int commandIndex = 0; commandIndex < this.commands.size(); commandIndex++) {
                if (commandIndex < 0) break; // may occur if a branch returns a negative value or after a return
                argument_variables.get("<commandIndex>").setValue(commandIndex);
                ConfigCommandsHandler.logDebug(localDebug, "<commandIndex> set to %s", commandIndex);

                HashMap<String, Object> rawCommand = this.commands.get(commandIndex);

                String type = (String) rawCommand.get("type");
                ConfigCommandsHandler.logDebug(localDebug, "Running %s", type);

                ConfigCommandsHandler.increaseIndentation();
                switch (type) {
                    case "command" -> run_command((List<String>) rawCommand.get("info"));
                    case "set" -> run_set((HashMap<String, Object>) rawCommand.get("info"));
                    case "do" -> run_do((HashMap<String, Object>) rawCommand.get("info"));
                    case "tag" -> ConfigCommandsHandler.logDebug(localDebug, "Skipping over tag");
                    case "if" -> commandIndex = run_if((HashMap<String, Object>) rawCommand.get("info")) - 1;
                    case "goto" -> commandIndex = run_goto((HashMap<String, Object>) rawCommand.get("info")) - 1;
                    case "return" -> {
                        run_return((HashMap<String, Object>) rawCommand.get("info"));
                        commandIndex = -2; // ends execution with negative commandIndex
                    }
                }
                ConfigCommandsHandler.decreaseIndentation();
            }
        } catch (Throwable e) {
            if (localDebug) {
                ConfigCommandsHandler.logNormal("Error occurred while running the command: %s", e.getMessage());
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                ConfigCommandsHandler.logNormal(stackTrace.toString());
            }
            sender.sendMessage("Error occurred while running the command: " + e.getMessage());
        } finally {
            // always reset indentation
            ConfigCommandsHandler.setIndentation(startIndentation);
        }
    }

    private String run_command(List<String> parts) throws CommandRunException {
        ConfigCommandsHandler.logDebug(localDebug, "Command has parts: %s", parts.toString());
        StringBuilder command = new StringBuilder();
        boolean variable_section = false;
        for (String piece : parts) {
            if (variable_section) {
                command.append(argument_variables.get(piece).forCommand());
            } else {
                command.append(piece);
            }
            variable_section = !variable_section;
        }
        ConfigCommandsHandler.logDebug(localDebug, "Command built as: %s", command);

        InternalCommandSenderArgument sender = (InternalCommandSenderArgument) argument_variables.get("<sender>");

        List<InternalArgument> commandParameter = Collections.singletonList(new InternalStringArgument(command.toString()));

        try {
            ConfigCommandsHandler.increaseIndentation();
            String result = (String) sender.runFunction("dispatchCommand", commandParameter).getValue();
            ConfigCommandsHandler.decreaseIndentation();
            ConfigCommandsHandler.logDebug(localDebug, "Command result: \"%s\"", result);
            return result;
        } catch (CommandException e) {
            List<InternalArgument> messageParameter = Collections.singletonList(new InternalStringArgument("Invalid command: " + command));
            sender.runFunction("sendMessage", messageParameter);
            throw e;
        }
    }

    private void run_set(HashMap<String, Object> info) throws CommandRunException {
        String variableName = (String) info.get("variable");
        InternalArgument variable = argument_variables.get(variableName);
        ConfigCommandsHandler.logDebug(localDebug, "Variable is %s", variableName);

        String expressionType = (String) info.get("expressionType");
        ConfigCommandsHandler.logDebug(localDebug, "expressionType is %s", expressionType);

        switch (expressionType) {
            case "expression" -> {
                Expression expression = (Expression) info.get("expression");
                ConfigCommandsHandler.logDebug(localDebug, "Expression is %s", expression);
                ConfigCommandsHandler.increaseIndentation();
                InternalArgument result = expression.evaluate(argument_variables, localDebug);
                ConfigCommandsHandler.decreaseIndentation();
                ConfigCommandsHandler.logDebug(localDebug, "Variable will be set to: %s", result.getValue());
                variable.setValue(result);
            }
            case "command" -> {
                String stringResult = run_command((List<String>) info.get("command"));
                ConfigCommandsHandler.logDebug(localDebug, "Variable will be set to: %s", stringResult);
                variable.setValue(stringResult);
            }
        }
    }

    private void run_do(HashMap<String, Object> info) throws CommandRunException {
        Expression expression = (Expression) info.get("expression");
        ConfigCommandsHandler.logDebug(localDebug, "Expression is %s", expression);

        ConfigCommandsHandler.increaseIndentation();
        expression.evaluate(argument_variables, localDebug);
        ConfigCommandsHandler.decreaseIndentation();
    }

    private int run_if(HashMap<String, Object> info) throws CommandRunException {
        Expression booleanExpression = (Expression) info.get("booleanExpression");
        ConfigCommandsHandler.logDebug(localDebug, "BooleanExpression is: %s", booleanExpression);
        Expression indexExpression = (Expression) info.get("indexExpression");
        ConfigCommandsHandler.logDebug(localDebug, "IndexExpression is: %s", indexExpression);

        ConfigCommandsHandler.logDebug(localDebug, "Evaluating BooleanExpression");
        ConfigCommandsHandler.increaseIndentation();
        boolean result = (boolean) booleanExpression.evaluate(argument_variables, localDebug).getValue();
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Boolean evaluated to %s", result);

        int returnIndex = -1;
        if (result) {
            ConfigCommandsHandler.logDebug(localDebug, "Evaluating IndexExpression");
            ConfigCommandsHandler.increaseIndentation();
            Object value = indexExpression.evaluate(argument_variables, localDebug).getValue();
            switch ((String) info.get("indexType")) {
                case "integer" -> returnIndex = (int) value;
                case "string" -> {
                    String target = (String) value;
                    if (!tagMap.containsKey(target))
                        throw new CommandRunException("Tried to jump to tag \"" + target + "\" which was not found.");
                    returnIndex = tagMap.get(target);
                }
            }
            ConfigCommandsHandler.decreaseIndentation();
        } else {
            returnIndex = (int) argument_variables.get("<commandIndex>").getValue() + 1;
        }
        ConfigCommandsHandler.logDebug(localDebug, "Next command index is %s", returnIndex);

        return returnIndex;
    }

    private int run_goto(HashMap<String, Object> info) throws CommandRunException {
        Expression indexExpression = (Expression) info.get("indexExpression");
        ConfigCommandsHandler.logDebug(localDebug, "IndexExpression is: %s", indexExpression);
        ConfigCommandsHandler.increaseIndentation();
        int returnIndex = -1;
        Object value = indexExpression.evaluate(argument_variables, localDebug).getValue();
        switch ((String) info.get("indexType")) {
            case "integer" -> returnIndex = (int) value;
            case "string" -> {
                String target = (String) value;
                if (!tagMap.containsKey(target))
                    throw new CommandRunException("Tried to jump to tag \"" + target + "\" which was not found.");
                returnIndex = tagMap.get(target);
            }
        }
        ConfigCommandsHandler.decreaseIndentation();
        ConfigCommandsHandler.logDebug(localDebug, "Next command index is %s", returnIndex);

        return returnIndex;
    }

    private void run_return(HashMap<String, Object> info) throws CommandRunException {
        Expression expression = (Expression) info.get("returnExpression");
        ConfigCommandsHandler.logDebug(localDebug, "Expression is " + expression);
        ConfigCommandsHandler.increaseIndentation();
        String returnValue = expression.evaluate(argument_variables, localDebug).getValue().toString();
        ConfigCommandsHandler.decreaseIndentation();

        ConfigCommandsHandler.logDebug(localDebug, "Return value is \"" + returnValue + "\"");
        List<InternalArgument> messageParameter = Collections.singletonList(new InternalStringArgument(returnValue));
        argument_variables.get("<sender>").runFunction("sendMessage", messageParameter);
    }
}
