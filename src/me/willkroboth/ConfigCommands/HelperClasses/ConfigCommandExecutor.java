package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalCommandSenderArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.*;

public class ConfigCommandExecutor {
    private final String name;

    private final ArrayList<String> argument_keys;
    private final HashMap<String, InternalArgument> argument_variables;

    private final ArrayList<HashMap<String, Object>> commands;

    private final Map<String, Integer> tagMap;

    private final boolean debugMode;
    private final IndentedLogger logger;

    public ConfigCommandExecutor(String name, ArrayList<String> argument_keys, ArrayList<HashMap<String, Object>> commands,
                                 Map<String, Integer> tagMap, boolean debugMode, IndentedLogger logger){
        this.name = name;

        this.argument_keys = argument_keys;
        this.argument_variables = new HashMap<>();

        this.commands = commands;

        this.tagMap = tagMap;

        this.debugMode = debugMode;
        this.logger = logger;
    }

    public ConfigCommandExecutor(String name, ArrayList<String> argument_keys, HashMap<String, InternalArgument> argument_variables,
                                 ArrayList<HashMap<String, Object>> commands, Map<String, Integer> tagMap, boolean debugMode, IndentedLogger logger){
        this.name = name;

        this.argument_keys = argument_keys;
        this.argument_variables = argument_variables;

        this.commands = commands;
        this.tagMap = tagMap;

        this.debugMode = debugMode;
        this.logger = logger;
    }

    public ConfigCommandExecutor copy(HashMap<String, Class<? extends InternalArgument>> argument_variable_classes){
        HashMap<String, InternalArgument> newArgumentVariables = new HashMap<>();
        for(String argName:argument_variable_classes.keySet()){
            InternalArgument newArg = InternalArgument.getInternalArgument(argument_variable_classes.get(argName));
            newArgumentVariables.put(argName, newArg);
        }

        return new ConfigCommandExecutor(name, argument_keys, newArgumentVariables, commands, tagMap, debugMode, logger);
    }

    public void execute(CommandSender sender, Object[] args) {
        int startIndentation = logger.getIndentation();
        try {
            if (debugMode) logger.info("Running ConfigCommand " + name + " with args: " + Arrays.toString(args));

            // setup default args
            argument_variables.get("<sender>").setValue(sender);
            if (debugMode) logger.info("<sender> set to " + argument_variables.get("<sender>").getValue());
            int numDefaultArgs = ConfigCommandBuilder.getDefaultArgs().size();

            // setup variable hashmap
            for (int i = 0; i < args.length; i++) {
                argument_variables.get(argument_keys.get(i + numDefaultArgs)).setValue(args[i]);
                if (debugMode) logger.info(argument_keys.get(i + numDefaultArgs) + " set to " + args[i].toString());
            }

            //run commands
            if (debugMode) {
                logger.info("Commands are:");
                logger.increaseIndentation();
                for (int commandIndex = 0; commandIndex < this.commands.size(); commandIndex++) {
                    logger.info(commandIndex + ": " + this.commands.get(commandIndex));
                }

                logger.decreaseIndentation();
            }

            for (int commandIndex = 0; commandIndex < this.commands.size(); commandIndex++) {
                if (commandIndex < 0) break; // may occur if a branch returns a negative value or after a return
                argument_variables.get("<commandIndex>").setValue(commandIndex);
                if (debugMode) logger.info("<commandIndex> set to " + commandIndex);

                HashMap<String, Object> rawCommand = this.commands.get(commandIndex);

                String type = (String) rawCommand.get("type");
                if (debugMode) logger.info("Running " + type);

                logger.increaseIndentation();
                switch (type) {
                    case "command" -> run_command((List<String>) rawCommand.get("info"));
                    case "set" -> run_set((HashMap<String, Object>) rawCommand.get("info"));
                    case "do" -> run_do((HashMap<String, Object>) rawCommand.get("info"));
                    case "tag" -> {
                        if(debugMode) logger.info("Skipping over tag");
                    }
                    case "if" -> commandIndex = run_if((HashMap<String, Object>) rawCommand.get("info")) - 1;
                    case "goto" -> commandIndex = run_goto((HashMap<String, Object>) rawCommand.get("info")) - 1;
                    case "return" -> {
                        run_return((HashMap<String, Object>) rawCommand.get("info"));
                        commandIndex = -2; // ends execution with negative commandIndex
                    }
                }
                logger.decreaseIndentation();
            }
        } catch (CommandRunException e){
            sender.sendMessage("Error occurred while running the command: " + e.getMessage());
        } finally {
            // always reset indentation
            logger.setIndentation(startIndentation);
        }
    }

