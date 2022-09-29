package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.CommandAPICommand;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionCreator;
import me.willkroboth.ConfigCommands.Functions.FunctionList;
import me.willkroboth.ConfigCommands.Functions.StaticFunctionList;
import me.willkroboth.ConfigCommands.HelperClasses.Expression;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AddThisArgumentConsumer;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.reflections.scanners.Scanners.SubTypes;

public abstract class InternalArgument implements FunctionCreator {
    // constructors
    public InternalArgument() {

    }

    public InternalArgument(Object value) {
        setValue(value);
    }

    public static InternalArgument getInternalArgument(Class<? extends InternalArgument> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz + "Could not be turned into object.", e);
        }
    }

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

    public static List<InternalArgument> getPluginInternalArguments(String pluginName) {
        return pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT));
    }

    public static List<String> getNames(List<InternalArgument> internalArguments) {
        List<String> names = new ArrayList<>(internalArguments.size());
        for (InternalArgument internalArgument : internalArguments) {
            names.add(internalArgument.getName());
        }
        return names;
    }

    public static Set<String> getArgumentTypes() {
        return argumentMap.keySet();
    }

    public static List<Class<? extends InternalArgument>> getRegisteredInternalArguments() {
        return foundClases;
    }

    public static String getNameForType(Class<? extends InternalArgument> type) {
        return type.equals(InternalArgument.class) ? "Any" : getInternalArgument(type).getName();
    }

    // registering subclasses
    private static final List<Class<? extends InternalArgument>> foundClases = new ArrayList<>();
    private static final Map<Class<? extends InternalArgument>, FunctionList> functions = new HashMap<>();
    private static final Map<Class<? extends InternalArgument>, StaticFunctionList> staticFunctions = new HashMap<>();

    private static <T> Set<T> convertSet(Set<Class<?>> classes) {
        return new HashSet<>((Collection<? extends T>) classes);
    }

    @SuppressWarnings("unused")
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

    public static void registerPackageOfInternalArguments(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger) {
        logger.info("Registering InternalArguments for package: " + packageName + " with classLoader: " + classLoader);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));
        Set<Class<?>> classes = reflections.get(SubTypes.of(InternalArgument.class).asClass(classLoader));
        if (debugMode) logger.info("Classes found:\n\t" + classes.toString().replace(", ", ",\n\t"));

        registerSetOfInternalArguments(convertSet(classes), pluginName, debugMode, logger);
    }

    public static void registerSetOfInternalArguments(Set<Class<? extends InternalArgument>> classes, String pluginName, boolean debugMode, Logger logger) {
        if (!pluginToInternalArguments.containsKey(pluginName.toLowerCase(Locale.ROOT)))
            pluginToInternalArguments.put(pluginName.toLowerCase(Locale.ROOT), new ArrayList<>());
        for (Class<? extends InternalArgument> clazz : classes) {
            registerInternalArgument(clazz, pluginName, debugMode, logger);
        }
        if (debugMode) logger.info("All classes registered");
    }

    private static final Map<String, AddThisArgumentConsumer> argumentMap = new HashMap<>();

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

        Expression.addToClassMap(object);
        foundClases.add(clazz);

        String type = object.getTypeTag();
        if (type == null) {
            if (debugMode)
                logger.info(clazz + " gave null typeTag. It will not be able to be used as a command argument");
        } else {
            argumentMap.put(type, object::addArgument);
        }
    }

    @SuppressWarnings("unused")
    public static void registerPackageOfFunctionAdders(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger) {
        logger.info("Registering FunctionAdders for package: " + packageName + " with classLoader: " + classLoader);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));
        Set<Class<?>> classes = reflections.get(SubTypes.of(FunctionAdder.class).asClass(classLoader));
        if (debugMode) logger.info("Classes found:\n\t" + classes.toString().replace(", ", ",\n\t"));

        registerSetOfFunctionAdders(convertSet(classes), pluginName, debugMode, logger);
    }

    public static void registerSetOfFunctionAdders(Set<Class<? extends FunctionAdder>> classes, String pluginName, boolean debugMode, Logger logger) {
        if (!pluginToInternalArguments.containsKey(pluginName.toLowerCase(Locale.ROOT)))
            pluginToInternalArguments.put(pluginName.toLowerCase(Locale.ROOT), new ArrayList<>());
        for (Class<? extends FunctionAdder> clazz : classes) {
            registerFunctionAdder(clazz, pluginName, debugMode, logger);
        }
        if (debugMode) logger.info("All classes registered");
    }

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

        addFunctions(object.getClassToAddTo(), object.getAddedFunctions());
        addStaticFunctions(object.getClassToAddTo(), object.getAddedStaticFunctions());
    }

    public static void createFunctionMaps() {
        ConfigCommandsHandler.logNormal("");
        ConfigCommandsHandler.logNormal("Initializing function maps");
        ConfigCommandsHandler.increaseIndentation();
        for (Class<? extends InternalArgument> clazz : foundClases) {
            InternalArgument object = getInternalArgument(clazz);

            ConfigCommandsHandler.logDebug(object.toString());
            try {
                functions.put(clazz, object.getFunctions());
                staticFunctions.put(clazz, object.getStaticFunctions());
            } catch (Exception e) {
                ConfigCommandsHandler.logError("Unexpected fatal exception when setting function map for %s", object);

                try {
                    object.getFunctions();
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

    public static String formatArgumentName(String name) {
        if (!name.startsWith("<")) {
            name = "<" + name;
        }
        if (!name.endsWith(">")) {
            name = name + ">";
        }
        return name;
    }

    public static void addArgument(Map<?, ?> arg, CommandAPICommand command,
                                   ArrayList<String> argument_keys,
                                   HashMap<String, Class<? extends InternalArgument>> argument_variable_classes,
                                   boolean localDebug)
            throws IncorrectArgumentKey {
        String name = (String) arg.get("name");
        if (name == null) throw new IncorrectArgumentKey(arg.toString(), "name", "Key not found.");
        name = formatArgumentName(name);
        ConfigCommandsHandler.logDebug(localDebug, "Arg has name: " + name);
        if (argument_keys.contains(name))
            throw new IncorrectArgumentKey(arg.toString(), "name", "Argument with this name already exists!");

        String type = (String) arg.get("type");
        if (type == null) throw new IncorrectArgumentKey(arg.toString(), "type", "Key not found.");
        ConfigCommandsHandler.logDebug(localDebug, "Arg has type: " + type);
        if (!argumentMap.containsKey(type))
            throw new IncorrectArgumentKey(arg.toString(), "type", "Type \"" + type + "\" was not found");

        try {
            argumentMap.get(type).add(arg, command, name, argument_keys, argument_variable_classes, localDebug);
        } catch (IncorrectArgumentKey e) {
            throw e;
        } catch (Exception e) {
            throw new IncorrectArgumentKey(arg.toString(), false, e.getMessage());
        }
    }

    // defining important variables for logic and function
    public String getName() {
        // default assumes class name is formatted like: Internal"name"Argument
        String name = this.getClass().getSimpleName();
        name = name.substring(8, name.length() - 8);
        return name;
    }

    // Note: while getTypeTag and addThisArgument both have default methods, at least one should be overridden
    // Most common override for getTypeTag should be to return null to disable being added as an argument
    public String getTypeTag() {
        // default type tag is the name
        // classes may override with null to disable being added as argument to commands
        return getName();
    }

    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name,
                            ArrayList<String> argument_keys,
                            HashMap<String, Class<? extends InternalArgument>> argument_variable_classes,
                            boolean localDebug) throws IncorrectArgumentKey {
        // default result from adding argument is to reject being added
        throw new IncorrectArgumentKey(arg.toString(), "type", getName() + " cannot be an argument");
    }

    // manage function arrays
    private static final Map<Class<? extends InternalArgument>, FunctionList> addedFunctions = new HashMap<>();

    public FunctionList getFunctions() {
        return merge(InternalArgument.getAddedFunctions(myClass()),
                functions(
                        new Function("forCommand")
                                .returns(InternalStringArgument.class, "The String that represents this argument in a command")
                                .executes(this::internalForCommand)
                )
        );
    }

    public static FunctionList getFunctionsFor(Class<? extends InternalArgument> clazz) {
        return functions.get(clazz);
    }

    public static void addFunctions(Class<? extends InternalArgument> clazz, FunctionList functionsToAdd) {
        if (functionsToAdd == null) return;
        if (!addedFunctions.containsKey(clazz)) addedFunctions.put(clazz, new FunctionList());
        addedFunctions.get(clazz).addAll(functionsToAdd);
    }

    private static FunctionList getAddedFunctions(Class<? extends InternalArgument> clazz) {
        if (!addedFunctions.containsKey(clazz)) return new FunctionList();
        return addedFunctions.get(clazz);
    }

    private static final Map<Class<? extends InternalArgument>, StaticFunctionList> addedStaticFunctions = new HashMap<>();

    public StaticFunctionList getStaticFunctions() {
        return InternalArgument.getAddedStaticFunctions(myClass());
    }

    public static StaticFunctionList getStaticFunctionsFor(Class<? extends InternalArgument> clazz) {
        return staticFunctions.get(clazz);
    }

    public static void addStaticFunctions(Class<? extends InternalArgument> clazz, StaticFunctionList functionsToAdd) {
        if (functionsToAdd == null) return;
        if (!addedStaticFunctions.containsKey(clazz)) addedStaticFunctions.put(clazz, new StaticFunctionList());
        addedStaticFunctions.get(clazz).addAll(functionsToAdd);
    }

    private static StaticFunctionList getAddedStaticFunctions(Class<? extends InternalArgument> clazz) {
        if (!addedStaticFunctions.containsKey(clazz)) return new StaticFunctionList();
        return addedStaticFunctions.get(clazz);
    }

    // connecting forCommand to the InternalArguments
    private InternalStringArgument internalForCommand(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalStringArgument(target.forCommand());
    }

    // interacts with functions
    public final boolean hasFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return functions.get(myClass()).hasFunction(function, parameterTypes);
    }

    public final boolean hasStaticFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return staticFunctions.get(myClass()).hasFunction(function, parameterTypes);
    }

    public final Class<? extends InternalArgument> getReturnTypeForFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return functions.get(myClass()).getFunction(function, parameterTypes).getReturnType(parameterTypes);
    }

    public final Class<? extends InternalArgument> getReturnTypeForStaticFunction(String function, List<Class<? extends InternalArgument>> parameterTypes) {
        return staticFunctions.get(myClass()).getFunction(function, parameterTypes).getReturnType(parameterTypes);
    }

    public final InternalArgument runFunction(String function, List<InternalArgument> parameters) {
        List<Class<? extends InternalArgument>> parameterTypes = new ArrayList<>();
        for (InternalArgument p : parameters) {
            parameterTypes.add(p.getClass());
        }

        return functions.get(myClass()).getFunction(function, parameterTypes).run(this, parameters);
    }

    public final InternalArgument runStaticFunction(String function, List<InternalArgument> parameters) {
        List<Class<? extends InternalArgument>> parameterTypes = new ArrayList<>();
        for (InternalArgument p : parameters) {
            parameterTypes.add(p.getClass());
        }

        return staticFunctions.get(myClass()).getFunction(function, parameterTypes).run(parameters);
    }

    // abstract functions for dealing with value
    public abstract void setValue(Object arg);

    public abstract Object getValue();

    public abstract void setValue(InternalArgument arg);

    public abstract String forCommand();

    // class managing methods

    public Class<? extends InternalArgument> myClass() {
        return getClass();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof InternalArgument argument)) return false;
        return argument.getValue().equals(getValue());
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    public int hashCode() {
        return getClass().getSimpleName().hashCode();
    }
}
