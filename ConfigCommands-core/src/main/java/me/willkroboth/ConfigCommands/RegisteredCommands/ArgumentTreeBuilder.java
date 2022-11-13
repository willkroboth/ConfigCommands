package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.SystemCommands.ReloadCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArgumentTreeBuilder extends ArgumentTree implements ReloadableExecutable {
    private static Argument<?> getArgument(String name, Map<String, Class<? extends InternalArgument>> argumentClasses, ConfigurationSection tree, boolean localDebug) throws IncorrectArgumentKey {
        String type = tree.getString("type");
        if (type == null) {
            ConfigCommandsHandler.logDebug(localDebug, "Type was not found, so this will be a LiteralArgument");
            return new LiteralArgument(name);
        }
        ConfigCommandsHandler.logDebug(localDebug, "Type is %s", type);

        Object argumentInfo = tree.get("argumentInfo");
        if (localDebug) {
            if (argumentInfo == null)
                ConfigCommandsHandler.logNormal("argumentInfo is null");
            else
                ConfigCommandsHandler.logNormal("argumentInfo has class %s", argumentInfo.getClass().getSimpleName());
        }
        Argument<?> out = InternalArgument.convertArgumentInformation(name, type, argumentClasses, argumentInfo, localDebug);
        ConfigCommandsHandler.logDebug(localDebug, "Argument created with class: %s", out.getClass().getSimpleName());
        return out;
    }

    private static Argument<?> modifyArgument(Argument<?> argument, ConfigurationSection tree, boolean localDebug) {
        // set permission
        String permission = tree.getString("permission");
        if (permission != null) {
            argument.withPermission(permission);
            ConfigCommandsHandler.logDebug(localDebug, "Added permission \"%s\" to branch", permission);
        }

        return argument;
    }

    public ArgumentTreeBuilder(String name, Map<String, Class<? extends InternalArgument>> argumentClasses,
                               ConfigurationSection tree, boolean localDebug, List<String> argumentPath) throws RegistrationException {
        super(modifyArgument(getArgument(name, argumentClasses, tree, localDebug), tree, localDebug));
        argumentPath.add(name);

        this.argumentClasses = argumentClasses;
        this.argumentPath = List.copyOf(argumentPath);
        this.localDebug = localDebug;

        ConfigCommandsHandler.logNormal("Building ArgumentTree...");
        // set executes
        List<String> executes = tree.getStringList("executes");
        if (executes.size() != 0) {
            ConfigCommandsHandler.logDebug(localDebug, "Adding executes");
            ConfigCommandsHandler.increaseIndentation();
            executor = new ExecutesBuilder(executes, argumentClasses, localDebug);
            super.executes(this::execute);
            ReloadCommandHandler.addCommand(argumentPath, this);
            ConfigCommandsHandler.decreaseIndentation();
        } else {
            ConfigCommandsHandler.logDebug(localDebug, "Not executable at this stage");
        }

        // set then
        ConfigurationSection then = tree.getConfigurationSection("then");
        if (then == null || then.getKeys(false).size() == 0) {
            ConfigCommandsHandler.logDebug(localDebug, "No branches");
        } else {
            ConfigCommandsHandler.logDebug(localDebug, "Adding branches");
            ConfigCommandsHandler.increaseIndentation();
            for (String branchName : then.getKeys(false)) {
                ConfigCommandsHandler.logDebug(localDebug, "Adding branch %s", branchName);
                ConfigCommandsHandler.increaseIndentation();
                super.then(new ArgumentTreeBuilder(
                        branchName, new LinkedHashMap<>(argumentClasses),
                        then.getConfigurationSection(branchName), localDebug, argumentPath
                ));
                ConfigCommandsHandler.decreaseIndentation();
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
        argumentPath.remove(argumentPath.size() - 1);
    }

    private ExecutesBuilder executor;

    // Stored information for reloading executor
    private final List<String> argumentPath;
    private final Map<String, Class<? extends InternalArgument>> argumentClasses;
    private final boolean localDebug;

    private void execute(CommandSender sender, Object[] args) {
        if(executor != null) executor.run(sender, args);
    }

    @Override
    public void reloadExecution(CommandSender sender) throws WrapperCommandSyntaxException {
        ConfigCommandsHandler.reloadConfigFile();
        FileConfiguration config = ConfigCommandsHandler.getConfigFile();
        ConfigurationSection commandSection = config.getConfigurationSection("commands");

        if (commandSection == null || commandSection.getKeys(false).size() == 0)
            throw CommandAPI.fail("No commands found in config.yml");

        ConfigurationSection command = commandSection.getConfigurationSection(argumentPath.get(0));
        if (command == null)
            throw CommandAPI.fail("No data was found for the command (Did you change it's name?)");

        ConfigurationSection argument = command;
        StringBuilder commandPath = new StringBuilder(argumentPath.get(0));
        for (int i = 1; i < argumentPath.size(); i++) {
            ConfigurationSection then = argument.getConfigurationSection("then");
            if(then == null) throw CommandAPI.fail("No arguments found under /" + commandPath + " (Did you remove them?)");

            String argumentName = argumentPath.get(i);
            argument = then.getConfigurationSection(argumentName);
            if(argument == null) throw CommandAPI.fail("Argument (" + argumentName + ") not found under /" + commandPath + " (Did you change it's name?)");

            commandPath.append(" ").append(argumentName);
        }

        List<String> executes = argument.getStringList("executes");
        if (executes.size() == 0) {
            sender.sendMessage("No executes found, disabling command path");
            executor = null;
            return;
        }

        try {
            executor = new ExecutesBuilder(executes, argumentClasses, localDebug);
        } catch (RegistrationException e) {
            throw CommandAPI.fail("Could not apply new commands: " + e.getMessage());
        }

        sender.sendMessage("Command successfully updated!");
    }
}
