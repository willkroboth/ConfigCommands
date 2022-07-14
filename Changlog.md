## [ConfigCommands v 2.0.0](/Releases/ConfigCommands-2.0.0.jar) (current)
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
