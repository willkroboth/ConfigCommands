package me.willkroboth.configcommands.internalarguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.exceptions.BadLiteralException;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.IncorrectArgumentKey;
import me.willkroboth.configcommands.functions.InstanceFunction;
import me.willkroboth.configcommands.functions.InstanceFunctionList;
import me.willkroboth.configcommands.functions.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link InternalArgument} that represents a CommandAPI {@link MultiLiteralArgument}.
 */
public class InternalMultiLiteralArgument extends InternalArgument implements CommandArgument {
    private String value;

    /**
     * Creates a new {@link InternalMultiLiteralArgument} with no initial value set.
     */
    public InternalMultiLiteralArgument() {
    }

    @Override
    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        if (argumentInfo == null)
            throw new IncorrectArgumentKey(name, "argumentInfo", "MultiLiteralArgument requires the literals it should use to be listed under argumentInfo");
        List<String> literals = assertArgumentInfoClass(argumentInfo, List.class, name);
        ConfigCommandsHandler.logDebug(localDebug, "Arg has literals %s", literals);
        try {
            return new MultiLiteralArgument(literals.toArray(String[]::new));
        } catch (BadLiteralException e) {
            throw new IncorrectArgumentKey(name, "argumentInfo", e.getMessage());
        }
    }

    @Override
    public boolean editArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, @Nullable Object argumentInfo) {
        List<String> info;
        if (argumentInfo == null) {
            info = new ArrayList<>();
            argument.set("argumentInfo", info);
        } else {
            try {
                info = assertArgumentInfoClass(argumentInfo, List.class, "");
            } catch (IncorrectArgumentKey ignored) {
                info = new ArrayList<>();
                argument.set("argumentInfo", info);
            }
        }

        if (message.isBlank()) {
            sendLiterals(sender, info);
        } else if (message.matches("\\d+")) {
            int target = Integer.parseInt(message);
            if (0 <= target && target < info.size()) {
                sender.sendMessage("Deleting literal \"" + info.get(target) + "\"");
                info.remove(target);
                argument.set("argumentInfo", info);
                ConfigCommandsHandler.saveConfigFile();

                sender.sendMessage();
                sendLiterals(sender, info);
            } else {
                sender.sendMessage("Given number is not in range 0 to " + (info.size() - 1));
            }
        } else {
            info.add(message);
            argument.set("argumentInfo", info);
            ConfigCommandsHandler.saveConfigFile();

            sendLiterals(sender, info);
        }
        return false;
    }

    private static void sendLiterals(CommandSender sender, List<String> info) {
        if (info.size() == 0) {
            sender.sendMessage("No literals");
            sender.sendMessage("Type anything to add it as a literal");
        } else {
            sender.sendMessage("Current literals:");
            for (int i = 0; i < info.size(); i++) {
                sender.sendMessage("  " + i + ". " + info.get(i));
            }
            sender.sendMessage("Type a number to delete the corresponding literal");
            sender.sendMessage("Type anything to add it as a literal");
        }
    }

    @Override
    public String[] formatArgumentInfo(Object argumentInfo) {
        if (argumentInfo == null) return new String[]{ChatColor.YELLOW + "ArgumentInfo is invalid!"};
        List<String> info;
        try {
            info = assertArgumentInfoClass(argumentInfo, List.class, "");
        } catch (IncorrectArgumentKey ignored) {
            return new String[]{ChatColor.YELLOW + "ArgumentInfo is invalid!"};
        }

        return info.toArray(String[]::new);
    }

    @Override
    public void setValue(Object arg) {
        value = (String) arg;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = (String) arg.getValue();
    }

    @Override
    public String forCommand() {
        return value;
    }

    private String getLiteral(InternalArgument target) {
        return (String) target.getValue();
    }

    @Override
    public InstanceFunctionList getInstanceFunctions() {
        return merge(
                super.getInstanceFunctions(),
                functions(
                        new InstanceFunction("getValue")
                                .withDescription("Gets the String this MultiLiteral was set to")
                                .withParameters()
                                .returns(InternalStringArgument.class)
                                .executes(((target, parameters) -> {
                                    return new InternalStringArgument(getLiteral(target));
                                })),
                        new InstanceFunction("equals")
                                .withDescription("Checks if this MultiLiteral was set to the given string")
                                .withParameters(new Parameter(InternalStringArgument.class, "string", "The string to check against"))
                                .returns(InternalBooleanArgument.class, "True if this MultiLiteral was set to the given string, and false otherwise")
                                .executes(((target, parameters) -> {
                                    return new InternalBooleanArgument(getLiteral(target).equals(getLiteral(parameters.get(0))));
                                }))
                )
        );
    }
}
