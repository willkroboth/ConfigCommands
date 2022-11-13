package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Parameter;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import me.willkroboth.ConfigCommands.Functions.StaticFunctionList;

public class InternalIntegerArgument extends InternalArgument implements CommandArgument {
    private int value;

    public InternalIntegerArgument() {
    }

    public InternalIntegerArgument(int value) {
        super(value);
    }

    @Override
    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        if (argumentInfo != null) {
            ConfigurationSection info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, name);
            min = info.getInt("min", Integer.MIN_VALUE);
            max = info.getInt("max", Integer.MAX_VALUE);
        }
        ConfigCommandsHandler.logDebug(localDebug, "Arg has %s, %s",
                min == Integer.MIN_VALUE ? "no min" : "min: " + min,
                max == Integer.MAX_VALUE ? "no max" : "max: " + max
        );
        return new IntegerArgument(name, min, max);
    }

    @Override
    public boolean editArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, @Nullable Object argumentInfo) {
        ConfigurationSection info;
        if (argumentInfo == null) {
            info = argument.createSection("argumentInfo");
        } else {
            try {
                info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, "");
            } catch (IncorrectArgumentKey ignored) {
                argument.set("argumentInfo", null);
                info = argument.createSection("argumentInfo");
            }
        }

        int min = info.getInt("min", Integer.MIN_VALUE);
        int max = info.getInt("max", Integer.MAX_VALUE);
        if (message.isBlank()) {
            if (min == Integer.MIN_VALUE) {
                sender.sendMessage("There is no min value");
            } else {
                sender.sendMessage("min is currently " + min);
            }
            if (max == Integer.MAX_VALUE) {
                sender.sendMessage("There is no max value");
            } else {
                sender.sendMessage("max is currently " + max);
            }
            sender.sendMessage("Type the key to change and the value you want to change it to");
            sender.sendMessage("Eg. \"min 0\" or \"max 10\"");
        } else if (message.startsWith("min ")) {
            String value = message.substring(4);
            int newMin;
            try {
                newMin = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                sender.sendMessage("\"" + value + "\" cannot be interpreted as an Integer");
                return false;
            }
            if (newMin > max) {
                sender.sendMessage("min (" + newMin + ") cannot be larger than the max (" + max + ")");
                return false;
            }
            info.set("min", newMin);
            ConfigCommandsHandler.saveConfigFile();
            sender.sendMessage("min is now " + newMin);
        } else if (message.startsWith("max ")) {
            String value = message.substring(4);
            int newMax;
            try {
                newMax = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                sender.sendMessage("\"" + value + "\" cannot be interpreted as an Integer");
                return false;
            }
            if (newMax < min) {
                sender.sendMessage("max (" + newMax + ") cannot be smaller than the min (" + min + ")");
                return false;
            }
            info.set("max", newMax);
            ConfigCommandsHandler.saveConfigFile();
            sender.sendMessage("max is now " + newMax);
        } else {
            sender.sendMessage("Sorry, I don't understand that message...");
        }
        return false;
    }

    @Override
    public String[] formatArgumentInfo(Object argumentInfo) {
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        if (argumentInfo != null) {
            try {
                ConfigurationSection info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, "");
                min = info.getInt("min", Integer.MIN_VALUE);
                max = info.getInt("max", Integer.MAX_VALUE);
            } catch (IncorrectArgumentKey ignored) {
            }
        }
        String[] out = new String[2];
        if (min == Integer.MIN_VALUE) {
            out[0] = "There is no min";
        } else {
            out[0] = "min: " + min;
        }
        if (max == Integer.MAX_VALUE) {
            out[1] = "There is no max";
        } else {
            out[1] = "max: " + max;
        }
        return out;
    }

    @Override
    public void setValue(Object arg) {
        value = (int) arg;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = (int) arg.getValue();
    }

    @Override
    public String forCommand() {
        return "" + value;
    }

    @Override
    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        new StaticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new Integer")
                                .withParameters()
                                .withParameters(new Parameter(InternalStringArgument.class, "value", "The value for the new Integer"))
                                .returns(InternalIntegerArgument.class, "An Integer containing the given value, or 0 if no value is given")
                                .throwsException("NumberFormatException if the given value cannot be interpreted as an Integer")
                                .executes(parameters -> {
                                    int result = 0;
                                    if (parameters.size() == 1) {
                                        try {
                                            result = Integer.parseInt((String) parameters.get(0).getValue());
                                        } catch (NumberFormatException e) {
                                            throw new CommandRunException(e);
                                        }
                                    }

                                    return new InternalIntegerArgument(result);
                                })
                                .withExamples(
                                        "Integer.new(\"10\") -> 10",
                                        "Integer.(\"-5\") -> -5",
                                        "Integer.new() -> 0",
                                        "Integer.(\"Hello\") -> NumberFormatException"
                                )
                )
        );
    }
}
