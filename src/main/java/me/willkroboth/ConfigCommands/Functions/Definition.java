package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.ArgList;

public class Definition {
    private final String name;
    private final ArgList parameters;

    public Definition(String name, ArgList parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Definition{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    public String getName() {
         return name;
    }

    public ArgList getParameters(){
        return parameters;
    }

    // https://dzone.com/articles/things-to-keep-in-mind-while-using-custom-classes
    // equals and hashcode required for usage in a HashMap
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Definition def = (Definition) o;
        if (!name.equals(def.name)) return false;
        return parameters.equals(def.parameters);
    }

    public int hashCode() {
        int result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }
}
