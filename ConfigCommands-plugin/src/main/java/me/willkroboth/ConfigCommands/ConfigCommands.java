package me.willkroboth.ConfigCommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.willkroboth.ConfigCommands.Commands.BuildCommandHandler;
import me.willkroboth.ConfigCommands.Commands.HelpCommandHandler;
import me.willkroboth.ConfigCommands.Commands.ReloadCommandHandler;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationException;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandBuilder;
import me.willkroboth.ConfigCommands.HelperClasses.Expression;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.NMS.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigCommands extends ConfigCommandsPlugin {
    // ConfigCommands' information as an AddOn
    protected String getPackageName() {
        return "me.willkroboth.ConfigCommands.InternalArguments";
    }

    protected int getRegisterMode() {
        return 1;
    }

    // Enable
    @Override
    public void onEnable() {
        ConfigCommandsHandler.setPlugin(this);

        String bukkit = Bukkit.getServer().toString();
        String version = bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1);
        ConfigCommandsHandler.setNMS(VersionHandler.getVersion(version));

        saveDefaultConfig();
        IndentedLogger logger = new IndentedLogger(getLogger());

        FileConfiguration config = getConfig();

        boolean debugMode = config.getBoolean("debug", false);
        if (debugMode) logger.info("Debug mode on! More information will be shown.");
        ConfigCommandsHandler.setDebugMode(debugMode);

        // register InternalArguments from addOns
        for (ConfigCommandAddOn addOn : ConfigCommandsHandler.getAddOns()) {
            logger.info("Enabling addOn " + addOn.getName());
            addOn.registerInternalArguments();
        }

        // display registrations
        if (debugMode) {
            logger.info("All recognized InternalArguments:\n\t" + AllInternalArguments.getFlat().toString().replace(", ", ",\n\t"));
            logger.info("Static class map:\n\t" + Expression.getClassMap().toString().replace(", ", ",\n\t"));
        }

        InternalArgument.createFunctionMaps(debugMode, logger);

        // register commands
        ConfigurationSection commands = config.getConfigurationSection("commands");

        if (commands == null || commands.getKeys(false).size() == 0) {
            logger.info("No commands given. Skipping");
        } else {
            List<String> failedCommands = new ArrayList<>();
            for (String key : commands.getKeys(false)) {
                logger.info("");
                logger.info("Loading command " + key);

                // vital data needed for command to work
                ConfigurationSection command = commands.getConfigurationSection(key);
                if (command == null) {
                    logger.warn(true, key + " has no data. Skipping.");
                    failedCommands.add("(key) " + key + ": No data found!");
                    continue;
                }

                boolean localDebug = command.getBoolean("debug", false);
                if (localDebug && !debugMode) logger.info("Debug turned on for " + key);
                localDebug = debugMode || localDebug;

                String name = (String) command.get("name");
                if (name == null) {
                    logger.warn(true, key + " has no command name. Skipping.");
                    failedCommands.add("(key) " + key + ": No name found!");
                    continue;
                }

                if (localDebug) logger.info(key + " has name: " + name);

                List<String> commandsToRun = command.getStringList("commands");
                if (commandsToRun.size() == 0) {
                    logger.warn(true, key + " has no commands. Skipping.");
                    failedCommands.add("(name) " + name + ": No commands found!");
                    continue;
                }

                if (localDebug) logger.info(key + " has " + commandsToRun.size() + " commands: " + commandsToRun);

                // less important, but will warn user if they don't exist
                String shortDescription = command.getString("shortDescription");
                if (shortDescription == null) logger.warn(false, key + " has no shortDescription.");

                if (localDebug) logger.info(key + " has shortDescription: " + shortDescription);

                String fullDescription = command.getString("fullDescription");
                if (fullDescription == null) logger.warn(false, key + " has no fullDescription.");

                if (localDebug) logger.info(key + " has fullDescription: " + fullDescription);

                String permission = command.getString("permission");
                if (permission == null) {
                    permission = ConfigCommandBuilder.getDefaultPermission(name);
                    logger.warn(false, key + " has no permission. Using \"" + permission + "\".");
                }

                if (localDebug) logger.info(key + " has permission " + permission);

                // Don't need to warn user about these
                List<Map<?, ?>> args = command.getMapList("args");
                if (localDebug) logger.info(key + " has args: " + args);

                List<String> aliases = command.getStringList("aliases");
                if (localDebug) logger.info(key + " has " + aliases.size() + " aliases " + aliases);

                // register command
                logger.info("Loading " + key + " with name: " + name);
                logger.increaseIndentation();
                try {
                    ReloadCommandHandler.addCommand(
                            new ConfigCommandBuilder(
                                    name, shortDescription, fullDescription, args, aliases,
                                    permission, commandsToRun, localDebug, logger
                            ),
                            key
                    );
                } catch (RegistrationException e) {
                    logger.warn(true, "Registration error: \"" + e.getMessage() + "\" Skipping registration.");
                    failedCommands.add("(name) " + name + ": Registration error: \"" + e.getMessage() + "\"");
                }
                logger.setIndentation(0);
            }

            // inform user of failed commands
            if (failedCommands.size() == 0) {
                logger.info("All commands were successfully registered.");
                logger.info("Note: this does not mean they will work as you expect.");
                if (debugMode) {
                    logger.info("If a command does not work, check the console output to try to find the problem.");
                } else {
                    logger.info("If a command does not work, turn on debug mode, then check the console output to try to find the problem.");
                }
            } else {
                logger.info(failedCommands.size() + " command(s) failed while registering:");
                logger.increaseIndentation();
                for (String message : failedCommands) {
                    logger.warn(true, message);
                }
                logger.decreaseIndentation();
                if (debugMode) {
                    logger.info("Scroll up to find more information.");
                } else {
                    logger.info("Turn on debug mode and scroll up to find more information.");
                }
            }
        }

        // set up help command
        new CommandTree("configcommandhelp")
                .withHelp("Displays information for available ConfigCommand functions",
                        "Displays information about the available ConfigCommands functions. Using just /configcommandhelp brings up a guided menu. You can also use tab-complete suggestions to explore the functions the same way.")
                .withPermission("configcommands.help")
                .executesPlayer(HelpCommandHandler::addUser)
                .executesConsole(HelpCommandHandler::addUser)
                .then(new StringArgument("addOn")
                        .replaceSuggestions(ArgumentSuggestions.strings(HelpCommandHandler::getAddOns))
                        .then(new StringArgument("internalArgument")
                                .replaceSuggestions(ArgumentSuggestions.strings(HelpCommandHandler::getInternalArguments))
                                .then(new MultiLiteralArgument("static", "nonStatic")
                                        .then(new GreedyStringArgument("function")
                                                .replaceSuggestions(ArgumentSuggestions.strings(HelpCommandHandler::getFunctions))
                                                .executesPlayer(HelpCommandHandler::displayInformation)
                                                .executesConsole(HelpCommandHandler::displayInformation)
                                        )
                                )
                        )
                )
                .register();

        getServer().getPluginManager().registerEvents(new HelpCommandHandler(), this);

        // set up build command
        new CommandAPICommand("configcommandbuild")
                .withHelp("Helps users create new commands",
                        "Opens a menu that guides users through creating a new command. Enables creating, editing, and deleting commands in-game.")
                .withPermission("configcommands.build")
                .executesPlayer(BuildCommandHandler::addUser)
                .executesConsole(BuildCommandHandler::addUser)
                .register();

        getServer().getPluginManager().registerEvents(new BuildCommandHandler(), this);

        // set up reload command
        new CommandAPICommand("configcommandreload")
                .withHelp("Reloads a command's code",
                        "Reloads a command's code from the config.yml, allowing its behavior to change without restarting the server.")
                .withPermission("configcommands.reload")
                .withArguments(new StringArgument("command")
                        .replaceSuggestions(ArgumentSuggestions.strings(ReloadCommandHandler.getCommandNames())))
                .executes(ReloadCommandHandler::reloadCommand)
                .register();

        logger.info("Done!");
    }
}
