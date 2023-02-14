package me.willkroboth.configcommands.systemcommands;

import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.ExecutionInfo;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.internalarguments.InternalBooleanArgument;
import me.willkroboth.configcommands.internalarguments.InternalIntegerArgument;
import me.willkroboth.configcommands.internalarguments.InternalStringArgument;
import org.bukkit.command.CommandSender;

import java.util.function.BiPredicate;

public class FunctionLineCommandHandler extends SystemCommandHandler {
    // command configuration
    @Override
    protected Argument<?> getArgumentTree() {
        return super.getArgumentTree()
                .withRequirement(BuildCommandHandler::canUseFunctionLine)
                .then(new LiteralArgument("command")
                        .then(new CommandArgument("run")
                                .executes(FunctionLineCommandHandler::execute)
                        )
                ).then(new LiteralArgument("set")
                        .then(new StringArgument("variable")
                                .then(new GreedyStringArgument("expression")
                                        .replaceSuggestions(suggestExpression((clazz, info) -> {
                                            // TODO: Something like this needs static analysis
                                            Class<? extends InternalArgument> variableClass = getVariableClass(info.previousArgs().get("variable"));
                                            return clazz.isAssignableFrom(variableClass);
                                        }))
                                        .executes(FunctionLineCommandHandler::execute)
                                )
                        )
                ).then(new LiteralArgument("do")
                        .then(new GreedyStringArgument("expression")
                                .replaceSuggestions(suggestExpression((clazz, info) -> true))
                                .executes(FunctionLineCommandHandler::execute)
                        )
                ).then(new LiteralArgument("tag")
                        .then(new GreedyStringArgument("name")
                                .executes(FunctionLineCommandHandler::execute)
                        )
                ).then(new LiteralArgument("if")
                        .then(new TextArgument("boolean")
                                .replaceSuggestions(suggestExpression((clazz, info) -> clazz.isAssignableFrom(InternalBooleanArgument.class)))
                                .then(new GreedyStringArgument("target")
                                        .replaceSuggestions(suggestExpression((clazz, info) ->
                                                clazz.isAssignableFrom(InternalIntegerArgument.class) || clazz.isAssignableFrom(InternalStringArgument.class)))
                                        .executes(FunctionLineCommandHandler::execute)
                                )
                        )
                ).then(new LiteralArgument("goto")
                        .then(new GreedyStringArgument("target")
                                .replaceSuggestions(suggestExpression((clazz, info) ->
                                        clazz.isAssignableFrom(InternalIntegerArgument.class) || clazz.isAssignableFrom(InternalStringArgument.class)))
                                .executes(FunctionLineCommandHandler::execute)
                        )
                ).then(new LiteralArgument("return")
                        .then(new GreedyStringArgument("expression")
                                .replaceSuggestions(suggestExpression((clazz, info) -> true))
                                .executes(FunctionLineCommandHandler::execute)
                        )
                );
    }

    private static final String[] helpMessages = new String[]{
            "Gives tab-complete coding suggestions for function lines"
            // TODO: Write some examples
    };

    @Override
    protected String[] getHelpMessages() {
        return helpMessages;
    }

    // command functions
    private static void execute(ExecutionInfo<CommandSender, ?> info) {
        // Literally do nothing
        // This command is never actually run, it always gets caught by BuildCommandHandler
        // Handling the command actually happens in FunctionLineCommandHandler#parse
    }

    private static ArgumentSuggestions<CommandSender> suggestExpression(BiPredicate<Class<? extends InternalArgument>, SuggestionInfo<CommandSender>> returnClassOkay) {
        return (info, builder) -> {
            // TODO: Fill this in
            //  Do static analysis on the code for better suggestions?
        };
    }

    // Accessed by BuildCommandHandler
    protected static String parseCommand(String command) {
        // TODO: Fill this in
    }
}
