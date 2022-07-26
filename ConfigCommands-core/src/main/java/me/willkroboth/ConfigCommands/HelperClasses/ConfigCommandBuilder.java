package me.willkroboth.ConfigCommands.HelperClasses;

import dev.jorel.commandapi.CommandAPICommand;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.*;
import me.willkroboth.ConfigCommands.InternalArguments.*;
import me.willkroboth.ConfigCommands.SystemCommands.ReloadCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ConfigCommandBuilder extends CommandAPICommand {
    public static void registerCommandsFromConfig(ConfigurationSection commands, boolean globalDebug) {
        ConfigCommandsHandler.logNormal("");
        if(commands == null){
            ConfigCommandsHandler.logNormal("The configuration section for the commands was not found! Skipping");
            return;
        }

        ConfigCommandsHandler.logNormal("Registering commands from %s", commands.getCurrentPath());
        if (commands.getKeys(false).size() == 0) {
            ConfigCommandsHandler.logNormal("No commands found! Skipping");
            return;
        }

        List<String> failedCommands = new ArrayList<>();
        for (String key : commands.getKeys(false)) {
            ConfigCommandsHandler.logNormal("");
            ConfigCommandsHandler.logNormal("Loading command %s", key);

            // vital data needed for command to work
            ConfigurationSection command = commands.getConfigurationSection(key);
            if (command == null) {
                ConfigCommandsHandler.logError("%s has no data. Skipping.", key);
                failedCommands.add("(key) " + key + ": No data found!");
                continue;
            }

            boolean localDebug = command.getBoolean("debug", false);
            ConfigCommandsHandler.logDebug(localDebug && !globalDebug, "Debug turned on for %s", key);
            localDebug = globalDebug || localDebug;

            String name = (String) command.get("name");
            if (name == null) {
                ConfigCommandsHandler.logError("%s has no command name. Skipping.", key);
                failedCommands.add("(key) " + key + ": No name found!");
                continue;
            }

            ConfigCommandsHandler.logDebug(localDebug, "%s has name %s", key, name);

            List<String> commandsToRun = command.getStringList("commands");
            if (commandsToRun.size() == 0) {
                ConfigCommandsHandler.logError("%s has no commands. Skipping.", key);
                failedCommands.add("(name) " + name + ": No commands found!");
                continue;
            }

            ConfigCommandsHandler.logDebug(localDebug, "%s has %s command(s): %s", key, commandsToRun.size(), commandsToRun);

            // less important, but will warn user if they don't exist
            String shortDescription = command.getString("shortDescription");
            if (shortDescription == null) ConfigCommandsHandler.logWarning("%s has no shortDescription.", key);
            ConfigCommandsHandler.logDebug(localDebug, "%s has shortDescription: %s", key, shortDescription);

            String fullDescription = command.getString("fullDescription");
            if (fullDescription == null) ConfigCommandsHandler.logWarning("%s has no fullDescription.", key);
            ConfigCommandsHandler.logDebug(localDebug, "%s has fullDescription: %s", key, fullDescription);

            String permission = command.getString("permission");
            if (permission == null) {
                permission = buildDefaultPermission(name);
                ConfigCommandsHandler.logWarning("%s has no permission. Using \"%s\".", key, permission);
            }
            ConfigCommandsHandler.logDebug(localDebug, "%s has permission %s", key, permission);

            // Don't need to warn user about these
            List<Map<?, ?>> args = command.getMapList("args");
            ConfigCommandsHandler.logDebug(localDebug, key + " has args: " + args);

            List<String> aliases = command.getStringList("aliases");
            ConfigCommandsHandler.logDebug(localDebug, "%s has %s alias(es): %s", key, aliases.size(), aliases);

            // register command
            ConfigCommandsHandler.logNormal("Loading %s with name: %s", key, name);
            int indentation = ConfigCommandsHandler.getIndentation();
            ConfigCommandsHandler.increaseIndentation();
            try {
                ReloadCommandHandler.addCommand(
                        new ConfigCommandBuilder(
                                name, shortDescription, fullDescription, args,
                                aliases, permission, commandsToRun, localDebug
                        ),
                        key
                );
            } catch (RegistrationException e) {
                ConfigCommandsHandler.logError("Registration error: \"%s\" Skipping registration", e.getMessage());
                failedCommands.add("(name) " + name + ": Registration error: \"" + e.getMessage() + "\"");
            }
            // reset indentation in case of error
            ConfigCommandsHandler.setIndentation(indentation);
        }

        // inform user of failed commands
        if (failedCommands.size() == 0) {
            ConfigCommandsHandler.logNormal("All commands were successfully registered.");
            ConfigCommandsHandler.logNormal("Note: this does not mean they will work as you expect.");
            if (globalDebug) {
                ConfigCommandsHandler.logNormal("If a command does not work, check the console output to try to find the problem.");
            } else {
                ConfigCommandsHandler.logNormal("If a command does not work, turn on debug mode, then check the console output to try to find the problem.");
            }
        } else {
            ConfigCommandsHandler.logNormal("%s command(s) failed while registering:", failedCommands.size());
            ConfigCommandsHandler.increaseIndentation();
            for (String message : failedCommands) {
                ConfigCommandsHandler.logError(message);
            }
            ConfigCommandsHandler.decreaseIndentation();
            if (globalDebug) {
                ConfigCommandsHandler.logNormal("Scroll up to find more information.");
            } else {
                ConfigCommandsHandler.logNormal("Turn on debug mode and scroll up to find more information.");
            }
        }
    }

    public static String buildDefaultPermission(String name){
        return "configcommands." + name.toLowerCase(Locale.ROOT);
    }

    private final ArrayList<String> argument_keys = new ArrayList<>();
    private final HashMap<String, Class<? extends InternalArgument>> argument_variable_classes = new HashMap<>();

    private ArrayList<HashMap<String, Object>> commands = new ArrayList<>();

    private Map<String, Integer> tagMap = new HashMap<>();

    private final boolean localDebug;

    private ConfigCommandExecutor executor;

    public ConfigCommandBuilder(String name, String shortDescription, String fullDescription, List<Map<?, ?>> args,
                                List<String> aliases, String permission, List<String> commands,
                                boolean localDebug) throws RegistrationException {
        //set name
        super(name);

        // set debug variable
        this.localDebug = localDebug;

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
        executor = new ConfigCommandExecutor(name, argument_keys, this.commands, tagMap, this.localDebug);
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
            ConfigCommandsHandler.logDebug(localDebug, "Adding argument: %s", arg);
            ConfigCommandsHandler.increaseIndentation();
            InternalArgument.addArgument(arg, this, argument_keys, argument_variable_classes, localDebug);
            ConfigCommandsHandler.decreaseIndentation();
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
        executor = new ConfigCommandExecutor(getName(), argument_keys, this.commands, tagMap, localDebug);
    }

    private void parse_commands(List<String> commands) throws RegistrationException {
        // Commands:
        // - Run command, indicated by /
        // - define and set variables, indicated by <name> =
        // - run functions, indicated by do
        // - define branch target tag, indicated by tag
        // - conditional branch, indicated by if
        // - branch, indicated by goto
        // - return value, indicated by return
        int index = 0;
        for (String command : commands) {
            ConfigCommandsHandler.logDebug(localDebug, "Parsing command: %s", command);

            ConfigCommandsHandler.increaseIndentation();
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
            ConfigCommandsHandler.decreaseIndentation();
            index++;
        }
    }

    private void parse_command_to_run(String command) {
        List<String> command_sections = getCommandSections(command);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "command");
        new_command.put("info", command_sections);
        commands.add(new_command);
    }

    private List<String> getCommandSections(String command){
        List<String> command_sections = new ArrayList<>();
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

        ConfigCommandsHandler.logDebug(localDebug, "Command has been split into: %s", command_sections);
        return command_sections;
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

    private record intPair(int first, int last){
    }

    private void parse_set(String command) throws RegistrationException {
        String[] parts = command.split(" = ");
        if (parts.length != 2) throw new InvalidSetCommand(command, "Invalid format. Must contain only 1 \" = \".");

        ConfigCommandsHandler.logDebug(localDebug, "Set split into: %s", Arrays.toString(parts));

        String variable = parts[0];
        if (!(variable.charAt(0) == '<' && variable.charAt(variable.length() - 1) == '>')) {
            throw new InvalidSetCommand(command, "Invalid variable: " + variable + ". Must be wrapped by < >.");
        }
        ConfigCommandsHandler.logDebug(localDebug, "Variable is " + variable);

        String rawExpression = parts[1];
        Class<? extends InternalArgument> returnType;
        HashMap<String, Object> info;
        if (rawExpression.startsWith("/")) {
            // setting a variable to a command
            ConfigCommandsHandler.logDebug(localDebug, "Expression looks like a command.");
            ConfigCommandsHandler.logDebug(localDebug, "Parsing \"%s\" as command.", rawExpression);

            // parse command
            List<String> command_sections = getCommandSections(rawExpression);

            info = new HashMap<>();
            info.put("variable", variable);
            info.put("expressionType", "command");
            info.put("command", command_sections);

            returnType = InternalStringArgument.class;
        } else {
            // expression is code
            Expression expression = Expression.parseExpression(rawExpression, argument_variable_classes, localDebug);
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
            ConfigCommandsHandler.logDebug(localDebug, "%s already found in argument keys, and the return type matches.", variable);
        } else {
            argument_keys.add(variable);
            argument_variable_classes.put(variable, returnType);
            ConfigCommandsHandler.logDebug("%s not found in arguments keys, so it was created to match return type.", variable);
        }

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "set");
        new_command.put("info", info);
        commands.add(new_command);
    }

    private void parse_do(String command) throws RegistrationException {
        if (!command.startsWith("do ")) throw new InvalidDoCommand(command, "Invalid format. Must start with \"do \".");

        command = command.substring(3);
        ConfigCommandsHandler.logDebug(localDebug, "Do trimmed off to get: %s", command);

        Expression expression = Expression.parseExpression(command, argument_variable_classes, localDebug);

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
        ConfigCommandsHandler.logDebug(localDebug, "Tag trimmed off to get: %s", tag);
        ConfigCommandsHandler.logDebug(localDebug, "Tag will have index: %s", index);
        tagMap.put(tag, index);

        HashMap<String, Object> new_command = new HashMap<>();
        new_command.put("type", "tag");
        commands.add(new_command);
    }

    private void parse_if(String command) throws RegistrationException {
        if (!command.startsWith("if ")) throw new InvalidIfCommand(command, "Invalid format. Must start with \"if \"");

        command = command.substring(3);
        ConfigCommandsHandler.logDebug(localDebug, "If trimmed off to get: %s", command);

        String[] parts = command.split(" goto ");
        if (parts.length != 2) throw new InvalidIfCommand(command, "Invalid format. Must contain \" goto \" once.");
        ConfigCommandsHandler.logDebug(localDebug, "If split into: %s", Arrays.toString(parts));

        String booleanString = parts[0];
        ConfigCommandsHandler.logDebug(localDebug, "booleanExpression is: %s", booleanString);
        Expression booleanExpression = Expression.parseExpression(booleanString, argument_variable_classes, localDebug);
        ConfigCommandsHandler.logDebug(localDebug, "booleanExpression parsed to: %s", booleanExpression);
        Class<? extends InternalArgument> returnType = booleanExpression.getEvaluationType(argument_variable_classes);
        if (!returnType.isAssignableFrom(InternalBooleanArgument.class))
            throw new InvalidIfCommand(command, "Invalid booleanExpression. Must return InternalBooleanArgument, but instead returns " + returnType.getSimpleName());
        ConfigCommandsHandler.logDebug(localDebug, "booleanExpression correctly returns a boolean");

        String indexString = parts[1];
        ConfigCommandsHandler.logDebug("indexExpression is: %s", indexString);
        Expression indexExpression = Expression.parseExpression(indexString, argument_variable_classes, localDebug);
        ConfigCommandsHandler.logDebug(localDebug, "indexExpression parsed to: %s", indexExpression);
        returnType = indexExpression.getEvaluationType(argument_variable_classes);

        HashMap<String, Object> info = new HashMap<>();
        if(returnType.isAssignableFrom(InternalIntegerArgument.class)){
            ConfigCommandsHandler.logDebug(localDebug, "indexExpression correctly returns an integer");
            info.put("indexType", "integer");
        } else if(returnType.isAssignableFrom(InternalStringArgument.class)){
            ConfigCommandsHandler.logDebug(localDebug, "indexExpression correctly returns a string");
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
        ConfigCommandsHandler.logDebug(localDebug, "Goto trimmed off to get: %s", indexString);

        ConfigCommandsHandler.logDebug(localDebug, "indexExpression is: %s", indexString);
        Expression indexExpression = Expression.parseExpression(indexString, argument_variable_classes, localDebug);
        ConfigCommandsHandler.logDebug(localDebug, "indexExpression parsed to: %s", indexExpression);
        Class<? extends InternalArgument> returnType = indexExpression.getEvaluationType(argument_variable_classes);

        HashMap<String, Object> info = new HashMap<>();
        if(returnType.isAssignableFrom(InternalIntegerArgument.class)){
            ConfigCommandsHandler.logDebug(localDebug, "indexExpression correctly returns an integer");
            info.put("indexType", "integer");
        } else if(returnType.isAssignableFrom(InternalStringArgument.class)){
            ConfigCommandsHandler.logDebug(localDebug, "indexExpression correctly returns a string");
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
        ConfigCommandsHandler.logDebug(localDebug, "Return trimmed off to get: %s", returnString);

        ConfigCommandsHandler.logDebug(localDebug, "returnExpression is: %s", returnString);
        Expression returnExpression = Expression.parseExpression(returnString, argument_variable_classes, localDebug);
        ConfigCommandsHandler.logDebug(localDebug, "returnExpression parsed to: %s", returnExpression);

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
}
