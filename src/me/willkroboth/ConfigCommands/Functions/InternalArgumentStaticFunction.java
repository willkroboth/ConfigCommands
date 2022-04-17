package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

@FunctionalInterface
public interface InternalArgumentStaticFunction {
    InternalArgument apply(List<InternalArgument> parameters);
}
