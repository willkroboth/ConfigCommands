package me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.FunctionSyntax.InvalidFunctionLine;
import me.willkroboth.ConfigCommands.RegisteredCommands.CompilerState;
import me.willkroboth.ConfigCommands.RegisteredCommands.InterpreterState;

class Tag extends FunctionLine {
    public static FunctionLine parse(CompilerState compilerState) throws InvalidFunctionLine {
        if (!compilerState.getCommand().startsWith("tag "))
            throw new InvalidFunctionLine("tag", compilerState.getCommand(), "Invalid format. Must start with \"tag \"");

        String tag = compilerState.getCommand().substring(4);
        ConfigCommandsHandler.logDebug(compilerState, "Tag trimmed off to get: %s", tag);
        ConfigCommandsHandler.logDebug(compilerState, "Tag will have index: %s", compilerState.getIndex());
        compilerState.addTag(tag, compilerState.getIndex());

        return new Tag(tag);
    }

    private final String name;

    private Tag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "tag " + name;
    }

    @Override
    public int run(InterpreterState interpreterState) {
        ConfigCommandsHandler.logDebug(interpreterState, "Skipping over tag");

        return interpreterState.nextIndex();
    }
}
