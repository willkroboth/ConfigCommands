package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.StaticFunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.List;

public class InternalIntegerArgument extends InternalArgument {
    private int value;

    public InternalIntegerArgument() {
    }

    public InternalIntegerArgument(int value) {
        super(value);
    }

    @Override
    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        if(argumentInfo != null) {
            ConfigurationSection info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, name);
            min = info.getInt("min", Integer.MIN_VALUE);
            max = info.getInt("max", Integer.MAX_VALUE);
        }
        ConfigCommandsHandler.logDebug(localDebug, "Arg has min: %s, max: %s", min, max);
        return new IntegerArgument(name, min, max);
    }

    @Override
    public StaticFunctionList getStaticFunctions() {
        return staticMerge(
                super.getStaticFunctions(),
                staticExpandDefinition(strings("", "new"), args(args(), args(InternalStringArgument.class)),
                        new StaticFunction(this::initialize, InternalIntegerArgument.class)
                )
        );
    }

    public InternalArgument initialize(List<InternalArgument> arguments) {
        int result = 0;
        if (arguments.size() == 1) {
            InternalStringArgument arg = (InternalStringArgument) arguments.get(0);
            String word = (String) arg.getValue();
            try {
                result = Integer.parseInt(word);
            } catch (NumberFormatException e) {
                throw new CommandRunException("Word: \"" + word + "\" cannot be parsed as int.");
            }
        }

        return new InternalIntegerArgument(result);
    }

    // value
    @Override
    public void setValue(Object arg) {
        value = (int) arg;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = (int) arg.getValue();
    }

    @Override
    public String forCommand() {
        return "" + value;
    }
}
