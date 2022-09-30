package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.executors.ExecutorType;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedCommandSenderMessenger;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.CommandTreeBuilder;
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

public class BuildCommandHandler extends SystemCommandHandler implements Listener {
    // command configuration
    protected ArgumentTree getArgumentTree() {
        return super.getArgumentTree().executes(BuildCommandHandler::addUser, ExecutorType.CONSOLE, ExecutorType.PLAYER);
    }

    private static final String[] helpMessages = new String[]{
            "Opens a menu that guides users through creating a new command",
            "Enables creating, editing, and deleting commands in-game",
            "Usage:",
            "\t/configcommands build"
    };

    protected String[] getHelpMessages() {
        return helpMessages;
    }


    // command functions
    private static final Map<CommandSender, CommandContext> activeUsers = new HashMap<>();
    private static final Map<CommandSender, String> commandsBeingEditing = new HashMap<>();
    private static final Map<CommandSender, List<String>> argumentPaths = new HashMap<>();
    private static final List<CommandSender> forwardedFromArgumentInfo = new ArrayList<>();
    private static final List<CommandSender> passToFunctionCommand = new ArrayList<>();

    private static void addUser(CommandSender sender, Object[] ignored) {
        sender.sendMessage("Welcome to the ConfigCommand build menu!");
        sender.sendMessage("Enter ## at any time to cancel.");
        // Going back is handled by each step in case they need to update the above variables
        // Each step should provide the behavior that typing "back" puts the user on the previous step
        sender.sendMessage("Type back to return to previous step.");
        ConfigCommandsHandler.reloadConfigFile();
        activeUsers.put(sender, new CommandContext(null, "", BuildCommandHandler::chooseCommand));
        handleMessage(sender, "", null);
    }

    // events
    @EventHandler
    public void onChatSent(AsyncPlayerChatEvent event) {
        handleMessage(event.getPlayer(), event.getMessage(), event);
    }

    @EventHandler
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        handleMessage(event.getPlayer(), event.getMessage(), event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        activeUsers.remove(player);
        commandsBeingEditing.remove(player);
    }

    @EventHandler
    public void onConsoleSent(ServerCommandEvent event) {
        handleMessage(event.getSender(), event.getCommand(), event);
    }

    private static void handleMessage(CommandSender sender, String message, Cancellable event) {
        if (activeUsers.containsKey(sender)) {
            if (passToFunctionCommand.contains(sender)) {
                if (message.equals("##")) {
                    // just closed function menu
                    // don't cancel message b/c FunctionCommandHandler will cancel it
                    passToFunctionCommand.remove(sender);
                    activeUsers.get(sender).doNextStep(sender, "");
                }
            } else {
                if (event != null) event.setCancelled(true);
                if (sender instanceof Player) sender.sendMessage("");

                if (message.equals("##")) {
                    sender.sendMessage("Closing the ConfigCommand build menu.");
                    sender.sendMessage("All command changes will take effect once server restarts.");
                    // TODO: Resolve reloading
//                    sender.sendMessage("If you changed the commands of a registered command, you can update it using /configcommands reload");
                    activeUsers.remove(sender);
                    commandsBeingEditing.remove(sender);
                    argumentPaths.remove(sender);
                } else {
                    activeUsers.get(sender).doNextStep(sender, message);
                }
            }
        }
    }

    // Build menu steps
    private static CommandContext setContext(CommandSender sender, CommandContext previousContext, Object previousChoice, CommandStep nextStep) {
        CommandContext newContext = new CommandContext(previousContext, previousChoice, nextStep);
        activeUsers.put(sender, newContext);
        return newContext;
    }

    private static CommandContext goBack(CommandSender sender, int steps, CommandContext currentContext) {
        CommandContext newContext = currentContext;
        for (int i = 0; i < steps; i++) {
            newContext = newContext.getPreviousContext();
            if (newContext == null) break;
        }
        activeUsers.put(sender, newContext);
        return newContext;
    }

