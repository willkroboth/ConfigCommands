package me.willkroboth.ConfigCommands.HelperClasses;

import dev.jorel.commandapi.CommandAPICommand;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.*;
import me.willkroboth.ConfigCommands.InternalArguments.*;
import org.bukkit.command.CommandSender;

import java.util.*;

public class ConfigCommandBuilder extends CommandAPICommand {
    public static String getDefaultPermission(String name){
        return "configcommands." + name.toLowerCase(Locale.ROOT);
    }

    private final ArrayList<String> argument_keys = new ArrayList<>();
    private final HashMap<String, Class<? extends InternalArgument>> argument_variable_classes = new HashMap<>();

    private ArrayList<HashMap<String, Object>> commands = new ArrayList<>();

    private Map<String, Integer> tagMap = new HashMap<>();

    private final boolean debugMode;
    private final IndentedLogger logger;

    private ConfigCommandExecutor executor;

    public ConfigCommandBuilder(String name, String shortDescription, String fullDescription, List<Map<?, ?>> args,
                                List<String> aliases, String permission, List<String> commands,
                                boolean debug, IndentedLogger l) throws RegistrationException {
        //set name
        super(name);

        // set debug variables
        debugMode = debug;
        logger = l;

        // setup arguments
        addDefaultArgs();
        parse_args(args);

        // set aliases
        super.withAliases(aliases.toArray(new String[0]));
        // set permission
        super.withPermission(permission);
        // set commands
        parse_commands(commands);

        // set help
        if(shortDescription != null) super.withShortDescription(shortDescription);
        if(fullDescription != null) super.withFullDescription(fullDescription);

        super.executes(this::execute);
        try {
            super.register();
        } catch (Exception e){
            throw new RegistrationException("Encountered " + e.getClass().getSimpleName() + " when registering: " + e.getMessage());
        }
        executor = new ConfigCommandExecutor(name, argument_keys, this.commands, tagMap, debugMode, logger);
    }

    private void addDefaultArgs() {
        for(Map.Entry<String, Class<? extends InternalArgument>> arg: getDefaultArgs().entrySet()){
            argument_keys.add(arg.getKey());
            argument_variable_classes.put(arg.getKey(), arg.getValue());
        }
    }

    public static Map<String, Class<? extends InternalArgument>> getDefaultArgs(){
        return Map.of(
                "<sender>", InternalCommandSenderArgument.class,
                "<commandIndex>", InternalIntegerArgument.class
        );
    }

    private void parse_args(List<Map<?, ?>> args) throws RegistrationException {
        // sets command to execute with arguments and sets up arguments variables
        for (Map<?, ?> arg : args) {
            if (debugMode) logger.info("Adding argument: " + arg.toString());
            logger.increaseIndentation();
            InternalArgument.addArgument(arg, this, argument_keys, argument_variable_classes, debugMode, logger);
            logger.decreaseIndentation();
        }
    }

    // update commands to reflect new behavior from config file
    public void refreshExecutor(List<String> commands) throws RegistrationException {
        // refresh relevant variables
        this.commands = new ArrayList<>();
        this.tagMap = new HashMap<>();

        // parse new commands
        parse_commands(commands);

        // create new executor
        executor = new ConfigCommandExecutor(super.getName(), argument_keys, this.commands, tagMap, debugMode, logger);
    }

    private void parse_commands(List<String> commands) throws RegistrationException {
        // Commands:
        // - Run command, indicated by /
        // - define and set variables, indicated by <name> =
        // - run functions, indicated by do
        // - conditional branch, indicated by if
        // - branch, indicated by goto
        // - return value, indicated by return
        int index = 0;
        for (String command : commands) {
            if (debugMode) logger.info("Parsing command: " + command);

            logger.increaseIndentation();
            switch (command.charAt(0)) {
                case '/' -> parse_command_to_run(command);
                case '<' -> parse_set(command);
                case 'd' -> parse_do(command);
                case 't' -> parse_tag(command, index);
                case 'i' -> parse_if(command);
                case 'g' -> parse_goto(command);
                case 'r' -> parse_return(command);
                default -> throw new RegistrationException("Command not recognized, invalid format: \"" + command + "\"");
            }
            logger.decreaseIndentation();
            index++;
        }
    }

