package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InternalStringArgument extends InternalArgument implements CommandArgument {
    private String value;

    public InternalStringArgument() {
    }

    public InternalStringArgument(String value) {
        super(value);
    }


    @Override
    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        if(argumentInfo == null) return new StringArgument(name);
        ConfigurationSection info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, name);
        String type = info.getString("subtype");
        ConfigCommandsHandler.logDebug(localDebug, "Arg has subtype: %s", type);
        if (type == null) return new StringArgument(name);
        return switch (type) {
            case "string" -> new StringArgument(name);
            case "text" -> new TextArgument(name);
            case "greedy" -> new GreedyStringArgument(name);
            default -> throw new IncorrectArgumentKey(name, "subtype", "Did not find StringArgument subtype: \"" + type + "\"");
        };
    }

    @Override
    public FunctionList getFunctions() {
        return merge(
                super.getFunctions(),
                entries(
                        entry(new Definition("charAt", args(InternalIntegerArgument.class)),
                                new Function(this::charAt, InternalStringArgument.class)),
                        entry(new Definition("contains", args(InternalStringArgument.class)),
                                new Function(this::contains, InternalBooleanArgument.class)),
                        entry(new Definition("endsWith", args(InternalStringArgument.class)),
                                new Function(this::endsWith, InternalBooleanArgument.class)),
                        entry(new Definition("equals", args(InternalStringArgument.class)),
                                new Function(this::stringEquals, InternalBooleanArgument.class)),
                        entry(new Definition("equalsIgnoreCase", args(InternalStringArgument.class)),
                                new Function(this::stringEqualsIgnoreCase, InternalBooleanArgument.class)),
                        entry(new Definition("isEmpty", args()),
                                new Function(this::isEmpty, InternalBooleanArgument.class)),
                        entry(new Definition("length", args()),
                                new Function(this::length, InternalIntegerArgument.class)),
                        entry(new Definition("replace", args(InternalStringArgument.class, InternalStringArgument.class)),
                                new Function(this::replace, InternalStringArgument.class)),
                        entry(new Definition("startsWith", args(InternalStringArgument.class)),
                                new Function(this::startsWith, InternalBooleanArgument.class)),
                        entry(new Definition("toInt", args()),
                                new Function(this::toInt, InternalIntegerArgument.class))
                ),
                expandDefinition(strings("indexOf"),
                        args(
                                args(InternalStringArgument.class),
                                args(InternalStringArgument.class, InternalIntegerArgument.class)
                        ), new Function(this::indexOf, InternalIntegerArgument.class)
                ),
                expandDefinition(strings("join"), AllInternalArguments.get(),
                        new Function(this::join, InternalStringArgument.class)
                ),
                expandDefinition(strings("lastIndexOf"),
                        args(
                                args(InternalStringArgument.class),
                                args(InternalStringArgument.class, InternalIntegerArgument.class)
                        ), new Function(this::lastIndexOf, InternalIntegerArgument.class)
                ),
                expandDefinition(strings("substring"),
                        args(
                                args(InternalIntegerArgument.class),
                                args(InternalIntegerArgument.class, InternalIntegerArgument.class)
                        ), new Function(this::substring, InternalStringArgument.class)
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
}
