package me.willkroboth.NumberArguments.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.FloatArgument;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalBooleanArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalFloatArgument extends InternalArgument implements NumberFunctions {
    private float value;

    public InternalFloatArgument(){}

    public InternalFloatArgument(float value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        float min;
        if(arg.get("min") == null){
            min = Float.MIN_VALUE;
        }
        else {
            try {
                min = Float.parseFloat(arg.get("min").toString());
            } catch (NumberFormatException e){
                throw new IncorrectArgumentKey(arg.toString(), "min", "Could not be interpreted as float.");
            }
        }
        if(debugMode) logger.info("Arg has min: " + min);

        float max;
        if(arg.get("max") == null){
            max = Float.MAX_VALUE;
        }
        else {
            try {
                max = Float.parseFloat(arg.get("max").toString());
            } catch (NumberFormatException e){
                throw new IncorrectArgumentKey(arg.toString(), "max", "Could not be interpreted as float.");
            }
        }
        if(debugMode) logger.info("Arg has max: " + max);

        command.withArguments(new FloatArgument(name, min, max));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalFloatArgument.class);
    }

    public FunctionList getFunctions() {
        return merge(
                super.getFunctions(),
                generateFunctions()
        );
    }

    public InternalBooleanArgument lessThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((float) target.getValue() < (float) arguments.get(0).getValue());
    }

    public InternalBooleanArgument lessThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((float) target.getValue() <= (float) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((float) target.getValue() > (float) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((float) target.getValue() >= (float) arguments.get(0).getValue());
    }

    public InternalBooleanArgument equalTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((float) target.getValue() == (float) arguments.get(0).getValue());
    }

    public InternalBooleanArgument notEqualTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((float) target.getValue() != (float) arguments.get(0).getValue());
    }

    public InternalFloatArgument add(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalFloatArgument((float) target.getValue() + (float) arguments.get(0).getValue());
    }

    public InternalFloatArgument subtract(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalFloatArgument((float) target.getValue() - (float) arguments.get(0).getValue());
    }

    public InternalFloatArgument multiply(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalFloatArgument((float) target.getValue() * (float) arguments.get(0).getValue());
    }

    public InternalFloatArgument divide(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalFloatArgument((float) target.getValue() / (float) arguments.get(0).getValue());
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                staticEntries(
                        staticEntry(new Definition("maxValue", args()),
                                new StaticFunction(this::maxValue, InternalFloatArgument.class)),
                        staticEntry(new Definition("minValue", args()),
                                new StaticFunction(this::minValue, InternalFloatArgument.class))
                )
        );
    }

    public InternalFloatArgument maxValue(List<InternalArgument> parameters){
        return new InternalFloatArgument(Float.POSITIVE_INFINITY);
    }

    public InternalFloatArgument minValue(List<InternalArgument> parameters){
        return new InternalFloatArgument(Float.NEGATIVE_INFINITY);
    }

    // value
    public void setValue(Object arg) { value = (float) arg; }

    public Object getValue() { return value; }

    public void setValue(InternalArgument arg) { value = (float) arg.getValue(); }

    public String forCommand() { return "" + value; }
}