    private void parse_command_to_run(String command) {
        ArrayList<String> command_sections = new ArrayList<>();
        String[] keys = argument_keys.toArray(new String[0]);

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

        if (debugMode) logger.info("Command has been split into: " + command_sections);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "command");
        new_command.put("info", command_sections);
        commands.add(new_command);
    }

    private intPair get_first_index_and_length(String string, String[] keys) {
        return get_first_index_and_length(string, keys, 0);
    }

    private intPair get_first_index_and_length(String string, String[] keys, int from_index) {
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

    private void parse_set(String command) throws RegistrationException {
        String[] parts = command.split(" = ");
        if (parts.length != 2) throw new InvalidSetCommand(command, "Invalid format. Must contain only 1 \" = \".");

        if (debugMode) logger.info("Set split into: " + Arrays.toString(parts));

        String variable = parts[0];
        if (!(variable.charAt(0) == '<' && variable.charAt(variable.length() - 1) == '>')) {
            throw new InvalidSetCommand(command, "Invalid variable: " + variable + ". Must be wrapped by < >.");
        }
        if (debugMode) logger.info("Variable is " + variable);

        String rawExpression = parts[1];
        Class<? extends InternalArgument> returnType;
        HashMap<String, Object> info;
        if (rawExpression.startsWith("/")) {
            // setting a variable to a command
            if (debugMode) logger.info("Expression looks like a command.");
            if (debugMode) logger.info("Parsing \"" + rawExpression + "\" as command.");
            command = rawExpression;

            // parse command
            ArrayList<String> command_sections = new ArrayList<>();
            String[] keys = argument_keys.toArray(new String[0]);

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

            if (debugMode) logger.info("Command has been split into: " + command_sections);

            info = new HashMap<>();

            info.put("variable", variable);
            info.put("expressionType", "command");
            info.put("command", command_sections);

            returnType = InternalStringArgument.class;
        } else {
            // expression is code
            Expression expression = Expression.parseExpression(rawExpression, argument_variable_classes, debugMode, logger);
            returnType = expression.getEvaluationType(argument_variable_classes);

            info = new HashMap<>();

            info.put("variable", variable);
            info.put("expressionType", "expression");
            info.put("expression", expression);
        }

        if (argument_keys.contains(variable)) {
            Class<? extends InternalArgument> currentType = argument_variable_classes.get(variable);
            if (!currentType.equals(returnType)) {
                throw new InvalidSetCommand(command, "Wrong type. Set variable(" + variable +
                        ") was previously made as " + currentType.getSimpleName() +
                        ", but is now being set as " + returnType.getSimpleName());
            }
            if (debugMode) logger.info(variable + " already found in argument keys, and the return type matches.");
        } else {
            argument_keys.add(variable);
            argument_variable_classes.put(variable, returnType);
            if (debugMode)
                logger.info(variable + " not found in arguments keys, so it was created to match return type.");
        }

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "set");
        new_command.put("info", info);
        commands.add(new_command);
    }

    private void parse_do(String command) throws RegistrationException {
        if (!command.startsWith("do ")) throw new InvalidDoCommand(command, "Invalid format. Must start with \"do \".");

        command = command.substring(3);
        if (debugMode) logger.info("Do trimmed off to get: " + command);

        Expression expression = Expression.parseExpression(command, argument_variable_classes, debugMode, logger);

        HashMap<String, Object> info = new HashMap<>();

        info.put("expression", expression);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "do");
        new_command.put("info", info);
        commands.add(new_command);
    }

    private void parse_tag(String command, int index) throws RegistrationException{
        if(!command.startsWith("tag ")) throw new InvalidExpressionCommand("tag", command, "Invalid format. Must start with \"tag\"");

        String tag = command.substring(4);
        if (debugMode) logger.info("Tag trimmed off to get: " + tag);
        if (debugMode) logger.info("Tag will have index: " + index);
        tagMap.put(tag, index);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "tag");
        commands.add(new_command);
    }

    private void parse_if(String command) throws RegistrationException {
        if (!command.startsWith("if ")) throw new InvalidIfCommand(command, "Invalid format. Must start with \"if \"");

        command = command.substring(3);
        if (debugMode) logger.info("If trimmed off to get: " + command);

        String[] parts = command.split(" goto ");
        if (parts.length != 2) throw new InvalidIfCommand(command, "Invalid format. Must contain \" goto \" once.");
        if (debugMode) logger.info("If split into: " + Arrays.toString(parts));

        String booleanString = parts[0];
        if (debugMode) logger.info("booleanExpression is: " + booleanString);
        Expression booleanExpression = Expression.parseExpression(booleanString, argument_variable_classes, debugMode, logger);
        if (debugMode) logger.info("booleanExpression parsed to: " + booleanExpression);
        Class<? extends InternalArgument> returnType = booleanExpression.getEvaluationType(argument_variable_classes);
        if (!returnType.isAssignableFrom(InternalBooleanArgument.class))
            throw new InvalidIfCommand(command, "Invalid booleanExpression. Must return InternalBooleanArgument, but instead returns " + returnType.getSimpleName());
        if (debugMode) logger.info("booleanExpression correctly returns a boolean");

        String indexString = parts[1];
        if (debugMode) logger.info("indexExpression is: " + indexString);
        Expression indexExpression = Expression.parseExpression(indexString, argument_variable_classes, debugMode, logger);
        if (debugMode) logger.info("indexExpression parsed to: " + indexExpression);
        returnType = indexExpression.getEvaluationType(argument_variable_classes);

        HashMap<String, Object> info = new HashMap<>();
        if(returnType.isAssignableFrom(InternalIntegerArgument.class)){
            if(debugMode) logger.info("indexExpression correctly returns an integer");
            info.put("indexType", "integer");
        } else if(returnType.isAssignableFrom(InternalStringArgument.class)){
            if(debugMode) logger.info("indexExpression correctly returns a string");
            info.put("indexType", "string");
        } else {
            throw new InvalidIfCommand(command, "Invalid indexExpression. Must return InternalIntegerArgument or InternalStringArgument, but instead returns " + returnType.getSimpleName());
        }
        info.put("indexExpression", indexExpression);
        info.put("booleanExpression", booleanExpression);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "if");
        new_command.put("info", info);
        commands.add(new_command);
    }

    private void parse_goto(String command) throws RegistrationException {
        if (!command.startsWith("goto "))
            throw new InvalidGotoCommand(command, "Invalid format. Must start with \"goto \"");

        String indexString = command.substring(5);
        if (debugMode) logger.info("Goto trimmed off to get: " + indexString);

        if (debugMode) logger.info("indexExpression is: " + indexString);
        Expression indexExpression = Expression.parseExpression(indexString, argument_variable_classes, debugMode, logger);
        if (debugMode) logger.info("indexExpression parsed to: " + indexExpression);
        Class<? extends InternalArgument> returnType = indexExpression.getEvaluationType(argument_variable_classes);

        HashMap<String, Object> info = new HashMap<>();
        if(returnType.isAssignableFrom(InternalIntegerArgument.class)){
            if(debugMode) logger.info("indexExpression correctly returns an integer");
            info.put("indexType", "integer");
        } else if(returnType.isAssignableFrom(InternalStringArgument.class)){
            if(debugMode) logger.info("indexExpression correctly returns a string");
            info.put("indexType", "string");
        } else {
            throw new InvalidIfCommand(command, "Invalid indexExpression. Must return InternalIntegerArgument or InternalStringArgument, but instead returns " + returnType.getSimpleName());
        }
        info.put("indexExpression", indexExpression);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "goto");
        new_command.put("info", info);
        commands.add(new_command);
    }

    private void parse_return(String command) throws RegistrationException {
        if (!command.startsWith("return "))
            throw new InvalidReturnCommand(command, "Invalid format. Must start with \"return \"");

        String returnString = command.substring(7);
        if (debugMode) logger.info("Return trimmed off to get: " + returnString);

        if (debugMode) logger.info("returnExpression is: " + returnString);
        Expression returnExpression = Expression.parseExpression(returnString, argument_variable_classes, debugMode, logger);
        if (debugMode) logger.info("returnExpression parsed to: " + returnExpression);

        HashMap<String, Object> info = new HashMap<>();

        info.put("returnExpression", returnExpression);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "return");
        new_command.put("info", info);
        commands.add(new_command);
    }

    public void execute(CommandSender sender, Object[] args) {
        executor.copy(argument_variable_classes).execute(sender, args);
    }

    static class intPair{
        int first;
        int last;
        public intPair(int first, int last){
            this.first = first;
            this.last = last;
        }
    }
}
