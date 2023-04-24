package me.willkroboth.configcommands.internalarguments;

import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.functions.InstanceFunction;
import me.willkroboth.configcommands.functions.InstanceFunctionList;
import me.willkroboth.configcommands.functions.Parameter;
import me.willkroboth.configcommands.nms.OpSender;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * An {@link InternalArgument} that represents a Bukkit {@link CommandSender}.
 */
public class InternalCommandSenderArgument extends InternalArgument {
    private CommandSender value;
    private OpSender opSender;

    /**
     * Creates a new {@link InternalCommandSenderArgument} with no initial value set.
     */
    public InternalCommandSenderArgument() {
    }

    /**
     * Creates a new {@link InternalCommandSenderArgument} with the initial value set to the given {@link CommandSender}.
     *
     * @param value The initial {@link CommandSender} value for this {@link InternalCommandSenderArgument}.
     */
    public InternalCommandSenderArgument(CommandSender value) {
        super(value);
    }

    /**
     * Sets the internal value of this {@link InternalCommandSenderArgument} to the given {@link CommandSender}.
     * This also creates an {@link OpSender} wrapping the given {@link CommandSender} using
     * {@link OpSender#makeOpSender(CommandSender)}, which can be accessed using
     * {@link InternalCommandSenderArgument#getOpSender()}.
     *
     * @param arg The new value for this {@link InternalCommandSenderArgument}.
     */
    @Override
    public void setValue(Object arg) {
        value = (CommandSender) arg;
        opSender = OpSender.makeOpSender(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    /**
     * @return The current {@link OpSender} held by this {@link InternalCommandSenderArgument}.
     */
    public OpSender getOpSender() {
        return opSender;
    }

    /**
     * Sets the internal value of this {@link InternalCommandSenderArgument} to the given {@link CommandSender}
     * held by the given {@link InternalArgument}.
     * This also creates an {@link OpSender} wrapping the given {@link CommandSender} using
     * {@link OpSender#makeOpSender(CommandSender)}, which can be accessed using
     * {@link InternalCommandSenderArgument#getOpSender()}.
     *
     * @param arg The {@link InternalArgument} holding the new value for this {@link InternalCommandSenderArgument}.
     */
    @Override
    public void setValue(InternalArgument arg) {
        value = (CommandSender) arg.getValue();
        opSender = OpSender.makeOpSender(value);
    }

    @Override
    public String forCommand() {
        return value.getName();
    }

    private CommandSender getCommandSender(InternalArgument target) {
        return (CommandSender) target.getValue();
    }

    @Override
    public InstanceFunctionList getInstanceFunctions() {
        return merge(super.getInstanceFunctions(),
                functions(
                        new InstanceFunction("dispatchCommand")
                                .withDescription("Runs a command using this CommandSender")
                                .withParameters(new Parameter(InternalStringArgument.class, "command", "The command to run"))
                                .returns(InternalStringArgument.class, "The result of the command")
                                .throwsException(
                                        "CommandRunException when a problem occurs while running the command"
                                )
                                .executes((target, parameters) -> {
                                    String command = (String) parameters.get(0).getValue();

                                    OpSender targetOpSender = ((InternalCommandSenderArgument) target).getOpSender();
                                    try {
                                        // Note: this used to say `targetOpSender.getServer().dispatchCommand...`
                                        //  However, that didn't work for the ConsoleOpSender because it is a singleton
                                        //  and holds an independent CommandMap that dosen't have commands registered to
                                        //  it, causing command calls to be not found. Using Bukkit.getServer() is
                                        //  instead is probably fine.
                                        Bukkit.getServer().dispatchCommand(targetOpSender, command);
                                    } catch (CommandException e) {
                                        throw new CommandRunException(e);
                                    }
                                    return new InternalStringArgument(targetOpSender.getResult());
                                })
                                .withExamples(
                                        "do <sender>.dispatchCommand(\"say Hello\") -> \"\"",
                                        "do <sender>.dispatchCommand(\"tp willkroboth 0 100 0\") -> \"Teleported willkroboth to 0.5 100 0.5\"",
                                        "do <sender>.dispatchCommand(\"echo Hello\") -> \"Hello\""
                                ),
                        new InstanceFunction("getName")
                                .returns(InternalStringArgument.class, "The name used to refer to this CommandSender")
                                .executes((target, parameters) -> {
                                    return new InternalStringArgument(getCommandSender(target).getName());
                                }),
                        new InstanceFunction("getType")
                                .returns(InternalStringArgument.class, "The type of this CommandSender, either " +
                                        "\"player\", \"entity\", \"commandBlock\", \"console\", or \"proxy\"")
                                .throwsException(
                                        "Exception when this CommandSender doesn't seem to be one of the above types"
                                )
                                .executes((target, parameters) -> {
                                    CommandSender targetCommandSender = getCommandSender(target);

                                    if (targetCommandSender instanceof OpSender op)
                                        targetCommandSender = op.getSender();

                                    String result = "";
                                    if (targetCommandSender instanceof Player)
                                        result = "player";
                                    else if (targetCommandSender instanceof Entity)
                                        result = "entity";
                                    else if (targetCommandSender instanceof BlockCommandSender)
                                        result = "commandBlock";
                                    else if (targetCommandSender instanceof ConsoleCommandSender)
                                        result = "console";
                                    else if (targetCommandSender instanceof ProxiedCommandSender)
                                        result = "proxy";

                                    if (result.equals(""))
                                        throw new CommandRunException("CommandSender " + targetCommandSender + " is of unknown class " + targetCommandSender.getClass());

                                    return new InternalStringArgument(result);
                                }),
                        new InstanceFunction("hasPermission")
                                .withDescription("Checks if this CommandSender has a certain permission")
                                .withParameters(new Parameter(InternalStringArgument.class, "permission", "The permission to check for"))
                                .returns(InternalBooleanArgument.class, "True if this CommandSender has the given permission, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target).hasPermission((String) parameters.get(0).getValue()));
                                }),
                        new InstanceFunction("isCommandBlock")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is a command block, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof BlockCommandSender);
                                }),
                        new InstanceFunction("isConsole")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is the server console, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof ConsoleCommandSender);
                                }),
                        new InstanceFunction("isEntity")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is an entity, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof Entity);
                                }),
                        new InstanceFunction("isOp")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender has operator status, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target).isOp());
                                }),
                        new InstanceFunction("isPlayer")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is a player, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof Player);
                                }),
                        new InstanceFunction("isProxy")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is a proxy, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof ProxiedCommandSender);
                                }),
                        new InstanceFunction("sendMessage")
                                .withDescription("Sends a message to this CommandSender")
                                .withParameters(new Parameter(InternalStringArgument.class, "message", "the message to send"))
                                .returns(InternalVoidArgument.class)
                                .executes((target, parameters) -> {
                                    getCommandSender(target).sendMessage((String) parameters.get(0).getValue());
                                })
                                .withExamples(
                                        "do <sender>.sendMessage(\"Hello!\")"
                                ),
                        new InstanceFunction("setOp")
                                .withDescription("Sets this CommandSender's operator status to the given Boolean")
                                .withParameters(new Parameter(InternalBooleanArgument.class, "new status"))
                                .returns(InternalVoidArgument.class)
                                .executes((target, parameters) -> {
                                    getCommandSender(target).setOp((boolean) parameters.get(0).getValue());
                                })
                )
        );
    }
}
