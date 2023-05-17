package me.willkroboth.configcommands.systemcommands;

import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.ExecutorType;
import me.willkroboth.configcommands.functions.*;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
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

/**
 * A class that handles the {@code /configcommands functions} command
 */
public class FunctionsCommandHandler extends SystemCommandHandler implements Listener {
    // command configuration
    @Override
    protected Argument<?> getArgumentTree() {
        return super.getArgumentTree()
                .executes(FunctionsCommandHandler::addUser, ExecutorType.CONSOLE, ExecutorType.PLAYER)
                .then(new StringArgument("addOn")
                        .replaceSuggestions(ArgumentSuggestions.strings(FunctionsCommandHandler::getAddOns))
                        .then(new StringArgument("internalArgument")
                                .replaceSuggestions(ArgumentSuggestions.strings(FunctionsCommandHandler::getInternalArguments))
                                .then(new MultiLiteralArgument("static", "instance")
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
            "\tUse tab-completion: /configcommands functions <addOn> <internalArgument> <instance/static> <function>"
    };

    @Override
    protected String[] getHelpMessages() {
        return helpMessages;
    }

    // command functions
    private static final Map<CommandSender, CommandContext> activeUsers = new HashMap<>();

    // accessed by BuildCommandHandler
    /**
     * Opens the {@code /configcommands functions} menu for the given {@link CommandSender}.
     *
     * @param sender  The {@link CommandSender} to open the menu for.
     * @param ignored This parameter is not used, and only exists so this function matches the {@link CommandExecutor}
     *                FunctionalInterface.
     */
    protected static void addUser(CommandSender sender, CommandArguments ignored) {
        sender.sendMessage("Welcome to the ConfigCommand function menu!");
        sender.sendMessage("Enter ## at any time to cancel.");
        sender.sendMessage("Type back to return to previous step.");
        activeUsers.put(sender, new CommandContext(null, "", FunctionsCommandHandler::chooseAddOn));
        handleMessage(sender, "", null);
    }

    // events

    /**
     * Intercepts chat messages sent by players. If the player has the
     * {@code /configcommands functions} menu open, their chat message will
     * be canceled so no one else sees it, and this class will handle
     * the message appropriately.
     *
     * @param event The {@link AsyncPlayerChatEvent} being listened for.
     */
    @EventHandler
    public void onChatSent(AsyncPlayerChatEvent event) {
        handleMessage(event.getPlayer(), event.getMessage(), event);
    }

    /**
     * Intercepts commands sent by players. If the player has the
     * {@code /configcommands functions} menu open, their command will
     * be canceled so no one else sees it, and this class will handle
     * the message appropriately.
     *
     * @param event The {@link PlayerCommandPreprocessEvent} being listened for.
     */
    @EventHandler
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        handleMessage(event.getPlayer(), event.getMessage(), event);
    }

    /**
     * Kicks players out of the {@code /configcommands functions}
     * menu when they leave the server.
     *
     * @param event The {@link PlayerQuitEvent} being listened for.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        activeUsers.remove(player);
    }

    /**
     * Intercepts messages sent from the console. If the console has the
     * {@code /configcommands functions} menu open, the message will be
     * canceled so no one else sees it and this class will handle the
     * message appropriately.
     *
     * @param event The {@link ServerCommandEvent} being listened for.
     */
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
                if (activeUsers.get(sender).previousContext() == null) {
                    sender.sendMessage("No step to go back to.");
                } else {
                    activeUsers.put(sender, activeUsers.get(sender).previousContext());
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
        Set<String> addOns = InternalArgument.getPluginsNamesWithInternalArguments();
        if (message.isBlank()) {
            if (addOns.size() == 1) {
                context = setContext(sender, context, "ConfigCommands", FunctionsCommandHandler::chooseInternalArgument);

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("Choose the AddOn you need help with.");
                sender.sendMessage(addOns.toString());
            }
        } else {
            if (addOns.contains(message)) {
                context = setContext(sender, context, message, FunctionsCommandHandler::chooseInternalArgument);

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("\"" + message + "\" is not a recognized AddOn");
            }
        }
    }

    private static String[] getAddOns(SuggestionInfo<CommandSender> ignored) {
        return InternalArgument.getPluginsNamesWithInternalArguments().toArray(new String[0]);
    }

    private static void chooseInternalArgument(CommandSender sender, String message, CommandContext context) {
        List<InternalArgument<?>> internalArguments = InternalArgument.getPluginInternalArguments((String) context.previousChoice());
        if (message.isBlank()) {
            sender.sendMessage("Choose the InternalArgument you need help with.");
            sender.sendMessage(InternalArgument.getNames(internalArguments).toString());
        } else {
            List<String> names = InternalArgument.getNames(internalArguments);
            if (names.contains(message)) {
                InternalArgument<?> argument = internalArguments.get(names.indexOf(message));

                context = setContext(sender, context, argument, FunctionsCommandHandler::chooseFunction);

                context.doNextStep(sender, "");
            } else {
                sender.sendMessage("\"" + message + "\" is not a recognized InternalArgument for this AddOn");
            }
        }
    }

    private static String[] getInternalArguments(SuggestionInfo<CommandSender> info) {
        String addOn = info.previousArgs().getUnchecked(0);
        assert addOn != null;

        if (!InternalArgument.getPluginsNamesWithInternalArguments().contains(addOn)) return new String[0];

        List<InternalArgument<?>> internalArguments = InternalArgument.getPluginInternalArguments(addOn);
        return InternalArgument.getNames(internalArguments).toArray(new String[0]);
    }

    // Cast is totally safe, generics are silly
    @SuppressWarnings("unchecked")
    private static <T> void chooseFunction(CommandSender sender, String message, CommandContext context) {
        Class<? extends InternalArgument<T>> clazz = ((InternalArgument<T>) context.previousChoice()).myClass();
        if (message.isBlank()) {
            sender.sendMessage("Choose the function you need help with.");

            sender.sendMessage("InstanceFunctions: " + Arrays.toString(InternalArgument.getInstanceFunctionsFor(clazz).getNames()));
            sender.sendMessage("StaticFunctions: " + Arrays.toString(InternalArgument.getStaticFunctionsFor(clazz).getNames()));
        } else {
            InstanceFunctionList<?> functions = InternalArgument.getInstanceFunctionsFor(clazz);
            if (functions.hasName(message)) {
                InstanceFunction<?> function = functions.getByName(message);

                context = setContext(sender, context, function, FunctionsCommandHandler::displayInformation);

                context.doNextStep(sender, "");
                return;
            }

            StaticFunctionList staticFunctions = InternalArgument.getStaticFunctionsFor(clazz);
            if (staticFunctions.hasName(message)) {
                StaticFunction staticFunction = staticFunctions.getByName(message);

                context = setContext(sender, context, staticFunction, FunctionsCommandHandler::displayInformation);

                context.doNextStep(sender, "");
                return;
            }

            sender.sendMessage("\"" + message + "\" is not a recognized function.");
        }
    }

    private static String[] getFunctions(SuggestionInfo<CommandSender> info) {
        String addOn = info.previousArgs().getUnchecked(0);
        assert addOn != null;

        if (!InternalArgument.getPluginsNamesWithInternalArguments().contains(addOn)) return new String[0];

        List<InternalArgument<?>> internalArguments = InternalArgument.getPluginInternalArguments(addOn);

        List<String> names = InternalArgument.getNames(internalArguments);
        String internalArgument = info.previousArgs().getUnchecked(1);
        assert internalArgument != null;

        if (!names.contains(internalArgument)) return new String[0];
        InternalArgument<?> argument = internalArguments.get(names.indexOf(internalArgument));

        String staticChoice = info.previousArgs().getUnchecked(2);
        assert staticChoice != null;

        if (staticChoice.equals("static")) {
            return InternalArgument.getStaticFunctionsFor(argument.myClass()).getNames();
        } else if (staticChoice.equals("instance")) {
            return InternalArgument.getInstanceFunctionsFor(argument.myClass()).getNames();
        } else {
            return new String[0];
        }
    }

    private static void displayInformation(CommandSender sender, String message, CommandContext context) {
        if (message.isBlank()) {
            InternalArgument<?> argument = (InternalArgument<?>) context.previousContext().previousChoice();
            sender.sendMessage("Class: " + argument.getName());

            FunctionBuilder<?, ?> function = (FunctionBuilder<?, ?>) context.previousChoice();
            if (function instanceof InstanceFunction) {
                sender.sendMessage("Instance function");
            } else if (function instanceof StaticFunction) {
                sender.sendMessage("Static function");
            }
            function.outputInformation(sender);
            sender.sendMessage("Type anything to continue");
        } else {
            handleMessage(sender, "back", null);
        }
    }

    private static void displayInformation(CommandSender sender, CommandArguments args) {
        String addOn = args.getUnchecked("addOn");
        assert addOn != null;

        if (!InternalArgument.getPluginsNamesWithInternalArguments().contains(addOn)) {
            sender.sendMessage(ChatColor.RED + "Invalid command: AddOn \"" + addOn + "\" does not exist");
            return;
        }

        List<InternalArgument<?>> internalArguments = InternalArgument.getPluginInternalArguments(addOn);

        List<String> names = InternalArgument.getNames(internalArguments);
        String internalArgument = args.getUnchecked("internalArgument");
        assert internalArgument != null;

        if (!names.contains(internalArgument)) {
            sender.sendMessage(ChatColor.RED + "Invalid command: InternalArgument \"" + internalArgument + "\" does not exist for the given AddOn");
            return;
        }
        InternalArgument<?> argument = internalArguments.get(names.indexOf(internalArgument));

        String staticChoice = args.getUnchecked(2);
        assert staticChoice != null;

        if (staticChoice.equals("static")) {
            String functionName = args.getUnchecked("function");
            StaticFunction function = InternalArgument.getStaticFunctionsFor(argument.myClass()).getByName(functionName);
            if (function == null) {
                sender.sendMessage(ChatColor.RED + "Invalid command: StaticFunction \"" + functionName + "\" dose not exist for the given InternalArgument");
                return;
            }

            sender.sendMessage("Class: " + argument.getName());
            sender.sendMessage("Static function");
            function.outputInformation(sender);
        } else if (staticChoice.equals("instance")) {
            String functionName = args.getUnchecked("function");
            InstanceFunction<?> function = InternalArgument.getInstanceFunctionsFor(argument.myClass()).getByName(functionName);
            if (function == null) {
                sender.sendMessage(ChatColor.RED + "Invalid command: InstanceFunction \"" + functionName + "\" dose not exist for the given InternalArgument");
                return;
            }

            sender.sendMessage("Class: " + argument.getName());
            sender.sendMessage("Instance function");
            function.outputInformation(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid command: expected instance or static but found \"" + staticChoice + "\"");
        }
    }
}
