package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.*;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expressions.Expression;
import me.willkroboth.ConfigCommands.RegisteredCommands.FunctionLines.FunctionLine;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.reflections.scanners.Scanners.SubTypes;

/**
 * A class that represents objects inside the {@link FunctionLine} and {@link Expression} system.
 * They hold an internal value ({@link InternalArgument#setValue(Object)} and {@link InternalArgument#getValue()}),
 * perform instance and static functions ({@link InternalArgument#runInstanceFunction(String, List)}
 * and {@link InternalArgument#runStaticFunction(String, List)}), and may be added as arguments to a command if
 * they implement {@link CommandArgument}.
 */
public abstract class InternalArgument implements FunctionCreator {
    // constructors

    /**
     * Creates a new {@link InternalArgument}. This constructor is used when creating an object through
     * reflection and must be mirrored in {@link InternalArgument} subclasses.
     */
    public InternalArgument() {
    }

    /**
     * Creates a new {@link InternalArgument} and gives it a value using {@link InternalArgument#setValue(Object)}.
     *
     * @param value The new value for this {@link InternalArgument}.
     */
    public InternalArgument(Object value) {
        setValue(value);
    }

    /**
     * Creates an {@link InternalArgument} using its class. This uses the {@link InternalArgument#InternalArgument()}
     * constructor.
     *
     * @param clazz The {@link InternalArgument} clazz object.
     * @return The {@link InternalArgument} object.
     */
    public static InternalArgument getInternalArgument(Class<? extends InternalArgument> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz + "Could not be turned into object.", e);
        }
    }

    /**
     * Creates an {@link FunctionAdder} using its class.
     *
     * @param clazz The {@link FunctionAdder} clazz object.
     * @return The {@link FunctionAdder} object.
     */
    private static FunctionAdder getFunctionAdder(Class<? extends FunctionAdder> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException(clazz + " could not be turned into object.", e);
        }
    }

    // providing and processing information related to InternalArguments
    private static final Map<String, List<InternalArgument>> pluginToInternalArguments = new HashMap<>();

    /**
     * Gets a list of {@link InternalArgument} objects that were created by given plugin or had functions added
     * to them by a {@link FunctionAdder} from that plugin.
     *
     * @param pluginName The name of the plugin to get {@link InternalArgument} objects for.
     * @return The list of {@link InternalArgument} objects "belonging" to the plugin.
     */
    public static List<InternalArgument> getPluginInternalArguments(String pluginName) {
        return pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT));
    }

    /**
     * Turns a list of {@link InternalArgument} objects into a list of their names.
     *
     * @param internalArguments The list of {@link InternalArgument} objects to transform.
     * @return A list of the names of each {@link InternalArgument} given by {@link InternalArgument#getName()}.
     */
    public static List<String> getNames(List<InternalArgument> internalArguments) {
        List<String> names = new ArrayList<>(internalArguments.size());
        for (InternalArgument internalArgument : internalArguments) {
            names.add(internalArgument.getName());
        }
        return names;
    }

    /**
     * @return The list of {@link InternalArgument} class objects that have been registered.
     */
    public static List<Class<? extends InternalArgument>> getRegisteredInternalArguments() {
        return foundClasses;
    }

    /**
     * Gets the name for a {@link InternalArgument} class. This is usually the result of
     * {@link InternalArgument#getName()}, but there are two special cases:
     * <ul>
     *     <li>{@link InternalArgument}{@code .class} -> "Any"</li>
     *     <li>{@link InternalVoidArgument}{@code .class} -> "Nothing"</li>
     * </ul>
     *
     * @param type The {@link InternalArgument} class object to get a type for.
     * @return The name of the given {@link InternalArgument} type.
     */
    public static String getNameForType(Class<? extends InternalArgument> type) {
        if (type.equals(InternalArgument.class)) return "Any";
        if (type.equals(InternalVoidArgument.class)) return "Nothing";
        return getInternalArgument(type).getName();
    }

    // registering subclasses
    private static final List<Class<? extends InternalArgument>> foundClasses = new ArrayList<>();
    private static final Map<Class<? extends InternalArgument>, InstanceFunctionList> instanceFunctions = new HashMap<>();
    private static final Map<Class<? extends InternalArgument>, StaticFunctionList> staticFunctions = new HashMap<>();

    private static <T> Set<T> convertSet(Set<Class<?>> classes) {
        return new HashSet<>((Collection<? extends T>) classes);
    }

    /**
     * Registers all {@link InternalArgument} and {@link FunctionAdder} subclasses in a given package.
     *
     * @param packageName The name of the package. This can be found on the first line of a java file, after the
     *                    {@code package} keyword and before the semicolon. All classes in the given package and any
     *                    enclosed package will be inspected, so be as specific as possible to avoid excessive searching.
     * @param pluginName  The name of the plugin responsible for registering the classes.
     * @param classLoader The {@link ClassLoader} responsible for loading the classes of interest. If you are loading
     *                    classes from the same jar as your {@link JavaPlugin}, you can get this using
     *                    {@code JavaPlugin#getClassLoader()}.
     * @param debugMode   True if debug messages should be logged, and false otherwise.
     * @param logger      The {@link Logger} to log messages in.
     */
    public static void registerFullPackage(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger) {
        logger.info("Registering InternalArguments and FunctionAdders for package: " + packageName + " with classLoader: " + classLoader);
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));

        Set<Class<?>> internalArguments = reflections.get(SubTypes.of(InternalArgument.class).asClass(classLoader));
        if (debugMode)
            logger.info("InternalArguments found:\n\t" + internalArguments.toString().replace(", ", ",\n\t"));
        registerSetOfInternalArguments(convertSet(internalArguments), pluginName, debugMode, logger);

        Set<Class<?>> functionAdders = reflections.get(SubTypes.of(FunctionAdder.class).asClass(classLoader));
        if (debugMode) logger.info("FunctionAdders found:\n\t" + functionAdders.toString().replace(", ", ",\n\t"));
        registerSetOfFunctionAdders(convertSet(functionAdders), pluginName, debugMode, logger);
    }

    /**
     * Registers all {@link InternalArgument} subclasses in a given package.
     *
     * @param packageName The name of the package. This can be found on the first line of a java file, after the
     *                    {@code package} keyword and before the semicolon. All classes in the given package and any
     *                    enclosed package will be inspected, so be as specific as possible to avoid excessive searching.
     * @param pluginName  The name of the plugin responsible for registering the classes.
     * @param classLoader The {@link ClassLoader} responsible for loading the classes of interest. If you are loading
     *                    classes from the same jar as your {@link JavaPlugin}, you can get this using
     *                    {@code JavaPlugin#getClassLoader()}.
     * @param debugMode   True if debug messages should be logged, and false otherwise.
     * @param logger      The {@link Logger} to log messages in.
     */
    public static void registerPackageOfInternalArguments(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger) {
        logger.info("Registering InternalArguments for package: " + packageName + " with classLoader: " + classLoader);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));
        Set<Class<?>> classes = reflections.get(SubTypes.of(InternalArgument.class).asClass(classLoader));
        if (debugMode) logger.info("Classes found:\n\t" + classes.toString().replace(", ", ",\n\t"));

        registerSetOfInternalArguments(convertSet(classes), pluginName, debugMode, logger);
    }

    /**
     * Registers all {@link InternalArgument} classes in a given set.
     *
     * @param classes    The set of {@link InternalArgument} class objects to register.
     * @param pluginName The name of the plugin responsible for registering the classes.
     * @param debugMode  True if debug messages should be logged, and false otherwise.
     * @param logger     The {@link Logger} to log messages in.
     */
    public static void registerSetOfInternalArguments(Set<Class<? extends InternalArgument>> classes, String pluginName, boolean debugMode, Logger logger) {
        if (!pluginToInternalArguments.containsKey(pluginName.toLowerCase(Locale.ROOT)))
            pluginToInternalArguments.put(pluginName.toLowerCase(Locale.ROOT), new ArrayList<>());
        for (Class<? extends InternalArgument> clazz : classes) {
            registerInternalArgument(clazz, pluginName, debugMode, logger);
        }
        if (debugMode) logger.info("All classes registered");
    }

    private static final Map<String, CommandArgument> typeMap = new HashMap<>();

    /**
     * Registers a single {@link InternalArgument} class.
     *
     * @param clazz      The {@link InternalArgument} class object to register.
     * @param pluginName The name of the plugin responsible for registering the classes.
     * @param debugMode  True if debug messages should be logged, and false otherwise.
     * @param logger     The {@link Logger} to log messages in.
     */
    private static void registerInternalArgument(Class<? extends InternalArgument> clazz, String pluginName, boolean debugMode, Logger logger) {
        if (clazz.isAssignableFrom(InternalVoidArgument.class)) return;

        InternalArgument object;
        try {
            object = getInternalArgument(clazz);
        } catch (IllegalArgumentException e) {
            // Make sure class can be turned into an object for future use
            logger.log(Level.SEVERE, "Error when registering InternalArgument subclass: " + clazz.getSimpleName());
            logger.log(Level.SEVERE, e.getMessage());
            if (debugMode) e.printStackTrace();
            return;
        }
        pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT)).add(object);

        Expression.addToStaticClassMap(object);
        foundClasses.add(clazz);

        if (object instanceof CommandArgument ca) {
            if (debugMode) logger.info(clazz + " can be added to commands");
            typeMap.put(ca.getTypeTag(), ca);
        }
    }

    /**
     * Registers all {@link FunctionAdder} subclasses in a given package.
     *
     * @param packageName The name of the package. This can be found on the first line of a java file, after the
     *                    {@code package} keyword and before the semicolon. All classes in the given package and any
     *                    enclosed package will be inspected, so be as specific as possible to avoid excessive searching.
     * @param pluginName  The name of the plugin responsible for registering the classes.
     * @param classLoader The {@link ClassLoader} responsible for loading the classes of interest. If you are loading
     *                    classes from the same jar as your {@link JavaPlugin}, you can get this using
     *                    {@code JavaPlugin#getClassLoader()}.
     * @param debugMode   True if debug messages should be logged, and false otherwise.
     * @param logger      The {@link Logger} to log messages in.
     */
    public static void registerPackageOfFunctionAdders(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger) {
        logger.info("Registering FunctionAdders for package: " + packageName + " with classLoader: " + classLoader);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));
        Set<Class<?>> classes = reflections.get(SubTypes.of(FunctionAdder.class).asClass(classLoader));
        if (debugMode) logger.info("Classes found:\n\t" + classes.toString().replace(", ", ",\n\t"));

        registerSetOfFunctionAdders(convertSet(classes), pluginName, debugMode, logger);
    }

    /**
     * Registers all {@link FunctionAdder} classes in a given set.
     *
     * @param classes    The set of {@link FunctionAdder} class objects to register.
     * @param pluginName The name of the plugin responsible for registering the classes.
     * @param debugMode  True if debug messages should be logged, and false otherwise.
     * @param logger     The {@link Logger} to log messages in.
     */
    public static void registerSetOfFunctionAdders(Set<Class<? extends FunctionAdder>> classes, String pluginName, boolean debugMode, Logger logger) {
        if (!pluginToInternalArguments.containsKey(pluginName.toLowerCase(Locale.ROOT)))
            pluginToInternalArguments.put(pluginName.toLowerCase(Locale.ROOT), new ArrayList<>());
        for (Class<? extends FunctionAdder> clazz : classes) {
            registerFunctionAdder(clazz, pluginName, debugMode, logger);
        }
        if (debugMode) logger.info("All classes registered");
    }

    /**
     * Registers a single {@link FunctionAdder} class.
     *
     * @param clazz      The {@link FunctionAdder} class object to register.
     * @param pluginName The name of the plugin responsible for registering the classes.
     * @param debugMode  True if debug messages should be logged, and false otherwise.
     * @param logger     The {@link Logger} to log messages in.
     */
    private static void registerFunctionAdder(Class<? extends FunctionAdder> clazz, String pluginName, boolean debugMode, Logger logger) {
        FunctionAdder object;
        try {
            object = getFunctionAdder(clazz);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Error when registering FunctionAdder: " + clazz.getSimpleName());
            logger.log(Level.SEVERE, e.getMessage());
            if (debugMode) e.printStackTrace();
            return;
        }
        InternalArgument subObject;
        try {
            subObject = getInternalArgument(object.getClassToAddTo());
        } catch (IllegalArgumentException e) {
            logger.warning("Could not turn FunctionAdder's (" + clazz + ") InternalArgument (" + object.getClassToAddTo() + ") into an object");
            if (debugMode) e.printStackTrace();
            return;
        }

        pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT)).add(subObject);

        addInstanceFunctions(object.getClassToAddTo(), object.getAddedFunctions());
        addStaticFunctions(object.getClassToAddTo(), object.getAddedStaticFunctions());
    }

    /**
     * Creates the function maps used when checking if {@link InternalArgument} classes have certain functions. This
     * calls {@link InternalArgument#getInstanceFunctions()} and {@link InternalArgument#getStaticFunctions()} on every
     * registered {@link InternalArgument}.
     */
    public static void createFunctionMaps() {
        ConfigCommandsHandler.logNormal("");
        ConfigCommandsHandler.logNormal("Initializing function maps");
        ConfigCommandsHandler.increaseIndentation();
        for (Class<? extends InternalArgument> clazz : foundClasses) {
            InternalArgument object = getInternalArgument(clazz);

            ConfigCommandsHandler.logDebug(object.toString());
            try {
                instanceFunctions.put(clazz, object.getInstanceFunctions());
                staticFunctions.put(clazz, object.getStaticFunctions());
            } catch (Exception e) {
                ConfigCommandsHandler.logError("Unexpected fatal exception when setting function map for %s", object);

                try {
                    object.getInstanceFunctions();
                } catch (Exception ignored) {
                    ConfigCommandsHandler.logError("Couldn't get functions");
                }

                try {
                    object.getStaticFunctions();
                } catch (Exception ignored) {
                    ConfigCommandsHandler.logError("Couldn't get static functions");
                }

                throw e;
            }
        }
        ConfigCommandsHandler.decreaseIndentation();
    }

    /**
     * Formats an argument name to work with the {@link Expression} format. This format is {@code <name>}.
     * If the given name doesn't start with &lt;, that is added, and if the name doesn't start with &gt;, that is added.
     *
     * @param name The name to format.
     * @return The formatted name.
     */
    public static String formatArgumentName(String name) {
        if (!name.startsWith("<")) {
            name = "<" + name;
        }
        if (!name.endsWith(">")) {
            name = name + ">";
        }
        return name;
    }

    /**
     * Returns the set of Strings that are valid inputs to the second parameter of
     * {@link InternalArgument#convertArgumentInformation(String, String, Map, Object, boolean)},
     * or the names of the registered {@link CommandArgument}
     *
     * @return The set of Strings that can be used as an argument type in commands.
     */
    public static Set<String> getArgumentTypes() {
        return typeMap.keySet();
    }

    /**
     * Converts ConfigCommands config information into a CommandAPI {@link Argument} object, based on
     * the registered {@link CommandArgument} classes.
     *
     * @param name            The node name for the {@link Argument}.
     * @param type            The type of the {@link CommandArgument}. The valid Strings for this parameter is given by {@link InternalArgument#getArgumentTypes()}.
     * @param argumentClasses A map from name to {@link InternalArgument} class objects that represent each argument already available in this command.
     *                        This method adds the new argument to this map, formatting the name first using {@link InternalArgument#formatArgumentName(String)}.
     * @param argumentInfo    The argument info object, taken from the config file using {@link FileConfiguration#get(String)}.
     * @param localDebug      True if debug messages should be sent, and false otherwise.
     * @return A CommandAPI {@link Argument} that represents the given information.
     * @throws IncorrectArgumentKey If a key describing the argument is incorrect.
     */
    public static Argument<?> convertArgumentInformation(String name, String type,
                                                         Map<String, Class<? extends InternalArgument>> argumentClasses,
                                                         Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        if (!typeMap.containsKey(type))
            throw new IncorrectArgumentKey(name, "type", "\"" + type + "\" is not a recognized type that can be added to a command.");

        CommandArgument object = typeMap.get(type);
        String argumentName = formatArgumentName(name);
        argumentClasses.put(argumentName, object.myClass());
        ConfigCommandsHandler.logDebug(localDebug, "Argument %s available as %s", argumentName, object.getClass().getSimpleName());
        try {
            return object.createArgument(name, argumentInfo, localDebug);
        } catch (RuntimeException e) {
            throw new IncorrectArgumentKey(name, "argumentInfo", e.getMessage());
        }
    }

    /**
     * Passes editing argument information to the correct {@link CommandArgument#editArgumentInfo(CommandSender, String, ConfigurationSection, Object)}
     * method based on the type given.
     *
     * @param sender       The {@link CommandSender} editing the information.
     * @param message      The message to process.
     * @param argument     The {@link ConfigurationSection} that holds all the information for the argument.
     *                     This is useful if argumentInfo object is null and a new section for the argumentInfo
     *                     needs to be created.
     * @param type         The type of the {@link CommandArgument}. The valid Strings for this parameter is given by {@link InternalArgument#getArgumentTypes()}.
     * @param argumentInfo The current argumentInfo object, gotten by running {@code argument.get("argumentInfo")}.
     * @return true if the user is done editing the argumentInfo, and false if more steps need to happen.
     */
    public static boolean passEditArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, String type, @Nullable Object argumentInfo) {
        return typeMap.get(type).editArgumentInfo(sender, message, argument, argumentInfo);
    }

    /**
     * Turns an argumentInfo object into an array of Strings based on the registered {@link CommandArgument} types.
     *
     * @param type         The type of the {@link CommandArgument}. The valid Strings for this parameter is given by {@link InternalArgument#getArgumentTypes()}.
     * @param argumentInfo The current argumentInfo object, gotten by running {@code argument.get("argumentInfo")}.
     * @return An array of Strings that represent the argumentInfo object.
     */
    public static String[] formatArgumentInfo(String type, @Nullable Object argumentInfo) {
        return typeMap.get(type).formatArgumentInfo(argumentInfo);
    }

    // defining important variables for logic and function

    /**
     * @return The name of this InternalArgument. The default implementation of this method assumes the class name is
     * {@code Internal[name]Argument}.
     */
    public String getName() {
        String name = this.getClass().getSimpleName();
        name = name.substring(8, name.length() - 8);
        return name;
    }

    // manage function arrays
    private static final Map<Class<? extends InternalArgument>, InstanceFunctionList> addedInstanceFunctions = new HashMap<>();

    /**
     * Builds the {@link InstanceFunction} objects that can be run for this {@link InternalArgument}. The default
     * implementation of this method provides a link to the {@link InternalArgument#forCommand()} function, as well
     * as all the {@link InstanceFunction} objects that have been added through {@link InternalArgument#addInstanceFunctions(Class, InstanceFunctionList)}.
     * You can access these functions in your own {@link InternalArgument} using {@code super.getInstanceFunctions()}
     * and merge them into your own functions using {@link FunctionCreator#merge(InstanceFunctionList...)}.
     *
     * @return An {@link InstanceFunctionList} that holds the {@link InstanceFunction} objects that can be run for this {@link InternalArgument}.
     */
    public InstanceFunctionList getInstanceFunctions() {
        return merge(InternalArgument.getAddedInstanceFunctions(myClass()),
                functions(
                        new InstanceFunction("forCommand")
                                .returns(InternalStringArgument.class, "The String that represents this argument in a command")
                                .executes((target, parameters) -> {
                                    return new InternalStringArgument(target.forCommand());
                                })
                )
        );
    }

    /**
     * Returns the {@link InstanceFunctionList} created for the requested
     * {@link InternalArgument} class by {@link InternalArgument#createFunctionMaps()}.
     *
     * @param clazz The {@link InternalArgument} class object to get the {@link InstanceFunctionList} for.
     * @return The {@link InstanceFunctionList} created for the given class.
     */
    public static InstanceFunctionList getInstanceFunctionsFor(Class<? extends InternalArgument> clazz) {
        return instanceFunctions.get(clazz);
    }

    /**
     * Adds {@link InstanceFunction}s to the given {@link InternalArgument} class. This is automatically called when
     * registering {@link FunctionAdder}s, but can also be used to directly add functions. All the added functions
     * can be accessed using {@link InternalArgument#getAddedInstanceFunctions(Class)}.
     *
     * @param clazz          The {@link InternalArgument} class to add the functions to.
     * @param functionsToAdd An {@link InstanceFunctionList} containing the functions to add.
     */
    public static void addInstanceFunctions(Class<? extends InternalArgument> clazz, InstanceFunctionList functionsToAdd) {
        if (functionsToAdd == null) return;
        addedInstanceFunctions.computeIfAbsent(clazz, (key) -> new InstanceFunctionList()).addAll(functionsToAdd);
    }

    /**
     * Gets the {@link InstanceFunction}s added to the given {@link InternalArgument} by
     * {@link InternalArgument#addInstanceFunctions(Class, InstanceFunctionList)}.
     *
     * @param clazz The {@link InternalArgument} class to get functions for.
     * @return An {@link InstanceFunctionList} holding all the {@link InstanceFunction}s added to the given {@link InternalArgument}.
     */
    private static InstanceFunctionList getAddedInstanceFunctions(Class<? extends InternalArgument> clazz) {
        return addedInstanceFunctions.getOrDefault(clazz, new InstanceFunctionList());
    }

    private static final Map<Class<? extends InternalArgument>, StaticFunctionList> addedStaticFunctions = new HashMap<>();

    /**
     * Builds the {@link StaticFunction} objects that can be run for this {@link InternalArgument}. The default
     * implementation of this method provides all the {@link StaticFunction} objects that have been added through
     * {@link InternalArgument#addStaticFunctions(Class, StaticFunctionList)}. You can access these functions in
     * your own {@link InternalArgument} using {@code super.getStaticFunctions()} and merge them
     * into your own functions using {@link FunctionCreator#merge(StaticFunctionList...)}.
     *
     * @return An {@link StaticFunctionList} that holds the {@link StaticFunction} objects that can be run for this {@link InternalArgument}.
     */
    public StaticFunctionList getStaticFunctions() {
        return InternalArgument.getAddedStaticFunctions(myClass());
    }

    /**
     * Returns the {@link StaticFunctionList} created for the requested
     * {@link InternalArgument} class by {@link InternalArgument#createFunctionMaps()}.
     *
     * @param clazz The {@link InternalArgument} class object to get the {@link StaticFunctionList} for.
     * @return The {@link StaticFunctionList} created for the given class.
     */
    public static StaticFunctionList getStaticFunctionsFor(Class<? extends InternalArgument> clazz) {
        return staticFunctions.get(clazz);
    }

    /**
     * Adds {@link StaticFunction}s to the given {@link InternalArgument} class. This is automatically called when
     * registering {@link FunctionAdder}s, but can also be used to directly add functions. All the added functions
     * can be accessed using {@link InternalArgument#getAddedStaticFunctions(Class)}.
     *
     * @param clazz          The {@link InternalArgument} class to add the functions to.
     * @param functionsToAdd An {@link StaticFunctionList} containing the functions to add.
     */
    public static void addStaticFunctions(Class<? extends InternalArgument> clazz, StaticFunctionList functionsToAdd) {
        if (functionsToAdd == null) return;
        addedStaticFunctions.computeIfAbsent(clazz, (key) -> new StaticFunctionList()).addAll(functionsToAdd);
    }

    /**
     * Gets the {@link StaticFunction}s added to the given {@link InternalArgument} by
     * {@link InternalArgument#addStaticFunctions(Class, StaticFunctionList)}.
     *
     * @param clazz The {@link InternalArgument} class to get functions for.
     * @return An {@link StaticFunctionList} holding all the {@link StaticFunction}s added to the given {@link InternalArgument}.
     */
    private static StaticFunctionList getAddedStaticFunctions(Class<? extends InternalArgument> clazz) {
        return addedStaticFunctions.getOrDefault(clazz, new StaticFunctionList());
    }

    // interacts with functions

    /**
     * Checks if this {@link InternalArgument} has an {@link InstanceFunction} with the given signature.
     *
     * @param function       The name of the {@link InstanceFunction} to search for.
     * @param parameterTypes A List of {@link InternalArgument} class objects that are being input.
     * @return True if an {@link InstanceFunction} with the given name and accepting the given parameters belonging
     * to this {@link InternalArgument} is found, and false otherwise.
     */
    public final boolean hasInstanceFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return instanceFunctions.get(myClass()).hasFunction(function, parameterTypes);
    }

    /**
     * Checks if this {@link InternalArgument} has an {@link StaticFunction} with the given signature.
     *
     * @param function       The name of the {@link StaticFunction} to search for.
     * @param parameterTypes A List of {@link InternalArgument} class objects that are being input.
     * @return True if an {@link StaticFunction} with the given name and accepting the given parameters belonging
     * to this {@link InternalArgument} is found, and false otherwise.
     */
    public final boolean hasStaticFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return staticFunctions.get(myClass()).hasFunction(function, parameterTypes);
    }

    /**
     * Gets the class that running the {@link InstanceFunction} with the given signature would return.
     *
     * @param function       The name of the {@link InstanceFunction} to search for.
     * @param parameterTypes A List of {@link InternalArgument} class objects that are being input.
     * @return The {@link InternalArgument} class of the Object that would be returned if the given {@link InstanceFunction} was run.
     */
    public final Class<? extends InternalArgument> getReturnTypeForInstanceFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return instanceFunctions.get(myClass()).getFunction(function, parameterTypes).getReturnType(parameterTypes);
    }

    /**
     * Gets the class that running the {@link StaticFunction} with the given signature would return.
     *
     * @param function       The name of the {@link StaticFunction} to search for.
     * @param parameterTypes A List of {@link InternalArgument} class objects that are being input.
     * @return The {@link InternalArgument} class of the Object that would be returned if the given {@link StaticFunction} was run.
     */
    public final Class<? extends InternalArgument> getReturnTypeForStaticFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return staticFunctions.get(myClass()).getFunction(function, parameterTypes).getReturnType(parameterTypes);
    }

    /**
     * Runs the given {@link InstanceFunction}.
     *
     * @param function   The name of the {@link InstanceFunction} to run.
     * @param parameters A List of {@link InternalArgument} objects that are the parameters of the function.
     * @return An {@link InternalArgument} that is the result of running the given function.
     */
    public final InternalArgument runInstanceFunction(String function, List<InternalArgument> parameters) {
        List<Class<? extends InternalArgument>> parameterTypes = new ArrayList<>();
        for (InternalArgument p : parameters) {
            parameterTypes.add(p.getClass());
        }

        return instanceFunctions.get(myClass()).getFunction(function, parameterTypes).run(this, parameters);
    }

    /**
     * Runs the given {@link StaticFunction}.
     *
     * @param function   The name of the {@link StaticFunction} to run.
     * @param parameters A List of {@link InternalArgument} objects that are the parameters of the function.
     * @return An {@link InternalArgument} that is the result of running the given function.
     */
    public final InternalArgument runStaticFunction(String function, List<InternalArgument> parameters) {
        List<Class<? extends InternalArgument>> parameterTypes = new ArrayList<>();
        for (InternalArgument p : parameters) {
            parameterTypes.add(p.getClass());
        }

        return staticFunctions.get(myClass()).getFunction(function, parameterTypes).run(parameters);
    }

    // abstract functions for dealing with value

    /**
     * Sets the internal value of this {@link InternalArgument} to the given object.
     *
     * @param arg The new value for this {@link InternalArgument}.
     */
    public abstract void setValue(Object arg);

    /**
     * @return The current value held by this {@link InternalArgument}.
     */
    public abstract Object getValue();

    /**
     * Sets the internal value of this {@link InternalArgument} to the value stored by the given {@link InternalArgument}.
     *
     * @param arg The {@link InternalArgument} holding the new value for this {@link InternalArgument}.
     */
    public abstract void setValue(InternalArgument arg);

    /**
     * @return A String that represents the value of this {@link InternalArgument} best in a command.
     */
    public abstract String forCommand();

    // class managing methods
    @Override
    public Class<? extends InternalArgument> myClass() {
        return getClass();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof InternalArgument argument)) return false;
        return argument.getValue().equals(getValue());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return getClass().getSimpleName().hashCode();
    }
}
