package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.Definition;
import me.willkroboth.ConfigCommands.Functions.Function;
import me.willkroboth.ConfigCommands.Functions.FunctionCreator;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.*;
import me.willkroboth.ConfigCommands.Functions.StaticFunction;
import me.willkroboth.ConfigCommands.InternalArguments.HelperClasses.AllInternalArguments;
import me.willkroboth.ConfigCommands.RegisteredCommands.Expression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.reflections.scanners.Scanners.SubTypes;

public abstract class InternalArgument implements FunctionCreator {
    // constructors
    public InternalArgument(){

    }

    public InternalArgument(Object value){
        setValue(value);
    }

    public static InternalArgument getInternalArgument(Class<? extends InternalArgument> clazz){
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz + "Could not be turned into object.", e);
        }
    }

    private static FunctionAdder getFunctionAdder(Class<? extends FunctionAdder> clazz){
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(clazz + " could not be turned into object.", e);
        }
    }

    // providing and processing information related to InternalArguments
    private static final Map<String, List<InternalArgument>> pluginToInternalArguments = new HashMap<>();
    private static final Map<Class<? extends InternalArgument>, InternalArgument> classToObject = new HashMap<>();
    public static List<InternalArgument> getPluginInternalArguments(String pluginName){
        return pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT));
    }

    public static List<String> getNames(List<InternalArgument> internalArguments){
        List<String> names = new ArrayList<>(internalArguments.size());
        for(InternalArgument internalArgument: internalArguments){
            names.add(internalArgument.getName());
        }
        return names;
    }

    public static Map<Definition, Function> getFunctions(Class<? extends InternalArgument> clazz){
        return functions.get(clazz);
    }

    public static Map<Definition, StaticFunction> getStaticFunctions(Class<? extends InternalArgument> clazz){
        return staticFunctions.get(clazz);
    }

    public static List<String> getNames(Map<Definition, Function> functions){
        List<String> names = new ArrayList<>();

        for(Definition definition: functions.keySet()){
            String name = definition.getName();
            if(!names.contains(name)){
                names.add(name);
            }
        }

        return names;
    }

    public static List<String> getStaticNames(Map<Definition, StaticFunction> functions){
        List<String> names = new ArrayList<>();

        for(Definition definition: functions.keySet()){
            String name = definition.getName();
            if(!names.contains(name)){
                names.add(name);
            }
        }

        return names;
    }

    public static Map<Definition, Function> getAliases(String name, Map<Definition, Function> functions){
        List<Function> targets = new ArrayList<>();
        for (Definition definition: functions.keySet()){
            if(definition.getName().equals(name)){
                targets.add(functions.get(definition));
            }
        }

        Map<Definition, Function> out = new HashMap<>();
        for(Definition definition: functions.keySet()){
            if(definition.getName().equals(name) || targets.contains(functions.get(definition))){
                out.put(definition, functions.get(definition));
            }
        }

        return out;
    }

    public static Map<Definition, StaticFunction> getStaticAliases(String name, Map<Definition, StaticFunction> functions){
        List<StaticFunction> targets = new ArrayList<>();
        for (Definition definition: functions.keySet()){
            if(definition.getName().equals(name)){
                targets.add(functions.get(definition));
            }
        }

        Map<Definition, StaticFunction> out = new HashMap<>();
        for(Definition definition: functions.keySet()){
            if(definition.getName().equals(name) || targets.contains(functions.get(definition))){
                out.put(definition, functions.get(definition));
            }
        }

        return out;
    }

    public static String getParameterString(Map<Definition, Function> aliases){
        if(aliases.size() == 0) return "\n  none";
        StringBuilder out = new StringBuilder();

        for(Definition definition: aliases.keySet()){
            Function function = aliases.get(definition);
            List<Class<? extends InternalArgument>> parameter = definition.getParameters();

            out.append("\n  var.");
            out.append(definition.getName());
            out.append('(');
            if(parameter.size() != 0){
                for (Class<? extends InternalArgument> clazz : parameter) {
                    out.append(classToObject.get(clazz).getName());
                    out.append(", ");
                }
                out.delete(out.length() - 2, out.length());
            }
            out.append(") -> ");
            Class<? extends InternalArgument> clazz = function.getReturnType();
            out.append(clazz.isAssignableFrom(InternalVoidArgument.class)? "Void" : classToObject.get(clazz).getName());
        }

        return out.toString();
    }

    public static String getStaticParameterString(Map<Definition, StaticFunction> aliases){
        if(aliases.size() == 0) return "\n  none";
        StringBuilder out = new StringBuilder();

        for(Definition definition: aliases.keySet()){
            StaticFunction function = aliases.get(definition);
            List<Class<? extends InternalArgument>> parameter = definition.getParameters();

            out.append("\n  Class.");
            out.append(definition.getName());
            out.append('(');
            if(parameter.size() != 0){
                for (Class<? extends InternalArgument> clazz : parameter) {
                    out.append(classToObject.get(clazz).getName());
                    out.append(", ");
                }
                out.delete(out.length() - 2, out.length());
            }
            out.append(") -> ");
            Class<? extends InternalArgument> clazz = function.getReturnType();
            out.append(clazz.isAssignableFrom(InternalVoidArgument.class)? "Void" : classToObject.get(clazz).getName());
        }

        return out.toString();
    }

    public static Set<String> getArgumentTypes(){
        return typeMap.keySet();
    }

    // registering subclasses
    private static final Map<Class<? extends InternalArgument>, Map<Definition, Function>> functions = new HashMap<>();
    private static final Map<Class<? extends InternalArgument>, Map<Definition, StaticFunction>> staticFunctions = new HashMap<>();

    private static <T> Set<T> convertSet(Set<Class<?>> classes){ return new HashSet<>((Collection<? extends T>) classes); }

    @SuppressWarnings("unused")
    public static void registerFullPackage(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger){
        logger.info("Registering InternalArguments and FunctionAdders for package: " + packageName + " with classLoader: " + classLoader);
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));

        Set<Class<?>> internalArguments = reflections.get(SubTypes.of(InternalArgument.class).asClass(classLoader));
        if(debugMode) logger.info("InternalArguments found:\n\t" + internalArguments.toString().replace(", ", ",\n\t"));
        registerSetOfInternalArguments(convertSet(internalArguments), pluginName, debugMode, logger);

        Set<Class<?>> functionAdders = reflections.get(SubTypes.of(FunctionAdder.class).asClass(classLoader));
        if(debugMode) logger.info("FunctionAdders found:\n\t" + functionAdders.toString().replace(", ", ",\n\t"));
        registerSetOfFunctionAdders(convertSet(functionAdders), pluginName, debugMode, logger);
    }

    public static void registerPackageOfInternalArguments(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger){
        logger.info("Registering InternalArguments for package: " + packageName + " with classLoader: " + classLoader);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));
        Set<Class<?>> classes = reflections.get(SubTypes.of(InternalArgument.class).asClass(classLoader));
        if (debugMode) logger.info("Classes found:\n\t" + classes.toString().replace(", ", ",\n\t"));

        registerSetOfInternalArguments(convertSet(classes), pluginName, debugMode, logger);
    }

    public static void registerSetOfInternalArguments(Set<Class<? extends InternalArgument>> classes, String pluginName, boolean debugMode, Logger logger){
        if(!pluginToInternalArguments.containsKey(pluginName.toLowerCase(Locale.ROOT)))
            pluginToInternalArguments.put(pluginName.toLowerCase(Locale.ROOT), new ArrayList<>());
        for(Class<? extends InternalArgument> clazz:classes){
            registerInternalArgument(clazz, pluginName, debugMode, logger);
        }
        if (debugMode) logger.info("All classes registered");
    }

    private static final Map<String, InternalArgument> typeMap = new HashMap<>();
    private static void registerInternalArgument(Class<? extends InternalArgument> clazz, String pluginName, boolean debugMode, Logger logger){
        if(clazz.isAssignableFrom(InternalVoidArgument.class)) return;

        InternalArgument object;
        try {
            object = getInternalArgument(clazz);
        } catch (IllegalArgumentException e) {
            // Make sure class can be turned into an object for future use
            logger.log(Level.SEVERE, "Error when registering InternalArgument subclass: " + clazz.getSimpleName());
            logger.log(Level.SEVERE, e.getMessage());
            if(debugMode) e.printStackTrace();
            return;
        }
        pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT)).add(object);
        classToObject.put(clazz, object);

        AllInternalArguments.addToAllClasses(clazz);
        Expression.addToClassMap(object);

        String type = object.getTypeTag();
        if(type == null){
            if(debugMode) logger.info(clazz + " gave null typeTag. It will not be able to be used as a command argument");
        } else {
            typeMap.put(type, object);
        }
    }

    @SuppressWarnings("unused")
    public static void registerPackageOfFunctionAdders(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger){
        logger.info("Registering FunctionAdders for package: " + packageName + " with classLoader: " + classLoader);

        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader));
        Set<Class<?>> classes = reflections.get(SubTypes.of(FunctionAdder.class).asClass(classLoader));
        if (debugMode) logger.info("Classes found:\n\t" + classes.toString().replace(", ", ",\n\t"));

        registerSetOfFunctionAdders(convertSet(classes), pluginName, debugMode, logger);
    }

    public static void registerSetOfFunctionAdders(Set<Class<? extends FunctionAdder>> classes, String pluginName, boolean debugMode, Logger logger){
        if(!pluginToInternalArguments.containsKey(pluginName.toLowerCase(Locale.ROOT)))
            pluginToInternalArguments.put(pluginName.toLowerCase(Locale.ROOT), new ArrayList<>());
        for(Class<? extends FunctionAdder> clazz:classes){
            registerFunctionAdder(clazz, pluginName, debugMode, logger);
        }
        if (debugMode) logger.info("All classes registered");
    }

    private static void registerFunctionAdder(Class<? extends FunctionAdder> clazz, String pluginName, boolean debugMode, Logger logger){
        FunctionAdder object;
        try {
            object = getFunctionAdder(clazz);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Error when registering FunctionAdder: " + clazz.getSimpleName());
            logger.log(Level.SEVERE, e.getMessage());
            if(debugMode) e.printStackTrace();
            return;
        }
        InternalArgument subObject;
        try {
            subObject = getInternalArgument(object.getClassToAddTo());
        } catch (IllegalArgumentException e) {
            logger.warning("Could not turn FunctionAdder's (" + clazz + ") InternalArgument (" + object.getClassToAddTo() + ") into an object");
            if(debugMode) e.printStackTrace();
            return;
        }

        pluginToInternalArguments.get(pluginName.toLowerCase(Locale.ROOT)).add(subObject);

        addFunctions(object.getClassToAddTo(), object.getAddedFunctions());
        addStaticFunctions(object.getClassToAddTo(), object.getAddedStaticFunctions());
    }

    public static void createFunctionMaps(){
        ConfigCommandsHandler.logNormal("");
        ConfigCommandsHandler.logNormal("Initializing function maps");
        ConfigCommandsHandler.increaseIndentation();
        for(Class<? extends InternalArgument> clazz: AllInternalArguments.getFlat()) {
            InternalArgument object = getInternalArgument(clazz);

            ConfigCommandsHandler.logDebug(object.toString());
            try {
                functions.put(clazz, Map.ofEntries(object.getFunctions().toArray(new FunctionEntry[0])));
                staticFunctions.put(clazz, Map.ofEntries(object.getStaticFunctions().toArray(new StaticFunctionEntry[0])));
            } catch (Exception e){
                ConfigCommandsHandler.logError("Unexpected fatal exception when setting function map for %s", object);

                try { object.getFunctions(); }
                catch (Exception ignored){ ConfigCommandsHandler.logError("Couldn't get functions"); }

                try { object.getStaticFunctions(); }
                catch (Exception ignored){ ConfigCommandsHandler.logError("Couldn't get static functions"); }

                throw e;
            }
        }
        ConfigCommandsHandler.decreaseIndentation();
    }

    public static String formatArgumentName(String name){
        if(!name.startsWith("<")){
            name = "<" +name;
        }
        if(!name.endsWith(">")){
            name = name + ">";
        }
        return name;
    }

    public static Argument<?> convertArgumentInformation(String name, String type,
                                                         Map<String, Class<? extends InternalArgument>> argumentClasses,
                                                         Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        if(!typeMap.containsKey(type))
            throw new IncorrectArgumentKey(name, "type", "\"" + type + "\" is not a recognized type that can be added to a command.");

        InternalArgument object = typeMap.get(type);
        String argumentName = formatArgumentName(name);
        argumentClasses.put(argumentName, object.getClass());
        ConfigCommandsHandler.logDebug(localDebug, "Argument %s available as %s", argumentName, object.getClass().getSimpleName());
        try {
            return object.createArgument(name, argumentInfo, localDebug);
        } catch (RuntimeException e){
            throw new IncorrectArgumentKey(name, "argumentInfo", e.getMessage());
        }
    }

    // defining important variables for logic and function
    public String getName(){
        // default assumes class name is formatted like: Internal"name"Argument
        String name = this.getClass().getSimpleName();
        name = name.substring(8, name.length()-8);
        return name;
    }

    // Note: while getTypeTag and createArgument both have default methods, at least one should be overridden
    // Most common override for getTypeTag should be to return null to disable being added as an argument
    public String getTypeTag(){
        // default type tag is the name
        // classes may override with null to disable being added as argument to commands
        return getName();
    }

    protected <T> T assertArgumentInfoClass(@NotNull Object argumentInfo, Class<? extends T> clazz, String arg) throws IncorrectArgumentKey {
        if (clazz.isAssignableFrom(argumentInfo.getClass())) return clazz.cast(argumentInfo);
        throw new IncorrectArgumentKey(arg, "argumentInfo", "Expected argumentInfo to have class " + clazz.getSimpleName());
    }

    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey{
        throw new IncorrectArgumentKey(name, "type", getTypeTag() + " cannot be an argument");
    }

    // manage function arrays
    private static final Map<Class<? extends InternalArgument>, FunctionList> addedFunctions = new HashMap<>();

    public FunctionList getFunctions(){
        return merge(InternalArgument.getAddedFunctions(myClass()),
                entries(
                        entry(new Definition("forCommand", args()),
                                new Function(this::internalForCommand, InternalStringArgument.class))
                )
        );
    }

    public static void addFunctions(Class<? extends InternalArgument> clazz, FunctionList functionsToAdd){
        if(functionsToAdd == null) return;
        if(!addedFunctions.containsKey(clazz)) addedFunctions.put(clazz, new FunctionList());
        addedFunctions.get(clazz).addAll(functionsToAdd);
    }

    private static FunctionList getAddedFunctions(Class<? extends InternalArgument> clazz){
        if(!addedFunctions.containsKey(clazz)) return new FunctionList();
        return addedFunctions.get(clazz);
    }

    private static final Map<Class<? extends InternalArgument>, StaticFunctionList> addedStaticFunctions = new HashMap<>();

    public StaticFunctionList getStaticFunctions(){
        return InternalArgument.getAddedStaticFunctions(myClass());
    }

    public static void addStaticFunctions(Class<? extends InternalArgument> clazz, StaticFunctionList functionsToAdd){
        if(functionsToAdd == null) return;
        if(!addedStaticFunctions.containsKey(clazz)) addedStaticFunctions.put(clazz, new StaticFunctionList());
        addedStaticFunctions.get(clazz).addAll(functionsToAdd);
    }

    private static StaticFunctionList getAddedStaticFunctions(Class<? extends InternalArgument> clazz){
        if(!addedStaticFunctions.containsKey(clazz)) return new StaticFunctionList();
        return addedStaticFunctions.get(clazz);
    }

    // connecting forCommand to the InternalArguments
    private InternalStringArgument internalForCommand(InternalArgument target, List<InternalArgument> parameters){
        return new InternalStringArgument(target.forCommand());
    }

    // interacts with functions
    public final boolean hasFunction(String function, ArgList parameterTypes){
        return functions.get(myClass()).containsKey(new Definition(function, parameterTypes));
    }

    public final boolean hasStaticFunction(String function, ArgList parameterTypes){
        return staticFunctions.get(myClass()).containsKey(new Definition(function, parameterTypes));
    }

    public final Class<? extends InternalArgument> getReturnTypeForFunction(String function, ArgList parameterTypes) {
        return functions.get(myClass()).get(new Definition(function, parameterTypes)).getReturnType();
    }

    public final Class<? extends InternalArgument> getReturnTypeForStaticFunction(String function, ArgList parameterTypes) {
        return staticFunctions.get(myClass()).get(new Definition(function, parameterTypes)).getReturnType();
    }

    public final InternalArgument runFunction(String function, List<InternalArgument> parameters) {
        ArgList parameterTypes = new ArgList();
        for(InternalArgument p:parameters){
            parameterTypes.add(p.getClass());
        }

        return functions.get(myClass()).get(new Definition(function, parameterTypes)).run(this, parameters);
    }

    public final InternalArgument runStaticFunction(String function, List<InternalArgument> parameters) {
        ArgList parameterTypes = new ArgList();
        for(InternalArgument p:parameters){
            parameterTypes.add(p.getClass());
        }

        return staticFunctions.get(myClass()).get(new Definition(function, parameterTypes)).run(parameters);
    }

    // abstract functions for dealing with value
    public abstract void setValue(Object arg);

    public abstract Object getValue();

    public abstract void setValue(InternalArgument arg);

    public abstract String forCommand();

    // class managing methods

    public Class<? extends InternalArgument> myClass() { return getClass(); }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof InternalArgument argument)) return false;
        return argument.getValue().equals(getValue());
    }

    public String toString(){ return getClass().getSimpleName(); }

    public int hashCode() { return getClass().getSimpleName().hashCode(); }
}

