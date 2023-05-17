package me.willkroboth.configcommands.internalarguments;

import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.IncorrectArgumentKey;
import me.willkroboth.configcommands.functions.*;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.functions.executions.StaticExecution;
import me.willkroboth.configcommands.registeredcommands.expressions.Expression;
import me.willkroboth.configcommands.registeredcommands.functionlines.FunctionLine;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.reflections.scanners.Scanners.SubTypes;

/**
 * A class that represents objects inside the {@link FunctionLine} and {@link Expression} system.
 * They hold an internal value ({@link InternalArgument#setValue(Object)} and {@link InternalArgument#getValue()}),
 * have instance and static functions ({@link InternalArgument#getInstanceExecution(String, List)}
 * and {@link InternalArgument#getStaticExecution(String, List)}) , and may be added as arguments to a command if
 * they implement {@link CommandArgument}.
 */
@SuppressWarnings("unused")
public abstract class InternalArgument<T> extends SafeFunctionCreator<T> {
    ////////////////////////////////
    // SECTION : CREATING CLASSES //
    ////////////////////////////////

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
    public InternalArgument(T value) {
        setValue(value);
    }

    /**
     * Creates an {@link InternalArgument} using its class. This uses the {@link InternalArgument#InternalArgument()}
     * constructor.
     *
     * @param clazz The {@link InternalArgument} clazz object.
     * @return The {@link InternalArgument} object.
     */
    public static InternalArgument<?> getInternalArgument(Class<? extends InternalArgument<?>> clazz) {
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
    private static FunctionAdder<?> getFunctionAdder(Class<? extends FunctionAdder<?>> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException(clazz + " could not be turned into object.", e);
        }
    }

    // Reflections returns generic sets that totally can be converted, Java just doesn't like it
    @SuppressWarnings("unchecked")
    private static <T> Set<T> whyGenerics(Set<?> set) {
        return (Set<T>) set;
    }

    /////////////////////////////////////
    // SECTION : PROVIDING INFORMATION //
    /////////////////////////////////////

    private static final Map<String, List<InternalArgument<?>>> pluginToInternalArguments = new HashMap<>();

    public static Set<String> getPluginsNamesWithInternalArguments() {
        return pluginToInternalArguments.keySet();
    }

