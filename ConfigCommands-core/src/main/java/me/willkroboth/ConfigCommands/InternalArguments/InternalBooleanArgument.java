package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;

import javax.annotation.Nullable;
import java.util.List;

public class InternalBooleanArgument extends InternalArgument implements CommandArgument{
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
    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                expandDefinition(strings("and", "&&"), args(args(InternalBooleanArgument.class)),
                        new Function(this::and, InternalBooleanArgument.class)),
                expandDefinition(strings("or", "||"), args(args(InternalBooleanArgument.class)),
                        new Function(this::or, InternalBooleanArgument.class)),
                expandDefinition(strings("not", "!"), args(args()),
                        new Function(this::not, InternalBooleanArgument.class))
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

    @Override
    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                staticExpandDefinition(strings("", "new"), args(args(InternalStringArgument.class)),
                        new StaticFunction(this::initialize, InternalBooleanArgument.class))
        );
    }

    public InternalArgument initialize(List<InternalArgument> o) {
        String value = (String) (o).get(0).getValue();
        return new InternalBooleanArgument(Boolean.parseBoolean(value));
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
}
