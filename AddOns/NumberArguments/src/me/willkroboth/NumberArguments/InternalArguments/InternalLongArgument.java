package me.willkroboth.NumberArguments.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LongArgument;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalBooleanArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalLongArgument extends InternalArgument implements NumberFunctions {
    private long value;

    public InternalLongArgument(){}

    public InternalLongArgument(long value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        long min;
        if(arg.get("min") == null){
            min = Long.MIN_VALUE;
        }
        else {
            try {
                min = Long.parseLong(arg.get("min").toString());
            } catch (NumberFormatException e){
                throw new IncorrectArgumentKey(arg.toString(), "min", "Could not be interpreted as a long.");
            }
        }
        if(debugMode) logger.info("Arg has min: " + min);

        long max;
        if(arg.get("max") == null){
            max = Long.MAX_VALUE;
        }
        else {
            try {
                max = Long.parseLong(arg.get("max").toString());
            } catch (NumberFormatException e){
                throw new IncorrectArgumentKey(arg.toString(), "max", "Could not be interpreted as a long.");
            }
        }
        if(debugMode) logger.info("Arg has max: " + max);

        command.withArguments(new LongArgument(name, min, max));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalLongArgument.class);
    }

    // functions
    public FunctionList getFunctions() {
        return merge(
                super.getFunctions(),
                generateFunctions()
        );
    }

    public InternalBooleanArgument lessThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((long) target.getValue() < (long) arguments.get(0).getValue());
    }

    public InternalBooleanArgument lessThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((long) target.getValue() <= (long) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((long) target.getValue() > (long) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((long) target.getValue() >= (long) arguments.get(0).getValue());
    }

    public InternalBooleanArgument equalTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((long) target.getValue() == (long) arguments.get(0).getValue());
    }

    public InternalBooleanArgument notEqualTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((long) target.getValue() != (long) arguments.get(0).getValue());
    }

    public InternalLongArgument add(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalLongArgument((long) target.getValue() + (long) arguments.get(0).getValue());
    }

    public InternalLongArgument subtract(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalLongArgument((long) target.getValue() - (long) arguments.get(0).getValue());
    }

    public InternalLongArgument multiply(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalLongArgument((long) target.getValue() * (long) arguments.get(0).getValue());
    }

    public InternalLongArgument divide(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalLongArgument((long) target.getValue() / (long) arguments.get(0).getValue());
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                generateStaticFunctions()
        );
    }

    public InternalLongArgument maxValue(List<InternalArgument> parameters){
        return new InternalLongArgument(Long.MAX_VALUE);
    }

    public InternalLongArgument minValue(List<InternalArgument> parameters){
        return new InternalLongArgument(Long.MIN_VALUE);
    }

    public InternalLongArgument initialize(List<InternalArgument> parameters) {
        long result = 0;
        if (parameters.size() == 1) {
            InternalStringArgument arg = (InternalStringArgument) parameters.get(0);
            String word = (String)arg.getValue();

            try {
                result = Long.parseLong(word);
            } catch (NumberFormatException var6) {
                throw new CommandRunException("Word: \"" + word + "\" cannot be parsed as long.");
            }
        }

        return new InternalLongArgument(result);
    }

    // value
    public void setValue(Object arg) { value = (long) arg; }

    public Object getValue() { return value; }

    public void setValue(InternalArgument arg) { value = (long) arg.getValue(); }

    public String forCommand() { return "" + value; }
}
