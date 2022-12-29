package me.willkroboth.ConfigCommands.Functions;

import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expressions.Expression;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A class for building most everything about a function that can be called in {@link Expression}s.
 * This is subclassed by {@link InstanceFunction} and {@link StaticFunction}, which specifies the
 * context of when a function can be run and add the ability to define code for the function to run.
 *
 * @param <Impl> The subclass that gets returned when chaining methods in the build process.
 */
public abstract class FunctionBuilder<Impl extends FunctionBuilder<Impl>> {
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
    private final Impl instance;

    // Set information

    /**
     * Creates a new {@link FunctionBuilder} with the given name.
     *
     * @param name The name used to call this function.
     */
    @SuppressWarnings("unchecked")
    public FunctionBuilder(String name) {
        this.name = name;
        this.instance = (Impl) this;
    }

    /**
     * Adds aliases to this function.
     *
     * @param aliases An array of Strings that can also be used to call this function.
     * @return The current function builder.
     */
    public Impl withAliases(String... aliases) {
        this.aliases.addAll(List.of(aliases));

        return instance;
    }

    /**
     * Adds a description to this function.
     *
     * @param description A String that describes what this function does.
     * @return The current function builder.
     */
    public Impl withDescription(String description) {
        this.description = description;

        return instance;
    }

    /**
     * Adds parameters to this function. If this method is called again,
     * both ways of calling the method will work, on for as many overloads are wanted.
     *
     * @param parameters An array of {@link Parameter} objects that define
     *                   what needs to be passed in to run this function.
     * @return The current function builder.
     */
    public Impl withParameters(Parameter... parameters) {
        this.parameters.add(parameters);

        return instance;
    }

    /**
     * Sets the class this function returns when run.
     *
     * @param clazz An {@link InternalArgument} class object that this function should return when run.
     * @return The current function builder.
     */
    public Impl returns(Class<? extends InternalArgument> clazz) {
        this.returnTypeFunction = (parameters) -> clazz;

        return instance;
    }

    /**
     * Sets the class this function returns when run and gives a message that describes the returned value.
     *
     * @param clazz   An {@link InternalArgument} class object that this function should return when run
     * @param message A String that describes what this function returns.
     * @return The current function builder.
     */
    public Impl returns(Class<? extends InternalArgument> clazz, String message) {
        this.returnTypeFunction = (parameters) -> clazz;
        this.returnMessage = message;

        return instance;
    }

    /**
     * Gives a function that determines what class this function returns based on the parameters
     * passed in. This can be used to make different parameters have a different return class.
     *
     * @param classFunction A function that inputs a list of {@link InternalArgument} class objects
     *                      and returns a single {@link InternalArgument} class object this function
     *                      should return when parameters with the classes defined by the list are input.
     *                      This function should be defined for all lists of parameters give to this function
     *                      using {@link FunctionBuilder#withParameters(Parameter...)}, as well
     *                      as an empty list which is used when determining the class to display
     *                      when the function's help is requested.
     * @return The current function builder.
     */
    public Impl returns(Function<List<Class<? extends InternalArgument>>, Class<? extends InternalArgument>> classFunction) {
        this.returnTypeFunction = classFunction;

        return instance;
    }

    /**
     * Gives a function that determines what class this function returns based on the parameters
     * passed in and a message that describes the returned value. This can be used to make
     * different parameters have a different return class.
     *
     * @param classFunction A function that inputs a list of {@link InternalArgument} class objects
     *                      and returns a single {@link InternalArgument} class object this function
     *                      should return when parameters with the classes defined by the list are input.
     *                      This function should be defined for all lists of parameters give to this function
     *                      using {@link FunctionBuilder#withParameters(Parameter...)}, as well
     *                      as an empty list which is used when determining the class to display
     *                      when the function's help is requested.
     * @param message       A String that describes what this function returns.
     * @return The current function builder.
     */
    public Impl returns(Function<List<Class<? extends InternalArgument>>, Class<? extends InternalArgument>> classFunction, String message) {
        this.returnTypeFunction = classFunction;
        this.returnMessage = message;

        return instance;
    }

    /**
     * Adds messages to this function that describe what kinds of exceptions it throws.
     *
     * @param messages An array of Strings that describe the exceptions this method throws.
     * @return The current function builder.
     */
    public Impl throwsException(String... messages) {
        this.throwMessages.addAll(List.of(messages));

        return instance;
    }

