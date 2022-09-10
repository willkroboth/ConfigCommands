package me.willkroboth.ConfigCommands.InternalArguments;

import me.willkroboth.ConfigCommands.NMS.OpSender;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.FunctionList;
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

    @Override
    public FunctionList getFunctions() {
        return merge(
                super.getFunctions(),
                entries(
                        entry(new Definition("dispatchCommand", args(InternalStringArgument.class)),
                                new Function(this::dispatchCommand, InternalStringArgument.class)),
                        entry(new Definition("getName", args()),
                                new Function(this::getSenderName, InternalStringArgument.class)),
                        entry(new Definition("getType", args()),
                                new Function(this::getType, InternalStringArgument.class)),
                        entry(new Definition("hasPermission", args(InternalStringArgument.class)),
                                new Function(this::hasPermission, InternalBooleanArgument.class)),
                        entry(new Definition("isCommandBlock", args()),
                                new Function(this::isCommandBlock, InternalBooleanArgument.class)),
                        entry(new Definition("isConsole", args()),
                                new Function(this::isConsole, InternalBooleanArgument.class)),
                        entry(new Definition("isEntity", args()),
                                new Function(this::isEntity, InternalBooleanArgument.class)),
                        entry(new Definition("isOp", args()),
                                new Function(this::isOp, InternalBooleanArgument.class)),
                        entry(new Definition("isPlayer", args()),
                                new Function(this::isPlayer, InternalBooleanArgument.class)),
                        entry(new Definition("isProxy", args()),
                                new Function(this::isProxy, InternalBooleanArgument.class)),
                        entry(new Definition("sendMessage", args(InternalStringArgument.class)),
                                new Function(this::sendMessage, InternalVoidArgument.class)),
                        entry(new Definition("setOp", args(InternalBooleanArgument.class)),
                                new Function(this::setOp, InternalVoidArgument.class))
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

    @Override
    public void setValue(Object arg) {
        value = (CommandSender) arg;
        opSender = OpSender.makeOpSender(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    public OpSender getOpSender() {
        return opSender;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = (CommandSender) arg.getValue();
        opSender = OpSender.makeOpSender(value);
    }

    @Override
    public String forCommand() {
        return value.getName();
    }
}
