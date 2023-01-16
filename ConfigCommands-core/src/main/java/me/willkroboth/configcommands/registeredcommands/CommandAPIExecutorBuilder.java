package me.willkroboth.configcommands.registeredcommands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIExecutor;
import dev.jorel.commandapi.commandsenders.AbstractCommandSender;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.ExecutorType;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.RegistrationException;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import me.willkroboth.configcommands.systemcommands.ReloadCommandHandler;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class that builds a CommandAPI {@link CommandAPIExecutor} based on the values in a config file.
 * See {@link CommandAPIExecutorBuilder#CommandAPIExecutorBuilder(ConfigurationSection, List, Map, SharedDebugValue)}
 */
public class CommandAPIExecutorBuilder extends CommandAPIExecutor<CommandSender, AbstractCommandSender<? extends CommandSender>> {
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

    /**
     * Creates a CommandAPI {@link CommandAPIExecutor} based on the values in a config file.
     *
     * @param executable      A {@link ConfigurationSection} that contains the data for this Executor.
     *                        The data here is expected to look like the following:
     *                        <pre>{@code executes:} - Code to run when any {@link CommandSender} runs this command</pre>
     *                        <pre>&nbsp;    Defined by {@link CommandAPIExecutorBuilder}</pre>
     *                        <pre>{@code executesPlayer:} - Code to run when a {@link Player} runs this command</pre>
     *                        <pre>&nbsp;    Defined by {@link CommandAPIExecutorBuilder}, like before and for all executes sections</pre>
     *                        <pre>{@code executesEntity:} - Code to run when an {@link Entity} runs this command</pre>
     *                        <pre>{@code executesCommandBlock:} - Code to run when a {@link CommandBlock} runs this command</pre>
     *                        <pre>{@code executesConsole:} - Code to run when the {@link ConsoleCommandSender} runs this command</pre>
     *                        <pre>{@code executesProxy:} - Code to run when a {@link ProxiedCommandSender} runs this command</pre>
     *                        <pre>{@code executesNative:} - Code to run when any {@link CommandSender} runs this command, but wrapped as a {@link NativeProxyCommandSender}</pre>
     *                        See <a href="https://commandapi.jorel.dev/8.6.0/normalexecutors.html#restricting-who-can-run-your-command">
     *                        restricting who can run your command</a> on the CommandAPI documentation for more information about these different executes types.
     * @param argumentPath    A List of the names of previous arguments, starting with the literal command name
     * @param argumentClasses A Map linking the name to the {@link InternalArgument}
     *                        class for each of the previous arguments, including default arguments
     * @param localDebug      The {@link SharedDebugValue} object being used for this command path
     * @throws RegistrationException If there is an error reading the data for the command
     */
    public CommandAPIExecutorBuilder(ConfigurationSection executable, List<String> argumentPath,
                                     Map<String, Class<? extends InternalArgument>> argumentClasses, SharedDebugValue localDebug) throws RegistrationException {
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
                                     SharedDebugValue localDebug) throws RegistrationException {
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
    private final SharedDebugValue localDebug;

    /**
     * Reloads this command from the config file. This uses the {@code argumentPath} defined when constructing this
     * object to navigate through the config file. The currently registered executes will be cleared and rebuilt
     * the same way as when constructing this object (See
     * {@link CommandAPIExecutorBuilder#CommandAPIExecutorBuilder(ConfigurationSection, List, Map, SharedDebugValue)})
     *
     * @param sender The CommandSender who triggered the reload, used to send messages about the result of the reload.
     * @throws WrapperCommandSyntaxException If there is an error reloading this executor.
     */
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

                if (thisExecutes) sender.sendMessage("Found and generated " + executor.name + " executor");
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
