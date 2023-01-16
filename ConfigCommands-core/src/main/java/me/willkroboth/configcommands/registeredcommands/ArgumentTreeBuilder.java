package me.willkroboth.configcommands.registeredcommands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.IncorrectArgumentKey;
import me.willkroboth.configcommands.exceptions.RegistrationException;
import me.willkroboth.configcommands.helperclasses.SharedDebugValue;
import me.willkroboth.configcommands.internalarguments.CommandArgument;
import me.willkroboth.configcommands.internalarguments.InternalArgument;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that builds a CommandAPI {@link Argument} based on the values in a config file.
 * See {@link ArgumentTreeBuilder#buildArgumentTree(String, Map, ConfigurationSection, SharedDebugValue, List)}
 */
public class ArgumentTreeBuilder {
    /**
     * Creates a CommandAPI {@link Argument} based on the values in a config file.
     *
     * @param name            The nodeName used to construct the backing {@link Argument}
     * @param argumentClasses A Map linking the name to the {@link InternalArgument}
     *                        class for each of the previous arguments, including default arguments
     * @param tree            A {@link ConfigurationSection} that contains the data for this tree.
     *                        The data here is expected to look like the following:
     *                        <pre>{@code type: [type]} (Valid values depend on the registered {@link CommandArgument} classes. See {@link CommandArgument#getTypeTag()})</pre>
     *                        <pre>{@code argumentInfo:}</pre>
     *                        <pre>    Defined by the {@link CommandArgument} type used. See {@link CommandArgument#createArgument(String, Object, boolean)}</pre>
     *                        <pre>{@code permission: [permission]}</pre>
     *                        <pre>{@code executes:} Defined by {@link CommandAPIExecutorBuilder}</pre>
     *                        <pre>{@code ...}</pre>
     *                        <pre>{@code then:}</pre>
     *                        <pre>&nbsp;    {@code ArgumentTree1}</pre>
     *                        <pre>&nbsp;    {@code ArgumentTree2}</pre>
     *                        <pre>&nbsp;    {@code ...}</pre>
     * @param localDebug      The {@link SharedDebugValue} object being used for this command path
     * @param argumentPath    A List of the names of previous arguments, starting with the literal command name
     * @return The CommandAPI {@link Argument} defined by the given data
     * @throws RegistrationException If there is an error reading the data for the command
     */
    public static Argument<?> buildArgumentTree(String name, Map<String, Class<? extends InternalArgument>> argumentClasses,
                               ConfigurationSection tree, SharedDebugValue localDebug, List<String> argumentPath) throws RegistrationException {
        Argument<?> argument = getArgument(name, argumentClasses, tree, localDebug);

        String permission = tree.getString("permission");
        if (permission != null) {
            argument.withPermission(permission);
            ConfigCommandsHandler.logDebug(localDebug, "Added permission \"%s\" to branch", permission);
        }

        argumentPath.add(name);

        ConfigCommandsHandler.logNormal("Building ArgumentTree...");
        // set executes
        argument.setExecutor(new CommandAPIExecutorBuilder(tree, argumentPath, argumentClasses, localDebug));

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
                argument.then(ArgumentTreeBuilder.buildArgumentTree(
                        branchName, new LinkedHashMap<>(argumentClasses),
                        then.getConfigurationSection(branchName), localDebug, argumentPath
                ));
                ConfigCommandsHandler.decreaseIndentation();
            }
            ConfigCommandsHandler.decreaseIndentation();
        }
        argumentPath.remove(argumentPath.size() - 1);

        return argument;
    }

    private static Argument<?> getArgument(String name, Map<String, Class<? extends InternalArgument>> argumentClasses, ConfigurationSection tree, SharedDebugValue localDebug) throws IncorrectArgumentKey {
        String type = tree.getString("type");

        // Null type equals LiteralArgument
        if (type == null) {
            ConfigCommandsHandler.logDebug(localDebug, "Type was not found, so this will be a LiteralArgument");
            return new LiteralArgument(name);
        }
        ConfigCommandsHandler.logDebug(localDebug, "Type is %s", type);

        Object argumentInfo = tree.get("argumentInfo");
        if (localDebug.isDebug()) {
            if (argumentInfo == null)
                ConfigCommandsHandler.logNormal("argumentInfo is null");
            else
                ConfigCommandsHandler.logNormal("argumentInfo has class %s", argumentInfo.getClass().getSimpleName());
        }

        // Get InternalArgument to generate an Argument based on the CommandArguments it knows about
        Argument<?> out = InternalArgument.convertArgumentInformation(name, type, argumentClasses, argumentInfo, localDebug.isDebug());
        ConfigCommandsHandler.logDebug(localDebug, "Argument created with class: %s", out.getClass().getSimpleName());
        return out;
    }
}
