package me.willkroboth.configcommands.internalarguments;

import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.functions.*;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.functions.executions.StaticExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An {@link InternalArgument} that represents a Java {@link List} of other {@link InternalArgument} values.
 */
public class InternalListArgument extends InternalArgument<List<InternalArgument<?>>> {
    private List<InternalArgument<?>> value;

    /**
     * Creates a new {@link InternalListArgument} with no initial value set.
     */
    public InternalListArgument() {
    }

    /**
     * Creates a new {@link InternalListArgument} with the initial value set to the given {@link Collection}.
     *
     * @param value A {@link Collection} of {@link InternalArgument}s this {@link InternalListArgument} should contain.
     */
    public InternalListArgument(Collection<InternalArgument<?>> value) {
        super(new ArrayList<>(value));
    }

    /**
     * Creates a new {@link InternalListArgument} with the initial value set to the given {@link List}.
     *
     * @param value A {@link List} of {@link InternalArgument}s this {@link InternalListArgument} should contain.
     */
    public InternalListArgument(List<InternalArgument<?>> value) {
        super(value);
    }

    @Override
    public void setValue(List<InternalArgument<?>> arg) {
        value = arg;
    }

    @Override
    public List<InternalArgument<?>> getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument<List<InternalArgument<?>>> arg) {
        value = arg.getValue();
    }

    @Override
    public String forCommand() {
        StringBuilder out = new StringBuilder("[");
        if (value.size() != 0) {
            for (InternalArgument<?> element : value) {
                out.append(element.forCommand());
                out.append(", ");
            }
            out.delete(out.length() - 2, out.length());
        }
        out.append("]");
        return out.toString();
    }

    @Override
    public InstanceFunctionList<List<InternalArgument<?>>> getInstanceFunctions() {
        return merge(super.getInstanceFunctions(),
                functions(
                        instanceFunction("add")
                                .withDescription("Adds an item to this list")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalArgument.any(), "item", "The item to add"))
                                        .executes((list, item) -> list.getValue().add(item))
                                )
                                .withExamples(
                                        "<list> = List.new() -> []",
                                        "do <list>.add(Integer.new(\"10\")) -> [10]",
                                        "do <list>.add(\"a\") -> [10, \"a\"]",
                                        "do <list>.add(Boolean.(\"true\")) -> [10, \"a\", true]"
                                ),
                        instanceFunction("addAll")
                                .withDescription("Adds multiple items to this list from another list")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalListArgument.class, "list", "A list of the items to add"))
                                        .executes((list, itemList) -> list.getValue().addAll(itemList.getValue()))
                                )
                                .withExamples(
                                        "<list1> has [\"a\", \"b\"], <list2> has [\"c\", \"d\"]",
                                        "do <list1>.addAll(<list2>)",
                                        "  <list1> now has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list2>.addAll(<list1>)",
                                        "  <list2> now has [\"c\",\"d\", \"a\", \"b\", \"c\", \"d\"]"
                                ),
                        instanceFunction("contains")
                                .withDescription("Checks if the list has the given item")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalArgument.any(), "item", "The item to look for"))
                                        .returns(InternalBooleanArgument.class, "True if the item is in the list, and false otherwise")
                                        .executes((list, item) -> new InternalBooleanArgument(list.getValue().contains(item)))
                                )
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\"]",
                                        "do <list>.contains(\"a\") -> True",
                                        "do <list>.contains(\"d\") -> False",
                                        "do <list>.contains(Integer.(\"10\")) -> False"
                                ),
                        instanceFunction("get")
                                .withDescription("Gets an item from the list")
                                .withExecutions(genericExecution(InternalIntegerArgument.any(), clazz -> InstanceExecution
                                        .withParameters(
                                                parameter(InternalIntegerArgument.class, "index", "The index of the element"),
                                                parameter(clazz, "class", "The class of the element, represented by an object")
                                        )
                                        .returns(clazz, "The object at the given index, with the class specified by the reference object")
                                        .executes((list, index, classObject) -> {
                                            InternalArgument<?> out;
                                            try {
                                                out = list.getValue().get(index.getValue());
                                            } catch (IndexOutOfBoundsException e) {
                                                throw new CommandRunException(e);
                                            }

                                            Class<? extends InternalArgument<Object>> classReference = classObject.myClass();
                                            if (!classReference.isAssignableFrom(out.getClass()))
                                                throw new CommandRunException(new ClassCastException("Cannot turn object with class " + out.getName() + " into a " + classObject.getName()));

                                            return classReference.cast(out);
                                        })
                                ))
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <list>.size()",
                                        "ClassCastException when the item found at the given index is not an instance of the reference class"
                                )
                                .withExamples(
                                        "<list> has [\"a\", 10]",
                                        "do <list>.get(Integer.(\"0\"), \"\") -> \"a\"",
                                        "do <list>.get(Integer.(\"1\"), Integer.()) -> 10",
                                        "do <list>.get(Integer.(\"3\"), \"\") -> IndexOutOfBounds",
                                        "do <list>.get(Integer.(\"0\"), Integer.()) -> ClassCastException"
                                ),
                        instanceFunction("indexOf")
                                .withDescription("Gives the index of an item in the list")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalArgument.any(), "item", "The item to look for"))
                                        .returns(InternalIntegerArgument.class, "The index of the given item in the list, or -1 if the list dose not have the item")
                                        .executes((list, item) -> new InternalIntegerArgument(list.getValue().indexOf(item)))
                                )
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\"]",
                                        "do <list>.indexOf(\"a\") -> 0",
                                        "do <list>.indexOf(\"c\") -> 2",
                                        "do <list>.indexOf(\"d\") -> -1"
                                ),
                        instanceFunction("lastIndexOf")
                                .withDescription("Gives the greatest index where the given item can be found")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalArgument.any(), "item", "The item to look for"))
                                        .returns(InternalIntegerArgument.class, "The greatest index of the given item in the list, or -1 if the list dose not have the item")
                                        .executes((list, item) -> new InternalIntegerArgument(list.getValue().lastIndexOf(item)))

                                )
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"b\", \"a\"]",
                                        "do <list>.lastIndexOf(\"a\") -> 3",
                                        "do <list>.lastIndexOf(\"b\") -> 2",
                                        "do <list>.lastIndexOf(\"d\") -> -1"
                                ),
                        instanceFunction("remove")
                                .withDescription("Removes an item from the list")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalIntegerArgument.class, "index", "The index to remove"))
                                        .executes((list, index) -> {
                                            try {
                                                list.getValue().remove((int) index.getValue());
                                            } catch (IndexOutOfBoundsException e) {
                                                throw new CommandRunException(e);
                                            }
                                        })
                                )
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <list>.size()"
                                )
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list>.remove(Integer.(\"0\"))",
                                        "  <list> now has [\"b\", \"c\", \"d\"]",
                                        "do <list>.remove(Integer.(\"1\"))",
                                        "  <list> now has [\"b\", \"d\"]",
                                        "do <list>.remove(Integer.(\"2\")) -> IndexOutOfBounds"
                                ),
                        instanceFunction("set")
                                .withDescription("Replaces the item at the given index with a new item")
                                .withExecutions(InstanceExecution
                                        .withParameters(
                                                parameter(InternalIntegerArgument.class, "index", "The index to put the new item at"),
                                                parameter(InternalArgument.any(), "item", "The item to add to the list")
                                        )
                                        .executes((target, index, item) -> {
                                            try {
                                                target.getValue().set(index.getValue(), item);
                                            } catch (IndexOutOfBoundsException e) {
                                                throw new CommandRunException(e);
                                            }
                                        })
                                )
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <list>.size()"
                                )
                                .withExamples(
                                        "<list> has [\"a\", \"b\", \"c\", \"d\"]",
                                        "do <list>.set(Integer.(\"1\"), \"Hello\")",
                                        "  <list> now has [\"a\", \"Hello\", \"c\", \"d\"]",
                                        "do <list>.set(Integer.(\"2\"), Integer.(\"10\"))",
                                        "  <list> now has [\"a\", \"Hello\", 10, \"d\"]",
                                        "do <list>.set(Integer.(\"4\"), \"\") -> IndexOutOfBounds"
                                ),
                        instanceFunction("size")
                                .withDescription("Gives the size of the list")
                                .withExecutions(InstanceExecution
                                        .returns(InternalIntegerArgument.class, "The number of elements currently in the list")
                                        .executes(list -> new InternalIntegerArgument(list.getValue().size()))
                                )
                                .withExamples(
                                        "<list> has [\"a\", \"b\"]",
                                        "do <list>.size() -> 2",
                                        "do <list>.add(\"c\")",
                                        "  <list> now has [\"a\", \"b\", \"c\"]",
                                        "do <list>.size() -> 3"
                                ),
                        instanceFunction("subList")
                                .withDescription("Creates a new list with the items from a section of this list")
                                .withExecutions(InstanceExecution
                                        .withParameters(
                                                parameter(InternalIntegerArgument.class, "start", "The index of the first element to include"),
                                                parameter(InternalIntegerArgument.class, "end", "The index just after the last element to include")
                                        )
                                        .returns(InternalListArgument.class, "A new list with the elements of this list between start and end")
                                        .executes((list, start, end) -> {
                                            try {
                                                return new InternalListArgument(list.getValue().subList(start.getValue(), end.getValue()));
                                            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                                                throw new CommandRunException(e);
                                            }
                                        })
                                )
                                .throwsException(
                                        "IndexOutOfBoundsException when start < 0 or end > <list>.size()",
                                        "IllegalArgumentException when start > end"
                                )
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
                        staticFunction("new")
                                .withAliases("")
                                .withDescription("Creates a new list with no elements")
                                .withExecutions(StaticExecution
                                        .returns(InternalListArgument.class)
                                        .executes(() -> new InternalListArgument(new ArrayList<>()))
                                )
                                .withExamples(
                                        "List.new() -> []",
                                        "List.() -> []"
                                )
                )
        );
    }
}
