package me.willkroboth.configcommands.internalarguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import me.willkroboth.configcommands.functions.*;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.functions.executions.StaticExecution;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link InternalArgument} that represents a Java {@link Boolean}.
 */
public class InternalBooleanArgument extends InternalArgument<Boolean> implements CommandArgument<Boolean> {
    private boolean value;

    /**
     * Creates a new {@link InternalBooleanArgument} with no initial value set.
     */
    public InternalBooleanArgument() {
    }

    /**
     * Creates a new {@link InternalBooleanArgument} with the initial value set to the given boolean.
     *
     * @param value The initial boolean value for this {@link InternalBooleanArgument}.
     */
    public InternalBooleanArgument(boolean value) {
        super(value);
    }

    @Override
    public Argument<Boolean> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) {
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
    public void setValue(Boolean arg) {
        value = arg;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument<Boolean> arg) {
        value = arg.getValue();
    }

    @Override
    public String forCommand() {
        return String.valueOf(value);
    }

    @Override
    public InstanceFunctionList<Boolean> getInstanceFunctions() {
        return merge(super.getInstanceFunctions(),
                functions(
                        instanceFunction("and")
                                .withAliases("&&")
                                .withDescription("Performs the logical and operation with another Boolean")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalBooleanArgument.class, "other", "the other Boolean"))
                                        .returns(InternalBooleanArgument.class, "True if this and the other boolean are true, and false otherwise")
                                        .executes((a, b) -> new InternalBooleanArgument(a.getValue() && b.getValue()))
                                )
                                .withExamples(
                                        "do Boolean.(\"true\").and(Boolean.(\"true\")) -> True",
                                        "do Boolean.(\"false\").and(Boolean.(\"true\")) -> False",
                                        "do Boolean.(\"true\").&&(Boolean.(\"false\")) -> False",
                                        "do Boolean.(\"false\").&&(Boolean.(\"false\")) -> False"
                                ),
                        instanceFunction("not")
                                .withAliases("!")
                                .withDescription("Performs the logical not operation on this Boolean")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this Boolean is true, and false otherwise")
                                        .executes((a) -> new InternalBooleanArgument(!a.getValue()))
                                )
                                .withExamples(
                                        "Boolean.(\"true\").not() -> False",
                                        "Boolean.(\"false\").!() -> True"
                                ),
                        instanceFunction("or")
                                .withAliases("||")
                                .withDescription("Performs the logical or operation with another Boolean")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalBooleanArgument.class, "other", "the other Boolean"))
                                        .returns(InternalBooleanArgument.class, "True if this or the other Boolean is true, and false otherwise")
                                        .executes((a, b) -> new InternalBooleanArgument(a.getValue() || b.getValue()))
                                )
                                .withExamples(
                                        "do Boolean.(\"true\").or(Boolean.(\"true\")) -> True",
                                        "do Boolean.(\"false\").or(Boolean.(\"true\")) -> True",
                                        "do Boolean.(\"true\").||(Boolean.(\"false\")) -> True",
                                        "do Boolean.(\"false\").||(Boolean.(\"false\")) -> False"
                                )
                )
        );
    }

    @Override
    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        staticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new Boolean with the given value")
                                .withExecutions(StaticExecution
                                        .returns(InternalBooleanArgument.class, "A boolean with the value false because no value was given")
                                        .executes(() -> new InternalBooleanArgument(false)), StaticExecution

                                        .withParameters(parameter(InternalStringArgument.class, "value", "the value for the Boolean"))
                                        .returns(InternalBooleanArgument.class, "True if the value string equals \"true\", ignoring case, and false otherwise")
                                        .executes((value) -> new InternalBooleanArgument(Boolean.parseBoolean(value.getValue())))
                                )
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
