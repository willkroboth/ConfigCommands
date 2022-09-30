package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Parameter;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.Functions.StaticFunctionList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InternalIntegerArgument extends InternalArgument {
    private int value;

    public InternalIntegerArgument() {
    }

    public InternalIntegerArgument(int value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean localDebug) throws IncorrectArgumentKey {
        int min;
        if (arg.get("min") == null) {
            min = Integer.MIN_VALUE;
        } else {
            try {
                min = Integer.parseInt(arg.get("min").toString());
            } catch (NumberFormatException e) {
                throw new IncorrectArgumentKey(arg.toString(), "min", "Could not be interpreted as an int.");
            }
        }
        ConfigCommandsHandler.logDebug(localDebug, "Arg has min: %s", min);

        int max;
        if (arg.get("max") == null) {
            max = Integer.MAX_VALUE;
        } else {
            try {
                max = Integer.parseInt(arg.get("max").toString());
            } catch (NumberFormatException e) {
                throw new IncorrectArgumentKey(arg.toString(), "max", "Could not be interpreted as an int.");
            }
        }
        ConfigCommandsHandler.logDebug(localDebug, "Arg has max: %s", max);

        command.withArguments(new IntegerArgument(name, min, max));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalIntegerArgument.class);
    }

    public void setValue(Object arg) {
        value = (int) arg;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(InternalArgument arg) {
        value = (int) arg.getValue();
    }

    public String forCommand() {
        return "" + value;
    }

    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        new StaticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new Integer")
                                .withParameters()
                                .withParameters(new Parameter(InternalStringArgument.class, "value", "The value for the new Integer"))
                                .returns(InternalIntegerArgument.class, "An Integer containing the given value, or 0 if no value is given")
                                .withThrowMessages("NumberFormatException if the given value cannot be interpreted as an Integer")
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
