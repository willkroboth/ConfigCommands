package me.willkroboth.NumberArguments.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.InternalArguments.FunctionAdder;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalBooleanArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;

import java.util.List;

public class IntegerFunctionAdder extends FunctionAdder implements NumberFunctions {
    public Class<? extends InternalArgument> getClassToAddTo() { return InternalIntegerArgument.class; }

    public FunctionList getAddedFunctions() { return generateFunctions(); }

    public InternalBooleanArgument lessThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((int) target.getValue() < (int) arguments.get(0).getValue());
    }

    public InternalBooleanArgument lessThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((int) target.getValue() <= (int) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThan(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((int) target.getValue() > (int) arguments.get(0).getValue());
    }

    public InternalBooleanArgument greaterThanOrEqual(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((int) target.getValue() >= (int) arguments.get(0).getValue());
    }

    public InternalBooleanArgument equalTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((int) target.getValue() == (int) arguments.get(0).getValue());
    }

    public InternalBooleanArgument notEqualTo(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalBooleanArgument((int) target.getValue() != (int) arguments.get(0).getValue());
    }

    public InternalIntegerArgument add(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalIntegerArgument((int) target.getValue() + (int) arguments.get(0).getValue());
    }

    public InternalIntegerArgument subtract(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalIntegerArgument((int) target.getValue() - (int) arguments.get(0).getValue());
    }

    public InternalIntegerArgument multiply(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalIntegerArgument((int) target.getValue() * (int) arguments.get(0).getValue());
    }

    public InternalIntegerArgument divide(InternalArgument target, List<InternalArgument> arguments) {
        return new InternalIntegerArgument((int) target.getValue() / (int) arguments.get(0).getValue());
    }

    public StaticFunctionList getAddedStaticFunctions() {
        return staticMerge(
                staticEntries(
                        staticEntry(new Definition("maxValue", args()),
                                new StaticFunction(this::maxValue, myClass())),
                        staticEntry(new Definition("minValue", args()),
                                new StaticFunction(this::minValue, myClass()))
                )
        );
    }

    public InternalIntegerArgument maxValue(List<InternalArgument> parameters){
        return new InternalIntegerArgument(Integer.MAX_VALUE);
    }

    public InternalIntegerArgument minValue(List<InternalArgument> parameters){
        return new InternalIntegerArgument(Integer.MIN_VALUE);
    }

    public InternalArgument initialize(List<InternalArgument> parameters) {
        return new InternalIntegerArgument().initialize(parameters);
    }
}