    /**
     * Adds messages to this function that give examples for using this function.
     *
     * @param examples An array of Strings that explain how to use this function.
     * @return The current function builder.
     */
    public Impl withExamples(String... examples) {
        this.examples.addAll(List.of(examples));

        return instance;
    }

    // Use information

    /**
     * @return The name assigned to this function when it was constructed.
     * See {@link FunctionBuilder#FunctionBuilder(String)}.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The aliases assigned to this function by {@link FunctionBuilder#withAliases(String...)}.
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * @return A List of the different {@link Parameter} combinations that can be used to call this function.
     */
    public List<Parameter[]> getParameters() {
        return parameters;
    }

    /**
     * Gives the class that this function returns when called using the given classes.
     *
     * @param parameterTypes A list of {@link InternalArgument} class objects that
     *                       represent the type of objects being passed to this function.
     * @return A {@link InternalArgument} class object that this function returns when
     * given objects with the classes given in the {@code parameterTypes} list
     */
    public Class<? extends InternalArgument> getReturnType(List<Class<? extends InternalArgument>> parameterTypes) {
        return returnTypeFunction.apply(parameterTypes);
    }

    /**
     * Sends the help information for this function with the format:
     * <pre>
     * {@code Function: name} (Set by {@link FunctionBuilder#FunctionBuilder(String)})
     * {@code Aliases:} (Set by {@link FunctionBuilder#withAliases(String...)})
     * {@code
     *   - alias1
     *   - alias2
     *   - ...
     * Description: description} (Set by {@link FunctionBuilder#withDescription(String)})
     * {@code Multiple input combinations available:
     *   No parameters} (Set by a call to {@link FunctionBuilder#withParameters(Parameter...)} with no parameters)
     *   {@code Parameters:} (Set by a call to {@link FunctionBuilder#withParameters(Parameter...)} with the following parameters)
     *     {@code - parameter1} (See {@link Parameter#toString()})
     *     {@code - parameter2
     * - parameter3
     * - ...
     * }
     * {@code Returns: returnClass - returnMessage} (Set by {@link FunctionBuilder#returns(Class, String)})
     * {@code Throws:} (Set by {@link FunctionBuilder#throwsException(String...)})
     * {@code   - message1
     *   - message2
     *   - ...
     * Examples:} (Set by {@link FunctionBuilder#withExamples(String...)})
     * {@code   - message1
     *   - message2
     *   - ...
     * }</pre>
     *
     * @param sender The {@link CommandSender} to send the help message to.
     */
    public void outputInformation(CommandSender sender) {
        sender.sendMessage("Function: " + name);

        if (aliases.size() != 0) {
            sender.sendMessage("Aliases:");
            for (String alias : aliases) {
                sender.sendMessage("  - " + alias);
            }
        }

        if (description != null) sender.sendMessage("Description: " + description);

        if (parameters.size() == 0) {
            sender.sendMessage("No parameters");
        } else if (parameters.size() == 1) {
            sender.sendMessage("Parameters:");
            for (Parameter parameter : parameters.get(0)) {
                sender.sendMessage("  - " + parameter);
            }
        } else {
            sender.sendMessage("Multiple input combinations available:");
            for (Parameter[] parameterArray : parameters) {
                if (parameterArray.length == 0) {
                    sender.sendMessage("  No parameters");
                    continue;
                }
                sender.sendMessage("  Parameters:");
                for (Parameter parameter : parameterArray) {
                    sender.sendMessage("    - " + parameter);
                }
            }
        }

        if (returnMessage == null) {
            sender.sendMessage("Returns: " + InternalArgument.getNameForType(returnTypeFunction.apply(List.of())));
        } else {
            sender.sendMessage("Returns: " + InternalArgument.getNameForType(returnTypeFunction.apply(List.of())) + " - " + returnMessage);
        }

        if (throwMessages.size() != 0) {
            sender.sendMessage("Throws:");
            for (String throwMessage : throwMessages) {
                sender.sendMessage("  - " + throwMessage);
            }
        }

        if (examples.size() != 0) {
            sender.sendMessage("Examples:");
            for (String example : examples) {
                sender.sendMessage("  - " + example);
            }
        }
    }
}
