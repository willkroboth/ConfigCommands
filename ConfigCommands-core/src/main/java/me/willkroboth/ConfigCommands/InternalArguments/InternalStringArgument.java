package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionList;
import me.willkroboth.ConfigCommands.Functions.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalStringArgument extends InternalArgument {
    private String value;

    public InternalStringArgument() {
    }

    public InternalStringArgument(String value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean localDebug) throws IncorrectArgumentKey {
        String type = (String) arg.get("subtype");
        ConfigCommandsHandler.logDebug(localDebug, "Arg has subtype: " + type);
        command.withArguments(
                type == null ? new StringArgument(name) :
                        switch (type) {
                            case "string" -> new StringArgument(name);
                            case "text" -> new TextArgument(name);
                            case "greedy" -> new GreedyStringArgument(name);
                            default -> throw new IncorrectArgumentKey(arg.toString(), "subtype", "Did not find StringArgument subtype: \"" + type + "\"");
                        }
        );
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalStringArgument.class);
    }

    public FunctionList getFunctions() {
        return merge(
                super.getFunctions(),
                functions(
                        // TODO: Add function info
                        new Function("charAt")
                                .withParameters(new Parameter(InternalIntegerArgument.class))
                                .returns(InternalStringArgument.class)
                                .executes(this::charAt),
                        new Function("contains")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::contains),
                        new Function("endsWith")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::endsWith),
                        new Function("equals")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::stringEquals),
                        new Function("equalsIgnoreCase")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::stringEqualsIgnoreCase),
                        new Function("indexOf")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .withParameters(
                                        new Parameter(InternalStringArgument.class),
                                        new Parameter(InternalIntegerArgument.class)
                                )
                                .returns(InternalIntegerArgument.class)
                                .executes(this::indexOf),
                        new Function("isEmpty")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isEmpty),
                        new Function("join")
                                .withParameters(new Parameter(InternalArgument.class))
                                .returns(InternalStringArgument.class)
                                .executes(this::join),
                        new Function("lastIndexOf")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .withParameters(
                                        new Parameter(InternalStringArgument.class),
                                        new Parameter(InternalIntegerArgument.class)
                                )
                                .returns(InternalIntegerArgument.class)
                                .executes(this::lastIndexOf),
                        new Function("length")
                                .returns(InternalIntegerArgument.class)
                                .executes(this::length),
                        new Function("replace")
                                .withParameters(
                                        new Parameter(InternalStringArgument.class),
                                        new Parameter(InternalStringArgument.class)
                                )
                                .returns(InternalStringArgument.class)
                                .executes(this::replace),
                        new Function("startsWith")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::startsWith),
                        new Function("substring")
                                .withParameters(new Parameter(InternalIntegerArgument.class))
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class),
                                        new Parameter(InternalIntegerArgument.class)
                                )
                                .returns(InternalStringArgument.class)
                                .executes(this::substring),
                        new Function("toInt")
                                .returns(InternalIntegerArgument.class)
                                .executes(this::toInt)
                )
        );
    }

    private String getString(InternalArgument target) {
        return (String) target.getValue();
    }

    private int getInt(InternalArgument target) {
        return (int) target.getValue();
    }

    private InternalArgument substring(InternalArgument target, List<InternalArgument> parameters) {
        String result;
        if (parameters.size() == 1) {
            result = getString(target).substring(getInt(parameters.get(0)));
        } else {
            result = getString(target).substring(getInt(parameters.get(0)), getInt(parameters.get(1)));
        }
        return new InternalStringArgument(result);
    }

    private InternalArgument lastIndexOf(InternalArgument target, List<InternalArgument> parameters) {
        int result;
        if (parameters.size() == 1) {
            result = getString(target).lastIndexOf(getString(parameters.get(0)));
        } else {
            result = getString(target).lastIndexOf(getString(parameters.get(0)), getInt(parameters.get(1)));
        }
        return new InternalIntegerArgument(result);
    }

    private InternalArgument indexOf(InternalArgument target, List<InternalArgument> parameters) {
        int result;
        if (parameters.size() == 1) {
            result = getString(target).indexOf(getString(parameters.get(0)));
        } else {
            result = getString(target).indexOf(getString(parameters.get(0)), getInt(parameters.get(1)));
        }
        return new InternalIntegerArgument(result);
    }

    private InternalArgument startsWith(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getString(target).startsWith(getString(parameters.get(0))));
    }

    private InternalArgument replace(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalStringArgument(getString(target).replace(getString(parameters.get(0)), getString(parameters.get(1))));
    }

    private InternalArgument length(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalIntegerArgument(getString(target).length());
    }

    private InternalArgument isEmpty(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getString(target).isEmpty());
    }

    private InternalArgument stringEqualsIgnoreCase(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getString(target).equalsIgnoreCase(getString(parameters.get(0))));
    }

    private InternalArgument stringEquals(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getString(target).equals(getString(parameters.get(0))));
    }

    private InternalArgument endsWith(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getString(target).endsWith(getString(parameters.get(0))));
    }

    private InternalArgument contains(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getString(target).contains(getString(parameters.get(0))));
    }

    private InternalArgument charAt(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalStringArgument("" + getString(target).charAt(getInt(parameters.get(0))));
    }

    public InternalIntegerArgument toInt(InternalArgument target, List<InternalArgument> parameters) {
        String s = (String) target.getValue();
        try {
            return new InternalIntegerArgument(Integer.parseInt(s));
        } catch (NumberFormatException ignored) {
            throw new CommandRunException("NumberFormatException: value: \"" + s + "\" could not be interpreted as int.");
        }
    }

    public InternalStringArgument join(InternalArgument target, List<InternalArgument> parameters) {
        String arg = parameters.get(0).forCommand();
        return new InternalStringArgument(target.getValue() + arg);
    }

    public void setValue(Object arg) {
        value = (String) arg;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(InternalArgument arg) {
        value = (String) arg.getValue();
    }

    public String forCommand() {
        return value;
    }
}
