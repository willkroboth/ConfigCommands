package me.willkroboth.configcommands.internalarguments;

import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.functions.InstanceFunctionList;
import me.willkroboth.configcommands.nms.OpSender;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * An {@link InternalArgument} that represents a Bukkit {@link CommandSender}.
 */
public class InternalCommandSenderArgument extends InternalArgument<CommandSender> {
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
    public void setValue(CommandSender arg) {
        value = arg;
        opSender = OpSender.makeOpSender(value);
    }

    @Override
    public CommandSender getValue() {
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
    public void setValue(InternalArgument<CommandSender> arg) {
        value = arg.getValue();
        opSender = OpSender.makeOpSender(value);
    }

    @Override
    public String forCommand() {
        return value.getName();
    }

    @Override
    public InstanceFunctionList<CommandSender> getInstanceFunctions() {
        return merge(super.getInstanceFunctions(),
                functions(
                        instanceFunction("dispatchCommand")
                                .withDescription("Runs a command using this CommandSender")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "command", "The command to run"))
                                        .returns(InternalStringArgument.class, "The result of the command")
                                        .executes((sender, command) -> {
                                            OpSender targetOpSender;
                                            if (sender instanceof InternalCommandSenderArgument iCSA)
                                                targetOpSender = iCSA.getOpSender();
                                            else
                                                targetOpSender = OpSender.makeOpSender(sender.getValue());

                                            try {
                                                // Note: this used to say `targetOpSender.getServer().dispatchCommand...`
                                                //  However, that didn't work for the ConsoleOpSender because it is manages
                                                //  its own server that holds an independent CommandMap that doesn't have
                                                //  commands registered to it, causing command calls to be not found.
                                                //  Using Bukkit.getServer() instead is probably fine.
                                                Bukkit.getServer().dispatchCommand(targetOpSender, command.getValue());
                                            } catch (CommandException e) {
                                                throw new CommandRunException(e);
                                            }
                                            return new InternalStringArgument(targetOpSender.getResult());
                                        })
                                )
                                .throwsException(
                                        "CommandRunException when a problem occurs while running the command"
                                )
                                .withExamples(
                                        "do <sender>.dispatchCommand(\"say Hello\") -> \"\"",
                                        "do <sender>.dispatchCommand(\"tp willkroboth 0 100 0\") -> \"Teleported willkroboth to 0.5 100 0.5\"",
                                        "do <sender>.dispatchCommand(\"echo Hello\") -> \"Hello\""
                                ),
                        instanceFunction("getName")
                                .withExecutions(InstanceExecution
                                        .returns(InternalStringArgument.class, "The name used to refer to this CommandSender")
                                        .executes(sender -> new InternalStringArgument(sender.getValue().getName()))
                                ),
                        instanceFunction("getType")
                                .withExecutions(InstanceExecution
                                        .returns(InternalStringArgument.class, "The type of this CommandSender, either " +
                                                "\"player\", \"entity\", \"commandBlock\", \"console\", or \"proxy\"")
                                        .executes(sender -> {
                                            CommandSender targetCommandSender = sender.getValue();

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
                                        })
                                )
                                .throwsException(
                                        "Exception when this CommandSender doesn't seem to be one of the above types"
                                ),
                        instanceFunction("hasPermission")
                                .withDescription("Checks if this CommandSender has a certain permission")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "permission", "The permission to check for"))
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender has the given permission, and false otherwise")
                                        .executes((sender, permission) -> new InternalBooleanArgument(sender.getValue().hasPermission(permission.getValue())))
                                ),
                        instanceFunction("isCommandBlock")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender is a command block, and false otherwise")
                                        .executes(sender -> new InternalBooleanArgument(sender.getValue() instanceof BlockCommandSender))
                                ),
                        instanceFunction("isConsole")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender is the server console, and false otherwise")
                                        .executes(sender -> new InternalBooleanArgument(sender.getValue() instanceof ConsoleCommandSender))
                                ),
                        instanceFunction("isEntity")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender is an entity, and false otherwise")
                                        .executes(sender -> new InternalBooleanArgument(sender.getValue() instanceof Entity))
                                ),
                        instanceFunction("isOp")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender has operator status, and false otherwise")
                                        .executes(sender -> new InternalBooleanArgument(sender.getValue().isOp()))
                                ),
                        instanceFunction("isPlayer")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender is a player, and false otherwise")
                                        .executes(sender -> new InternalBooleanArgument(sender.getValue() instanceof Player))
                                ),
                        instanceFunction("isProxy")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if this CommandSender is a proxy, and false otherwise")
                                        .executes(sender -> new InternalBooleanArgument(sender.getValue() instanceof ProxiedCommandSender))
                                ),
                        instanceFunction("sendMessage")
                                .withDescription("Sends a message to this CommandSender")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "message", "the message to send"))
                                        .executes((sender, message) -> sender.getValue().sendMessage(message.getValue()))
                                )
                                .withExamples(
                                        "do <sender>.sendMessage(\"Hello!\")"
                                ),
                        instanceFunction("setOp")
                                .withDescription("Sets this CommandSender's operator status to the given Boolean")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalBooleanArgument.class, "new status"))
                                        .executes((sender, status) -> sender.getValue().setOp(status.getValue()))
                                )
                )
        );
    }
}
