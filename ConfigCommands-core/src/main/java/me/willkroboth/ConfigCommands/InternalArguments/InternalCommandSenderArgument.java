package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionList;
import me.willkroboth.ConfigCommands.Functions.Parameter;
import me.willkroboth.ConfigCommands.NMS.OpSender;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

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

    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                // TODO: Add function info
                functions(
                        new Function("dispatchCommand")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalStringArgument.class)
                                .executes(this::dispatchCommand),
                        new Function("getName")
                                .returns(InternalStringArgument.class)
                                .executes(this::getSenderName),
                        new Function("getType")
                                .returns(InternalStringArgument.class)
                                .executes(this::getType),
                        new Function("hasPermission")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalBooleanArgument.class)
                                .executes(this::hasPermission),
                        new Function("isCommandBlock")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isCommandBlock),
                        new Function("isConsole")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isConsole),
                        new Function("isEntity")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isEntity),
                        new Function("isOp")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isOp),
                        new Function("isPlayer")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isPlayer),
                        new Function("isProxy")
                                .returns(InternalBooleanArgument.class)
                                .executes(this::isProxy),
                        new Function("sendMessage")
                                .withParameters(new Parameter(InternalStringArgument.class))
                                .returns(InternalVoidArgument.class)
                                .executes(this::sendMessage),
                        new Function("setOp")
                                .withParameters(new Parameter(InternalBooleanArgument.class))
                                .returns(InternalVoidArgument.class)
                                .executes(this::setOp)
                )
        );
    }

    private CommandSender getCommandSender(InternalArgument target) {
        return (CommandSender) target.getValue();
    }

    public InternalStringArgument dispatchCommand(InternalArgument target, List<InternalArgument> parameters) {
        String command = (String) parameters.get(0).getValue();

        OpSender targetOpSender = ((InternalCommandSenderArgument) target).getOpSender();
        targetOpSender.getServer().dispatchCommand(targetOpSender, command);
        return new InternalStringArgument(targetOpSender.getResult());
    }

    private InternalArgument getSenderName(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalStringArgument(getCommandSender(target).getName());
    }

    private InternalArgument getType(InternalArgument target, List<InternalArgument> parameters) {
        CommandSender targetCommandSender = getCommandSender(target);

        if (targetCommandSender instanceof OpSender opSender)
            targetCommandSender = opSender.getSender();

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
    }

    private InternalArgument hasPermission(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target).hasPermission((String) parameters.get(0).getValue()));
    }

    private InternalArgument isCommandBlock(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target) instanceof BlockCommandSender);
    }

    private InternalArgument isConsole(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target) instanceof ConsoleCommandSender);
    }

    private InternalArgument isEntity(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target) instanceof Entity);
    }

    private InternalArgument isOp(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target).isOp());
    }

    private InternalArgument isPlayer(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target) instanceof Player);
    }

    private InternalArgument isProxy(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalBooleanArgument(getCommandSender(target) instanceof ProxiedCommandSender);
    }

    public InternalArgument sendMessage(InternalArgument target, List<InternalArgument> parameters) {
        String message = (String) parameters.get(0).getValue();
        getCommandSender(target).sendMessage(message);

        return InternalVoidArgument.getInstance();
    }

    private InternalArgument setOp(InternalArgument target, List<InternalArgument> parameters) {
        getCommandSender(target).setOp((boolean) parameters.get(0).getValue());
        return InternalVoidArgument.getInstance();
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
}