    private String run_command(List<String> parts) throws CommandRunException {
        if (debugMode) logger.info("Command has parts: " + parts.toString());
        StringBuilder command = new StringBuilder();
        boolean variable_section = false;
        for(String piece:parts){
            if (variable_section){
                command.append(argument_variables.get(piece).forCommand());
            } else {
                command.append(piece);
            }
            variable_section = !variable_section;
        }
        if(debugMode) logger.info("Command built as: " + command);

        InternalCommandSenderArgument sender = (InternalCommandSenderArgument) argument_variables.get("<sender>");

        List<InternalArgument> commandParameter = Collections.singletonList(new InternalStringArgument(command.toString()));

        try {
            logger.increaseIndentation();
            String result = (String) sender.runFunction("dispatchCommand", commandParameter).getValue();
            logger.decreaseIndentation();
            if (debugMode) logger.info("Command result: \"" + result + "\"");
            return result;
        } catch (CommandException e){
            List<InternalArgument> messageParameter = Collections.singletonList(new InternalStringArgument("Invalid command: " + command));
            sender.runFunction("sendMessage", messageParameter);
            throw e;
        }
    }

    private void run_set(HashMap<String, Object> info) throws CommandRunException {
        String variableName = (String) info.get("variable");
        InternalArgument variable = argument_variables.get(variableName);
        if(debugMode) logger.info("Variable is " + variableName);

        String expressionType = (String) info.get("expressionType");
        if (debugMode) logger.info("expressionType is " + expressionType);

        switch (expressionType) {
            case "expression" -> {
                Expression expression = (Expression) info.get("expression");
                if (debugMode) logger.info("Expression is " + expression);
                logger.increaseIndentation();
                InternalArgument result = expression.evaluate(argument_variables, debugMode, logger);
                logger.decreaseIndentation();
                if (debugMode) logger.info("Variable will be set to: " + result.getValue());
                variable.setValue(result);
            }
            case "command" -> {
                String stringResult = run_command((List<String>) info.get("command"));
                if (debugMode) logger.info("Variable will be set to: " + stringResult);
                variable.setValue(stringResult);
            }
        }
    }

    private void run_do(HashMap<String, Object> info) throws CommandRunException {
        Expression expression = (Expression) info.get("expression");
        if (debugMode) logger.info("Expression is " + expression);

        logger.increaseIndentation();
        expression.evaluate(argument_variables, debugMode, logger);
        logger.decreaseIndentation();
    }

    private int run_if(HashMap<String, Object> info) throws CommandRunException {
        Expression booleanExpression = (Expression) info.get("booleanExpression");
        if(debugMode) logger.info("BooleanExpression is: " + booleanExpression);
        Expression indexExpression = (Expression) info.get("indexExpression");
        if(debugMode) logger.info("IndexExpression is: " + indexExpression);

        if (debugMode) logger.info("Evaluating BooleanExpression");
        logger.increaseIndentation();
        boolean result = (boolean) booleanExpression.evaluate(argument_variables, debugMode, logger).getValue();
        logger.decreaseIndentation();
        if(debugMode) logger.info("Boolean evaluated to " + result);

        int returnIndex = -1;
        if (result){
            if (debugMode) logger.info("Evaluating IndexExpression");
            logger.increaseIndentation();
            Object value = indexExpression.evaluate(argument_variables, debugMode, logger).getValue();
            switch ((String) info.get("indexType")) {
                case "integer" -> returnIndex = (int) value;
                case "string" -> {
                    String target = (String) value;
                    if(!tagMap.containsKey(target)) throw new CommandRunException("Tried to jump to tag \"" + target + "\" which was not found.");
                    returnIndex = tagMap.get(target);
                }
            }
            logger.decreaseIndentation();
        } else {
            returnIndex = (int) argument_variables.get("<commandIndex>").getValue() + 1;
        }
        if(debugMode) logger.info("Next command index is " + returnIndex);

        return returnIndex;
    }

    private int run_goto(HashMap<String, Object> info) throws CommandRunException {
        Expression indexExpression = (Expression) info.get("indexExpression");
        if(debugMode) logger.info("IndexExpression is: " + indexExpression);
        logger.increaseIndentation();
        int returnIndex = -1;
        Object value = indexExpression.evaluate(argument_variables, debugMode, logger).getValue();
        switch ((String) info.get("indexType")) {
            case "integer" -> returnIndex = (int) value;
            case "string" -> {
                String target = (String) value;
                if(!tagMap.containsKey(target)) throw new CommandRunException("Tried to jump to tag \"" + target + "\" which was not found.");
                returnIndex = tagMap.get(target);
            }
        }
        logger.decreaseIndentation();
        if(debugMode) logger.info("Next command index is " + returnIndex);

        return returnIndex;
    }

    private void run_return(HashMap<String, Object> info) throws CommandRunException {
        Expression expression = (Expression) info.get("returnExpression");
        if (debugMode) logger.info("Expression is " + expression);
        logger.increaseIndentation();
        String returnValue = expression.evaluate(argument_variables, debugMode, logger).getValue().toString();
        logger.decreaseIndentation();

        if(debugMode) logger.info("Return value is \"" + returnValue + "\"");
        List<InternalArgument> messageParameter = Collections.singletonList(new InternalStringArgument(returnValue));
        argument_variables.get("<sender>").runFunction("sendMessage", messageParameter);
    }
}
