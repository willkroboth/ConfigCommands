package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIExecutor;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.ExecutorType;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.SystemCommands.ReloadCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandAPIExecutorBuilder extends CommandAPIExecutor<CommandSender> {
    private static final List<ExecutorInformation> executors = List.of(
            new ExecutorInformation("executes", "executes", ExecutorType.ALL),
            new ExecutorInformation("player", "executesPlayer", ExecutorType.PLAYER),
            new ExecutorInformation("entity", "executesEntity", ExecutorType.ENTITY),
            new ExecutorInformation("block", "executesCommandBlock", ExecutorType.BLOCK),
            new ExecutorInformation("console", "executesConsole", ExecutorType.CONSOLE),
            new ExecutorInformation("proxy", "executesProxy", ExecutorType.PROXY),
            new ExecutorInformation("native", "executesNative", ExecutorType.NATIVE)
    );

    private record ExecutorInformation(String name, String sectionName, ExecutorType type) {
    }

    public CommandAPIExecutorBuilder(ConfigurationSection executable, List<String> argumentPath,
                                     Map<String, Class<? extends InternalArgument>> argumentClasses, boolean localDebug) throws RegistrationException {
        boolean canExecute = false;
        ConfigCommandsHandler.increaseIndentation();
        for (ExecutorInformation executor : executors) {
            // |= dose not short circuit the calculation of generateExecutor
            canExecute |= generateExecutor(executor.name, executable.getStringList(executor.sectionName), executor.type, argumentClasses, localDebug);
        }
        ConfigCommandsHandler.decreaseIndentation();
        if (canExecute) {
            ReloadCommandHandler.addCommand(argumentPath, this);
        } else {
            ConfigCommandsHandler.logDebug(localDebug, "Not executable at this stage");
        }

        // Save info for reloading executors
        this.argumentClasses = argumentClasses;
        this.argumentPath = List.copyOf(argumentPath);
        this.localDebug = localDebug;
    }

    private boolean generateExecutor(String name, List<String> executes, ExecutorType type,
                                     Map<String, Class<? extends InternalArgument>> argumentClasses,
                                     boolean localDebug) throws RegistrationException {
        if (executes.size() == 0) return false;

        ConfigCommandsHandler.logDebug(localDebug, "Adding %s executor", name);
        ConfigCommandsHandler.increaseIndentation();
        CommandExecutorBuilder executor = new CommandExecutorBuilder(executes, argumentClasses, type, localDebug);
        ConfigCommandsHandler.decreaseIndentation();
        addNormalExecutor(executor);
        return true;
    }

    // Stored information for reloading executor
    private final List<String> argumentPath;
    private final Map<String, Class<? extends InternalArgument>> argumentClasses;
    private final boolean localDebug;

    public void reloadExecution(CommandSender sender) throws WrapperCommandSyntaxException {
        ConfigCommandsHandler.logDebug(localDebug, "Reloading argument path %s", argumentPath);
        // Find ConfigurationSection
        ConfigCommandsHandler.reloadConfigFile();
        FileConfiguration config = ConfigCommandsHandler.getConfigFile();
        ConfigurationSection commandSection = config.getConfigurationSection("commands");

        if (commandSection == null || commandSection.getKeys(false).size() == 0)
            throw CommandAPI.failWithString("No commands found in config.yml");

        ConfigurationSection command = commandSection.getConfigurationSection(argumentPath.get(0));
        if (command == null)
            throw CommandAPI.failWithString("No data was found for the command (Did you change it's name?)");

        ConfigurationSection argument = command;
        StringBuilder commandPath = new StringBuilder(argumentPath.get(0));
        for (int i = 1; i < argumentPath.size(); i++) {
            ConfigurationSection then = argument.getConfigurationSection("then");
            if (then == null)
                throw CommandAPI.failWithString("No arguments found under /" + commandPath + " (Did you remove them?)");

            String argumentName = argumentPath.get(i);
            argument = then.getConfigurationSection(argumentName);
            if (argument == null)
                throw CommandAPI.failWithString("Argument (" + argumentName + ") not found under /" + commandPath + " (Did you change it's name?)");

            commandPath.append(" ").append(argumentName);
        }

        // Clear and rebuild executors
        setNormalExecutors(new ArrayList<>());
        ConfigCommandsHandler.logDebug(localDebug, "Rebuilding executes");
        boolean canExecute = false;

        ConfigCommandsHandler.increaseIndentation();
        for (ExecutorInformation executor : executors) {
            try {
                // |= dose not short circuit the calculation of generateExecutor
                boolean thisExecutes = generateExecutor(executor.name, argument.getStringList(executor.sectionName), executor.type, argumentClasses, localDebug);
                canExecute |= thisExecutes;

                if(thisExecutes) sender.sendMessage("Found and generated " + executor.name + " executor");
            } catch (RegistrationException e) {
                ConfigCommandsHandler.logDebug(localDebug, e.getMessage());
                sender.sendMessage("Found " + executor.name + " executor, but it was invalid");
                sender.sendMessage(e.getMessage());
            }
        }
        ConfigCommandsHandler.decreaseIndentation();
        if (canExecute) {
            sender.sendMessage("Command successfully updated!");
        } else {
            sender.sendMessage("No valid executors were found. The command will not be executable until reloaded successfully");
        }
    }
}
