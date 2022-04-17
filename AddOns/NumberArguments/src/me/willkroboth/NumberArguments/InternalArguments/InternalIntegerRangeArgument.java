package me.willkroboth.NumberArguments.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalBooleanArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalVoidArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalIntegerRangeArgument extends InternalArgument {
    private IntegerRange value;

    public InternalIntegerRangeArgument(){}

    public InternalIntegerRangeArgument(IntegerRange value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        command.withArguments(new IntegerRangeArgument(name));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalIntegerRangeArgument.class);
    }

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                entries(
                        entry(new Definition("getUpperBound", args()),
                                new Function(this::getUpperBound, InternalIntegerArgument.class)),
                        entry(new Definition("setUpperBound", args(InternalIntegerArgument.class)),
                                new Function(this::setUpperBound, InternalVoidArgument.class)),
                        entry(new Definition("getLowerBound", args()),
                                new Function(this::getLowerBound, InternalIntegerArgument.class)),
                        entry(new Definition("setLowerBound", args(InternalIntegerArgument.class)),
                                new Function(this::setLowerBound, InternalVoidArgument.class))
                ),
                expandDefinition(strings("isInRange"),
                        args(
                                args(InternalIntegerArgument.class),
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalLongArgument.class)
                        ), new Function(this::isInRange, InternalBooleanArgument.class)
                )
        );
    }

    private IntegerRange getIntegerRange(InternalArgument target){ return (IntegerRange) target.getValue(); }

    public InternalIntegerArgument getUpperBound(InternalArgument target, List<InternalArgument> parameters){
        return new InternalIntegerArgument(getIntegerRange(target).getLowerBound());
    }

    public InternalVoidArgument setUpperBound(InternalArgument target, List<InternalArgument> parameters){
        int low = ((IntegerRange)target.getValue()).getLowerBound();
        int high = (int) parameters.get(0).getValue();

        IntegerRange newRange = new IntegerRange(low, high);
        target.setValue(newRange);
        return InternalVoidArgument.getInstance();
    }

    public InternalIntegerArgument getLowerBound(InternalArgument target, List<InternalArgument> parameters){
        return new InternalIntegerArgument(getIntegerRange(target).getLowerBound());
    }

    public InternalVoidArgument setLowerBound(InternalArgument target, List<InternalArgument> parameters){
        int high = ((IntegerRange)target.getValue()).getUpperBound();
        int low = (int) parameters.get(0).getValue();

        IntegerRange newRange = new IntegerRange(low, high);
        target.setValue(newRange);
        return InternalVoidArgument.getInstance();
    }

    public InternalBooleanArgument isInRange(InternalArgument target, List<InternalArgument> parameters){
        int i = (int) parameters.get(0).getValue();

        return new InternalBooleanArgument(getIntegerRange(target).isInRange(i));
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                staticExpandDefinition(strings("new", ""), args(args(InternalIntegerArgument.class, InternalIntegerArgument.class)),
                        new StaticFunction(this::initialize, InternalIntegerRangeArgument.class)),
                staticEntries(
                        staticEntry(new Definition("newGreaterThanOrEqual", args(InternalIntegerArgument.class)),
                                new StaticFunction(this::rangeGreaterThanOrEqual, InternalIntegerRangeArgument.class)),
                        staticEntry(new Definition("newLessThanOrEqual", args(InternalIntegerArgument.class)),
                                new StaticFunction(this::rangeLessThanOrEqual, InternalIntegerRangeArgument.class))
                )
        );
    }

    public InternalIntegerRangeArgument initialize(List<InternalArgument> parameters){
        int low = (int) parameters.get(0).getValue();
        int high = (int) parameters.get(1).getValue();
        if (high < low) throw new CommandRunException("Low value (" + low + ") must be greater than or equal to high value (" + high + ")");

        return new InternalIntegerRangeArgument(new IntegerRange(low, high));
    }

    public InternalIntegerRangeArgument rangeGreaterThanOrEqual(List<InternalArgument> parameters){
        int min = (int) parameters.get(0).getValue();

        return new InternalIntegerRangeArgument(IntegerRange.integerRangeGreaterThanOrEq(min));
    }

    public InternalIntegerRangeArgument rangeLessThanOrEqual(List<InternalArgument> parameters){
        int max = (int) parameters.get(0).getValue();

        return new InternalIntegerRangeArgument(IntegerRange.integerRangeLessThanOrEq(max));
    }

    public void setValue(Object arg) {
        value = (IntegerRange) arg;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(InternalArgument arg) {
        value = (IntegerRange) arg.getValue();
    }

    public String forCommand() {
        return value.toString();
    }
}
