# ConfigCommands v 3.0.0
## Supported Minecraft Versions:
- 1.16.5
- 1.17, 1.17.1
- 1.18, 1.18.1, 1.18.2
- 1.19, 1.19.1, 1.19.2
## Dependencies
[CommandAPI](https://commandapi.jorel.dev/) v 8.5.1 by [JorelAli](https://github.com/JorelAli) ([download](https://github.com/JorelAli/CommandAPI/releases/download/8.5.1/CommandAPI-8.5.1.jar))

For server owners: Make sure to put the CommandAPI.jar file in your plugins folder as well.

## Other libraries
[Reflections](https://github.com/ronmamo/reflections) by [ronmamo](https://github.com/ronmamo) maven release [org.reflections:reflections:0.10.2](https://github.com/ronmamo/reflections/releases/tag/0.10.2)

For server owners: No need to worry about this one, it is automatically imported using the Spigot library loader.

## Internal Arguments
InternalArguments define the classes and functions available in [Expressions](README.md#expressions), and the main purpose of [AddOns](AddOns.md) is to add more InternalArguments and functions. All InternalArguments extend the abstract class [InternalArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalArgument.java) and define a set of functions and static functions. 

The InternalArgument class provides one non-static method to all subarguments by default:
  
- forCommand
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Varies by implementation. Should return a string that represents the value in the context of a command.

Subclasses of the [FunctionAdder](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/FunctionAdder.java) can add more functions to existing InternalArguments, but this is described in more detail by the description of [AddOns](AddOns.md).

The main plugin provides 5 InternalArguments, described below, as well as the [InternalVoidArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalVoidArgument.java). The InternalVoidArgument does not have any static or non-static functions and should not be a parameter of any other function. It is returned by functions that shouldn't return anything.

### InternalArguments provided by ConfigCommands:
- [InternalArrayListArgument](#internalarraylistargument)
- [InternalBooleanArgument](#internalbooleanargument)
- [InternalCommandSenderArgument](#internalcommandsenderargument)
- [InternalIntegerArgument](#internalintegerargument)
- [InternalStringArgument](#internalstringargument)

### [InternalArrayListArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalArrayListArgument.java)
#### Adding to command: 
Cannot be added to command
#### Java Class: [ArrayList<InternalArgument>](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)
#### Functions:
- add
  
Parameters: AllInternalArguments
  
Result: InternalVoidArgument
  
Description: Adds any InternalArgument to the target ArrayList
  
- addAll
  
Parameters: InternalArrayListArgument
  
Result: InternalVoidArgument
  
Description: Adds all InternalArguments from the given ArrayList to the target ArrayList
  
- contains
  
Parameters: AllInternalArguments
  
Result: InternalBooleanArgument
  
Description: Returns true if the target ArrayList contain the given InternalArgument, and false otherwise
  
- forCommand
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Returns a String representing the target ArrayList with the format: `"[val1, val2, val3...]"`
  
- get
  
Parameters: InternalIntegerArgument, AllInternalArguments
  
Result: AllInternalArguments
  
Description: Returns the InternalArgument at the index of the given Integer. Uses the second parameter to determine the type of the result.
  
- indexOf
  
Parameters: AllInternalArguments
  
Result: InternalIntegerArgument
  
Description: Returns the index of the given InternalArgument or -1 if the value is not present in the target ArrayList
  
- lastIndexOf
  
Parameters: AllInternalArguments
  
Result: InternalIntegerArgument
  
Description: Returns the last index of the given InternalArgument or -1 if the value is not present in the target ArrayList
  
- remove
  
Parameters: InternalIntegerArgument
  
Result: InternalVoidArgument
  
Description: Removes the argument at the given index from the target ArrayList
  
- set
  
Parameters: InternalIntegerArgument, AllInternalArguments
  
Result: InternalVoidArgument
  
Description: Replaces the value at the given index with the InternalArgument given by the second parameter
  
- size
  
Parameters: None
  
Result: InternalIntegerArgument
  
Description: Returns the size of the target ArrayList
  
- subList
  
Parameters: InternalIntegerArgument, InternalIntegerArgument
  
Result: InternalArrayListArgument
  
Description: Creates a sublist from the first index inclusive to the last index exclusive of the target ArrayList
  
#### Static Class Name: ArrayList
  
#### Static Functions:
  
- new, ""
  
Parameters: None
  
Result: InternalArrayListArgument
  
Description: Creates a new empty ArrayList
  

### [InternalBooleanArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalBooleanArgument.java)
#### Adding to command: 
type: Boolean
#### Java Class: [Boolean](https://docs.oracle.com/javase/8/docs/api/java/lang/Boolean.html)
#### Functions:
- and, &&
  
Parameters: InternalBooleanArgument
  
Result: InternalBooleanArgument
  
Description: Returns the logical and of the given and target boolean
  
- forCommand
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Returns a string representing the target boolean, either `"true"` or `"false"`
  
- or, ||
  
Parameters: InternalBooleanArgument
  
Result: InternalBooleanArgument
  
Description: Returns the logical or of the given and target boolean
  
- not, !
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns the logical not of the target boolean

#### Static Class Name: Boolean
#### Static Functions:
- new, ""
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Creates a new boolean with the value of the given String. If the String equals `"true"`, ignoring case, the boolean will be `true`, otherwise it will be `false`.
  

### [InternalCommandSenderArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalCommandSenderArgument.java)
#### Adding to command: 
Cannot be added to command
#### Java Class: [CommandSender](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/command/CommandSender.html)
#### Functions:
- dispatchCommand
  
Parameters: InternalStringArgument
  
Result: InternalStringArgument
  
Description: Sends the given String as a command being sent by an operator level version of the target CommandSender and returns the last message sent to the CommandSender as a result of the command.
  
- forCommand
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Returns the name of the target CommandSender
  
- getName
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Returns the name of the target CommandSender
  
- getType
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Returns a String representing the type of the target CommandSender. Either `"player"`, `"entity"`, `"commandBlock"`, `"console"`, or `"proxy"`.
  
- hasPermission
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender has a permission represented by the given string, and false otherwise
  
- isCommandBlock
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender is an instance of [BlockCommandSender](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/command/BlockCommandSender.html) and false otherwise. Equivalent to `<sender>.getType().equals("commandBlock")`.
  
- isConsole
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender is an instance of [ConsoleCommandSender](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/command/ConsoleCommandSender.html) and false otherwise. Equivalent to `<sender>.getType().equals("console")`.
  
- isEntity
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender is an instance of [Entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Entity.html) and false otherwise. Equivalent to `<sender>.getType().equals("entity")`.
  
- isOp
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender is an operator and false otherwise.
  
- isPlayer
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender is an instance of [Player](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Player.html) and false otherwise. Equivalent to `<sender>.getType().equals("player")`.
  
- isProxy
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target CommandSender is an instance of [ProxiedCommandSender](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/command/ProxiedCommandSender.html) and false otherwise. Equivalent to `<sender>.getType().equals("_")`.
  
- sendMessage
  
Parameters: InternalStringArgument
  
Result: InternalVoidArgument
  
Description: Sends the given String to the target CommandSender as a chat message
  
- setOp
  
Parameters: InternalBooleanArgument
  
Result: InternalVoidArgument
  
Description: Sets the operator status of the target CommandSender to the given boolean value

#### Static Class Name: CommandSender
#### Static Functions:
None by default
  

### [InternalIntegerArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalIntegerArgument.java)
#### Adding to command: 
type: Integer
  
min - determines the minimum value that may be input to the command
  
max - determines the maximum value that may be input to the command
#### Java Class: [Integer](https://docs.oracle.com/javase/8/docs/api/java/lang/Integer.html)
#### Functions:
- forCommand
  
Parameters: None
  
Result: InternalStringArgument
  
Description: Returns the decimal string representation of the target Integer

#### Static Class Name: Integer
#### Static Functions:
- new, ""
  
Parameters: None / InternalStringArgument
  
Result: InternalIntegerArgument
  
Description: Creates a new Integer. If a string is passed in it will attempt to use it as the value. If no arguments are given, the Integer will default to 0.
  

### [InternalStringArgument](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/InternalArguments/InternalStringArgument.java)
#### Adding to command: 
type: String
  
subtype: None / `string` / `text` / `greedy`
- None -> StringArgument
- `string` -> StringArgument
- `text` -> TextArgument
- `greedy` -> GreedyStringArgument
  
See the CommandAPI documentation for a great description of the different [StringArgument](https://commandapi.jorel.dev/7.0.0/stringarguments.html) types
#### Java Class: [String](https://docs.oracle.com/javase/7/docs/api/java/lang/String.html)
#### Functions:
  
- charAt
  
Parameters: InternalIntegerArgument
  
Result: InternalStringArgument
  
Description: Returns the character found at the given index in the target string
  
- contains
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Returns true if the given string is found within the target string and false otherwise
  
- endsWith
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Returns true if the target string ends with the given string and false otherwise
  
- equals
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Returns true if the given string is the same as the target string and false otherwise
  
- equalsIgnoreCase
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Returns true if the given string is the same as the target string, ignoring differences in capitalization, and false otherwise 
  
- indexOf
  
Parameters: InternalStringArgument / InternalStringArgument, InternalIntegerArgument
  
Result: InternalIntegerArgument
  
Description: If only a string is input, returns the index of the first character of the first instance of the given string in the target string. If a string and an int are input, the first instance of the given string in the target string must be after the given index. If an index is not found, returns -1.
  
- isEmpty
  
Parameters: None
  
Result: InternalBooleanArgument
  
Description: Returns true if the target string has a length of 0 and false otherwise
  
- join
  
Parameters: AllInternalArguments
  
Result: InternalStringArgument
  
Description: Returns the result of concatenating the target String with the given InternalArgument. It uses the `forCommand()` method to convert the InternalArgument to a String.
  
- lastIndexOf
  
Parameters: InternalStringArgument / InternalStringArgument, InternalIntegerArgument
  
Result: InternalIntegerArgument
  
Description: If only a string is input, returns the index of the first character of the last instance of the given string in the target string. If a string and an int are input, the las instance of the given string in the target string must be before the given index. If an index is not found, returns -1.
  
- length
  
Parameters: None
  
Result: InternalIntegerArgument
  
Description: Returns the number of characters in the target string
  
- replace
  
Parameters: InternalStringArgument, InternalStringArgument
  
Result: InternalStringArgument
  
Description: Returns the result of replacing all instances of the first given string in the target string with the second given string
  
- startsWith
  
Parameters: InternalStringArgument
  
Result: InternalBooleanArgument
  
Description: Returns true if the target string starts with the given string and false otherwise
  
- substring
  
Parameters: InternalIntegerArgument / InternalIntegerArgument, InternalIntegerArgument
  
Result: InternalStringArgument
  
Description: If only one int is given, returns a string made up of the characters found after the given index inclusive. If two ints are given, returns a string made up of the characters starting at the first index inclusive until the last index exclusive.
  
- toInt
  
Parameters: None
  
Result: InternalIntegerArgument
  
Description: Returns the Integer represented by the target String

#### Static Class Name: String
#### Static Functions:
None by default.
  
