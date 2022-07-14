package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalIntegerArgument extends InternalArgument{
    private int value;

    public InternalIntegerArgument() {
    }

    public InternalIntegerArgument(int value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        int min;
        if(arg.get("min") == null){
            min = Integer.MIN_VALUE;
        }
        else {
            try {
                min = Integer.parseInt(arg.get("min").toString());
            } catch (NumberFormatException e){
                throw new IncorrectArgumentKey(arg.toString(), "min", "Could not be interpreted as an int.");
            }
        }
        if(debugMode) logger.info("Arg has min: " + min);

        int max;
        if(arg.get("max") == null){
            max = Integer.MAX_VALUE;
        }
        else {
            try {
                max = Integer.parseInt(arg.get("max").toString());
            } catch (NumberFormatException e){
                throw new IncorrectArgumentKey(arg.toString(), "max", "Could not be interpreted as an int.");
            }
        }
        if(debugMode) logger.info("Arg has max: " + max);

        command.withArguments(new IntegerArgument(name, min, max));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalIntegerArgument.class);
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(
                super.getStaticFunctions(),
                staticExpandDefinition(strings("", "new"), args(args(), args(InternalStringArgument.class)),
                        new StaticFunction(this::initialize, InternalIntegerArgument.class)
                )
        );
    }

    public InternalArgument initialize(List<InternalArgument> arguments) {
        int result = 0;
        if(arguments.size() == 1){
            InternalStringArgument arg = (InternalStringArgument) arguments.get(0);
            String word = (String) arg.getValue();
            try {
                result = Integer.parseInt(word);
            } catch (NumberFormatException e){
                throw new CommandRunException("Word: \"" + word + "\" cannot be parsed as int.");
            }
        }

        return new InternalIntegerArgument(result);
    }

    // value
    public void setValue(Object arg) { value = (int) arg; }

    public Object getValue() { return value; }

    public void setValue(InternalArgument arg) { value = (int) arg.getValue(); }

    public String forCommand() { return "" + value; }
}
