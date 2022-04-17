package me.willkroboth.ConfigCommands.Functions.NonGenericVarargs;

import java.util.ArrayList;
import java.util.List;

public class NestedArgList extends ArrayList<ArgList> {
    public NestedArgList(ArgList... args) {
        super(List.of(args));
    }
}
