package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.List;

@FunctionalInterface
public interface InternalArgumentFunction {
    InternalArgument apply(InternalArgument target, List<InternalArgument> parameters);
}
