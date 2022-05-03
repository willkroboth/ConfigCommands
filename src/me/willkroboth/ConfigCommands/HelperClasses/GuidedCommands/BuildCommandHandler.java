package me.willkroboth.ConfigCommands.HelperClasses.GuidedCommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.ConfigCommands.ConfigCommands;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandBuilder;
import me.willkroboth.ConfigCommands.HelperClasses.IgnoredIndentedLogger;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.*;

public class BuildCommandHandler implements Listener {
    private static final Map<CommandSender, CommandContext> activeUsers = new HashMap<>();
    private static final Map<CommandSender, String> keysBeingEditing = new HashMap<>();
    private static final List<CommandSender> passToHelpCommand = new ArrayList<>();

    // command functions
    public static void addUser(CommandSender sender, Object[] ignored){
        sender.sendMessage("Welcome to the ConfigCommand build menu!");
        sender.sendMessage("Enter ## at any time to cancel.");
        sender.sendMessage("Type back to return to previous step.");
        ConfigCommands.reloadConfigFile();
        activeUsers.put(sender, new CommandContext(null, "", BuildCommandHandler::chooseCommand));        handleMessage(sender, "", null);
    }

    private static CommandContext setContext(CommandSender sender, CommandContext previousContext, Object previousChoice, CommandStep nextStep){
        CommandContext newContext = new CommandContext(previousContext, previousChoice, nextStep);
        activeUsers.put(sender, newContext);
        return newContext;
    }

    private static CommandContext goBack(CommandSender sender, int steps, CommandContext currentContext){
        CommandContext newContext = currentContext;
        for (int i = 0; i < steps; i++) {
            newContext = newContext.getPreviousContext();
            if (newContext == null) break;
        }
        activeUsers.put(sender, newContext);
        return newContext;
    }

    private static void goBackStep(CommandSender sender, String message, CommandContext context){
        handleMessage(sender, "back", null);
    }

    // events
    @EventHandler
    public void onChatSent(AsyncPlayerChatEvent event){
        handleMessage(event.getPlayer(), event.getMessage(), event);
    }

