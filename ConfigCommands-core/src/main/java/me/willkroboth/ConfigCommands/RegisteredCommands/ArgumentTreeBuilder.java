package me.willkroboth.ConfigCommands.RegisteredCommands;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArgumentTreeBuilder extends ArgumentTree {
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

        ConfigCommandsHandler.logNormal("Building ArgumentTree...");
        // set executes
        super.setExecutor(new CommandAPIExecutorBuilder(tree, argumentPath, argumentClasses, localDebug));

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
}
