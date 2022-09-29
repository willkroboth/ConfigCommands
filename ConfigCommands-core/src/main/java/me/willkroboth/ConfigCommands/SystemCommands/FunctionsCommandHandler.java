package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.ExecutorType;
import me.willkroboth.ConfigCommands.Functions.*;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.*;

public class FunctionsCommandHandler extends SystemCommandHandler implements Listener {
    // command configuration
    protected ArgumentTree getArgumentTree() {
        return super.getArgumentTree()
                .executes(FunctionsCommandHandler::addUser, ExecutorType.CONSOLE, ExecutorType.PLAYER)
                .then(new StringArgument("addOn")
                        .replaceSuggestions(ArgumentSuggestions.strings(FunctionsCommandHandler::getAddOns))
                        .then(new StringArgument("internalArgument")
                                .replaceSuggestions(ArgumentSuggestions.strings(FunctionsCommandHandler::getInternalArguments))
                                .then(new MultiLiteralArgument("static", "nonStatic")
                                        .then(new GreedyStringArgument("function")
                                                .replaceSuggestions(ArgumentSuggestions.strings(FunctionsCommandHandler::getFunctions))
                                                .executes((CommandExecutor) FunctionsCommandHandler::displayInformation, ExecutorType.CONSOLE, ExecutorType.PLAYER)
                                        )
                                )
                        )
                );
    }

    private static final String[] helpMessages = new String[]{
            "Displays information about the available ConfigCommands functions",
            "Usage:",
            "\tBring up guided menu: /configcommands functions",
            "\tUse tab-completion: /configcommands functions <addOn> <internalArgument> <(non)static> <function>"
    };

    protected String[] getHelpMessages() {
        return helpMessages;
    }

    // command functions
    private static final Map<CommandSender, CommandContext> activeUsers = new HashMap<>();

    // accessed by BuildCommandHandler
    protected static void addUser(CommandSender sender, Object[] ignored) {
        sender.sendMessage("Welcome to the ConfigCommand function menu!");
        sender.sendMessage("Enter ## at any time to cancel.");
        sender.sendMessage("Type back to return to previous step.");
        activeUsers.put(sender, new CommandContext(null, "", FunctionsCommandHandler::chooseAddOn));
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
    }

    @EventHandler
    public void onConsoleSent(ServerCommandEvent event) {
        handleMessage(event.getSender(), event.getCommand(), event);
    }

    private static void handleMessage(CommandSender sender, String message, Cancellable event) {
        if (activeUsers.containsKey(sender)) {
            if (event != null) event.setCancelled(true);
            if (sender instanceof Player) sender.sendMessage("");
            if (message.equals("##")) {
                sender.sendMessage("Closing the ConfigCommand functions menu.");
                activeUsers.remove(sender);
            } else if (message.equalsIgnoreCase("back")) {
                if (activeUsers.get(sender).getPreviousContext() == null) {
                    sender.sendMessage("No step to go back to.");
                } else {
                    activeUsers.put(sender, activeUsers.get(sender).getPreviousContext());
                    activeUsers.get(sender).doNextStep(sender, "");
                }
            } else {
                activeUsers.get(sender).doNextStep(sender, message);
            }
        }
    }

    // Help menu steps or command suggestions
    private static CommandContext setContext(CommandSender sender, CommandContext previousContext, Object previousChoice, CommandStep nextStep) {
        CommandContext newContext = new CommandContext(previousContext, previousChoice, nextStep);
        activeUsers.put(sender, newContext);
        return newContext;
    }

    private static void chooseAddOn(CommandSender sender, String message, CommandContext context) {
        if (message.isBlank()) {
            Set<String> addOns = ConfigCommandAddOn.getAddOns().keySet();
            if (addOns.size() == 1) {
                context = setContext(sender, context, "ConfigCommands", FunctionsCommandHandler::chooseInternalArgument);

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Choose the AddOn you need help with.");
                sender.sendMessage(addOns.toString());
            }
        } else {
            if (ConfigCommandAddOn.getAddOn(message) != null) {
                context = setContext(sender, context, message, FunctionsCommandHandler::chooseInternalArgument);

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("\"" + message + "\" is not a recognized AddOn");
            }
        }
    }

    private static String[] getAddOns(SuggestionInfo ignored) {
        return ConfigCommandAddOn.getAddOns().keySet().toArray(new String[0]);
    }

    private static void chooseInternalArgument(CommandSender sender, String message, CommandContext context) {
        List<InternalArgument> internalArguments = InternalArgument.getPluginInternalArguments((String) context.getPreviousChoice());
        if (message.isBlank()) {
            sender.sendMessage("Choose the InternalArgument you need help with.");
            sender.sendMessage(InternalArgument.getNames(internalArguments).toString());
        } else {
            List<String> names = InternalArgument.getNames(internalArguments);
            if (names.contains(message)) {
                InternalArgument argument = internalArguments.get(names.indexOf(message));

                context = setContext(sender, context, argument, FunctionsCommandHandler::chooseFunction);

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("\"" + message + "\" is not a recognized InternalArgument for this AddOn");
            }
        }
    }