    @EventHandler
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event){
        handleMessage(event.getPlayer(), event.getMessage(), event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        activeUsers.remove(player);
        keysBeingEditing.remove(player);
    }

    @EventHandler
    public void onConsoleSent(ServerCommandEvent event){
        handleMessage(event.getSender(), event.getCommand(), event);
    }

    private static void handleMessage(CommandSender sender, String message, Cancellable event) {
        if (activeUsers.containsKey(sender)) {
            if (passToHelpCommand.contains(sender)) {
                if (message.equals("##")) {
                    // just closed help menu
                    // don't cancel message b/c HelpCommandHandler will cancel it
                    passToHelpCommand.remove(sender);
                    activeUsers.get(sender).doNextStep(sender, "");
                }
            } else {
                if (event != null) event.setCancelled(true);
                if (sender instanceof Player) sender.sendMessage("");
                if (message.equals("##")) {
                    sender.sendMessage("Closing the ConfigCommand build menu.");
                    sender.sendMessage("All command changes will take effect once server restarts.");
                    activeUsers.remove(sender);
                    keysBeingEditing.remove(sender);
                } else if (message.equalsIgnoreCase("back")) {
                    CommandContext previous = activeUsers.get(sender).getPreviousContext();
                    if (previous == null) {
                        sender.sendMessage("No step to go back to.");
                    } else {
                        activeUsers.put(sender, previous);
                        activeUsers.get(sender).doNextStep(sender, "");
                    }
                } else {
                    activeUsers.get(sender).doNextStep(sender, message);
                }
            }
        }
    }

    // Build menu steps
    private static void chooseCommand(CommandSender sender, String message, CommandContext context) {
        keysBeingEditing.remove(sender);

        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");

        if(commands == null){
            config.createSection("commands");
            commands = config.getConfigurationSection("commands");
        }
        assert commands != null;
        Set<String> keys = commands.getKeys(false);

        if (message.isBlank()){
            if(keys.size() != 0) {
                sender.sendMessage("Found " + keys.size() + " command" + (keys.size() == 1? "": "s") + " to edit");
                sender.sendMessage("Type one of the following keys to edit:");
                for(String key:keys) {
                    sender.sendMessage("  " + key);
                }
                sender.sendMessage("Or type \"create\" to make a new command");
            } else {
                sender.sendMessage("No commands found");
                sender.sendMessage("Type \"create\" to make a new command");
            }
        } else {
            if(keys.contains(message)){
                if(keysBeingEditing.containsValue(message)){
                    sender.sendMessage("Someone else is currently editing that command!");
                } else {
                    keysBeingEditing.put(sender, message);
                    context = setContext(sender, context, message, BuildCommandHandler::editCommand);
                    context.doNextStep(sender, "");
                }
            } else if(message.equals("create")){
                context = setContext(sender, context, "", BuildCommandHandler::createCommand);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage(message + " is not recognized as an existing command");
            }
        }
    }

    private static void createCommand(CommandSender sender, String message, CommandContext context) {
        if (message.isBlank()){
            sender.sendMessage("What key should the command be stored under?");
        } else {
            FileConfiguration config = ConfigCommands.getConfigFile();
            ConfigurationSection commands = config.getConfigurationSection("commands");
            assert commands != null;
            Set<String> keys = commands.getKeys(false);
            if(!keys.contains(message)){
                commands.createSection(message);

                context = goBack(sender, 1, context);
                context = setContext(sender, context, message, BuildCommandHandler::editCommand);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("That key is already in use!");
            }
        }
    }

    private static void editCommand(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        if(message.isBlank()){
            sender.sendMessage("Editing command with key \"" + key + "\"");
            sender.sendMessage("Select one of the following options using its number to continue:");
            sender.sendMessage("  1. See current info");
            sender.sendMessage("  2. Change key in config.yml");
            sender.sendMessage("  3. Change name");
            sender.sendMessage("  4. Edit arguments");
            sender.sendMessage("  5. Change short description");
            sender.sendMessage("  6. Change full description");
            sender.sendMessage("  7. Change permission");
            sender.sendMessage("  8. Edit aliases");
            sender.sendMessage("  9. Edit commands");
            sender.sendMessage("  10. Delete entire command");
        }
        else {
            CommandStep nextStep = switch (message) {
                case "1" -> BuildCommandHandler::seeInfo;
                case "2" -> BuildCommandHandler::changeKey;
                case "3" -> BuildCommandHandler::changeName;
                case "4" -> BuildCommandHandler::editArguments;
                case "5" -> BuildCommandHandler::changeShortDescription;
                case "6" -> BuildCommandHandler::changeFullDescription;
                case "7" -> BuildCommandHandler::changePermission;
                case "8" -> BuildCommandHandler::editAliases;
                case "9" -> BuildCommandHandler::editCommands;
                case "10" -> BuildCommandHandler::deleteCommand;
                default -> null;
            };
            if(nextStep == null) {
                sender.sendMessage("Message \"" + message + "\" is not a number from 1 to 10");
                return;
            }
            context = setContext(sender, context, key, nextStep);
            context.doNextStep(sender, "");
        }
    }

    private static void deleteCommand(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        if(message.isBlank()){
            sender.sendMessage("Are you sure you want to delete command: \"" + key + "\"?");
            sender.sendMessage(ChatColor.YELLOW + "THIS ACTION CANNOT BE UNDONE");
            sender.sendMessage("Type \"yes\" to confirm or anything else to go back.");
        }
        else {
            if(message.equals("yes")){
                FileConfiguration config = ConfigCommands.getConfigFile();
                ConfigurationSection commands = config.getConfigurationSection("commands");
                assert commands != null;
                commands.set(key, null);
                ConfigCommands.saveConfigFile();
                sender.sendMessage("Deleted command \"" + key + "\"");

                context = goBack(sender, 2, context);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Not deleting command \"" + key + "\"");

                context = goBack(sender, 1, context);
                context.doNextStep(sender, "");
            }
        }
    }

    private static void editCommands(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;
        List<String> commandList = command.getStringList("commands");

        if (message.isBlank()) {
            if (commandList.size() == 0) {
                sender.sendMessage("No commands");
                sender.sendMessage("Type a new command");
            } else {
                sender.sendMessage("Current commands:");
                for (int i = 0; i < commandList.size(); i++) {
                    sender.sendMessage("  " + (i) + ". " + commandList.get(i));
                }
                sender.sendMessage("Type a number to delete the corresponding command");
                sender.sendMessage("Type anything else to add it as a new command");
            }
            sender.sendMessage("Type '?' for help with the command format.");
            sender.sendMessage("Type \"functions\" to use /configcommandhelp to get help with functions");
        } else {
            if (message.matches("[0-9]+")) {
                int target = Integer.parseInt(message);
                if (0 <= target && target < commandList.size()) {
                    sender.sendMessage("Deleting command \"" + commandList.get(target) + "\"");
                    commandList.remove(target);
                    command.set("commands", commandList);
                    ConfigCommands.saveConfigFile();

                    context.doNextStep(sender, "");
                } else {
                    sender.sendMessage("Given number is not in range 0 to " + (commandList.size() - 1));
                }
            } else if (message.equals("?")) {
                sender.sendMessage("Options for commands:");
                sender.sendMessage("  Commands:");
                sender.sendMessage("    Regular command - \"/say Hello World\"");
                sender.sendMessage("    Command referencing variables - \"/tp <x> <y> <z>\"");
                sender.sendMessage("  Set variable:");
                sender.sendMessage("    To result of function - \"<counter> = Integer.new(\"10\")\"");
                sender.sendMessage("    To result of command - \"<result> = /data get entity @p Health\"");
                sender.sendMessage("  Run function:");
                sender.sendMessage("    do <sender>.dispatchCommand(<message>)");
                sender.sendMessage("  Define branch tag:");
                sender.sendMessage("    tag Option 1");
                sender.sendMessage("  Branch conditionally:");
                sender.sendMessage("    if <message>.equals(\"1\") goto \"Option 1\"");
                sender.sendMessage("  Jump:");
                sender.sendMessage("    Jump to line # - \"goto Integer.(\"4\")\"");
                sender.sendMessage("    Jump to tag - \"goto \"Option 1\"\"");
                sender.sendMessage("    End execution - \"goto Integer.(\"-1\")\"");
                sender.sendMessage("  Output result as string:");
                sender.sendMessage("    return <counter>");
            } else if (message.equals("functions")) {
                HelpCommandHandler.addUser(sender, null);
                passToHelpCommand.add(sender);
            } else {
                context = setContext(sender, context, message, BuildCommandHandler::addCommand);
                context.doNextStep(sender, "");
            }
        }
    }

    private static void addCommand(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousContext().getPreviousChoice();
        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;
        List<String> commandList = command.getStringList("commands");

        String newCommand = (String) context.getPreviousChoice();

        if(message.isBlank()){
            if (commandList.size() == 0) {
                commandList.add(newCommand);
                command.set("commands", commandList);
                ConfigCommands.saveConfigFile();

                context = goBack(sender, 1, context);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Current commands:");
                for (int i = 0; i < commandList.size(); i++) {
                    sender.sendMessage("  " + (i) + ". " + commandList.get(i));
                }
                sender.sendMessage("Type an index to place the new command at");
            }
        } else {
            if(message.matches("[0-9]+")){
                int target = Integer.parseInt(message);
                if(0 <= target && target <= commandList.size()){
                    commandList.add(target, newCommand);
                    command.set("commands", commandList);
                    ConfigCommands.saveConfigFile();

                    context = goBack(sender, 1, context);
                    context.doNextStep(sender, "");
                } else {
                    sender.sendMessage("Given number is not in range 0 to " + commandList.size());
                }
            } else {
                sender.sendMessage("Cannot recognize message \"" + message + "\" as number");
            }
        }
    }

    private static void editArguments(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;
        List<Map<?, ?>> args = command.getMapList("args");

        if(message.isBlank()){
            if (args.size() == 0) {
                sender.sendMessage("No arguments");
                sender.sendMessage("Type anything to start creating an argument with that name");
            } else {
                sender.sendMessage("Current arguments:");
                for (int i = 0; i < args.size(); i++) {
                    sender.sendMessage("  " + (i+1) + ". " + args.get(i));
                }
                sender.sendMessage("Type a number to edit the corresponding argument");
                sender.sendMessage("Type anything else to start creating an argument with that name");
            }
        } else {
            if(message.matches("[0-9]+")){
                int target = Integer.parseInt(message);
                if(0 < target && target <= args.size()){
                    // skip step that chooses type
                    context = setContext(sender, context, args.get(target-1), BuildCommandHandler::goBackStep);
                    context = setContext(sender, context, args.get(target-1), BuildCommandHandler::addParametersToArgument);
                    context.doNextStep(sender, "");
                } else {
                    sender.sendMessage("Given number is not in range 1 to " + args.size());
                }
            } else {
                message = InternalArgument.formatArgumentName(message);
                boolean nameUsedBefore = false;
                for(Map<?, ?> arg:args){
                    String name = (String) arg.get("name");
                    if(name != null && message.equals(InternalArgument.formatArgumentName(name))){
                        nameUsedBefore = true;
                        break;
                    }
                }
                if(nameUsedBefore) {
                    sender.sendMessage("Name " + message + " is already in use!");
                }
                else {
                    sender.sendMessage("Creating argument with name: \"" + message + "\"");
                    Map<String, String> arg = new HashMap<>();
                    arg.put("name", message);
                    context = setContext(sender, context, arg, BuildCommandHandler::chooseTypeForArgument);

                    context.doNextStep(sender, "");
                }
            }
        }
    }

    private static void chooseTypeForArgument(CommandSender sender, String message, CommandContext context) {
        if(message.isBlank()){
            sender.sendMessage("What will the argument type be?");
            sender.sendMessage("Type '?' for a list of valid argument types");
        } else if(message.equals("?")) {
            sender.sendMessage("Argument types:");
            sender.sendMessage(InternalArgument.getArgumentTypes().toString());
        } else if(InternalArgument.getArgumentTypes().contains(message)) {
            Map<String, String> arg = (Map<String, String>) context.getPreviousChoice();
            arg.put("type", message);

            context = setContext(sender, context, arg, BuildCommandHandler::addParametersToArgument);

            context.doNextStep(sender, "");
        } else {
            sender.sendMessage("Type \"" + message + "\" not found.");
        }
    }

    private static void addParametersToArgument(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousContext().getPreviousContext().getPreviousChoice();
        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;
        List<Map<?, ?>> args = command.getMapList("args");

        Map<String, String> arg = (Map<String, String>) context.getPreviousChoice();


        if(message.isBlank()){
            sender.sendMessage("Current argument: " + arg.toString());
            sender.sendMessage(testArgument(arg, args));
            sender.sendMessage("Create a new key or overwrite an old one by typing \"key:value\"");
            sender.sendMessage("Delete a key by typing \"key:\"");
            sender.sendMessage("Delete the argument by typing \"delete\"");
            sender.sendMessage("Finish adding the argument by typing \"confirm\"");
        } else if(message.equals("confirm")){
            context = setContext(sender, context, key, BuildCommandHandler::addArgument);

            context.doNextStep(sender, "");
        } else if(message.equals("delete")){
            args.remove(arg);
            command.set("args", args);
            ConfigCommands.saveConfigFile();

            context = goBack(sender, 2, context);
            context.doNextStep(sender, "");
        } else {
            String[] parts = message.split(":", 2);
            if(parts.length != 2){
                sender.sendMessage("Unknown \"key:value\" format.");
            } else{
                String argKey = parts[0];
                if(argKey.isBlank()){
                    sender.sendMessage("key cannot be blank");
                    return;
                }
                String argValue = parts[1];
                if(argValue.isBlank()){
                    arg.remove(argKey);
                    sender.sendMessage("Removed key: " + argKey);
                } else {
                    arg.put(argKey, argValue);
                    sender.sendMessage("Added " + message);
                }
                context.doNextStep(sender, "");
            }
        }
    }

    private static String testArgument(Map<String, String> arg, List<Map<?, ?>> previousArgs){
        CommandAPICommand dummyCommand = new CommandAPICommand("dummy");
        ArrayList<String> argument_keys = new ArrayList<>();
        HashMap<String, Class<? extends InternalArgument>> argument_variable_classes = new HashMap<>();
        for(Map.Entry<String, Class<? extends InternalArgument>> preArg: ConfigCommandBuilder.getDefaultArgs().entrySet()) {
            argument_keys.add(preArg.getKey());
            argument_variable_classes.put(preArg.getKey(), preArg.getValue());
        }
        for(Map<?, ?> preArg: previousArgs){
            if(!preArg.equals(arg)) {
                argument_keys.add((String) preArg.get("name"));
                argument_variable_classes.put((String) preArg.get("name"), InternalArgument.class);
            }
        }
        boolean debugMode = false;
        IndentedLogger logger = new IgnoredIndentedLogger();
        try {
            InternalArgument.addArgument(arg, dummyCommand, argument_keys, argument_variable_classes, debugMode, logger);
            Argument argument = dummyCommand.getArguments().get(0);
            return "Argument adds without problem, producing a " + argument.getClass().getSimpleName() + " with rawType: " + argument.getRawType().toString();
        } catch (IncorrectArgumentKey e) {
            return "Attempting to add argument throws error: " + e.getMessage();
        }
    }

    private static void addArgument(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;
        List<Map<?, ?>> args = command.getMapList("args");

        Map<String, String> arg = (Map<String, String>) context.getPreviousContext().getPreviousChoice();

        args.remove(arg);

        if(message.isBlank()){
            if (args.size() == 0) {
                sender.sendMessage("Added argument: " + arg);
                args.add(arg);
                command.set("args", args);
                ConfigCommands.saveConfigFile();

                context = goBack(sender, 3, context);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Current arguments:");
                for (int i = 0; i < args.size(); i++) {
                    sender.sendMessage("  " + (i+1) + ". " + args.get(i));
                }
                sender.sendMessage("Adding argument: " + arg);
                sender.sendMessage("Type an index to place the new argument at");
            }
        } else {
            if(message.matches("[0-9]+")){
                int target = Integer.parseInt(message);
                if(0 < target && target <= args.size()+1){
                    args.add(target-1, arg);
                    command.set("args", args);
                    ConfigCommands.saveConfigFile();

                    context = goBack(sender, 3, context);
                    context.doNextStep(sender, "");
                } else {
                    sender.sendMessage("Given number is not in range 1 to " + (args.size()+1));
                }
            } else {
                sender.sendMessage("Cannot recognize message \"" + message + "\" as number");
            }
        }
    }

    private static void editAliases(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;
        List<String> aliases = command.getStringList("aliases");

        if(message.isBlank()){
            if (aliases.size() == 0) {
                sender.sendMessage("No aliases");
                sender.sendMessage("Type anything to add it as an alias");
            } else {
                sender.sendMessage("Current aliases:");
                for (int i = 0; i < aliases.size(); i++) {
                    sender.sendMessage("  " + (i+1) + ". " + aliases.get(i));
                }
                sender.sendMessage("Type a number to delete the corresponding alias");
                sender.sendMessage("Type anything else to add it as an alias");
            }
        } else {
            if(message.matches("[0-9]+")){
                int target = Integer.parseInt(message);
                if(0 < target && target <= aliases.size()){
                    sender.sendMessage("Deleting alias \"" + aliases.get(target-1) + "\"");
                    aliases.remove(target-1);
                    command.set("aliases", aliases);
                    ConfigCommands.saveConfigFile();

                    context.doNextStep(sender, "");
                } else {
                    sender.sendMessage("Given number is not in range 1 to " + aliases.size());
                }
            } else {
                sender.sendMessage("Adding alias \"" + message + "\"");

                aliases.add(message);
                command.set("aliases", aliases);
                ConfigCommands.saveConfigFile();

                context.doNextStep(sender, "");
            }
        }
    }

    private static void changePermission(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();

        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;

        if(message.isBlank()){
            String permission = command.getString("permission");
            if (permission == null) {
                sender.sendMessage("Current permission is null");
                String name = command.getString("name");
                sender.sendMessage("Default permission is \"" +
                        ConfigCommandBuilder.getDefaultPermission(name == null? key:name) + "\""
                );
            } else {
                sender.sendMessage("Current permission: \"" + permission + "\"");
            }
            sender.sendMessage("Please type the permission you would like to use or back to go back.");
        } else {
            command.set("permission", message);
            ConfigCommands.saveConfigFile();

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void handleSimpleChange(CommandSender sender, String message, CommandContext context, String section){
        String key = (String) context.getPreviousChoice();

        FileConfiguration config = ConfigCommands.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");
        assert commands != null;
        ConfigurationSection command = commands.getConfigurationSection(key);
        assert command != null;

        if(message.isBlank()){
            String value = command.getString(section);
            if (value == null) {
                sender.sendMessage("Current " + section + " is null");
            } else {
                sender.sendMessage("Current " + section + ": \"" + value + "\"");
            }
            sender.sendMessage("Please type the " + section + " you would like to use or back to go back.");
        } else {
            command.set(section, message);
            ConfigCommands.saveConfigFile();

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void changeFullDescription(CommandSender sender, String message, CommandContext context) {
        handleSimpleChange(sender, message, context, "fullDescription");
    }

    private static void changeShortDescription(CommandSender sender, String message, CommandContext context) {
        handleSimpleChange(sender, message, context, "shortDescription");
    }

    private static void changeName(CommandSender sender, String message, CommandContext context) {
        handleSimpleChange(sender, message, context, "name");
    }

    private static void changeKey(CommandSender sender, String message, CommandContext context) {
        String key = (String) context.getPreviousChoice();
        if(message.isBlank()){
            sender.sendMessage("Current key: " + key);
            sender.sendMessage("What would you like the new key to be?");
        } else {
            FileConfiguration config = ConfigCommands.getConfigFile();
            ConfigurationSection commands = config.getConfigurationSection("commands");
            assert commands != null;
            Set<String> keys = commands.getKeys(false);
            if(!keys.contains(message)){
                commands.set(message, commands.get(key));
                keysBeingEditing.put(sender, message);

                commands.set(key, null);
                ConfigCommands.saveConfigFile();

                context = goBack(sender, 2, context);
                context = setContext(sender, context, message, BuildCommandHandler::editCommand);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("That key is already in use!");
            }
        }
    }

    private static void seeInfo(CommandSender sender, String message, CommandContext context) {
        if(message.isBlank()) {
            String key = (String) context.getPreviousChoice();

            FileConfiguration config = ConfigCommands.getConfigFile();
            ConfigurationSection commands = config.getConfigurationSection("commands");
            assert commands != null;
            ConfigurationSection command = commands.getConfigurationSection(key);
            assert command != null;

            sender.sendMessage("Information for command " + key);
            sender.sendMessage("  Name: \"" + command.getString("name") + "\"");

            List<String> aliases = command.getStringList("aliases");
            if(aliases.size() == 0){
                sender.sendMessage("  No Aliases");
            } else {
                sender.sendMessage("  Aliases:");
                for (String alias: aliases){
                    sender.sendMessage("    " + alias);
                }
            }

            List<Map<?, ?>> args = command.getMapList("args");
            if(args.size() == 0){
                sender.sendMessage("  No Arguments");
            } else {
                sender.sendMessage("  Arguments:");
                for (Map<?, ?> arg: args){
                    sender.sendMessage("    " + arg.toString());
                }
            }

            sender.sendMessage("  Permission: \"" + command.getString("permission") + "\"");
            sender.sendMessage("  Short Description: \"" + command.getString("shortDescription") + "\"");
            sender.sendMessage("  Full Description: \"" + command.getString("fullDescription") + "\"");

            List<String> commandsToRun = command.getStringList("commands");
            if(commandsToRun.size() == 0){
                sender.sendMessage("  No Commands");
            } else {
                sender.sendMessage("  Commands:");
                for (int i = 0; i < commandsToRun.size(); i++){
                    sender.sendMessage("    " + (i+1) + ". "  + commandsToRun.get(i));
                }
            }

            sender.sendMessage("Type anything to continue");
        } else {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }
}