    /**
     * Gets a list of {@link InternalArgument} objects that were created by given plugin or had functions added
     * to them by a {@link FunctionAdder} from that plugin.
     *
     * @param pluginName The name of the plugin to get {@link InternalArgument} objects for.
     * @return The list of {@link InternalArgument} objects "belonging" to the plugin.
     */
    public static List<InternalArgument<?>> getPluginInternalArguments(String pluginName) {
        return pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT));
    }

    /**
     * Turns a list of {@link InternalArgument} objects into a list of their names.
     *
     * @param internalArguments The list of {@link InternalArgument} objects to transform.
     * @return A list of the names of each {@link InternalArgument} given by {@link InternalArgument#getName()}.
     */
    public static List<String> getNames(List<InternalArgument<?>> internalArguments) {
        List<String> names = new ArrayList<>(internalArguments.size());
        for (InternalArgument<?> internalArgument : internalArguments) {
            names.add(internalArgument.getName());
        }
        return names;
    }

    /**
     * @return The list of {@link InternalArgument} class objects that have been registered.
     */
    public static List<Class<? extends InternalArgument<?>>> getRegisteredInternalArguments() {
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
    public static <T> String getNameForType(Class<? extends InternalArgument<T>> type) {
        if (type.equals(InternalArgument.class)) return "Any";
        if (type.equals(InternalVoidArgument.class)) return "Nothing";
        return getInternalArgument(type).getName();
    }

    //////////////////////////////////////
    // SECTION : REGISTERING SUBCLASSES //
    //////////////////////////////////////

    private static final List<Runnable> registerProcesses = new ArrayList<>();

    private static final List<Class<? extends InternalArgument<?>>> foundClasses = new ArrayList<>();
    private static final Map<String, CommandArgument<?>> typeMap = new HashMap<>();

    private static final Map<Class<? extends InternalArgument<?>>, InstanceFunctionList<?>> instanceFunctions = new HashMap<>();
    private static final Map<Class<? extends InternalArgument<?>>, StaticFunctionList> staticFunctions = new HashMap<>();

    // Public methods that schedule register processes
    public static void registerFromJavaPlugin(JavaPlugin plugin, String packageName) {
        registerFromJavaPlugin(plugin, packageName, RegisterMode.All);
    }

    public static void registerFromJavaPlugin(JavaPlugin plugin, String packageName, RegisterMode registerMode) {
        registerFromPackage(packageName, plugin.getClass().getClassLoader(), registerMode, plugin.getName());
    }

    public static void registerFromPackage(String packageName, ClassLoader classLoader, RegisterMode registerMode, String pluginName) {
        registerProcesses.add(() -> {
            ConfigCommandsHandler.logNormal("Registering %s for package: %s with ClassLoader: %s",
                    switch (registerMode) {
                        case All -> "InternalArguments and FunctionAdders";
                        case INTERNAL_ARGUMENTS -> "InternalArguments";
                        case FUNCTION_ADDERS -> "FunctionAdders";
                    },
                    packageName, classLoader
            );
            Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));

            if(registerMode == RegisterMode.All || registerMode == RegisterMode.INTERNAL_ARGUMENTS) {
                Set<Class<?>> internalArguments = reflections.get(SubTypes.of(InternalArgument.class).asClass(classLoader));

                ConfigCommandsHandler.logDebug("InternalArguments found:\n\t%s", internalArguments.toString().replace(", ", ",\n\t"));

                registerInternalArgumentSet(whyGenerics(internalArguments), pluginName);
            }
            if(registerMode == RegisterMode.All || registerMode == RegisterMode.FUNCTION_ADDERS) {
                Set<Class<?>> functionAdders = reflections.get(SubTypes.of(FunctionAdder.class).asClass(classLoader));

                ConfigCommandsHandler.logDebug("FunctionAdders found:\n\t%s", functionAdders.toString().replace(", ", ",\n\t"));

                registerFunctionAdderSet(whyGenerics(functionAdders), pluginName);
            }
        });
    }

    public static void registerFromInternalArgumentClassSet(Set<Class<? extends InternalArgument<?>>> classes, String pluginName) {
        registerProcesses.add(() -> registerInternalArgumentSet(classes, pluginName));
    }

    public static void registerFromFunctionAdderClassSet(Set<Class<? extends FunctionAdder<?>>> classes, String pluginName) {
        registerProcesses.add(() -> registerFunctionAdderSet(classes, pluginName));
    }

    // Private methods to actually register classes
    private static void registerInternalArgumentSet(Set<Class<? extends InternalArgument<?>>> classes, String pluginName) {
        pluginName = pluginName.toLowerCase();
        for (Class<? extends InternalArgument<?>> clazz : classes) {
            registerInternalArgument(clazz, pluginName);
        }
        ConfigCommandsHandler.logDebug("All classes registered");
    }

    private static void registerInternalArgument(Class<? extends InternalArgument<?>> clazz, String pluginName) {
        if (clazz.isAssignableFrom(InternalVoidArgument.class)) return;

        InternalArgument<?> object;
        try {
            object = getInternalArgument(clazz);
        } catch (IllegalArgumentException e) {
            // Make sure class can be turned into an object for future use
            ConfigCommandsHandler.logError("Error when registering InternalArgument subclass: %s", clazz.getSimpleName());
            ConfigCommandsHandler.logError(e.getMessage());
            if (ConfigCommandsHandler.isDebugMode()) e.printStackTrace();
            return;
        }

        pluginToInternalArguments.computeIfAbsent(pluginName, (key) -> new ArrayList<>()).add(object);

        Expression.addToStaticClassMap(object);
        foundClasses.add(clazz);

        if (object instanceof CommandArgument<?> ca) {
            ConfigCommandsHandler.logDebug("%s can be added to commands", clazz.getSimpleName());
            typeMap.put(ca.getTypeTag(), ca);
        }
    }

    private static void registerFunctionAdderSet(Set<Class<? extends FunctionAdder<?>>> classes, String pluginName) {
        pluginName = pluginName.toLowerCase();
        for (Class<? extends FunctionAdder<?>> clazz : classes) {
            registerFunctionAdder(clazz, pluginName);
        }
        ConfigCommandsHandler.logDebug("All classes registered");
    }

    @SuppressWarnings("unchecked") // Just some silly stuff to make Java behave
    private static <T> void registerFunctionAdder(Class<? extends FunctionAdder<?>> clazz, String pluginName) {
        FunctionAdder<T> object;
        try {
            object = (FunctionAdder<T>) getFunctionAdder(clazz);
        } catch (IllegalArgumentException e) {
            ConfigCommandsHandler.logError("Error when registering FunctionAdder: %s", clazz.getSimpleName());
            ConfigCommandsHandler.logError(e.getMessage());
            if (ConfigCommandsHandler.isDebugMode()) e.printStackTrace();
            return;
        }

        Class<? extends InternalArgument<T>> classToAddTo = object.getClassToAddTo();
        InternalArgument<?> subObject;
        try {
            subObject = getInternalArgument(classToAddTo);
        } catch (IllegalArgumentException e) {
           ConfigCommandsHandler.logError("Could not turn FunctionAdder's (%s) InternalArgument (%s) into an object", clazz, object.getClassToAddTo());
            if (ConfigCommandsHandler.isDebugMode()) e.printStackTrace();
            return;
        }

        pluginToInternalArguments.computeIfAbsent(pluginName, (key) -> new ArrayList<>()).add(subObject);

        addInstanceFunctions(classToAddTo, object.getAddedFunctions());
        addStaticFunctions(classToAddTo, object.getAddedStaticFunctions());
    }

    // Register classes and use them to set up functions
    /**
     * Loads {@link InternalArgument} and {@link FunctionAdder} classes, then creates the function maps. First, this
     * runs all the processes setup by calls to the methods {@link InternalArgument#registerFromPackage(String, ClassLoader, RegisterMode, String)},
     * {@link InternalArgument#registerFromInternalArgumentClassSet(Set, String)}, and {@link InternalArgument#registerFromFunctionAdderClassSet(Set, String)}.
     * Then, {@link InternalArgument#getInstanceFunctions()} and {@link InternalArgument#getStaticFunctions()} are called
     * on every registered {@link InternalArgument} to set up the function maps.
     */
    public static void createFunctionMaps() {
        registerProcesses.forEach(Runnable::run);

        ConfigCommandsHandler.logNormal("");
        ConfigCommandsHandler.logNormal("Initializing function maps");
        ConfigCommandsHandler.increaseIndentation();
        for (Class<? extends InternalArgument<?>> clazz : foundClasses) {
            InternalArgument<?> object = getInternalArgument(clazz);

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

    /////////////////////////
    // SECTION : FUNCTIONS //
    /////////////////////////

    // Creating function maps
    private static final Map<Class<? extends InternalArgument<?>>, InstanceFunctionList<?>> addedInstanceFunctions = new HashMap<>();

    /**
     * Builds the {@link InstanceFunction} objects that can be run for this {@link InternalArgument}. The default
     * implementation of this method provides a link to the {@link InternalArgument#forCommand()} function, as well
     * as all the {@link InstanceFunction} objects that have been added through {@link InternalArgument#addInstanceFunctions(Class, InstanceFunctionList)}.
     * You can access these functions in your own {@link InternalArgument} using {@code super.getInstanceFunctions()}
     * and merge them into your own functions using {@link FunctionCreator#merge(InstanceFunctionList...)}.
     *
     * @return An {@link InstanceFunctionList} that holds the {@link InstanceFunction} objects that can be run for this {@link InternalArgument}.
     */
    public InstanceFunctionList<T> getInstanceFunctions() {
        return merge(InternalArgument.getAddedInstanceFunctions(myClass()),
                functions(
                        instanceFunction("forCommand")
                                .withExecutions(InstanceExecution
                                        .returns(InternalStringArgument.class, "The String that represents this argument in a command")
                                        .executes(t -> new InternalStringArgument(t.forCommand()))
                                )
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
    // The cast should be safe because only InstanceFunctionList<T> should be created for Class<? extends InternalArgument<T>>
    @SuppressWarnings("unchecked")
    public static <T> InstanceFunctionList<T> getInstanceFunctionsFor(Class<? extends InternalArgument<T>> clazz) {
        return (InstanceFunctionList<T>) instanceFunctions.get(clazz);
    }

    /**
     * Adds {@link InstanceFunction}s to the given {@link InternalArgument} class. This is automatically called when
     * registering {@link FunctionAdder}s, but can also be used to directly add functions. All the added functions
     * can be accessed using {@link InternalArgument#getAddedInstanceFunctions(Class)}.
     *
     * @param clazz          The {@link InternalArgument} class to add the functions to.
     * @param functionsToAdd An {@link InstanceFunctionList} containing the functions to add.
     */
    // The cast should be safe because only InstanceFunctionList<T> should be added for Class<? extends InternalArgument<T>>
    @SuppressWarnings("unchecked")
    public static <T> void addInstanceFunctions(Class<? extends InternalArgument<T>> clazz, InstanceFunctionList<T> functionsToAdd) {
        if (functionsToAdd == null) return;
        InstanceFunctionList<T> list = (InstanceFunctionList<T>) addedInstanceFunctions.computeIfAbsent(clazz, (key) -> new InstanceFunctionList<T>());
        list.addAll(functionsToAdd);
    }

    /**
     * Gets the {@link InstanceFunction}s added to the given {@link InternalArgument} by
     * {@link InternalArgument#addInstanceFunctions(Class, InstanceFunctionList)}.
     *
     * @param clazz The {@link InternalArgument} class to get functions for.
     * @return An {@link InstanceFunctionList} holding all the {@link InstanceFunction}s added to the given {@link InternalArgument}.
     */
    // The cast should be safe because only InstanceFunctionList<T> should be created for Class<? extends InternalArgument<T>>
    @SuppressWarnings("unchecked")
    private static <T> InstanceFunctionList<T> getAddedInstanceFunctions(Class<? extends InternalArgument<T>> clazz) {
        return (InstanceFunctionList<T>) addedInstanceFunctions.getOrDefault(clazz, new InstanceFunctionList<T>());
    }

    private static final Map<Class<? extends InternalArgument<?>>, StaticFunctionList> addedStaticFunctions = new HashMap<>();

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
    public static StaticFunctionList getStaticFunctionsFor(Class<? extends InternalArgument<?>> clazz) {
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
    public static void addStaticFunctions(Class<? extends InternalArgument<?>> clazz, StaticFunctionList functionsToAdd) {
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
    private static StaticFunctionList getAddedStaticFunctions(Class<? extends InternalArgument<?>> clazz) {
        return addedStaticFunctions.getOrDefault(clazz, new StaticFunctionList());
    }

    // Instance methods for checking for and running functions
//    /**
//     * Returns an {@link InstanceFunction} belonging to this {@link InternalArgument} with the given signature.
//     *
//     * @param function       The name of the {@link InstanceFunction} to search for.
//     * @param parameterTypes A List of {@link InternalArgument} class objects that are being input.
//     * @return A {@link InstanceFunction} with the given name and parameters, or null if one cannot be found.
//     */
    // The cast should be safe because only InstanceFunctionList<T> should be created for Class<? extends InternalArgument<T>>
    @SuppressWarnings("unchecked")
    public final InstanceExecution<T, ?> getInstanceExecution(String function, List<Class<? extends InternalArgument<?>>> parameterTypes) {
        InstanceFunction<T> instanceFunction = ((InstanceFunctionList<T>) instanceFunctions.get(myClass())).getByName(function);
        if (instanceFunction == null) return null;
        return instanceFunction.findExecution(parameterTypes);
    }

//    /**
//     * Returns an {@link StaticFunction} belonging to this {@link InternalArgument} with the given signature.
//     *
//     * @param function       The name of the {@link StaticFunction} to search for.
//     * @param parameterTypes A List of {@link InternalArgument} class objects that are being input.
//     * @return A {@link StaticFunction} with the given name and parameters, or null if one cannot be found.
//     */
    public final StaticExecution<?> getStaticExecution(String function, List<Class<? extends InternalArgument<?>>> parameterTypes) {
        StaticFunction staticFunction = staticFunctions.get(myClass()).getByName(function);
        if(staticFunction == null) return null;
        return staticFunction.findExecution(parameterTypes);
    }

    ////////////////////////////////////////////
    // SECTION : ADDING ARGUMENTS TO COMMANDS //
    ///////////////////////////////////////////

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
                                                         Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                         Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        if (!typeMap.containsKey(type))
            throw new IncorrectArgumentKey(name, "type", "\"" + type + "\" is not a recognized type that can be added to a command.");

        CommandArgument<?> object = typeMap.get(type);
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

    ////////////////////////////////////
    // SECTION : INSTANCE INFORMATION //
    ////////////////////////////////////

    /**
     * @return The name of this InternalArgument. The default implementation of this method assumes the class name is
     * {@code Internal[name]Argument}.
     */
    public String getName() {
        String name = this.getClass().getSimpleName();
        name = name.substring(8, name.length() - 8);
        return name;
    }

    // Value related methods

    /**
     * Sets the internal value of this {@link InternalArgument} to the given object.
     *
     * @param arg The new value for this {@link InternalArgument}.
     */
    public abstract void setValue(T arg);

    /**
     * @return The current value held by this {@link InternalArgument}.
     */
    public abstract T getValue();

    /**
     * Sets the internal value of this {@link InternalArgument} to the value stored by the given {@link InternalArgument}.
     *
     * @param arg The {@link InternalArgument} holding the new value for this {@link InternalArgument}.
     */
    public abstract void setValue(InternalArgument<T> arg);

    /**
     * @return A String that represents the value of this {@link InternalArgument} best in a command.
     */
    public abstract String forCommand();

    // Class related methods
    // The cast is safe, and totally works at runtime, generics are just kinda weird
    public static <T> Class<? extends InternalArgument<T>> any() {
        return sillyCast();
    }

    @SuppressWarnings("unchecked")
    private static <T> T sillyCast() {
        return (T) InternalArgument.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends InternalArgument<T>> myClass() {
        return (Class<? extends InternalArgument<T>>) getClass();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof InternalArgument<?> argument)) return false;
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