    private static String[] getInternalArguments(SuggestionInfo info) {
        String addOn = (String) info.previousArgs()[0];
        if (ConfigCommandAddOn.getAddOn(addOn) == null) return new String[0];

        List<InternalArgument> internalArguments = InternalArgument.getPluginInternalArguments(addOn);
        return InternalArgument.getNames(internalArguments).toArray(new String[0]);
    }

    private static void chooseFunction(CommandSender sender, String message, CommandContext context) {
        Class<? extends InternalArgument> clazz = ((InternalArgument) context.getPreviousChoice()).getClass();
        if (message.isBlank()) {
            sender.sendMessage("Choose the function you need help with.");

            sender.sendMessage("Functions: " + Arrays.toString(InternalArgument.getFunctionsFor(clazz).getNames()));
            sender.sendMessage("StaticFunctions: " + Arrays.toString(InternalArgument.getStaticFunctionsFor(clazz).getNames()));
        } else {

            FunctionList functions = InternalArgument.getFunctionsFor(clazz);
            if (functions.hasName(message)) {
                Function function = functions.getFromName(message);

                context = setContext(sender, context, function, FunctionsCommandHandler::displayInformation);

                context.doNextStep(sender, "");
                return;
            }

            StaticFunctionList staticFunctions = InternalArgument.getStaticFunctionsFor(clazz);
            if (staticFunctions.hasName(message)) {
                StaticFunction staticFunction = staticFunctions.getFromName(message);

                context = setContext(sender, context, staticFunction, FunctionsCommandHandler::displayInformation);

                context.doNextStep(sender, "");
                return;
            }

            sender.sendMessage("\"" + message + "\" is not a recognized function.");
        }
    }

    private static String[] getFunctions(SuggestionInfo info) {
        String addOn = (String) info.previousArgs()[0];

        if (ConfigCommandAddOn.getAddOn(addOn) == null) return new String[0];

        List<InternalArgument> internalArguments = InternalArgument.getPluginInternalArguments(addOn);

        List<String> names = InternalArgument.getNames(internalArguments);
        String internalArgument = (String) info.previousArgs()[1];
        if (!names.contains(internalArgument)) return new String[0];
        InternalArgument argument = internalArguments.get(names.indexOf(internalArgument));

        String staticChoice = (String) info.previousArgs()[2];
        if (staticChoice.equals("static")) {
            return InternalArgument.getStaticFunctionsFor(argument.getClass()).getNames();
        } else if (staticChoice.equals("nonStatic")) {
            return InternalArgument.getFunctionsFor(argument.getClass()).getNames();
        } else {
            return new String[0];
        }
    }

    private static void displayInformation(CommandSender sender, String message, CommandContext context) {
        if (message.isBlank()) {
            InternalArgument argument = (InternalArgument) context.getPreviousContext().getPreviousChoice();
            sender.sendMessage("Class: " + argument.getName());

            AbstractFunction<?> function = (AbstractFunction<?>) context.getPreviousChoice();
            if(function instanceof Function) {
                sender.sendMessage("Nonstatic function");
            } else if (function instanceof StaticFunction) {
                sender.sendMessage("Static function");
            }
            function.outputInformation(sender);
            sender.sendMessage("Type anything to continue");
        } else {
            handleMessage(sender, "back", null);
        }
    }

    private static void displayInformation(CommandSender sender, Object[] parameters) {
        String addOn = (String) parameters[0];

        if (ConfigCommandAddOn.getAddOn(addOn) == null) {
            sender.sendMessage(ChatColor.RED + "Invalid command: addOn \"" + addOn + "\" does not exist");
            return;
        }

        List<InternalArgument> internalArguments = InternalArgument.getPluginInternalArguments(addOn);

        List<String> names = InternalArgument.getNames(internalArguments);
        String internalArgument = (String) parameters[1];
        if (!names.contains(internalArgument)) {
            sender.sendMessage(ChatColor.RED + "Invalid command: internalArgument \"" + internalArgument + "\" does not exist for the given addOn");
            return;
        }
        InternalArgument argument = internalArguments.get(names.indexOf(internalArgument));

        String staticChoice = (String) parameters[2];

        if (staticChoice.equals("static")) {
            InternalArgument.getStaticFunctionsFor(argument.getClass()).getFromName((String) parameters[3]).outputInformation(sender);
        } else if (staticChoice.equals("nonStatic")) {
            InternalArgument.getFunctionsFor(argument.getClass()).getFromName((String) parameters[3]).outputInformation(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid command: expected static or nonStatic but found \"" + staticChoice + "\"");
        }
    }
}
