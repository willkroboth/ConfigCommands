package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionList;
import me.willkroboth.ConfigCommands.Functions.Parameter;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InternalCommandSenderArgument extends InternalArgument {
    private CommandSender value;
    private OpSender opSender;

    public InternalCommandSenderArgument() {
    }

    public InternalCommandSenderArgument(CommandSender value) {
        super(value);
    }

    public String getTypeTag() {
        return null;
    }

    public void setValue(Object arg) {
        value = (CommandSender) arg;
        opSender = OpSender.makeOpSender(value);
    }

    public Object getValue() {
        return value;
    }

    public OpSender getOpSender() {
        return opSender;
    }

    public void setValue(InternalArgument arg) {
        value = (CommandSender) arg.getValue();
        opSender = OpSender.makeOpSender(value);
    }

    public String forCommand() {
        return value.getName();
    }

    private CommandSender getCommandSender(InternalArgument target) {
        return (CommandSender) target.getValue();
    }

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                functions(
                        new Function("dispatchCommand")
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
                                        targetOpSender.getServer().dispatchCommand(targetOpSender, command);
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
                        new Function("getName")
                                .returns(InternalStringArgument.class, "The name used to refer to this CommandSender")
                                .executes((target, parameters) -> {
                                    return new InternalStringArgument(getCommandSender(target).getName());
                                }),
                        new Function("getType")
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
                        new Function("hasPermission")
                                .withDescription("Checks if this CommandSender has a certain permission")
                                .withParameters(new Parameter(InternalStringArgument.class, "permission", "The permission to check for"))
                                .returns(InternalBooleanArgument.class, "True if this CommandSender has the given permission, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target).hasPermission((String) parameters.get(0).getValue()));
                                }),
                        new Function("isCommandBlock")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is a command block, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof BlockCommandSender);
                                }),
                        new Function("isConsole")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is the server console, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof ConsoleCommandSender);
                                }),
                        new Function("isEntity")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is an entity, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof Entity);
                                }),
                        new Function("isOp")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender has operator status, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target).isOp());
                                }),
                        new Function("isPlayer")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is a player, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof Player);
                                }),
                        new Function("isProxy")
                                .returns(InternalBooleanArgument.class, "True if this CommandSender is a proxy, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getCommandSender(target) instanceof ProxiedCommandSender);
                                }),
                        new Function("sendMessage")
                                .withDescription("Sends a message to this CommandSender")
                                .withParameters(new Parameter(InternalStringArgument.class, "message", "the message to send"))
                                .returns(InternalVoidArgument.class)
                                .executes((target, parameters) -> {
                                    getCommandSender(target).sendMessage((String) parameters.get(0).getValue());
                                })
                                .withExamples(
                                        "do <sender>.sendMessage(\"Hello!\")"
                                ),
                        new Function("setOp")
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
