This branch is focused on resolving [#22](https://github.com/willkroboth/ConfigCommands/issues/22), implementing a [CommandTree](https://commandapi.jorel.dev/8.5.1/commandtrees.html)-like syntax for the commands in the config.yml file.

More formally and fully, new commands should look like this:
```yaml
commands:
  commandName:
    shortDescription: [shortDescription] (optional)
    fullDescription: [fullDescription] (optional)
    permission: [permission] (optional)
    aliases: (optional)
      - alias1
      - alias2
      - ...
    executes:
      - [Commands]
    then:
      - ArgumentTree1
      - ArgumentTree2
      - ...
```
equivalent to this CommandTree:
```java
new CommandTree("commandName")
        .withShortDescription("[shortDescription]")
        .withFullDescription("[fullDescription]")
        .withPermission("[permission]")
        .withAliases("alias1", "alias2", "...")
        .executes((sender, args) -> {
            // run [Commands]
        })
        .then(
            [ArgumentTree1]
        )
        .then(
            [ArgumentTree2]
        )
        .then(
            [...]
        ).register();
```
where Argument Trees are represented like this:
```yaml
- name: [name]
  type: [type]
  argumentInfo:
    "[depends on type, eg subtype: greedy for type: String]"
  permission: [permission] (optional)
  executes:
    - [Commands]
  then:
    - ArgumentTree1
    - ArgumentTree2
    - ...
```
and make this Java code:
```java
InternalArgument.convertArgumentInformation("[name]", "[type]", [argumentInfo])
        .withPermission("[permission]")
        .executes((sender, args) -> {
            // run [Commands]
        })
        .then(
            [ArgumentTree1]
        ).then(
            [ArgumentTree2]
        ).then(
            [...]
        )
```
where `InternalArgument#convertArgumentInformation` looks something like this:
```java
public static Argument<?> convertArgumentInformation(String name, String type, ConfigurationSection argumentInfo)
```

An ArgumentTree without a type would become a LiteralArgument, and the MultiLiteralArgument should also be added as a builtin case.