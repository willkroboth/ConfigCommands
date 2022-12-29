package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An {@link InternalArgument} that represents a Java {@link ArrayList} of other {@link InternalArgument} values.
 */
public class InternalArrayListArgument extends InternalArgument {
    private ArrayList<InternalArgument> value;

    /**
     * Creates a new {@link InternalArrayListArgument} with no initial value set.
     */
    public InternalArrayListArgument() {
    }

    /**
     * Creates a new {@link InternalArrayListArgument} with the initial value set to the given {@link Collection}.
     *
     * @param value A {@link Collection} of {@link InternalArgument}s this {@link InternalArrayListArgument} should contain.
     */
    public InternalArrayListArgument(Collection<InternalArgument> value) {
        super(new ArrayList<>(value));
    }

    /**
     * Creates a new {@link InternalArrayListArgument} with the initial value set to the given {@link ArrayList}.
     *
     * @param value A {@link ArrayList} of {@link InternalArgument}s this {@link InternalArrayListArgument} should contain.
     */
    public InternalArrayListArgument(ArrayList<InternalArgument> value) {
        super(value);
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

    private ArrayList<InternalArgument> getList(InternalArgument target) {
        return (ArrayList<InternalArgument>) target.getValue();
    }

    @Override
    public InstanceFunctionList getInstanceFunctions() {
        return merge(super.getInstanceFunctions(),
                functions(
                        new InstanceFunction("add")
                                .withDescription("Adds an item to this list")
                                .withParameters(new Parameter(InternalArgument.class, "item", "The item to add"))
                                .returns(InternalVoidArgument.class)
                                .executes((target, parameters) -> {
                                    getList(target).add(parameters.get(0));
                                })
                                .withExamples(
                                        "<list> = ArrayList.new() -> []",
                                        "do <list>.add(Integer.new(\"10\")) -> [10]",
                                        "do <list>.add(\"a\") -> [10, \"a\"]",
                                        "do <list>.add(Boolean.(\"true\")) -> [10, \"a\", true]"
                                ),
                        new InstanceFunction("addAll")
                                .withDescription("Adds multiple items to this list from another list")
                                .withParameters(new Parameter(InternalArrayListArgument.class, "list", "A list of the items to add"))
                                .returns(InternalVoidArgument.class)
                                .executes((target, parameters) -> {
                                    getList(target).addAll(getList(parameters.get(0)));
                                })
                                .withExamples(
                                        "<list1> has [\"a\", \"b\"], <list2> has [\"c\", \"d\"]",
                                        "do <list1>.addAll(<list2>)",
                                        "  <list1> now has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list2>.addAll(<list1>)",
                                        "  <list2> now has [\"c\",\"d\", \"a\", \"b\", \"c\", \"d\"]"
                                ),
                        new InstanceFunction("contains")
                                .withDescription("Checks if the list has the given item")
                                .withParameters(new Parameter(InternalArgument.class, "item", "The item to look for"))
                                .returns(InternalBooleanArgument.class, "True if the item is in the list, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getList(target).contains(parameters.get(0)));
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\"]",
                                        "do <list>.contains(\"a\") -> True",
                                        "do <list>.contains(\"d\") -> False",
                                        "do <list>.contains(Integer.(\"10\")) -> False"
                                ),
                        new InstanceFunction("get")
                                .withDescription("Gets an item from the list")
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class, "index", "The index of the element"),
                                        new Parameter(InternalArgument.class, "class", "The class of the element, represented by an object")
                                )
                                .returns((parameters) -> {
                                    // If no parameters are given (when outputting return type), return most general class
                                    if (parameters.size() != 2) return InternalArgument.class;
                                    // Returns a type defined by the second argument
                                    return parameters.get(1);
                                }, "The object at the given index, with the class specified by the reference object")
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <list>.size()",
                                        "ClassCastException when the item found at the given index is not an instance of the reference class"
                                )
                                .executes((target, parameters) -> {
                                    InternalIntegerArgument index = (InternalIntegerArgument) parameters.get(0);
                                    InternalArgument classReference = parameters.get(1);

                                    InternalArgument out;
                                    try {
                                        out = getList(target).get((int) index.getValue());
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new CommandRunException(e);
                                    }

                                    if (!classReference.getClass().isAssignableFrom(out.getClass()))
                                        throw new CommandRunException(new ClassCastException("Cannot turn object with class " + out.getName() + " into a " + classReference.getName()));

                                    return out;
                                })
                                .withExamples(
                                        "<list> has [\"a\", 10]",
                                        "do <list>.get(Integer.(\"0\"), \"\") -> \"a\"",
                                        "do <list>.get(Integer.(\"1\"), Integer.()) -> 10",
                                        "do <list>.get(Integer.(\"3\"), \"\") -> IndexOutOfBounds",
                                        "do <list>.get(Integer.(\"0\"), Integer.()) -> ClassCastException"
                                ),
                        new InstanceFunction("indexOf")
                                .withDescription("Gives the index of an item in the list")
                                .withParameters(new Parameter(InternalArgument.class, "item", "The item to look for"))
                                .returns(InternalIntegerArgument.class, "The index of the given item in the list, or -1 if the list dose not have the item")
                                .executes((target, parameters) -> {
                                    return new InternalIntegerArgument(getList(target).indexOf(parameters.get(0)));
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\"]",
                                        "do <list>.indexOf(\"a\") -> 0",
                                        "do <list>.indexOf(\"c\") -> 2",
                                        "do <list>.indexOf(\"d\") -> -1"
                                ),
                        new InstanceFunction("lastIndexOf")
                                .withDescription("Gives the greatest index where the given item can be found")
                                .withParameters(new Parameter(InternalArgument.class, "item", "The item to look for"))
                                .returns(InternalIntegerArgument.class, "The greatest index of the given item in the list, or -1 if the list dose not have the item")
                                .executes((target, parameters) -> {
                                    return new InternalIntegerArgument(getList(target).lastIndexOf(parameters.get(0)));
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"b\", \"a\"]",
                                        "do <list>.lastIndexOf(\"a\") -> 3",
                                        "do <list>.lastIndexOf(\"b\") -> 2",
                                        "do <list>.lastIndexOf(\"d\") -> -1"
                                ),
                        new InstanceFunction("remove")
                                .withDescription("Removes an item from the list")
                                .withParameters(new Parameter(InternalIntegerArgument.class, "index", "The index to remove"))
                                .returns(InternalVoidArgument.class)
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <list>.size()"
                                )
                                .executes((target, parameters) -> {
                                    try {
                                        getList(target).remove((int) parameters.get(0).getValue());
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new CommandRunException(e);
                                    }
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list>.remove(Integer.(\"0\"))",
                                        "  <list> now has [\"b\", \"c\", \"d\"]",
                                        "do <list>.remove(Integer.(\"1\"))",
                                        "  <list> now has [\"b\", \"d\"]",
                                        "do <list>.remove(Integer.(\"2\")) -> IndexOutOfBounds"
                                ),
                        new InstanceFunction("set")
                                .withDescription("Replaces the item at the given index with a new item")
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class, "index", "The index to put the new item at"),
                                        new Parameter(InternalArgument.class, "item", "The item to add to the list")
                                )
                                .returns(InternalVoidArgument.class)
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <list>.size()"
                                )
                                .executes((target, parameters) -> {
                                    try {
                                        getList(target).set((int) parameters.get(0).getValue(), parameters.get(1));
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new CommandRunException(e);
                                    }
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list>.set(Integer.(\"1\"), \"Hello\")",
                                        "  <list> now has [\"a\", \"Hello\", \"c\", \"d\"]",
                                        "do <list>.set(Integer.(\"2\"), Integer.(\"10\"))",
                                        "  <list> now has [\"a\", \"Hello\", 10, \"d\"]",
                                        "do <list>.set(Integer.(\"4\"), \"\") -> IndexOutOfBounds"
                                ),
                        new InstanceFunction("size")
                                .withDescription("Gives the size of the list")
                                .returns(InternalIntegerArgument.class, "The number of elements currently in the list")
                                .executes((target, parameters) -> {
                                    return new InternalIntegerArgument(getList(target).size());
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\"]",
                                        "do <list>.size() -> 2",
                                        "do <list>.add(\"c\")",
                                        "  <list> now has [\"a\", \"b\", \"c\"]",
                                        "do <list>.size() -> 3"
                                ),
                        new InstanceFunction("subList")
                                .withDescription("Creates a new list with the items from a section of this list")
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class, "start", "The index of the first element to include"),
                                        new Parameter(InternalIntegerArgument.class, "end", "The index just after the last element to include")
                                )
                                .returns(InternalArrayListArgument.class, "A new list with the elements of this list between start and end")
                                .throwsException(
                                        "IndexOutOfBoundsException when start < 0 or end > <list>.size()",
                                        "IllegalArgumentException when start > end"
                                )
                                .executes((target, parameters) -> {
                                    try {
                                        return new InternalArrayListArgument(getList(target).subList((int) parameters.get(0).getValue(), (int) parameters.get(1).getValue()));
                                    } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                                        throw new CommandRunException(e);
                                    }
                                })
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list>.subList(Integer.(\"1\"), Integer.(\"3\")) -> [\"b\", \"c\"]",
                                        "do <list>.subList(Integer.(\"0\"), <list>.size()) -> [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list>.subList(Integer.(\"-1\"), Integer.(\"5\")) -> IndexOutOfBounds because start (-1) < 0 and end (5) > <list>.size() (4)",
                                        "do <list>.subList(Integer.(\"3\"), Integer.(\"1\")) -> IllegalArgumentException because start (3) > end (1)"
                                )
                )
        );
    }

    @Override
    public StaticFunctionList getStaticFunctions() {
        return merge(super.getStaticFunctions(),
                functions(
                        new StaticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new list with no elements")
                                .returns(InternalArrayListArgument.class)
                                .executes(parameters -> {
                                    return new InternalArrayListArgument(new ArrayList<>());
                                })
                                .withExamples(
                                        "ArrayList.new() -> []",
                                        "ArrayList.() -> []"
                                )
                )
        );
    }
}
