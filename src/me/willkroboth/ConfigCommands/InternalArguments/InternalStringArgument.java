package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;

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

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        String type = (String) arg.get("subtype");
        if (debugMode) logger.info("Arg has subtype: " + type);
        command.withArguments(
                type == null ? new StringArgument(name):
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
                entries(
                        entry(new Definition("toInt", args()),
                                new Function(this::toInt, InternalIntegerArgument.class))
                ),
                expandDefinition(strings("join"), AllInternalArguments.get(),
                        new Function(this::join, InternalStringArgument.class))
        );
    }

    public InternalIntegerArgument toInt(InternalArgument target, List<InternalArgument> parameters) {
        String s = (String) target.getValue();
        try {
            return new InternalIntegerArgument(Integer.parseInt(s));
        } catch (NumberFormatException ignored){
            throw new CommandRunException("NumberFormatException: value: \"" + s + "\" could not be interpreted as int.");
        }
    }

    public InternalStringArgument join(InternalArgument target, List<InternalArgument> parameters){
        String arg = parameters.get(0).forCommand();
        return new InternalStringArgument(target.getValue() + arg);
    }

    public void setValue(Object arg) { value = (String) arg; }

    public Object getValue() { return value; }

    public void setValue(InternalArgument arg) { value = (String) arg.getValue(); }

    public String forCommand() { return value; }
}
