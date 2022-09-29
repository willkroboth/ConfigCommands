package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import me.willkroboth.ConfigCommands.Functions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                // TODO: Add function info
                functions(
                        new Function("and")
                                .withAliases("&&")
                                .withParameters(new Parameter(InternalBooleanArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::and),
                        new Function("not")
                                .withAliases("!")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::not),
                        new Function("or")
                                .withAliases("||")
                                .withParameters(new Parameter(InternalBooleanArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::or)
                )
        );
    }

    private InternalBooleanArgument and(InternalArgument target, List<InternalArgument> parameters) {
        boolean value = (boolean) parameters.get(0).getValue();
        boolean targetValue = (boolean) target.getValue();

        return new InternalBooleanArgument(value && targetValue);
    }

    private InternalBooleanArgument or(InternalArgument target, List<InternalArgument> parameters) {
        boolean value = (boolean) parameters.get(0).getValue();
        boolean targetValue = (boolean) target.getValue();


        return new InternalBooleanArgument(value || targetValue);
    }

    private InternalBooleanArgument not(InternalArgument target, List<InternalArgument> parameters) {
        boolean targetValue = (boolean) target.getValue();
        return new InternalBooleanArgument(!targetValue);
    }

    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        new StaticFunction("new")
                                .withAliases("")
                                .returns(InternalArrayListArgument.class)
                                .executes(this::initialize)
                )
        );
    }

    public InternalArgument initialize(List<InternalArgument> o) {
        String value = (String) (o).get(0).getValue();
        return new InternalBooleanArgument(Boolean.parseBoolean(value));
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
}
