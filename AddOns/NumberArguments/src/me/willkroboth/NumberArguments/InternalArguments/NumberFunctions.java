package me.willkroboth.NumberArguments.InternalArguments;

import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionCreator;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalBooleanArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;

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
}


