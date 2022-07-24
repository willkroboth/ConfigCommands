# ConfigCommands AddOns

The base ConfigCommands plugin only supports [5 InternalArguments](Plugin%20Description.md#internalarguments-provided-by-configcommands), but it also features an AddOn system for adding new InternalArguments and Functions. There are two sides of AddOns, development and usage, described below.

## Addons for Server Owners

Adding AddOns to the ConfigCommands base is as easy as adding any other plugin because each AddOn is literally a plugin. Just download the Addon and put it in your plugins folder! It will be automatically detected and integrated into every feature of ConfigCommands: adding arguments, creating expressions, and using `/configcommandhelp`.

### Known Addons:
1. [NumberArguments](https://github.com/willkroboth/NumberArguments)

## Addons for Developers

The following section serves to describe how one might go about creating their own ConfigCommand AddOn. It assumes a basic experience with creating plugins and using Java, and is split up into 5 sections: [Plugin Class](#plugin-class), [Creating InternalArguments](#creating-internalarguments), [Creating FunctionAdders](#creating-functionadders), [Registering InternalArguments and FunctionAdders](#registering-internalarguments-and-functionadders), and [Building Functions](#building-functions). For a full example of creating an AddOn with new InternalArguments and FunctionAdders, see the [NumberArguments](https://github.com/willkroboth/NumberArguments) AddOn.

### Plugin Class
#### TLDR; Extend ConfigCommandAddOn instead of JavaPlugin, make `getPackageName()` return the name of your package.
As mentioned earlier, all AddOns are plugins, so your project can be set up much the same way as any other plugin. To turn a regular plugin into an AddOn, the only difference is that instead of extending JavaPlugin directly like normal, your main class should extend the abstract class [ConfigCommandAddOn](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/HelperClasses/ConfigCommandAddOn.java). ConfigCommandAddOn extends JavaPlugin, so you can still use features such as `OnEnable()` or handling a config file if you want, but this signifies to the main plugin that this AddOn exists.

ConfigCommandAddOn provides a couple of default methods that make registering your AddOn's InternalArguments and FunctionAdders very easy, while also providing the opportunity to handle everything yourself if you choose.

#### getPackageName()
If you like the simple route, the only thing you need to do is implement the method `getPackageName()` and make it return the name of the package containing the classes of your plugin. Everything else is already taken care of, and you can move on to creating InternalArguments and FunctionAdders. A good example of this is the [NumberArguments](https://github.com/willkroboth/NumberArguments/blob/main/src/main/java/me/willkroboth/NumberArguments/NumberArguments.java) AddOn, whose main class has only 9 lines of code:
```java
package me.willkroboth.NumberArguments;

import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;

public class NumberArguments extends ConfigCommandAddOn {
    protected String getPackageName() {
        return "me.willkroboth.NumberArguments";
    }
}
```
#### getRegisterMode()
Implementing this method allows for a bit more control over what ConfigCommands does with your plugin. An implementation of `getRegisterMode()` should either return `0`, `1`, or `2`, which have the following effects:
- `0`: Register all InternalArguments and FunctionAdders in the package
- `1`: Register all InternalArguments in the package
- `2`: Register all FunctionAdders in the package

The default implementation of `getRegisterMode()` returns `0`, so the default behavior of an AddOn is to register all InternalArguments and FunctionAdders present in the package. If your AddOn only contains either InternalArguments or FunctionAdders, you can change the register mode to skip searching for the other type. A good example of this is the [ConfigCommands](ConfigCommands-plugin/src/main/java/me/willkroboth/ConfigCommands/ConfigCommands.java) base, which only has InternalArguments and so uses register mode 1:
```java
package me.willkroboth.ConfigCommands;

import me.willkroboth.ConfigCommands.HelperClasses.ConfigCommandAddOn;

public class ConfigCommands extends ConfigCommandAddOn {
    protected String getPackageName() {
        return "me.willkroboth.ConfigCommands.InternalArguments";
    }

    protected int getRegisterMode() {
        return 1;
    }
}
```
Additionally, if you would for some reason like to register neither InternalArguments nor FunctionAdders, you could make `getRegisterModer()` return any int other than `0`, `1`, or `2`.

#### registerInternalArguments()
If you need more control over how your InternalArguments and FunctionAdders are registered, you can implement the `registerInternalArguments()` method. The default implementation in ConfigCommandAddOn uses `getRegisterMode()` to choose between 3 ways of registering the InternalArguments and FunctionAdders, mentioned previously. If you need to be more precise, you can use the methods described in [Registering InternalArguments and FunctionAdders](#registering-internalarguments-and-functionadders) to do things however you want.

### Creating InternalArguments
InternalArguments are the equivalent of classes in the ConfigCommand system, defining static and non-static functions that users can use in their commands. InternalArguments can also be added to commands to allow input from players. Each InternalArgument you make should extend the abstract class [InternalArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalArgument.java), and the idea is that each InternalArgument is responsible for representing a certain Java class in the Expression system, like an Integer or CommandSender. By implementing various methods described below, you can translate the behavior of each class into the system.

#### Constructor
In order to find and use your InternalArgument, you must define a default constructor for your class. This constructor doesn't need to and probably shouldn't do anything, but if it doesn't exist your InternalArgument will fail to register and be ignored. You are free to add any other constructors you need. I suggest creating a constructor that takes in an object of the type you store as the InternalArgument's `value`, which makes it easier to create InternalArguments with a value in one line. A simple example of both these constructors is seen in [InternalIntegerArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalIntegerArgument.java):
```java
public class InternalIntegerArgument extends InternalArgument{
    private int value;

    public InternalIntegerArgument() {
    }

    public InternalIntegerArgument(int value) {
        super(value);
    }
}
```

#### getValue(), setValue(Object arg), setValue(InternalArgument arg), forCommand()
These 4 methods must be implemented and allow other systems to set and access an instance of the class that an instance of an InternalArgument holds. Here is a simple example of implementing these methods from [InternalIntegerArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalIntegerArgument.java):
```java
public class InternalIntegerArgument extends InternalArgument{
    private int value;
    
    public void setValue(Object arg) { value = (int) arg; }

    public Object getValue() { return value; }

    public void setValue(InternalArgument arg) { value = (int) arg.getValue(); }

    public String forCommand() { return "" + value; }
}
```
The InternalIntegerArgument represents the Java int, and it stores one in the instance variable `value`. The two `setValue()` methods change this value, while `getValue()` offers the value. `forCommand()` outputs the value as a string.

A more complicated implementation is found in [InternalCommandSenderArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalCommandSenderArgument.java):
```java
import me.willkroboth.ConfigCommands.NMS.OpSender;
import org.bukkit.command.CommandSender;

public class InternalCommandSenderArgument extends InternalArgument {
    private CommandSender value;
    private OpSender opSender;

    public void setValue(Object arg) {
        value = (CommandSender) arg;
        opSender = OpSender.makeOpSender(value);
    }

    public Object getValue() {
        return value;
    }

    public OpSender getOpSender() {
        return opSender;
    }

    public void setValue(InternalArgument arg) {
        value = (CommandSender) arg.getValue();
        opSender = OpSender.makeOpSender(value);
    }

    public String forCommand() {
        return value.getName();
    }
}
```
InternalCommandSenderArgument represents the Bukkit class CommandSender, but it also needs to keep track of an OpSender version for its functions. It makes sure its `opSender` is correctly updated alongside its `value`, as well as providing the method `getOpSender()`. The best way to represent a CommandSender in a command is using its name, so `forCommand()` uses the `getName()` method instead of `toString()`.

#### getName(), getTypeTag(), addArgument(...)
These methods define how the InternalArgument appears and can be accessed within the Command system. While they all have default implementations in InternalArgument, you should probably implement one or two of them.

`getName()` is primarily used as the static class name that references your InternalArgument, and will also appear when using `/configcommandhelp` to choose an InternalArgument to view the functions of. The default implementation uses your class's name and assumes it is formatted as `Internal[name]Argument`. If you name your class with a different format or want a different String to be used to reference its static functions, you should implement this method.

`getTypeTag()` is used to determine the type string that an argument needs to use to add your InternalArgument to a command. The default implementation directly returns the result of `getName()`, so if you want to use a different String from the name you should implement this method. Alternatively, if you do not want your InternalArgument to be available in commands, you should make this method return `null`.

The `addArgument(...)` method has the full signature:
```java
void addArgument(
  Map<?, ?> arg, 
  CommandAPICommand command, 
  String name,
  ArrayList<String> argument_keys,
  HashMap<String, Class<? extends InternalArgument>> argument_variable_classes,
  boolean debugMode, 
  IndentedLogger logger
) throws IncorrectArgumentKey
```
It is called when a command is trying to add your argument to its list. The default implementation throws an error explaining that this argument cannot be added to a command. If you did not make `getTypeTag()` return null to disable this feature on your InternalArgument, you should implement this method.

The minimum expected behavior is presented by [InternalBooleanArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalBooleanArgument.java):
```java
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InternalBooleanArgument extends InternalArgument {
    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) {
        command.withArguments(new BooleanArgument(name));
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalBooleanArgument.class);
    }
}
```
This implementation adds a new BooleanArgument to the backing CommandAPICommand, represented by `command`; adds `name` to the list of `argument_keys` this command will be able to use in its expressions; and maps the `name` to its class in the `argument_variable_classes` map.

A more complicated behavior is presented by [InternalStringArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalStringArgument.java):

```java
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;

import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.HelperClasses.IndentedLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InternalStringArgument extends InternalArgument {
    public void addArgument(Map<?, ?> arg, CommandAPICommand command, String name, ArrayList<String> argument_keys, HashMap<String, Class<? extends InternalArgument>> argument_variable_classes, boolean debugMode, IndentedLogger logger) throws IncorrectArgumentKey {
        String type = (String) arg.get("subtype");
        if (debugMode) logger.info("Arg has subtype: " + type);
        command.withArguments(
                type == null ? new StringArgument(name) :
                        switch (type) {
                            case "string" -> new StringArgument(name);
                            case "text" -> new TextArgument(name);
                            case "greedy" -> new GreedyStringArgument(name);
                            default ->
                                    throw new IncorrectArgumentKey(arg.toString(), "subtype", "Did not find StringArgument subtype: \"" + type + "\"");
                        }
        );
        argument_keys.add(name);
        argument_variable_classes.put(name, InternalStringArgument.class);
    }
}
```
InternalStringArgument supports adding the `subtype` parameter to choose between 3 different Argument types from the CommandAPI -- StringArgument, TextArgument, and GreedyString argument -- which you can read more about in the CommandAPI [documentation](https://commandapi.jorel.dev/7.0.0/stringarguments.html). This `addArgument(...)` implementation retrieves the `subtype` parameter from the `arg` map, sends a debug message using `debugMode` and `logger`, then chooses one of the argument types to add based on the subtype it found. If the given subtype is not valid, the method throws an IncorrectArgumentKey exception explaining this.

#### getFunctions(), getStaticFunctions()
These methods define the functions available for static and non-static references to your InternalArgument class in Expressions. The default implementation of `getFunctions()` references functions added to your class by FunctionAdders as well as `forCommand(None)`, which allows the `forCommand()` method mentioned earlier to be accessed in Expressions. The default implementation of `getStaticFunctions()` just references the static functions added by FunctionAdders. By creating your own implementation for these methods, you can add new static and non statics functions using the processes described in the section on [Building Functions](#building-functions).

### Creating FunctionAdders
The purpose of FunctionAdders is to add both static and non-static functions to existing InternalArguments. You might want to do this if your AddOn adds InternalArguments that give existing InternalArguments new functionality. For example, the [NumberArguments](https://github.com/willkroboth/NumberArguments) AddOn adds InternalArguments for other numbers like the float, long, and double, and so has the [IntegerFunctionAdder](https://github.com/willkroboth/NumberArguments/blob/main/src/main/java/me/willkroboth/NumberArguments/InternalArguments/IntegerFunctionAdder.java) to add operations like addition, multiplication, and subtraction between the already existing ints and the new floats, longs, and doubles. Each FunctionAdder you make should extend the abstract class [FunctionAdder](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/FunctionAdder.java), and implement some of the following methods:

#### getClassToAddTo()
The `getClassToAddTo()` method must be implemented by all FunctionAdders and indicates which InternalArgument the FunctionAdder will be adding functions to. For example, [IntegerFunctionAdder](https://github.com/willkroboth/NumberArguments/blob/main/src/main/java/me/willkroboth/NumberArguments/InternalArguments/IntegerFunctionAdder.java) wants to add functions to [InternalIntegerArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalIntegerArgument.java), so it makes `getClassToAddTo()` return `InternalIntegerArgument.class`, like so:
```java
import me.willkroboth.ConfigCommands.InternalArguments.FunctionAdder;
import me.willkroboth.ConfigCommands.InternalArguments.InternalIntegerArgument;

public class IntegerFunctionAdder extends FunctionAdder {
    public Class<? extends InternalArgument> getClassToAddTo() { return InternalIntegerArgument.class; }
}
```

#### getAddedFunctions(), getAddedStaticFunctions()
The default implementations of these methods return `null` to indicate that they don't add any functions. If you want your FunctionAdder to add static functions, you should implement `getAddedStaticFunctions()`, and if you want to add non-static functions, you should implement `getAddedFunctions()`. Function creation here should follow the methods described in the section on [Building Functions](#building-functions).

### Registering InternalArguments and FunctionAdders
To register InternalArguments and FunctionAdders, there are a few static methods from the [InternalArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalArgument.java) class that allow for increasing control over what gets registered. These methods must be run before or during the call to the `registerInternalArguments()` method of your AddOn to ensure that all InternalArguments and FunctionAdders are set up before the main plugin starts registering commands.

#### registerFullPackage(...), registerPackageOfInternalArguments(...), registerPackageOfFunctionAdders(...)
These methods use reflection to automatically find classes within a package that need to be registered. `registerFullPackage(...)` looks for both InternalArguments and FunctionAdders, while `registerPackageOfInternalArguments(...)` and `registerPackageOfFunctionAdders(...)` only look for InternalArguments or FunctionAdders respectively. The full signature of all three of these methods are as follows:
```java
void registerPackage(String packageName, String pluginName, ClassLoader classLoader, boolean debugMode, Logger logger)
```
A general example of these methods being used is found in the default implementation of `registerInternalArguments()` in the [ConfigCommandAddOn](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/HelperClasses/ConfigCommandAddOn.java) class.
```java
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ConfigCommandAddOn extends JavaPlugin {
    public void registerInternalArguments() {
        switch (getRegisterMode()) {
            case 0 -> InternalArgument.registerFullPackage(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case 1 -> InternalArgument.registerPackageOfInternalArguments(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
            case 2 -> InternalArgument.registerPackageOfFunctionAdders(
                    getPackageName(),
                    getName(),
                    getClassLoader(),
                    ConfigCommandsHandler.isDebugMode(),
                    getLogger()
            );
        }
    }
    
    
    protected int getRegisterMode() { return 0; }

    protected abstract String getPackageName();
}
```
`String packageName` represents the package your InternalArguments and FunctionAdders can be found in. In this case, the package name is accessed using the abstract `getPackageName()` method mentioned previously in the section on the [Plugin Class](#plugin-class).

`String pluginName` represents the name of your plugin, which is used in the `/configcommandhelp` command to associate InternalArguments to their AddOns. In this case, the plugin name is accessed using the `getName()` method provided by the JavaPlugin interface.

`ClassLoader classLoader` represents the class loader of your plugin, which is used to load the InternalArguments and FunctionAdders found. In this case, the classLoader is accessed using the `getClassLoader()` method provided by the JavaPlugin interface.

`boolean debugMode` is used to turn on and off debug messages that convey more specific information about the progress of the registering process. In this case, the debug state of the main plugin is read using the static `isDebugMode()` method of the [ConfigCommandsHandler](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/ConfigCommandsHandler.java) class. Note that the value returned by `isDebugMode()` is only set once the ConfigCommands plugin has read its config.yml file in its `onEnable()` method, and so can only be accessed by AddOns during or after the call to their `registerInternalArguments()` method.

`Logger logger` is the Logger to which debug and error messages are sent during the registering process. In this case, the logger is accessed using the `getLogger()` method provided by the JavaPlugin interface.

#### registerSetOfInternalArguments(...), registerSetOfFunctionAdders(...)
The full signature of these methods is:
```java
void registerSetOfType(Set<Class<? extends Type>> classes, String pluginName, boolean debugMode, Logger logger)
```
The only difference between these and the package methods is that the package methods find a set of classes to register, while you define a set of classes to register here. All the other parameters act the same as before.

### Building Functions
The most important function of InternalArguments and FunctionAdders is returning a list of static or non-static functions. All the classes and interfaces used to create functions are found in the package [me.willkroboth.ConfigCommands.Functions](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/Functions/). The most significant part of this collection is the [FunctionCreator](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/Functions/FunctionCreator.java) interface, which provides a few methods that make creating functions easier. Any class that needs to create functions should implement the FunctionCreator interface. The parent classes for InternalArguments and FunctionAdders already implement this interface, so the methods are automatically available to their subclasses.

The methods defined by FunctionCreator can be combined into their own sort of syntax, defined by the types taken in and returned by each method. This syntax is best explained by showing all the methods that might be used together.
```java
merge(FunctionList...) -> FunctionList;
staticMerge(StaticFunctionList..) -> StaticFunctionList;

entries(FunctionEntry...) -> FunctionList;
staticEntries(StaticFunctionEntry...) -> StaticFunctionList;

entry(Definition, Function) -> FunctionEntry;
staticEntry(Definition, StaticFunction) -> StaticFunctionEntry;

expandDefinition(List<String>, NestedArgList, Function) -> FunctionList;
staticExpandDefinition(List<String>, NestedArgList, StaticFunction) -> StaticFunctionList;

names(String...) -> List<String>;
args(ArgList...) -> NestedArgList;

args(Class<? extends InternalArgument>...) -> ArgList;

new Definition(String, ArgList) -> Definition;
new Function(InternalArgumentFunction, Class<? extends InternalArgument>) -> Function;
new StaticFunction(InternalArgumentStaticFunction, Class<? extends InternalArgument>) -> StaticFunction;
```
For InternalArguments, you would use these methods inside the `getFunctions()` and `getStaticFunctions()` methods, which want to return a FunctionList or StaticFunctionList respectively. You can also call `super.getFunctions()` or `super.getStaticFunctions()` which will return the FunctionLists of the parent class.

For FunctionAdders, you would use these methods inside the `getAddedFunctions()` and `getAddedStaticFunctions()` methods, which want to return a FunctionList or StaticFunctionList respectively.

As you can see, many methods are split into static and non-static variants, and they have the same behavior except that they handle either static or non-static functions. The following descriptions will only mention the non-static versions.

`merge` takes in multiple FunctionLists and concatenates them all together into 1 long FunctionList. This is useful for merging the outputs of multiple `expandDefinition` calls, usually one `entries` call, and, in the case of InternalArguments, a call to `super.getFunctions()`.

`entries` take multiple FunctionEntries and combines them into one FunctionList. This is useful for collecting multiple `entry` calls into a single output.

`entry` takes a Definition and Function and puts them together into a single FunctionEntry.

`expandDefinition` takes in a list of names, a NestedArgList, and a Function object and calls `entry` to create a new FunctionEntry for each combination of names and parameters. This is useful if you want the same Function to be accessed using multiple names and/or more than one set of parameters.

`names` and `args(ArgList...)` are used to create the list of names and NestedArgList for a call to `expandDefinition`.

`args(Class<? extends InternalArgument>...)` is used to create an ArgList. An ArgList stores the parameters of a Definition object.

Definition objects record a name and ArgList of parameters that characterizes how a Function should be called.

Function objects record a method to call to perform their function and a class that they return. The method passed into a function, [InternalArgumentFunction](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/Functions/InternalArgumentFunction.java), is a functional interface with the signature 
```
InternalArgument apply(InternalArgument target, List<InternalArgument> parameters)
```
You can fill this in with an appropriate lambda expression, or a reference to a method in your class. `target` represents the object on which the Function is being called and is guaranteed to be an instance of the class you are building functions for. `parameters` represents the objects being passed into the Function and is guaranteed to be filled with instances of the types put into the ArgList of the corresponding Definition. You should only need to check the state of your inputs if you assign multiple Definitions to the same function. [InternalArgumentStaticFunction](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/Functions/InternalArgumentStaticFunction.java) is almost the same; it just doesn't have a `target` object.

#### Example
I think the best way to clarify the above descriptions is to look at an example. All InternalArguments and FunctionAdders should build at least a couple functions, so anyone is a good choice to see a real application of these methods. However, just blindly looking at someone else's code isn't a very good way to get started, so I will try to explain an example from [InternalArrayListArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalArrayListArgument.java)
```java
public class InternalArrayListArgument extends InternalArgument {
    public FunctionList getFunctions() {
        return merge(super.getFunctions(),
                generateGets(),
                generateSets(),
                expandDefinition(
                        strings("add"), AllInternalArguments.get(),
                        new Function(this::add, InternalVoidArgument.class)
                ),
                entries(
                        entry(new Definition("size", args()),
                                new Function(this::size, InternalIntegerArgument.class)),
                        entry(new Definition("subList", args(InternalIntegerArgument.class, InternalIntegerArgument.class)),
                                new Function(this::subList, InternalArrayListArgument.class))
                )
        );
    }

    private FunctionList generateGets() {
        FunctionList gets = new FunctionList();
        for (Class<? extends InternalArgument> clazz : AllInternalArguments.getFlat()) {
            gets.add(entry(new Definition("get", args(InternalIntegerArgument.class, clazz)),
                    new Function(this::get, clazz)));
        }
        return gets;
    }

    private FunctionList generateSets() {
        FunctionList sets = new FunctionList();
        for (Class<? extends InternalArgument> clazz : AllInternalArguments.getFlat()) {
            sets.add(entry(new Definition("set", args(InternalIntegerArgument.class, clazz)),
                    new Function(this::set, InternalVoidArgument.class)));
        }
        return sets;
    }

    private ArrayList<InternalArgument> getList(InternalArgument target) {
        return (ArrayList<InternalArgument>) target.getValue();
    }
    
    public InternalVoidArgument add(InternalArgument target, List<InternalArgument> parameters) {
        getList(target).add(parameters.get(0));
        return InternalVoidArgument.getInstance();
    }

    public InternalArgument get(InternalArgument target, List<InternalArgument> parameters) {
        InternalIntegerArgument index = (InternalIntegerArgument) parameters.get(0);
        InternalArgument classReference = parameters.get(1);

        InternalArgument out = getList(target).get((int) index.getValue());
        if (!out.getClass().equals(classReference.getClass()))
            throw new CommandRunException("Tried to get " + classReference.getClass() + " from index " + index.getValue() + " but found " + out.getClass());
        return out;
    }

    private InternalArgument set(InternalArgument target, List<InternalArgument> parameters) {
        getList(target).set((int) parameters.get(0).getValue(), parameters.get(1));
        return InternalVoidArgument.getInstance();
    }
    
    private InternalArgument size(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalIntegerArgument(getList(target).size());
    }

    private InternalArgument subList(InternalArgument target, List<InternalArgument> parameters) {
        return new InternalArrayListArgument(getList(target).subList((int) parameters.get(0).getValue(), (int) parameters.get(1).getValue()));
    }

    public StaticFunctionList getStaticFunctions() {
        return staticMerge(super.getStaticFunctions(),
                staticExpandDefinition(
                        strings("", "new"), args(args()),
                        new StaticFunction(this::initialize, InternalArrayListArgument.class)
                )
        );
    }

    public InternalArgument initialize(List<InternalArgument> parameters) {
        return new InternalArrayListArgument(new ArrayList<>());
    }
}
```

The most notable feature of InternalArrayListArgument are the two methods `generateGets` and `generateSets`. The get and set functions had multiple possible input-output combinations that couldn't easily be generated by given methods like `expandDefinition`, so that part was outsourced to unique function calls. This is perfectly valid; you can build FunctionLists however you want, so if existing methods don't work, make your own way. Another example of a unique way of building functions is found in NumberArguments, which uses the interface [NumberFunctions](https://github.com/willkroboth/NumberArguments/blob/main/src/main/java/me/willkroboth/NumberArguments/InternalArguments/NumberFunctions.java) to build a repetitive set of math functions.

Another important feature on display is the class [AllInternalArguments](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/HelperClasses/AllInternalArguments.java). This class provides two methods for function building, `NestedArgList get()` and `ArgList getFlat()`. When ConfigCommands registers all the InternalArguments, it automatically populates this class with the classes it finds. If you want your function to accept any InternalArgument type, like the add function, you can pass the given `NestedArgList` into a call to `expandDefinition`.

Functions that don't have anything to return should return a [InternalVoidArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalVoidArgument.java). Since this object doesn't hold a value, you can access a shared singleton instance using `InternalVoidArgument.getInstance()`.

If you want your method to throw a custom exception, I suggest throwing a `CommandRunException`, as the get function does when the requested class does not match the given class. Other Exceptions are also caught, so you don't need to worry about catching those yourself, but it is cleaner if you wrap them with a `CommandRunException`.
