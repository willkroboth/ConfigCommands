## [ConfigCommands v 3.0.0](/Releases/ConfigCommands-3.0.0.jar)
### Features:
- [#19](https://github.com/willkroboth/ConfigCommands/issues/19) - Added support for Minecraft versions 1.16.5, 1.17, 1.17.1, 1.18, 1.18.1, 1.18.2, and 1.19 with one jar file
- Combined `/configcommandhelp`, `/configcommandbuild`, `/configcommandreload` into a single command `/configcommands`
- Renamed `/configcommands help` to `/configcommands functions`
- Added `/configcommands debug` for setting and reading local debug values
- Added `/configcommands help` for seeing help and usage information for the different branches of `/configcommands`

### Technical Changes:
- Refactored project as a multi-module maven project to better handle depending on different Spigot versions in the different NMS modules
- Added enum [ConfigCommandAddOn.RegisterMode](/ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/HelperClasses/ConfigCommandAddOn.java#L57) to let AddOns choose more exactly how they want to register thier InternalArguments and FunctionAdders
- Added package [SystemCommands](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/SystemCommands) and class [SystemCommandHandler](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/SystemCommands/SystemCommandHandler.java) to manage `/configcommands`
- Added class [ConfigCommandsHandler](ConfigCommands-core/src/main/java/me/willkroboth/ConfigCommands/ConfigCommandsHandler.java) to manage interactions with the ConfigCommands plugin

## [ConfigCommands v 2.0.0](/Releases/ConfigCommands-2.0.0.jar)
- [#8](https://github.com/willkroboth/ConfigCommands/issues/8) - Added `/configcommandreload` command for updating command behavior without needing to restart the server
- Fixed bug that prevented Expression function chains that started with a Static class reference

### Technical Changes:
- Now using Maven for dependency management and packaging
- Use Mojang-Mapped version of Craftbukkit and compile with special source
- Simplified nested package structure
- [#3](https://github.com/willkroboth/ConfigCommands/issues/3) - Ensured functions registered for the same InternalArgument have unique Definitions, preventing collisions when combining lists into a map
- [#6](https://github.com/willkroboth/ConfigCommands/issues/6) - Added different OpSender classes that disguise themselves as different CommandSender subclasses 

See all commits in [#18](https://github.com/willkroboth/ConfigCommands/pull/18)

## [ConfigCommands v 1.0.1](/Releases/ConfigCommands-1.0.1.jar)
- Fleshed out InternalStringArgument with relevant functions
- Add hasPermission function to InternalCommandSenderArgument
- Stopped InternalVoidArgument from being registered normally
- Fixed InternalArrayListArgument's remove function
- Fixed errors in show info menu of `/configcommandbuild`
- Added help messages for `/configcommandbuild` and `/configcommandhelp`

See full squashed commit here: [ConfigCommands-v-1.0.1](https://github.com/willkroboth/ConfigCommands/commit/5f95d9211ff1c0172487f018e4f9e81f55372397)

## [ConfigCommands v 1.0.0](/Releases/ConfigCommands-1.0.0.jar)  
- Initial release
