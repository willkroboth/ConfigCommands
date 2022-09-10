package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InternalArrayListArgument extends InternalArgument {
    private ArrayList<InternalArgument> value;

    public InternalArrayListArgument() {
    }

    public InternalArrayListArgument(Collection<InternalArgument> value) {
        super(new ArrayList<>(value));
    }

    public InternalArrayListArgument(ArrayList<InternalArgument> value) {
        super(value);
    }

    @Override
    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                generateGets(),
                generateSets(),
                expandDefinition(
                        strings("add"), AllInternalArguments.get(),
                        new Function(this::add, InternalVoidArgument.class)
                ),
                expandDefinition(strings("contains"), AllInternalArguments.get(),
                        new Function(this::contains, InternalBooleanArgument.class)),
                expandDefinition(strings("indexOf"), AllInternalArguments.get(),
                        new Function(this::indexOf, InternalIntegerArgument.class)),
                expandDefinition(strings("lastIndexOf"), AllInternalArguments.get(),
                        new Function(this::lastIndexOf, InternalIntegerArgument.class)),
                entries(
                        entry(new Definition("addAll", args(InternalArrayListArgument.class)),
                                new Function(this::addAll, InternalVoidArgument.class)),
                        entry(new Definition("remove", args(InternalIntegerArgument.class)),
                                new Function(this::remove, InternalVoidArgument.class)),
                        entry(new Definition("size", args()),
                                new Function(this::size, InternalIntegerArgument.class)),
                        entry(new Definition("subList", args(InternalIntegerArgument.class, InternalIntegerArgument.class)),
                                new Function(this::subList, InternalArrayListArgument.class))
                )
        );
    }

    private FunctionList generateGets() {
        FunctionList gets = new FunctionList();
        for (Class<? extends InternalArgument> clazz : AllInternalArguments.getFlat()) {
            gets.add(entry(new Definition("get", args(InternalIntegerArgument.class, clazz)),
                    new Function(this::get, clazz)));
        }
        return gets;
    }

    private FunctionList generateSets() {
        FunctionList sets = new FunctionList();
        for (Class<? extends InternalArgument> clazz : AllInternalArguments.getFlat()) {
            sets.add(entry(new Definition("set", args(InternalIntegerArgument.class, clazz)),
                    new Function(this::set, InternalVoidArgument.class)));
        }
        return sets;
    }

    private ArrayList<InternalArgument> getList(InternalArgument target) {
        return (ArrayList<InternalArgument>) target.getValue();
    }

    public InternalVoidArgument add(InternalArgument target, List<InternalArgument> parameters) {
        getList(target).add(parameters.get(0));
        return InternalVoidArgument.getInstance();
    }

    private InternalArgument addAll(InternalArgument target, List<InternalArgument> parameters) {
        getList(target).addAll(getList(parameters.get(0)));
        return InternalVoidArgument.getInstance();
    }

    private InternalArgument contains(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getList(target).contains(parameters.get(0)));
    }

    public InternalArgument get(InternalArgument target, List<InternalArgument> parameters) {
        InternalIntegerArgument index = (InternalIntegerArgument) parameters.get(0);
        InternalArgument classReference = parameters.get(1);

        InternalArgument out = getList(target).get((int) index.getValue());
        if (!out.getClass().equals(classReference.getClass()))
            throw new CommandRunException("Tried to get " + classReference.getClass() + " from index " + index.getValue() + " but found " + out.getClass());
        return out;
    }

    private InternalArgument indexOf(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalIntegerArgument(getList(target).indexOf(parameters.get(0)));
    }

    private InternalArgument remove(InternalArgument target, List<InternalArgument> parameters) {
        getList(target).remove((int) parameters.get(0).getValue());
        return InternalVoidArgument.getInstance();
    }

    private InternalArgument lastIndexOf(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalIntegerArgument(getList(target).lastIndexOf(parameters.get(0)));
    }

    private InternalArgument set(InternalArgument target, List<InternalArgument> parameters) {
        getList(target).set((int) parameters.get(0).getValue(), parameters.get(1));
        return InternalVoidArgument.getInstance();
    }

    private InternalArgument size(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalIntegerArgument(getList(target).size());
    }

    private InternalArgument subList(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalArrayListArgument(getList(target).subList((int) parameters.get(0).getValue(), (int) parameters.get(1).getValue()));
    }

    @Override
    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                staticExpandDefinition(
                        strings("", "new"), args(args()),
                        new StaticFunction(this::initialize, InternalArrayListArgument.class)
                )
        );
    }

    public InternalArgument initialize(List<InternalArgument> parameters) {
        return new InternalArrayListArgument(new ArrayList<>());
    }

    @Override
    public void setValue(Object arg) {
        value = (ArrayList<InternalArgument>) arg;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = getList(arg);
    }

    @Override
    public String forCommand() {
        StringBuilder out = new StringBuilder("[");
        if (value.size() != 0) {
            for (InternalArgument element : value) {
                out.append(element.forCommand());
                out.append(", ");
            }
            out.delete(out.length() - 2, out.length());
        }
        out.append("]");
        return out.toString();
    }
}
