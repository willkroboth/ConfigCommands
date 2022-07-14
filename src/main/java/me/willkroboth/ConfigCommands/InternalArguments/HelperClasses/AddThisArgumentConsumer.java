package me.willkroboth.ConfigCommands.InternalArguments.HelperClasses;

import dev.jorel.commandapi.CommandAPICommand;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface AddThisArgumentConsumer {
    void add(Map<?, ?> arg, CommandAPICommand command, String name,
             ArrayList<String> argument_keys,
             HashMap<String, Class<? extends InternalArgument>> argument_variable_classes,
             boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey;
}
