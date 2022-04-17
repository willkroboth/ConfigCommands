package me.willkroboth.NumberArguments.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.FloatRangeArgument;
import dev.jorel.commandapi.wrappers.FloatRange;
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

public class InternalFloatRangeArgument extends InternalArgument {
    private FloatRange value;

    public InternalFloatRangeArgument(){}

    public InternalFloatRangeArgument(FloatRange value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        command.withArguments(new FloatRangeArgument(name));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalFloatRangeArgument.class);
    }

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                entries(
                        entry(new Definition("getUpperBound", args()),
                                new Function(this::getUpperBound, InternalFloatArgument.class)),
                        entry(new Definition("setUpperBound", args(InternalFloatArgument.class)),
                                new Function(this::setUpperBound, InternalVoidArgument.class)),
                        entry(new Definition("getLowerBound", args()),
                                new Function(this::getLowerBound, InternalFloatArgument.class)),
                        entry(new Definition("setLowerBound", args(InternalFloatArgument.class)),
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

    private FloatRange getFloatRange(InternalArgument target){ return (FloatRange) target.getValue(); }

    public InternalFloatArgument getUpperBound(InternalArgument target, List<InternalArgument> parameters){
        return new InternalFloatArgument(getFloatRange(target).getLowerBound());
    }

    public InternalVoidArgument setUpperBound(InternalArgument target, List<InternalArgument> parameters){
        float low = ((FloatRange)target.getValue()).getLowerBound();
        float high = (float) parameters.get(0).getValue();

        FloatRange newRange = new FloatRange(low, high);
        target.setValue(newRange);
        return InternalVoidArgument.getInstance();
    }

    public InternalFloatArgument getLowerBound(InternalArgument target, List<InternalArgument> parameters){
        return new InternalFloatArgument(getFloatRange(target).getLowerBound());
    }

    public InternalVoidArgument setLowerBound(InternalArgument target, List<InternalArgument> parameters){
        float high = ((FloatRange)target.getValue()).getUpperBound();
        float low = (float) parameters.get(0).getValue();

        FloatRange newRange = new FloatRange(low, high);
        target.setValue(newRange);
        return InternalVoidArgument.getInstance();
    }

    public InternalBooleanArgument isInRange(InternalArgument target, List<InternalArgument> parameters){
        float f = (float) parameters.get(0).getValue();
        return new InternalBooleanArgument(getFloatRange(target).isInRange(f));
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                staticExpandDefinition(strings("new", ""), args(args(InternalFloatArgument.class, InternalFloatArgument.class)),
                        new StaticFunction(this::initialize, InternalFloatArgument.class)),
                staticEntries(
                        staticEntry(new Definition("newGreaterThanOrEqual", args(InternalFloatArgument.class)),
                                new StaticFunction(this::rangeGreaterThanOrEqual, InternalFloatRangeArgument.class)),
                        staticEntry(new Definition("newLessThanOrEqual", args(InternalFloatArgument.class)),
                                new StaticFunction(this::rangeLessThanOrEqual, InternalFloatRangeArgument.class))
                )
        );
    }

    public InternalFloatRangeArgument initialize(List<InternalArgument> parameters){
        float low = (float) parameters.get(0).getValue();
        float high = (float) parameters.get(1).getValue();
        if (high < low) throw new CommandRunException("Low value (" + low + ") must be greater than or equal to high value (" + high + ")");

        return new InternalFloatRangeArgument(new FloatRange(low, high));
    }

    public InternalFloatRangeArgument rangeGreaterThanOrEqual(List<InternalArgument> parameters){
        float min = (float) parameters.get(0).getValue();

        return new InternalFloatRangeArgument(FloatRange.floatRangeGreaterThanOrEq(min));
    }

    public InternalFloatRangeArgument rangeLessThanOrEqual(List<InternalArgument> parameters){
        float max = (float) parameters.get(0).getValue();

        return new InternalFloatRangeArgument(FloatRange.floatRangeLessThanOrEq(max));
    }

    public void setValue(Object arg) {
        value = (FloatRange) arg;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(InternalArgument arg) {
        value = (FloatRange) arg.getValue();
    }

    public String forCommand() {
        return value.toString();
    }
}
