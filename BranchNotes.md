This branch is focused on resolving [#25](https://github.com/willkroboth/ConfigCommands/issues/25), implementing better messages for `/configcommands functions`.

As an example, the messages currently look like this:
```
Console:
> configcommands functions configcommands Boolean nonStatic !
[INFO]: Aliases: [!, not]
[INFO]: Possible parameters:
  var.!() -> Boolean
  var.not() -> Boolean
> configcommands functions configcommands Boolean nonStatic and
[INFO]: Aliases: [&&, and]
[INFO]: Possible parameters:
  var.&&(Boolean) -> Boolean
  var.and(Boolean) -> Boolean
```
I want it to look more like this:

```
Console:
> configcommands functions configcommands Boolean nonStatic !
[INFO]: Class: Boolean
[INFO]: Function: not
[INFO]: Aliases:
[INFO]:   - !
[INFO]: Description: Returns the logical not of this Boolean
[INFO]: Parameters: none
[INFO]: Returns: The opposite of this Boolean
[INFO]: Examples:
[INFO]:   Boolean.("true").not() -> Boolean.("false")
[INFO]:   Boolean.("false").!() -> Boolean.("true")
> configcommands functions configcommands Boolean nonStatic and
[INFO]: Class: Boolean
[INFO]: Function: and
[INFO]: Aliases:
[INFO]:   - &&
[INFO]: Description: Returns the logical and of this Boolean and another Boolean
[INFO]: Parameters:
[INFO]:   - Boolean other -> the other Boolean
[INFO]: Returns: true if this and the other Boolean are true and false otherwise
[INFO]: Examples:
[INFO]:   Boolean.("true").and(Boolean.("true")) -> Boolean.("true")
[INFO]:   Boolean.("false").and(Boolean.("true")) -> Boolean.("false")
[INFO]:   Boolean.("true").&&(Boolean.("false")) -> Boolean.("false")
[INFO]:   Boolean.("false").&&(Boolean.("false")) -> Boolean.("false")
```
To make this happen, a new function definition system is needed. Maybe something like this:
```java
new Function("not")
        .withAliases("!")
        .withDescription("Returns the logical not of this Boolean");
        .returns(InternalBooleanArgument.class, "The opposite of this Boolean")
        .withExamples(
                "Boolean.("true").not() -> Boolean.("false")",
                "Boolean.("false").!() -> Boolean.("true")"
        )
        
new Function("and")
        .withAliases("&&")
        .withDescription("Returns the logical and of this Boolean and another Boolean")
        .withParameters(
                new Parameter(InternalBooleanArgument.class, "other", "the other Boolean")
        )
        .returns(InternalBooleanArgument.class, "true if this and the other Boolean are true and false otherwise")
        .withExamples(
                "Boolean.(\"true\").and(Boolean.(\"true\")) -> Boolean.(\"true\")", 
                "Boolean.(\"false\").and(Boolean.(\"true\")) -> Boolean.(\"false\")", 
                "Boolean.(\"true\").&&(Boolean.(\"false\")) -> Boolean.(\"false\")",
                "Boolean.(\"false\").&&(Boolean.(\"false\")) -> Boolean.(\"false\")"
        )
```