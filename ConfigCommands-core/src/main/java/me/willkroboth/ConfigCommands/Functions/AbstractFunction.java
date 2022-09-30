package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractFunction<T extends AbstractFunction<T>> {
    // Cosmetic information
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private String description = null;
    private String returnMessage = null;
    private final List<String> throwMessages = new ArrayList<>();
    private final List<String> examples = new ArrayList<>();

    // Input-output information
    private final List<Parameter[]> parameters = new ArrayList<>();
    private Function<List<Class<? extends InternalArgument>>, Class<? extends InternalArgument>> returnTypeFunction;

    // Building instance
    private final T instance;

    // Set information
    @SuppressWarnings("unchecked")
    public AbstractFunction(String name) {
        this.name = name;
        this.instance = (T) this;
    }

    public T withAliases(String... aliases) {
        this.aliases.addAll(List.of(aliases));

        return instance;
    }

    public T withDescription(String description) {
        this.description = description;

        return instance;
    }

    public T withParameters(Parameter... parameters) {
        this.parameters.add(parameters);

        return instance;
    }

    public T returns(Class<? extends InternalArgument> clazz) {
        this.returnTypeFunction = (parameters) -> clazz;

        return instance;
    }

    public T returns(Class<? extends InternalArgument> clazz, String message) {
        this.returnTypeFunction = (parameters) -> clazz;
        this.returnMessage = message;

        return instance;
    }

    public T returns(Function<List<Class<? extends InternalArgument>>, Class<? extends InternalArgument>> classFunction) {
        this.returnTypeFunction = classFunction;

        return instance;
    }

    public T returns(Function<List<Class<? extends InternalArgument>>, Class<? extends InternalArgument>> classFunction, String message) {
        this.returnTypeFunction = classFunction;
        this.returnMessage = message;

        return instance;
    }

    public T withThrowMessages(String... messages) {
        this.throwMessages.addAll(List.of(messages));

        return instance;
    }

    public T withExamples(String... examples) {
        this.examples.addAll(List.of(examples));

        return instance;
    }

    // Use information
    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<Parameter[]> getParameters() {
        return parameters;
    }

    public Class<? extends InternalArgument> getReturnType(List<Class<? extends InternalArgument>> parameterTypes) {
        return returnTypeFunction.apply(parameterTypes);
    }

    public void outputInformation(CommandSender sender) {
        sender.sendMessage("Function: " + name);

        if (aliases.size() != 0) {
            sender.sendMessage("Aliases:");
            for (String alias : aliases) {
                sender.sendMessage("  - " + alias);
            }
        }

        if(description != null) sender.sendMessage("Description: " + description);

        if(parameters.size() == 0) {
            sender.sendMessage("No parameters");
        } else if(parameters.size() == 1) {
            sender.sendMessage("Parameters:");
            for(Parameter parameter : parameters.get(0)) {
                sender.sendMessage("  - " + parameter);
            }
        } else {
            sender.sendMessage("Multiple input combinations available:");
            for(Parameter[] parameterArray : parameters) {
                sender.sendMessage("  Parameters:");
                for(Parameter parameter : parameterArray) {
                    sender.sendMessage("    - " + parameter);
                }
            }
        }

        if(returnMessage == null) {
            sender.sendMessage("Returns: " + InternalArgument.getNameForType(returnTypeFunction.apply(List.of())));
        } else {
            sender.sendMessage("Returns: " + InternalArgument.getNameForType(returnTypeFunction.apply(List.of())) + " - " + returnMessage);
        }

        if(throwMessages.size() != 0) {
            sender.sendMessage("Throws:");
            for(String throwMessage : throwMessages) {
                sender.sendMessage("  - " + throwMessage);
            }
        }

        if(examples.size() != 0) {
            sender.sendMessage("Examples:");
            for(String example : examples) {
                sender.sendMessage("  - " + example);
            }
        }
    }
}
