package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

public class Parameter {
    // Input type
    private final Class<? extends InternalArgument> type;

    // Cosmetic information
    private final String typeString;
    private String name = null;
    private String parameterMessage = null;

    // Set information
    public Parameter(Class<? extends InternalArgument> type) {
        this.type = type;
        this.typeString = InternalArgument.getNameForType(type);
    }

    public Parameter(Class<? extends InternalArgument> type, String name) {
        this.type = type;
        this.typeString = InternalArgument.getNameForType(type);
        this.name = name;
    }

    public Parameter(Class<? extends InternalArgument> type, String name, String parameterMessage) {
        this.type = type;
        this.typeString = InternalArgument.getNameForType(type);
        this.name = name;
        this.parameterMessage = parameterMessage;
    }

    // Get information
    public Class<? extends InternalArgument> getType() {
        return type;
    }

    @Override
    public String toString() {
        if (name == null) return typeString;
        if (parameterMessage == null) return typeString + " " + name;
        return typeString + " " + name + " -> " + parameterMessage;
    }
}
