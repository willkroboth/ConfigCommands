package me.willkroboth.NumberArguments.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
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

public class InternalDoubleArgument extends InternalArgument implements NumberFunctions {
    private double value;

    public InternalDoubleArgument(){}

    public InternalDoubleArgument(double value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) {
        command.withArguments(new DoubleArgument(name));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalDoubleArgument.class);
    }

    // functions
    public FunctionList getFunctions() {
        return merge(
                super.getFunctions(),
                generateFunctions()
        );
    }

    public InternalBooleanArgument lessThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((double) target.getValue() < (double) arguments.get(0).getValue());
    }

    public InternalBooleanArgument lessThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((double) target.getValue() <= (double) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((double) target.getValue() > (double) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((double) target.getValue() >= (double) arguments.get(0).getValue());
    }

    public InternalBooleanArgument equalTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((double) target.getValue() == (double) arguments.get(0).getValue());
    }

    public InternalBooleanArgument notEqualTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((double) target.getValue() != (double) arguments.get(0).getValue());
    }

    public InternalDoubleArgument add(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalDoubleArgument((double) target.getValue() + (double) arguments.get(0).getValue());
    }

    public InternalDoubleArgument subtract(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalDoubleArgument((double) target.getValue() - (double) arguments.get(0).getValue());
    }

    public InternalDoubleArgument multiply(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalDoubleArgument((double) target.getValue() * (double) arguments.get(0).getValue());
    }

    public InternalDoubleArgument divide(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalDoubleArgument((double) target.getValue() / (double) arguments.get(0).getValue());
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                generateStaticFunctions()
        );
    }

    public InternalDoubleArgument maxValue(List<InternalArgument> parameters){
        return new InternalDoubleArgument(Double.POSITIVE_INFINITY);
    }

    public InternalDoubleArgument minValue(List<InternalArgument> parameters){
        return new InternalDoubleArgument(Double.NEGATIVE_INFINITY);
    }

    public InternalDoubleArgument initialize(List<InternalArgument> parameters) {
        double result = 0;
        if (parameters.size() == 1) {
            InternalStringArgument arg = (InternalStringArgument) parameters.get(0);
            String word = (String)arg.getValue();

            try {
                result = Double.parseDouble(word);
            } catch (NumberFormatException var6) {
                throw new CommandRunException("Word: \"" + word + "\" cannot be parsed as double.");
            }
        }

        return new InternalDoubleArgument(result);
    }

    // value
    public void setValue(Object arg) { value = (double) arg; }

    public Object getValue() { return value; }

    public void setValue(InternalArgument arg) { value = (double) arg.getValue(); }

    public String forCommand() { return "" + value; }
}
