package me.willkroboth.ConfigCommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.RegistrationException;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;
import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandBuilder;
import me.willkroboth.ConfigCommands.HelperClasses.Expression;
import me.willkroboth.ConfigCommands.HelperClasses.GuidedCommands.BuildCommandHandler;
import me.willkroboth.ConfigCommands.HelperClasses.GuidedCommands.HelpCommandHandler;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigCommands extends ConfigCommandAddOn {

    private static boolean debugMode;

    public static boolean isDebugMode() {
        return debugMode;
    }

    private static final List<ConfigCommandAddOn> addOns = new ArrayList<>();

    public static void registerAddOn(ConfigCommandAddOn addOn) {
        addOns.add(addOn);
    }

    public static List<ConfigCommandAddOn> getAddOns() {
        return addOns;
    }

    private static ConfigCommands instance;

    public static ConfigCommandAddOn getAddOn(String name) {
        Plugin plugin = instance.getServer().getPluginManager().getPlugin(name);
        if (plugin instanceof ConfigCommandAddOn addOn) return addOn;
        return null;
    }

    public static FileConfiguration getConfigFile() {
        return instance.getConfig();
    }

    public static void saveConfigFile() {
        instance.saveConfig();
    }

    public static void reloadConfigFile() {
        instance.reloadConfig();
    }

    protected String getPackageName() {
        return "me.willkroboth.ConfigCommands";
    }

    protected int getRegisterMode() {
        return 1;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        IndentedLogger logger = new IndentedLogger(getLogger());

        FileConfiguration config = getConfig();

        debugMode = config.getBoolean("debug", false);
        if (debugMode) logger.info("Debug mode on! More information will be shown.");

        // register InternalArguments from addOns
        for (ConfigCommandAddOn addOn : addOns) {
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
                    new ConfigCommandBuilder(name, shortDescription, fullDescription, args, aliases, permission, commandsToRun, localDebug, logger);
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
        new CommandAPICommand("configCommandHelp")
                .withPermission("configcommands.help")
                .executesPlayer(HelpCommandHandler::addUser)
                .executesConsole(HelpCommandHandler::addUser)
                .register();

        new CommandAPICommand("configCommandHelp")
                .withPermission("configcommands.help")
                .withArguments(
                        new StringArgument("addOn").replaceSuggestions(ArgumentSuggestions.strings(HelpCommandHandler::getAddOns)),
                        new StringArgument("internalArgument").replaceSuggestions(ArgumentSuggestions.strings(HelpCommandHandler::getInternalArguments)),
                        new MultiLiteralArgument("static", "nonStatic"),
                        new GreedyStringArgument("function").replaceSuggestions(ArgumentSuggestions.strings(HelpCommandHandler::getFunctions))
                )
                .executesPlayer(HelpCommandHandler::displayInformation)
                .executesConsole(HelpCommandHandler::displayInformation)
                .register();

        getServer().getPluginManager().registerEvents(new HelpCommandHandler(), this);

        // set up build command
        new CommandAPICommand("configCommandBuild")
                .withPermission("configcommands.build")
                .executesPlayer(BuildCommandHandler::addUser)
                .executesConsole(BuildCommandHandler::addUser)
                .register();

        getServer().getPluginManager().registerEvents(new BuildCommandHandler(), this);

        logger.info("Done!");
    }
}


