package me.willkroboth.NumberArguments.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionCreator;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalBooleanArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;

import java.util.List;

public interface NumberFunctions extends FunctionCreator {
    default FunctionList generateFunctions() {
        return merge(
                expandDefinition(
                        strings("<", "lessThan"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::lessThan, InternalBooleanArgument.class)
                ),
                expandDefinition(
                        strings("<=", "lessThanOrEqualTo"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::lessThanOrEqual, InternalBooleanArgument.class)
                ),
                expandDefinition(
                        strings(">", "greaterThan"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::greaterThan, InternalBooleanArgument.class)
                ),
                expandDefinition(
                        strings(">=", "greaterThanOrEqualTo"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::greaterThanOrEqual, InternalBooleanArgument.class)
                ),
                expandDefinition(
                        strings("==", "equalTo"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::equalTo, InternalBooleanArgument.class)
                ),
                expandDefinition(
                        strings("!=", "notEqualTo"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::notEqualTo, InternalBooleanArgument.class)
                ),
                expandDefinition(
                        strings("+", "add"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::add, myClass())
                ),
                expandDefinition(
                        strings("-", "subtract"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::subtract, myClass())
                ),
                expandDefinition(
                        strings("*", "multiply"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::multiply, myClass())
                ),
                expandDefinition(
                        strings("/", "divide"),
                        args(
                                args(InternalDoubleArgument.class),
                                args(InternalFloatArgument.class),
                                args(InternalIntegerArgument.class),
                                args(InternalLongArgument.class)
                        ),
                        new Function(this::divide, myClass())
                ),
                expandDefinition(
                        strings("toDouble"),
                        args(args()),
                        new Function(this::toDouble, InternalDoubleArgument.class)
                ),
                expandDefinition(
                        strings("toFloat"),
                        args(args()),
                        new Function(this::toFloat, InternalFloatArgument.class)
                ),
                expandDefinition(
                        strings("toInt"),
                        args(args()),
                        new Function(this::toInt, InternalIntegerArgument.class)
                ),
                expandDefinition(
                        strings("toLong"),
                        args(args()),
                        new Function(this::toLong, InternalLongArgument.class)
                )
            );
    }

    InternalBooleanArgument lessThan(InternalArgument target, List<InternalArgument> parameters);

    InternalBooleanArgument lessThanOrEqual(InternalArgument target, List<InternalArgument> parameters);

    InternalBooleanArgument greaterThan(InternalArgument target, List<InternalArgument> parameters);

    InternalBooleanArgument greaterThanOrEqual(InternalArgument target, List<InternalArgument> parameters);

    InternalBooleanArgument equalTo(InternalArgument target, List<InternalArgument> parameters);

    InternalBooleanArgument notEqualTo(InternalArgument target, List<InternalArgument> parameters);

    InternalArgument add(InternalArgument target, List<InternalArgument> parameters);

    InternalArgument subtract(InternalArgument target, List<InternalArgument> parameters);

    InternalArgument multiply(InternalArgument target, List<InternalArgument> parameters);

    InternalArgument divide(InternalArgument target, List<InternalArgument> parameters);

    default InternalDoubleArgument toDouble(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalDoubleArgument((Double) target.getValue());
    }

    default InternalFloatArgument toFloat(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalFloatArgument((Float) target.getValue());
    }

    default InternalIntegerArgument toInt(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalIntegerArgument((Integer) target.getValue());
    }

    default InternalLongArgument toLong(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalLongArgument((Long) target.getValue());
    }

    default StaticFunctionList generateStaticFunctions(){
        return staticMerge(
                staticEntries(
                        staticEntry(new Definition("maxValue", args()),
                                new StaticFunction(this::maxValue, myClass())),
                        staticEntry(new Definition("minValue", args()),
                                new StaticFunction(this::minValue, myClass()))
                ),
                staticExpandDefinition(strings("", "new"),
                        args(
                                args(),
                                args(InternalStringArgument.class)
                        ),
                        new StaticFunction(this::initialize, myClass())
                )
        );
    }

    InternalArgument initialize(List<InternalArgument> parameters);

    InternalArgument minValue(List<InternalArgument> parameters);

    InternalArgument maxValue(List<InternalArgument> parameters);
}


