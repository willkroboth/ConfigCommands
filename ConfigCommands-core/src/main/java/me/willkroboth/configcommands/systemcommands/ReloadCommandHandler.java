package me.willkroboth.configcommands.systemcommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.configcommands.registeredcommands.CommandAPIExecutorBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that handles the {@code /configcommands reload} command.
 */
public class ReloadCommandHandler extends SystemCommandHandler {
    // Command configuration
    @Override
    protected ArgumentTree getArgumentTree() {
        ArgumentTree tree = super.getArgumentTree();
        commands.generateArgumentTrees().forEach(tree::then);
        return tree;
    }

    private static final String[] helpMessages = new String[]{
            "Reloads a command's code from the config.yml, allowing its behavior to change without restarting the server.",
            "Usage:",
            "\t/configcommands reload <command> <arguments>..."
    };

    @Override
    protected String[] getHelpMessages() {
        return helpMessages;
    }

    // Command functions
    private static class ArgumentPathTree {
        Map<String, ArgumentPathNode> children = new HashMap<>();

        public void put(List<String> argumentPath, CommandAPIExecutorBuilder command) {
            children.computeIfAbsent(argumentPath.get(0), k -> new ArgumentPathNode()).put(argumentPath.subList(1, argumentPath.size()), command);
        }

        public List<ArgumentTree> generateArgumentTrees() {
            List<ArgumentTree> out = new ArrayList<>(children.size());
            for (Map.Entry<String, ArgumentPathNode> node : children.entrySet()) {
                ArgumentTree tree = new LiteralArgument(node.getKey());

                CommandAPIExecutorBuilder command = node.getValue().command;
                if (command != null) tree.executes(reloadCommand(command));

                node.getValue().generateArgumentTrees().forEach(tree::then);
                out.add(tree);
            }
            return out;
        }
    }

    private static class ArgumentPathNode {
        Map<String, ArgumentPathNode> children = new HashMap<>();
        CommandAPIExecutorBuilder command;

        public void put(List<String> argumentPath, CommandAPIExecutorBuilder command) {
            if (argumentPath.size() == 0) {
                this.command = command;
            } else {
                children.computeIfAbsent(argumentPath.get(0), k -> new ArgumentPathNode()).put(argumentPath.subList(1, argumentPath.size()), command);
            }
        }

        public List<ArgumentTree> generateArgumentTrees() {
            List<ArgumentTree> out = new ArrayList<>(children.size());
            for (Map.Entry<String, ArgumentPathNode> node : children.entrySet()) {
                ArgumentTree tree = new LiteralArgument(node.getKey());

                CommandAPIExecutorBuilder command = node.getValue().command;
                if (command != null) tree.executes(reloadCommand(command));

                node.getValue().generateArgumentTrees().forEach(tree::then);
                out.add(tree);
            }
            return out;
        }
    }

    private static CommandExecutor reloadCommand(CommandAPIExecutorBuilder command) {
        return (sender, args) -> command.reloadExecution(sender);
    }

    private static final ArgumentPathTree commands = new ArgumentPathTree();

    // Accessed by CommandAPIExecutorBuilder

    /**
     * Adds a command path that can be reloaded by this command.
     *
     * @param argumentPath A list of Strings that represent the node names of each argument on the command path.
     * @param command      The {@link CommandAPIExecutorBuilder} at the end of the argument path that can be reloaded.
     */
    public static void addCommand(List<String> argumentPath, CommandAPIExecutorBuilder command) {
        commands.put(argumentPath, command);
    }
}
