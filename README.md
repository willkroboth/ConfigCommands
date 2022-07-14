# ConfigCommands

## A Bukkit/Spigot plugin for creating commands in-game
The ConfigCommands plugin allows users to quickly and simply add commands to their server -- no custom plugins needed! This plugin removes the hassle of learning Java and/or a plugin API and lets beginning server owners go directly to creating custom commands to their liking (though more complicated features such as [Expressions](#expressions) may take significant learning anyway). For those who have made a plugin before, this plugin can still help them quickly add simple commands without creating a whole new plugin. This is an extension of my [customcommands](https://github.com/willkroboth/Minecraft-Plugins/tree/main/custom%20commands) project and builds off of the [CommandAPI](https://commandapi.jorel.dev/) developed by [JorelAli](https://github.com/JorelAli) to provide features such as type-safe arguments, custom `/help` menu descriptions, and compatibility with `/execute`. The base plugin only supports using Integers, Strings, and Booleans in commands, but this can be built upon using the AddOn system, which is further described in the [AddOns](AddOns.md) file.

Note that this project is still in early development and might see major changes to its systems. If you find any bugs or have feature suggestions, don't hesitate to make a report in the issues section of this repository.

## Creating Commands
Commands added using ConfigCommands are stored in the config.yml, and so may be edited in there. However, the plugin also provides the command `/configcommandbuild` to help create and edit commands. The command can be used in the console and by players, and uses a guided command system to lead the user through the steps of creating or editing a command. I hope that the command explains itself well enough, but an overview of each of the features the command edits is included [below](#command-features). Most changes can only be applied after restarting the server, but the behavior of the command, defined by the [commands](#commands) section, can be reloaded in-game using `/configcommandreload <commandName>`. 

## Command Features
The features of a command are best introduced using an example. The config.yml file is automatically populated with such an example, the echo command:
```yml
commands:
  echo:
    name: echo
    args:
      - name: <string>
        type: String
        subtype: greedy
    shortDescription: Echos input back to you.
    fullDescription: Takes in a string and sends it back to you.
    aliases:
      - cat
    permission: configcommands.echo
    commands:
      - do <sender>.sendMessage(<string>)
```
### Distinction between name and key
```yml
commands:
  echo:
    name: echo
```
All commands have a key and a name. The key is the section in the config.yml under the `commands` section that contains all the data for the command. The name is a section within the key that players use to run the command (`/name ...`). It makes sense to identify the command in the config.yml using its name, but they do not have to be the same.
### Arguments
```yml
commands:
  echo:
    args:
      - name: <string>
        type: String
        subtype: greedy
```
This section determines the arguments that the command takes in. If this section is empty, the command won't have any arguments. Each argument needs a `name` and `type`, and may have additional parameters added based on the type. Each argument should have a unique name. If not, the command will fail to register. Note that two arguments are added by default, `<sender>` and `<commandIndex>`, so those names cannot be used.

In this case, the echo command has 1 argument: `{name=<string>, type=String, subtype=greedy}`. The name is `<string>`, and this is how this argument will be referenced in the commands. The type is `String`, so this argument will take in text. This argument has an additional parameter, subtype, which has the value `greedy`. In the case of the type String, this makes the argument input all remaining text in the command. You can read more about argument types and their parameters in the [plugin description](/Plugin%20Description.md).

### Short and Full Description
```yml
commands:
  echo:
    shortDescription: Echos input back to you.
    fullDescription: Takes in a string and sends it back to you.
```
These parameters determine the descriptions shown when using the `/help` command. The short description is shown when the command appears in the help list, while the full description is shown when `/help <name>` is run. If either is not present, they will default to `A Mojang Provided Command`. See the [CommandAPI Help Documentation](https://commandapi.jorel.dev/7.0.0/help.html) for a more detailed description of the difference between these two.
### Aliases
```yml
commands:
  echo:
    aliases:
      - cat
```
This section is simply a list of the aliases for the command. If it is not present, the command will not have any aliases.

In this case there is 1 alias, `cat`, so the command can be run using either `/echo ...` or `/cat ...`.
### Permission
```yml
commands:
  echo:
    permission: configcommands.echo
```
This parameter defines the permission node a player needs to run the command. These permissions can be given out the same as any other.

If this value is not present, the command will be given the default permission `configcommands.[name]`.

### Commands
```yml
commands:
  echo:
    commands:
      - do <sender>.sendMessage(<string>)
```
This section defines what happens when the command is run. If this section is empty, the command will not be registered since it wouldn't do anything anyway. In this case, the command sends the string passed into the command back to the sender. If any command in this section cannot be parsed, the command will not register. The acceptable format of a command is as follows:
```
[Command]
[Variable] = [Command]
[Variable] = [Expression]
do [Expression]
tag "name of tag"
if [Expression resulting in Boolean] goto [Expression resulting in Integer or String]
goto [Expression resulting in Integer or String]
return [Expression]
```
`[Command]` can be any command, typed the same way a player would run a command

Examples:
- `/tp 0 50 0`
- `/say Hello World`
- `/kill @e[type=!player]`

`[Variable]` refers to the name of a variable. Variables are described in more detail [below](#variables).

`[Expression]` can be any Expression, described in more detail [below](#expressions).

`"name of tag"` can be any text

Examples:
- `tag Option 1`
- `tag count loop`

`if` and `goto` are used to perform branching and looping. The first expression of an `if` must return true or false. If the expression is true, execution will jump to the target value; if false it will continue to the next line. The target of an if and goto must either return an integer, representing a line number to go to, or a string, representing a tag to go to.

Examples:
- `if <message>.equals("1") goto "Option 1"`
- `goto Integer.("4")`

`return` send a result to `<sender>`, equivalent to `<sender>.sendMessage([Expression])`, then end the command's execution.

Execution will also end if there is not a command at the current index, such as at the end of the list, or if an invalid number is jumped to by an if or goto command.

#### Note on running commands
The original motivation for this plugin was to allow sever administrators to allow non-admin players to only run commands with specific arguments. For example, a `/spawn` command:
```yml
commands:
  spawn:
    permission: server.spawn
    commands:
      - /tp 0 50 0
```
This way, the admin can let players teleport to spawn by giving them `server.spawn` instead of the power to teleport anywhere with `minecraft.command.teleport`. Therefore, all commands listed will be run as if the player who sent the command had operator status.

#### Variables
A variable is indicated by surrounding a name with < >. Two variables, `<sender>` and `<commandIndex>`, are available by default, and any arguments added to the command can be accessed using `<name>` (Note: you do not need to add another set of < > if they are already present around the argument's name). New variables can be created, or old ones set to a different value, using `<name> = [value]`. `[value]` can be a command (`<result> = /data get entity @p Health`) or an expression (`<counter> = Integer.("10")`). When setting an old variable to a value, the type of `[value]` must match the type already in the variable. When creating a new variable, it's type will be automatically set to whatever the type of `[value]` is.

Commands can also reference variables just by inserting them in the command. For example, the command `/tellraw <sender> {"color":"red","text":"<message>"}` where `<sender>` is the Player willkroboth and `<message>` is the String `"Hello"` will become `/tellraw willkroboth {"color":"red","text":"Hello"}` and so send a red `"Hello"` to willkroboth.

#### Expressions
Expressions are a vital part of the command running system, defined by the [Expression.java](/src/main/java/me/willkroboth/ConfigCommands/HelperClasses/Expression.java) file. Expressions have their own format, as follows:
```
"[value of a string]"
<[variable name]>
[Class name].[function name]([parameters])
[Expression].[function name]([parameters])
```
Strings are the simplest values, easily created by surrounding any text with `" "`. Other constant values, such as Integers, can be created by passing strings into their static constructor functions.

Variables can be referenced using `< >` around their name. When parsing, the plugin will make sure that the variable exists before it is referenced.

There are two types of functions and so two ways to call functions. Static functions are called using the name of the class they belong to, while non-static functions can be called on another `[Expression]`. `[parameters]` is a list of expressions, separated by `, `, that are used as the arguments to the function. Since calling a function is also an expression, you can chain function calls together in the same line. When parsing, the plugin will make sure the requested function with the given parameters exists.

The functions and class names that can be used in Expressions are defined by [InternalArguments](Plugin%20Description.md#internal-arguments) and you can read about the InternalArguments in each PluginDescription. The ConfigCommands plugin also provides the command `/configcommandhelp` to help users see what functions are currently available on their server. You can explore the available options in a guided menu system by just running `/configcommandhelp`, or through tab-complete suggestions. You can find the aliases of a function as well as the possible parameters and outputs.

Examples:
- `<sender>.sendMessage("Hello World")`
- `<arrayList>.get(Integer.("0"), "")`
