package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.*;

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

    public String getTypeTag() {
        return null;
    }

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                functions(
                        new Function("add")
                                .withDescription("Adds an item to this list")
                                .withParameters(new Parameter(InternalArgument.class, "item", "The item to add"))
                                .returns(InternalVoidArgument.class)
                                .executes(this::add)
                                .withExamples(
                                        "<list> = ArrayList.new() -> []",
                                        "do <list>.add(Integer.new(\"10\")) -> [10]",
                                        "do <list>.add(\"a\") -> [10, \"a\"]",
                                        "do <list>.add(Boolean.(\"true\")) -> [10, \"a\", true]"
                                ),
                        new Function("addAll")
                                .withParameters(new Parameter(InternalArrayListArgument.class))
                                .returns(InternalVoidArgument.class)
                                .executes(this::addAll),
                        // TODO: Write descriptions and examples and such for everything
                        new Function("contains")
                                .withParameters(new Parameter(InternalArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::contains),
                        new Function("get")
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class),
                                        new Parameter(InternalArgument.class)
                                )
                                .returns((parameters) -> {
                                    // If no parameters are given (when outputting return type), return most general class
                                    if(parameters.size() != 2) return InternalArgument.class;
                                    // Returns a type defined by the second argument
                                    return parameters.get(1);
                                })
                                .executes(this::get),
                        new Function("indexOf")
                                .withParameters(new Parameter(InternalArgument.class))
                                .returns(InternalIntegerArgument.class)
                                .executes(this::indexOf),
                        new Function("lastIndexOf")
                                .withParameters(new Parameter(InternalArgument.class))
                                .returns(InternalIntegerArgument.class)
                                .executes(this::lastIndexOf),
                        new Function("remove")
                                .withParameters(new Parameter(InternalIntegerArgument.class))
                                .returns(InternalVoidArgument.class)
                                .executes(this::remove),
                        new Function("set")
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class),
                                        new Parameter(InternalArgument.class)
                                )
                                .returns(InternalVoidArgument.class)
                                .executes(this::set),
                        new Function("size")
                                .returns(InternalIntegerArgument.class)
                                .executes(this::size),
                        new Function("subList")
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class),
                                        new Parameter(InternalIntegerArgument.class)
                                )
                                .returns(InternalArrayListArgument.class)
                                .executes(this::subList)
                )
        );
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

    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        new StaticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new list with no elements")
                                .returns(InternalArrayListArgument.class)
                                .executes(this::initialize)
                                .withExamples(
                                        "ArrayList.new() -> []",
                                        "ArrayList.() -> []"
                                )
                )
        );
    }

    public InternalArgument initialize(List<InternalArgument> parameters) {
        return new InternalArrayListArgument(new ArrayList<>());
    }

    public void setValue(Object arg) {
        value = (ArrayList<InternalArgument>) arg;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(InternalArgument arg) {
        value = getList(arg);
    }

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
