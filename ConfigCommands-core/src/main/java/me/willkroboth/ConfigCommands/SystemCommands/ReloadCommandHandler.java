package me.willkroboth.ConfigCommands.SystemCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import me.willkroboth.ConfigCommands.RegisteredCommands.ReloadableExecutable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommandHandler extends SystemCommandHandler {
    // command configuration
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

    protected String[] getHelpMessages() {
        return helpMessages;
    }

    private static class ArgumentPathTree {
        Map<String, ArgumentPathNode> children = new HashMap<>();

        public void put(List<String> argumentPath, ReloadableExecutable command) {
            children.computeIfAbsent(argumentPath.get(0), k -> new ArgumentPathNode()).put(argumentPath.subList(1, argumentPath.size()), command);
        }

        public List<ArgumentTree> generateArgumentTrees() {
            List<ArgumentTree> out = new ArrayList<>(children.size());
            for (Map.Entry<String, ArgumentPathNode> node : children.entrySet()) {
                ArgumentTree tree = new LiteralArgument(node.getKey());

                ReloadableExecutable command = node.getValue().command;
                if (command != null) tree.executes(reloadCommand(command));

                node.getValue().generateArgumentTrees().forEach(tree::then);
                out.add(tree);
            }
            return out;
        }
    }

    private static class ArgumentPathNode {
        Map<String, ArgumentPathNode> children = new HashMap<>();
        ReloadableExecutable command;

        public void put(List<String> argumentPath, ReloadableExecutable command) {
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

                ReloadableExecutable command = node.getValue().command;
                if (command != null) tree.executes(reloadCommand(command));

                node.getValue().generateArgumentTrees().forEach(tree::then);
                out.add(tree);
            }
            return out;
        }
    }

    private static CommandExecutor reloadCommand(ReloadableExecutable command) {
        return (sender, args) -> command.reloadExecution(sender);
    }

    // Command functions
    private static final ArgumentPathTree commands = new ArgumentPathTree();

    // Accessed by CommandTreeBuilder and ArgumentTreeBuilder
    public static void addCommand(List<String> argumentPath, ReloadableExecutable command) {
        commands.put(argumentPath, command);
    }
}
