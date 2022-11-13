package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import me.willkroboth.ConfigCommands.Functions.*;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class InternalBooleanArgument extends InternalArgument implements CommandArgument {
    private boolean value;

    public InternalBooleanArgument() {
    }

    public InternalBooleanArgument(boolean value) {
        super(value);
    }

    @Override
    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) {
        return new BooleanArgument(name);
    }

    @Override
    public boolean editArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, @Nullable Object argumentInfo) {
        sender.sendMessage("There are no options to configure for a BooleanArgument");
        return true;
    }

    @Override
    public String[] formatArgumentInfo(Object argumentInfo) {
        return new String[]{"There are no options to configure for a BooleanArgument"};
    }

    @Override
    public void setValue(Object arg) {
        value = (boolean) arg;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = (boolean) arg.getValue();
    }

    @Override
    public String forCommand() {
        return "" + value;
    }

    private boolean getBoolean(InternalArgument argument) {
        return (boolean) argument.getValue();
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
                                        "do Boolean.(\"true\").and(Boolean.(\"true\")) -> True",
                                        "do Boolean.(\"false\").and(Boolean.(\"true\")) -> False",
                                        "do Boolean.(\"true\").&&(Boolean.(\"false\")) -> False",
                                        "do Boolean.(\"false\").&&(Boolean.(\"false\")) -> False"
                                ),
                        new Function("not")
                                .withAliases("!")
                                .withDescription("Performs the logical not operation on this Boolean")
                                .returns(InternalBooleanArgument.class, "True if this Boolean is true, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(!getBoolean(target));
                                })
                                .withExamples(
                                        "Boolean.(\"true\").not() -> False",
                                        "Boolean.(\"false\").!() -> True"
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
                                        "do Boolean.(\"true\").or(Boolean.(\"true\")) -> True",
                                        "do Boolean.(\"false\").or(Boolean.(\"true\")) -> True",
                                        "do Boolean.(\"true\").||(Boolean.(\"false\")) -> True",
                                        "do Boolean.(\"false\").||(Boolean.(\"false\")) -> False"
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
