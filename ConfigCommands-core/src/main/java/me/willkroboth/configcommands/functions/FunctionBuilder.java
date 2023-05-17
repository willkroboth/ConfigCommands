package me.willkroboth.configcommands.functions;

import me.willkroboth.configcommands.functions.executions.Execution;
import me.willkroboth.configcommands.functions.executions.Signature;
import me.willkroboth.configcommands.functions.executions.SignatureMerge;
import me.willkroboth.configcommands.functions.executions.SignatureEmpty;
import me.willkroboth.configcommands.helperclasses.IndentedCommandSenderMessenger;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for building most everything about a function that can be called in {@link Expression}s.
 * This is subclassed by {@link InstanceFunction} and {@link StaticFunction}, which specifies the
 * context of when a function can be run and add the ability to define code for the function to run.
 *
 * @param <Impl> The subclass that gets returned when chaining methods in the build process.
 */
public abstract class FunctionBuilder<E extends Execution<?, ?>, Impl extends FunctionBuilder<E, Impl>> {
    // Cosmetic information
    private final String name;
    private final List<String> aliases = new ArrayList<>();
    private String description = null;
    private final List<String> throwMessages = new ArrayList<>();
    private final List<String> examples = new ArrayList<>();

    // In-out information
    private Signature<E> executions = new SignatureEmpty<>();

    // Building instance
    private final Impl instance;

    // Set information

    /**
     * Creates a new {@link FunctionBuilder} with the given name.
     *
     * @param name The name used to call this function.
     */
    @SuppressWarnings("unchecked")
    protected FunctionBuilder(String name) {
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

    @SafeVarargs
    public final Impl withExecutions(Signature<? extends E>... executions) {
        this.executions = SignatureMerge.merge(this.executions, executions);

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

    public E findExecution(List<Class<? extends InternalArgument<?>>> parameterTypes) {
        return executions.findExecution(parameterTypes);
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
        IndentedCommandSenderMessenger messenger = new IndentedCommandSenderMessenger(sender);

        messenger.sendMessage("Function: " + name);

        if (aliases.size() != 0) {
            messenger.sendMessage("Aliases:");
            messenger.increaseIndentation();
            for (String alias : aliases) {
                sender.sendMessage("- " + alias);
            }
            messenger.decreaseIndentation();
        }

        if (description != null) messenger.sendMessage("Description: " + description);

        executions.printExecutions(messenger);

        if (throwMessages.size() != 0) {
            sender.sendMessage("Throws:");
            messenger.increaseIndentation();
            for (String throwMessage : throwMessages) {
                sender.sendMessage("- " + throwMessage);
            }
            messenger.decreaseIndentation();
        }

        if (examples.size() != 0) {
            sender.sendMessage("Examples:");
            messenger.increaseIndentation();
            for (String example : examples) {
                sender.sendMessage("- " + example);
            }
            messenger.decreaseIndentation();
        }
    }
}