    private static void handleSimpleChange(CommandSender sender, String message, CommandContext context, String section) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();

        if (message.isBlank()) {
            String value = command.getString(section);
            if (value == null) {
                sender.sendMessage("Current " + section + " is null");
            } else {
                sender.sendMessage("Current " + section + ": \"" + value + "\"");
            }
            sender.sendMessage("Please type the " + section + " you would like to use or back to go back.");
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            command.set(section, message);
            ConfigCommandsHandler.saveConfigFile();
            sender.sendMessage(section + " is now: \"" + message + "\"");

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void chooseCommand(CommandSender sender, String message, CommandContext context) {
        FileConfiguration config = ConfigCommandsHandler.getConfigFile();
        ConfigurationSection commands = config.getConfigurationSection("commands");

        if (commands == null) {
            config.createSection("commands");
            commands = config.getConfigurationSection("commands");
        }
        assert commands != null;
        Set<String> keys = commands.getKeys(false);

        if (message.isBlank()) {
            if (keys.size() != 0) {
                sender.sendMessage("Found " + keys.size() + " command" + (keys.size() == 1 ? "" : "s") + " to edit");
                sender.sendMessage("Type one of the following keys to edit:");
                for (String key : keys) {
                    sender.sendMessage("  " + key);
                }
                sender.sendMessage("Or type \"create\" to make a new command");
            } else {
                sender.sendMessage("No commands found");
                sender.sendMessage("Type \"create\" to make a new command");
            }
        } else if (message.equals("back")) {
            sender.sendMessage("There is no step to go back to");
        } else {
            if (keys.contains(message)) {
                if (commandsBeingEditing.containsValue(message)) {
                    sender.sendMessage("Someone else is currently editing that command!");
                } else {
                    commandsBeingEditing.put(sender, message);
                    ConfigurationSection command = commands.getConfigurationSection(message);
                    context = setContext(sender, context, command, BuildCommandHandler::editCommand);
                    context.doNextStep(sender, "");
                }
            } else if (message.equals("create")) {
                context = setContext(sender, context, "", BuildCommandHandler::createCommand);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage(message + " is not recognized as an existing command");
            }
        }
    }

    private static void createCommand(CommandSender sender, String message, CommandContext context) {
        if (message.isBlank()) {
            sender.sendMessage("What should the command be called?");
        } else if (message.equals("back")) {
            sender.sendMessage("Cancelling command creation");
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            FileConfiguration config = ConfigCommandsHandler.getConfigFile();
            ConfigurationSection commands = config.getConfigurationSection("commands");
            assert commands != null;
            Set<String> keys = commands.getKeys(false);
            if (!keys.contains(message)) {
                ConfigurationSection command = commands.createSection(message);
                ConfigCommandsHandler.saveConfigFile();

                commandsBeingEditing.put(sender, message);
                context = goBack(sender, 1, context);
                context = setContext(sender, context, command, BuildCommandHandler::editCommand);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("That name is already in use!");
            }
        }
    }

    private static void editCommand(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
        if (message.isBlank()) {
            sender.sendMessage("Editing command /" + command.getName());
            sender.sendMessage("Select one of the following options using its number to continue:");
            sender.sendMessage("  1. See current info");
            sender.sendMessage("  2. Change name");
            sender.sendMessage("  3. Change short description");
            sender.sendMessage("  4. Change full description");
            sender.sendMessage("  5. Change permission");
            sender.sendMessage("  6. Edit aliases");
            sender.sendMessage("  7. Edit arguments");
            sender.sendMessage("  8. Edit executes");
            sender.sendMessage("  9. Delete entire command");
        } else if (message.equals("back")) {
            commandsBeingEditing.remove(sender);
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            CommandStep nextStep = switch (message) {
                case "1" -> BuildCommandHandler::seeInfo;
                case "2" -> BuildCommandHandler::changeName;
                case "3" -> BuildCommandHandler::changeShortDescription;
                case "4" -> BuildCommandHandler::changeFullDescription;
                case "5" -> BuildCommandHandler::changePermission;
                case "6" -> BuildCommandHandler::editAliases;
                case "7" -> BuildCommandHandler::editArguments;
                case "8" -> BuildCommandHandler::editExecutes;
                case "9" -> BuildCommandHandler::deleteCommand;
                default -> null;
            };
            if (nextStep == null) {
                sender.sendMessage("Message \"" + message + "\" is not a number from 1 to 9");
                return;
            }
            context = setContext(sender, context, command, nextStep);
            context.doNextStep(sender, "");
        }
    }

    private static void deleteCommand(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
        if (message.isBlank()) {
            sender.sendMessage("Are you sure you want to delete command: \"" + command.getName() + "\"?");
            sender.sendMessage(ChatColor.YELLOW + "THIS ACTION CANNOT BE UNDONE");
            sender.sendMessage("Type \"yes\" to confirm or anything else to go back.");
        } else if (message.equals("yes")) {
            ConfigurationSection parent = command.getParent();
            assert parent != null;
            parent.set(command.getName(), null);
            ConfigCommandsHandler.saveConfigFile();
            sender.sendMessage("Deleted command \"" + command.getName() + "\"");

            context = goBack(sender, 2, context);
            context.doNextStep(sender, "");
        } else {
            sender.sendMessage("Not deleting command \"" + command.getName() + "\"");

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }

    }

    private static void editExecutes(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();

        List<String> commandList = command.getStringList("executes");

        if (message.isBlank()) {
            if (commandList.size() == 0) {
                sender.sendMessage("No commands");
                sender.sendMessage("Type a new command");
            } else {
                sender.sendMessage("Current commands:");
                for (int i = 0; i < commandList.size(); i++) {
                    sender.sendMessage("  " + i + ". " + commandList.get(i));
                }
                sender.sendMessage("Type a number to delete the corresponding command");
                sender.sendMessage("Type anything else to add it as a new command");
            }
            sender.sendMessage("Type '?' for help with the command format.");
            sender.sendMessage("Type \"functions\" to use /configcommands functions to get help with functions");
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else if (message.matches("\\d+")) {
            int target = Integer.parseInt(message);
            if (0 <= target && target < commandList.size()) {
                sender.sendMessage("Deleting command \"" + commandList.get(target) + "\"");
                commandList.remove(target);
                command.set("executes", commandList);
                ConfigCommandsHandler.saveConfigFile();

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Given number is not in range 0 to " + (commandList.size() - 1));
            }
        } else if (message.equals("?")) {
            sender.sendMessage("Options for commands:");
            sender.sendMessage("  Run command:");
            sender.sendMessage("    Regular command - \"/say Hello World\"");
            sender.sendMessage("    Command referencing variables - \"/tp <x> <y> <z>\"");
            sender.sendMessage("  Set variable:");
            sender.sendMessage("    To result of function - \"<counter> = Integer.new(\"10\")\"");
            sender.sendMessage("    To result of command - \"<result> = /data get entity @p Health\"");
            sender.sendMessage("  Run expression:");
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
            FunctionsCommandHandler.addUser(sender, null);
            passToFunctionCommand.add(sender);
        } else {
            context = setContext(sender, context, message, BuildCommandHandler::addCommand);
            context.doNextStep(sender, "");
        }
    }

    private static void addCommand(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousContext().getPreviousChoice();
        List<String> commandList = command.getStringList("executes");

        String newCommand = (String) context.getPreviousChoice();

        if (message.isBlank()) {
            if (commandList.size() == 0) {
                commandList.add(newCommand);
                command.set("executes", commandList);
                ConfigCommandsHandler.saveConfigFile();

                context = goBack(sender, 1, context);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Current commands:");
                for (int i = 0; i < commandList.size(); i++) {
                    sender.sendMessage("  " + i + ". " + commandList.get(i));
                }
                sender.sendMessage("Type an index to place the new command at");
            }
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else if (message.matches("\\d+")) {
            int target = Integer.parseInt(message);
            if (0 <= target && target <= commandList.size()) {
                commandList.add(target, newCommand);
                command.set("executes", commandList);
                ConfigCommandsHandler.saveConfigFile();

                context = goBack(sender, 1, context);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Given number is not in range 0 to " + commandList.size());
            }
        } else {
            sender.sendMessage("Cannot recognize message \"" + message + "\" as number");
        }
    }

    private static void editArguments(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
        ConfigurationSection then = command.getConfigurationSection("then");
        if (then == null) {
            command.createSection("then");
            then = command.getConfigurationSection("then");
            assert then != null;
        }
        Set<String> keys = then.getKeys(false);

        if (message.isBlank()) {
            if (keys.size() == 0) {
                sender.sendMessage("No arguments");
                sender.sendMessage("Type anything to start creating an argument with that name");
            } else {
                sender.sendMessage("Current arguments:");
                for (String key : keys) {
                    sender.sendMessage("  " + key);
                }
                sender.sendMessage("Type the name of an argument to edit it");
                sender.sendMessage("Type anything else to start creating an argument with that name");
            }
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            if (!keys.contains(message)) {
                then.createSection(message);
                sender.sendMessage("Created argument " + message);
                ConfigCommandsHandler.saveConfigFile();
            }
            argumentPaths.computeIfAbsent(sender, k -> new ArrayList<>());
            argumentPaths.get(sender).add(message);

            context = setContext(sender, context, then.getConfigurationSection(message), BuildCommandHandler::editArgument);
            context.doNextStep(sender, "");
        }
    }

    private static void editArgument(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection argument = (ConfigurationSection) context.getPreviousChoice();

        if (message.isBlank()) {
            sender.sendMessage("Editing argument " + argument.getName());
            sender.sendMessage("Select one of the following options using its number to continue:");
            sender.sendMessage("  1. See current info");
            sender.sendMessage("  2. Change name");
            sender.sendMessage("  3. Change type");
            sender.sendMessage("  4. Edit argumentInfo");
            sender.sendMessage("  5. Change permission");
            sender.sendMessage("  6. Edit arguments");
            sender.sendMessage("  7. Edit executes");
            sender.sendMessage("  8. Delete argument");
        } else if (message.equals("back")) {
            List<String> path = argumentPaths.get(sender);
            path.remove(path.size() - 1);
            if (path.size() == 0)
                argumentPaths.remove(sender);

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            CommandStep nextStep = switch (message) {
                case "1" -> BuildCommandHandler::seeArgumentInfo;
                case "2" -> BuildCommandHandler::changeArgumentName;
                case "3" -> BuildCommandHandler::changeArgumentType;
                case "4" -> BuildCommandHandler::editArgumentInfo;
                case "5" -> BuildCommandHandler::changeArgumentPermission;
                case "6" -> BuildCommandHandler::editArguments;
                case "7" -> BuildCommandHandler::editExecutes;
                case "8" -> BuildCommandHandler::deleteArgument;
                default -> null;
            };
            if (nextStep == null) {
                sender.sendMessage("Message \"" + message + "\" is not a number from 1 to 8");
                return;
            }
            context = setContext(sender, context, argument, nextStep);
            context.doNextStep(sender, "");
        }
    }

    private static void deleteArgument(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection argument = (ConfigurationSection) context.getPreviousChoice();
        if (message.isBlank()) {
            sender.sendMessage("Are you sure you want to delete the argument: \"" + argument.getName() + "\"?");
            sender.sendMessage(ChatColor.YELLOW + "THIS ACTION CANNOT BE UNDONE");
            sender.sendMessage("Type \"yes\" to confirm or anything else to go back.");
        } else if (message.equals("yes")) {
            ConfigurationSection parent = argument.getParent();
            assert parent != null;
            parent.set(argument.getName(), null);
            ConfigCommandsHandler.saveConfigFile();
            sender.sendMessage("Deleted argument \"" + argument.getName() + "\"");

            context = goBack(sender, 2, context);
            context.doNextStep(sender, "");
        } else {
            sender.sendMessage("Not deleting argument \"" + argument.getName() + "\"");

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void changeArgumentPermission(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection argument = (ConfigurationSection) context.getPreviousChoice();

        if (message.isBlank()) {
            String value = argument.getString("permission");
            if (value == null) {
                sender.sendMessage("Current permission is null");
            } else {
                sender.sendMessage("Current permission: \"" + value + "\"");
            }
            sender.sendMessage("Please type the permission you would like to use null to remove the permission");
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            if (message.equals("null")) {
                argument.set("permission", null);
                sender.sendMessage("Permission for this argument was removed");
            } else {
                argument.set("permission", message);
                sender.sendMessage("Permission is now: \"" + message + "\"");
            }
            ConfigCommandsHandler.saveConfigFile();

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void editArgumentInfo(CommandSender sender, String message, CommandContext context) {
        if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
            return;
        }

        ConfigurationSection argument = (ConfigurationSection) context.getPreviousChoice();
        String type = argument.getString("type");
        Set<String> types = InternalArgument.getArgumentTypes();
        if (!types.contains(type)) {
            // Make sure type is valid
            forwardedFromArgumentInfo.add(sender);
            context = setContext(sender, context, argument, BuildCommandHandler::changeArgumentType);
            context.doNextStep(sender, "");
            return;
        }

        if (type == null) {
            sender.sendMessage("type is null");
            sender.sendMessage("There are no options to configure for a LiteralArgument");
        }

        if (message.isBlank()) {
            sender.sendMessage("type is " + type);
        }

        Object argumentInfo = argument.get("argumentInfo");
        if (InternalArgument.passEditArgumentInfo(sender, message, argument, type, argumentInfo)) {
            // When done, go back
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void changeArgumentType(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection argument = (ConfigurationSection) context.getPreviousChoice();
        String type = argument.getString("type");
        Set<String> types = InternalArgument.getArgumentTypes();
        if (message.isBlank()) {
            if (type == null) {
                sender.sendMessage("Type is null");
                sender.sendMessage("This will be a LiteralArgument");
            } else {
                sender.sendMessage("Type is \"" + type + "\"");
                if (types.contains(type)) {
                    sender.sendMessage("This type is valid");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Type is invalid!");
                }
            }
            sender.sendMessage("What will the argument type be?");
            sender.sendMessage("Type '?' for a list of valid argument types");
            sender.sendMessage("Type null for a LiteralArgument");
        } else if (message.equals("back")) {
            context = goBack(sender, forwardedFromArgumentInfo.contains(sender) ? 2 : 1, context);
            forwardedFromArgumentInfo.remove(sender);
            context.doNextStep(sender, "");
        } else if (message.equals("?")) {
            sender.sendMessage("Argument types:");
            sender.sendMessage(InternalArgument.getArgumentTypes().toString());
        } else if (message.equals("null")) {
            argument.set("type", null);
            sender.sendMessage("Type set to null");

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else if (InternalArgument.getArgumentTypes().contains(message)) {
            argument.set("type", message);
            sender.sendMessage("Type set to " + message);

            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            sender.sendMessage("Unknown type \"" + message + "\"");
        }
    }

    private static void changeArgumentName(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection argument = (ConfigurationSection) context.getPreviousChoice();
        if (message.isBlank()) {
            sender.sendMessage("Current name: " + argument.getName());
            sender.sendMessage("What would you like the new name to be?");
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            ConfigurationSection parent = argument.getParent();
            assert parent != null;

            Set<String> keys = parent.getKeys(false);
            if (!keys.contains(message)) {
                parent.set(message, argument);
                parent.set(argument.getName(), null);
                ConfigCommandsHandler.saveConfigFile();

                List<String> path = argumentPaths.get(sender);
                path.set(path.size() - 1, message);

                context = goBack(sender, 2, context);
                context = setContext(sender, context, message, BuildCommandHandler::editArgument);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("That name is already in use!");
            }
        }
    }

    private static void seeArgumentInfo(CommandSender sender, String message, CommandContext context) {
        // forward to seeInfo method, but argumentPaths is also set
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
        for (int i = 0; i < argumentPaths.get(sender).size(); i++) {
            // argumentPaths only gets added to after going through then and another argument, so this should be fine
            assert command != null;
            ConfigurationSection then = command.getParent();
            assert then != null;
            command = then.getParent();
        }

        context = goBack(sender, 1, context);
        context = setContext(sender, context, command, BuildCommandHandler::seeInfo);
        context.doNextStep(sender, "");
    }

    private static void editAliases(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
        List<String> aliases = command.getStringList("aliases");

        if (message.isBlank()) {
            if (aliases.size() == 0) {
                sender.sendMessage("No aliases");
                sender.sendMessage("Type anything to add it as an alias");
            } else {
                sender.sendMessage("Current aliases:");
                for (int i = 0; i < aliases.size(); i++) {
                    sender.sendMessage("  " + (i + 1) + ". " + aliases.get(i));
                }
                sender.sendMessage("Type a number to delete the corresponding alias");
                sender.sendMessage("Type anything else to add it as an alias");
            }
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else if (message.matches("\\d+")) {
            int target = Integer.parseInt(message);
            if (0 < target && target <= aliases.size()) {
                sender.sendMessage("Deleting alias \"" + aliases.get(target - 1) + "\"");
                aliases.remove(target - 1);
                command.set("aliases", aliases);
                ConfigCommandsHandler.saveConfigFile();

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Given number is not in range 1 to " + aliases.size());
            }
        } else {
            sender.sendMessage("Adding alias \"" + message + "\"");

            aliases.add(message);
            command.set("aliases", aliases);
            ConfigCommandsHandler.saveConfigFile();

            context.doNextStep(sender, "");
        }
    }

    private static void changePermission(CommandSender sender, String message, CommandContext context) {
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();

        if (message.isBlank()) {
            String permission = command.getString("permission");
            if (permission == null) {
                String name = command.getName();
                sender.sendMessage("Current permission is null");
                sender.sendMessage("Default permission is \"" + CommandTreeBuilder.buildDefaultPermission(name) + "\"");
            } else {
                sender.sendMessage("Current permission: \"" + permission + "\"");
            }
            sender.sendMessage("Please type the permission you would like to use or back to go back.");
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            command.set("permission", message);
            ConfigCommandsHandler.saveConfigFile();

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
        ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
        if (message.isBlank()) {
            sender.sendMessage("Current name: " + command.getName());
            sender.sendMessage("What would you like the new name to be?");
        } else if (message.equals("back")) {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        } else {
            ConfigurationSection commands = command.getParent();
            assert commands != null;

            Set<String> keys = commands.getKeys(false);
            if (!keys.contains(message)) {
                commands.set(message, command);
                ConfigurationSection newSection = commands.getConfigurationSection(message);
                commandsBeingEditing.put(sender, message);

                commands.set(command.getName(), null);
                ConfigCommandsHandler.saveConfigFile();

                // TODO: Resolve ReloadCommandHandler
                // ReloadCommandHandler.updateKey(command.getName(), message);

                context = goBack(sender, 2, context);
                context = setContext(sender, context, newSection, BuildCommandHandler::editCommand);
                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("That name is already in use!");
            }
        }
    }

    private static void seeInfo(CommandSender sender, String message, CommandContext context) {
        if (message.isBlank()) {
            ConfigurationSection command = (ConfigurationSection) context.getPreviousChoice();
            IndentedCommandSenderMessenger messenger = new IndentedCommandSenderMessenger(sender);

            messenger.sendMessage("Information for command " + command.getName());

            messenger.sendMessage("Short Description: \"" + command.getString("shortDescription") + "\"");
            messenger.sendMessage("Full Description: \"" + command.getString("fullDescription") + "\"");
            messenger.sendMessage("Permission: \"" + command.getString("permission") + "\"");

            List<String> aliases = command.getStringList("aliases");
            if (aliases.size() == 0) {
                messenger.sendMessage("No Aliases");
            } else {
                messenger.sendMessage("Aliases:");
                for (String alias : aliases) {
                    messenger.sendMessage("  " + alias);
                }
            }

            List<String> executes = command.getStringList("executes");
            if (executes.size() == 0) {
                messenger.sendMessage("Not executable at this stage");
            } else {
                messenger.sendMessage("Executes:");
                messenger.increaseIndentation();
                for (int i = 0; i < executes.size(); i++) {
                    messenger.sendMessage(i + ". " + executes.get(i));
                }
                messenger.decreaseIndentation();
            }

            ConfigurationSection then = command.getConfigurationSection("then");
            if (then == null || then.getKeys(false).size() == 0) {
                messenger.sendMessage("No arguments");
            } else {
                List<String> currentPath = new ArrayList<>(List.of(""));
                messenger.sendMessage("Arguments:");
                messenger.increaseIndentation();
                for (String branchName : then.getKeys(false)) {
                    currentPath.set(0, branchName); // overwrites previous value set on this level
                    messenger.sendMessage(branchName +
                            (currentPath.equals(argumentPaths.get(sender)) ? " - " + ChatColor.GREEN + "currently editing" : "")
                    );
                    messenger.increaseIndentation();
                    ConfigurationSection argument = then.getConfigurationSection(branchName);
                    assert argument != null;
                    printArgumentInfo(messenger, argument, currentPath);
                    messenger.decreaseIndentation();
                }
                messenger.decreaseIndentation();
            }

            messenger.sendMessage("Type anything to continue");
        } else {
            context = goBack(sender, 1, context);
            context.doNextStep(sender, "");
        }
    }

    private static void printArgumentInfo(IndentedCommandSenderMessenger messenger, ConfigurationSection argument, List<String> currentPath) {
        messenger.sendMessage("Permission: \"" + argument.getString("permission") + "\"");
        String type = argument.getString("type");
        messenger.sendMessage("Type: \"" + type + "\"");

        if (!InternalArgument.getArgumentTypes().contains(type)) {
            messenger.sendMessage(ChatColor.YELLOW + "Type is invalid!");
        } else {
            messenger.sendMessage("ArgumentInfo:");
            messenger.increaseIndentation();
            messenger.sendMessage(InternalArgument.formatArgumentInfo(type, argument.get("argumentInfo")));
            messenger.decreaseIndentation();
        }

        List<String> executes = argument.getStringList("executes");
        if (executes.size() == 0) {
            messenger.sendMessage("Not executable at this stage");
        } else {
            messenger.sendMessage("Executes:");
            messenger.increaseIndentation();
            for (int i = 0; i < executes.size(); i++) {
                messenger.sendMessage(i + ". " + executes.get(i));
            }
            messenger.decreaseIndentation();
        }

        ConfigurationSection then = argument.getConfigurationSection("then");
        if (then == null || then.getKeys(false).size() == 0) {
            messenger.sendMessage("No arguments");
        } else {
            currentPath.add(""); // extend path to another element
            messenger.sendMessage("Arguments:");
            messenger.increaseIndentation();
            for (String branchName : then.getKeys(false)) {
                messenger.sendMessage(branchName +
                        (currentPath.equals(argumentPaths.get(messenger.getSender())) ? " - " + ChatColor.GREEN + "currently editing" : "")
                );
                messenger.increaseIndentation();
                ConfigurationSection nextArgument = then.getConfigurationSection(branchName);
                assert nextArgument != null;
                currentPath.set(currentPath.size() - 1, branchName); // overwrites previous value set at this level
                printArgumentInfo(messenger, nextArgument, currentPath);
                messenger.decreaseIndentation();
            }
            messenger.decreaseIndentation();
            currentPath.remove(currentPath.size() - 1); // restore path length
        }
    }
}
