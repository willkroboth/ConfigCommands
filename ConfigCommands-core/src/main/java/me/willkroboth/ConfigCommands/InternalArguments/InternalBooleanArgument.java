package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import me.willkroboth.ConfigCommands.Functions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InternalBooleanArgument extends InternalArgument {
    private boolean value;

    public InternalBooleanArgument() {
    }

    public InternalBooleanArgument(boolean value) {
        super(value);
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean localDebug) {
        command.withArguments(new BooleanArgument(name));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalBooleanArgument.class);
    }

    private boolean getBoolean(InternalArgument argument) {
        return (boolean) argument.getValue();
    }

    public void setValue(Object arg) {
        value = (boolean) arg;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(InternalArgument arg) {
        value = (boolean) arg.getValue();
    }

    public String forCommand() {
        return "" + value;
    }

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                functions(
                        new Function("and")
                                .withAliases("&&")
                                .withDescription("Performs the logical and operation with another Boolean")
                                .withParameters(new Parameter(InternalBooleanArgument.class, "other", "the other Boolean"))
                                .returns(InternalBooleanArgument.class, "True if this and the other boolean are true, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getBoolean(target) && getBoolean(parameters.get(0)));
                                })
                                .withExamples(
                                        "do Boolean.(\"true\").and(Boolean.(\"true\")) -> Boolean.(\"true\")",
                                        "do Boolean.(\"false\").and(Boolean.(\"true\")) -> Boolean.(\"false\")",
                                        "do Boolean.(\"true\").&&(Boolean.(\"false\")) -> Boolean.(\"false\")",
                                        "do Boolean.(\"false\").&&(Boolean.(\"false\")) -> Boolean.(\"false\")"
                                ),
                        new Function("not")
                                .withAliases("!")
                                .withDescription("Performs the logical not operation on this Boolean")
                                .returns(InternalBooleanArgument.class, "True if this Boolean is true, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(!getBoolean(target));
                                })
                                .withExamples(
                                        "Boolean.(\"true\").not() -> Boolean.(\"false\")",
                                        "Boolean.(\"false\").!() -> Boolean.(\"true\")"
                                ),
                        new Function("or")
                                .withAliases("||")
                                .withDescription("Performs the logical or operation with another Boolean")
                                .withParameters(new Parameter(InternalBooleanArgument.class, "other", "the other Boolean"))
                                .returns(InternalBooleanArgument.class, "True if this or the other Boolean is true, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getBoolean(target) || getBoolean(parameters.get(0)));
                                })
                                .withExamples(
                                        "do Boolean.(\"true\").or(Boolean.(\"true\")) -> Boolean.(\"true\")",
                                        "do Boolean.(\"false\").or(Boolean.(\"true\")) -> Boolean.(\"true\")",
                                        "do Boolean.(\"true\").||(Boolean.(\"false\")) -> Boolean.(\"true\")",
                                        "do Boolean.(\"false\").||(Boolean.(\"false\")) -> Boolean.(\"false\")"
                                )
                )
        );
    }

    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        new StaticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new Boolean with the given value")
                                .withParameters()
                                .withParameters(new Parameter(InternalStringArgument.class, "value", "the value for the Boolean"))
                                .returns(InternalBooleanArgument.class, "True if the value string equals \"true\", ignoring case, and false if it is not equal or not given")
                                .executes(parameters -> {
                                    if (parameters.size() == 0) return new InternalBooleanArgument(false);
                                    return new InternalBooleanArgument(Boolean.parseBoolean((String) parameters.get(0).getValue()));
                                })
                                .withExamples(
                                        "Boolean.new(\"true\") -> True",
                                        "Boolean.new(\"false\") -> False",
                                        "Boolean.(\"yes\") -> False",
                                        "Boolean.() -> False"
                                )
                )
        );
    }
}
