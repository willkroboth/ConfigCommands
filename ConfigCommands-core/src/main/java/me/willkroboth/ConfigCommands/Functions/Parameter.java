package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

/**
 * A class that represents a parameter of a function, used for {@link FunctionBuilder#withParameters(Parameter...)}.
 */
public class Parameter {
    // Input type
    private final Class<? extends InternalArgument> type;

    // Cosmetic information
    private final String typeString;
    private String name = null;
    private String parameterMessage = null;

    // Set information

    /**
     * Creates a new {@link Parameter} with the given type.
     *
     * @param type The {@link InternalArgument} class that this {@link Parameter} accepts, as well as all subclasses.
     */
    public Parameter(Class<? extends InternalArgument> type) {
        this.type = type;
        this.typeString = InternalArgument.getNameForType(type);
    }

    /**
     * Creates a new {@link Parameter} with the given type and name.
     *
     * @param type The {@link InternalArgument} class that this {@link Parameter} accepts, as well as all subclasses.
     * @param name A string that describes the name of this parameter, useful as a reference when describing what the
     *             function that includes this {@link Parameter} dose.
     */
    public Parameter(Class<? extends InternalArgument> type, String name) {
        this.type = type;
        this.typeString = InternalArgument.getNameForType(type);
        this.name = name;
    }

    /**
     * Creates a new {@link Parameter} with the given type, name, and message.
     *
     * @param type             The {@link InternalArgument} class that this {@link Parameter} accepts, as well as all subclasses.
     * @param name             A string that describes the name of this parameter, useful as a reference when describing what the
     *                         function that includes this {@link Parameter} dose.
     * @param parameterMessage A message that describes this {@link Parameter}.
     */
    public Parameter(Class<? extends InternalArgument> type, String name, String parameterMessage) {
        this.type = type;
        this.typeString = InternalArgument.getNameForType(type);
        this.name = name;
        this.parameterMessage = parameterMessage;
    }

    // Get information

    /**
     * @return The {@link InternalArgument} class that this {@link Parameter} accepts. This {@link Parameter}
     * will accept this class and any subclass.
     */
    public Class<? extends InternalArgument> getType() {
        return type;
    }

    /**
     * @return A String representing this {@link Parameter}. There are three ways this String can be formatted,
     * depending upon how much information was given when the {@link Parameter} was constructed. These are:
     * <ul>
     *     <li>[type]</li>
     *     <li>[type] [name]</li>
     *     <li>[type] [name] -> [parameterMessage]</li>
     * </ul>
     */
    @Override
    public String toString() {
        if (name == null) return typeString;
        if (parameterMessage == null) return typeString + " " + name;
        return typeString + " " + name + " -> " + parameterMessage;
    }
}
