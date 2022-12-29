package me.willkroboth.ConfigCommands.InternalArguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.IncorrectArgumentKey;
import me.willkroboth.ConfigCommands.Functions.InstanceFunction;
import me.willkroboth.ConfigCommands.Functions.InstanceFunctionList;
import me.willkroboth.ConfigCommands.Functions.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An {@link InternalArgument} that represents a Java {@link String}.
 */
public class InternalStringArgument extends InternalArgument implements CommandArgument {
    private String value;

    /**
     * Creates a new {@link InternalStringArgument} with no initial value set.
     */
    public InternalStringArgument() {
    }

    /**
     * Creates a new {@link InternalStringArgument} with the initial value set to the given String.
     *
     * @param value The initial String value for this {@link InternalStringArgument}.
     */
    public InternalStringArgument(String value) {
        super(value);
    }

    @Override
    public Argument<?> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
        if (argumentInfo == null) return new StringArgument(name);
        ConfigurationSection info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, name);
        String type = info.getString("subtype");
        ConfigCommandsHandler.logDebug(localDebug, "Arg has subtype: %s", type);
        if (type == null) return new StringArgument(name);
        return switch (type) {
            case "string" -> new StringArgument(name);
            case "text" -> new TextArgument(name);
            case "greedy" -> new GreedyStringArgument(name);
            default ->
                    throw new IncorrectArgumentKey(name, "subtype", "Did not find StringArgument subtype: \"" + type + "\"");
        };
    }

    private static final List<String> acceptableSubtypes = List.of("string", "text", "greedy");

    @Override
    public boolean editArgumentInfo(CommandSender sender, String message, ConfigurationSection argument, @Nullable Object argumentInfo) {
        ConfigurationSection info;
        if (argumentInfo == null) {
            info = argument.createSection("argumentInfo");
        } else {
            try {
                info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, "");
            } catch (IncorrectArgumentKey ignored) {
                argument.set("argumentInfo", null);
                info = argument.createSection("argumentInfo");
            }
        }
        String subtype = info.getString("subtype");

        if (message.isBlank()) {
            if (subtype == null) {
                sender.sendMessage("Subtype is null");
            } else {
                sender.sendMessage("Subtype is " + subtype);
                if (!acceptableSubtypes.contains(subtype)) {
                    sender.sendMessage(ChatColor.YELLOW + "Subtype is invalid!");
                }
            }
            sender.sendMessage("Valid subtypes:");
            sender.sendMessage("  string: One word with letters, numbers, and underscore");
            sender.sendMessage("  text: A single word or any characters inside quotes");
            sender.sendMessage("  greedy: Any characters, but only at the end of the command");
            sender.sendMessage("What would you like the new subtype to be?");
        } else if (acceptableSubtypes.contains(message)) {
            info.set("subtype", message);
            ConfigCommandsHandler.saveConfigFile();
            sender.sendMessage("Subtype set to " + message);
            return true;
        } else {
            sender.sendMessage("\"" + message + "\" is not a recognized subtype");
        }
        return false;
    }

    @Override
    public String[] formatArgumentInfo(Object argumentInfo) {
        if (argumentInfo == null) return new String[]{ChatColor.YELLOW + "ArgumentInfo is invalid!"};
        ConfigurationSection info;
        try {
            info = assertArgumentInfoClass(argumentInfo, ConfigurationSection.class, "");
        } catch (IncorrectArgumentKey ignored) {
            return new String[]{ChatColor.YELLOW + "ArgumentInfo is invalid!"};
        }

        String type = info.getString("subtype");
        if (type == null) return new String[]{"subtype: null -> string by default"};
        return new String[]{"subtype: " + type + (acceptableSubtypes.contains(type) ? "" : ChatColor.YELLOW + " (invalid!)")};
    }

    @Override
    public void setValue(Object arg) {
        value = (String) arg;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument arg) {
        value = (String) arg.getValue();
    }

    @Override
    public String forCommand() {
        return value;
    }

    private String getString(InternalArgument target) {
        return (String) target.getValue();
    }

    private int getInt(InternalArgument target) {
        return (int) target.getValue();
    }

    @Override
    public InstanceFunctionList getInstanceFunctions() {
        return merge(
                super.getInstanceFunctions(),
                functions(
                        new InstanceFunction("charAt")
                                .withDescription("Gets the character at the given index")
                                .withParameters(new Parameter(InternalIntegerArgument.class, "index", "The index of the character"))
                                .returns(InternalStringArgument.class)
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <string>.length()"
                                )
                                .executes((target, parameters) -> {
                                    try {
                                        return new InternalStringArgument("" + getString(target).charAt(getInt(parameters.get(0))));
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new CommandRunException(e);
                                    }
                                })
                                .withExamples(
                                        "do \"Hello\".charAt(Integer.(\"0\")) -> \"H\"",
                                        "do \"Hello\".charAt(Integer.(\"4\")) -> \"o\"",
                                        "do \"Hello\".charAt(Integer.(\"5\")) -> IndexOutOfBounds"
                                ),
                        new InstanceFunction("contains")
                                .withDescription("Checks if this string contains the given string")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to look for"))
                                .returns(InternalBooleanArgument.class, "True if the other string can be found inside this string, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getString(target).contains(getString(parameters.get(0))));
                                })
                                .withExamples(
                                        "do \"therein\".contains(\"the\") -> True",
                                        "do \"therein\".contains(\"here\") -> True",
                                        "do \"therein\".contains(\"in\") -> True",
                                        "do \"therein\".contains(\"over\") -> False"
                                ),
                        new InstanceFunction("endsWith")
                                .withDescription("Checks if this string ends with the given string")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to look for"))
                                .returns(InternalBooleanArgument.class, "True if the other string can be found at the end of this string, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getString(target).endsWith(getString(parameters.get(0))));
                                })
                                .withExamples(
                                        "do \"therein\".endsWith(\"in\") -> True",
                                        "do \"therein\".endsWith(\"rein\") -> True",
                                        "do \"therein\".endsWith(\"the\") -> False"
                                ),
                        new InstanceFunction("equals")
                                .withDescription("Checks if this string is equal to another string")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to check against"))
                                .returns(InternalBooleanArgument.class, "True if the other string is identical to this string, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getString(target).equals(getString(parameters.get(0))));
                                })
                                .withExamples(
                                        "do \"Hello\".equals(\"Hello\") -> True",
                                        "do \"Hello\".equals(\"Hi\") -> False",
                                        "do \"Hello\".equals(\"hElLo\") -> False"
                                ),
                        new InstanceFunction("equalsIgnoreCase")
                                .withDescription("Checks if this string is equal to another string, ignoring differences in case")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to check against"))
                                .returns(InternalBooleanArgument.class, "True if the other string is identical to this string, ignoring case, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getString(target).equalsIgnoreCase(getString(parameters.get(0))));
                                })
                                .withExamples(
                                        "do \"Hello\".equals(\"Hello\") -> True",
                                        "do \"Hello\".equals(\"hElLo\") -> True",
                                        "do \"Hello\".equals(\"Hi\") -> False"
                                ),
                        new InstanceFunction("indexOf")
                                .withDescription("Gets the index of a another string within this string")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to look for"))
                                .withParameters(
                                        new Parameter(InternalStringArgument.class, "other", "The string to look for"),
                                        new Parameter(InternalIntegerArgument.class, "start index", "The index to start the search from")
                                )
                                .returns(InternalIntegerArgument.class, "The index of the first occurrence of the other string in this string, " +
                                        "or -1 if it is not present. If a start index is given, only the part of the string at or after that index is considered")
                                .executes((target, parameters) -> {
                                    int result;
                                    if (parameters.size() == 1) {
                                        result = getString(target).indexOf(getString(parameters.get(0)));
                                    } else {
                                        result = getString(target).indexOf(getString(parameters.get(0)), getInt(parameters.get(1)));
                                    }
                                    return new InternalIntegerArgument(result);
                                })
                                .withExamples(
                                        "do \"therein\".indexOf(\"rein\") -> 3",
                                        "do \"therein\".indexOf(\"in\") -> 5",
                                        "do \"therein\".indexOf(\"e\") -> 2",
                                        "do \"therein\".indexOf(\"e\", Integer.(\"3\")) -> 4",
                                        "do \"therein\".indexOf(\"over\") -> -1",
                                        "do \"therein\".indexOf(\"e\", Integer.(\"5\")) -> -1"
                                ),
                        new InstanceFunction("isEmpty")
                                .withDescription("Checks if this string has anything in it")
                                .returns(InternalBooleanArgument.class, "True if <string>.length() equals 0, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getString(target).isEmpty());
                                }),
                        new InstanceFunction("join")
                                .withDescription("Adds another object onto the end of this string")
                                .withParameters(new Parameter(InternalArgument.class, "other",
                                        "The object to add. Automatically converted to a string using the forCommand method."))
                                .returns(InternalStringArgument.class, "A new string that starts with this string and ends with the other object")
                                .executes((target, parameters) -> {
                                    return new InternalStringArgument(getString(target) + parameters.get(0).forCommand());
                                })
                                .withExamples(
                                        "do \"Hello\".join(\"World\") -> \"HelloWorld\"",
                                        "do \"Count: \".join(Integer.()) -> \"Count: 0\""
                                ),
                        new InstanceFunction("lastIndexOf")
                                .withDescription("Gets the index of another string within this string, starting from the end")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to look for"))
                                .withParameters(
                                        new Parameter(InternalStringArgument.class, "other", "The string to look for"),
                                        new Parameter(InternalIntegerArgument.class, "start index", "The index to start from")
                                )
                                .returns(InternalIntegerArgument.class, "The index of the last occurrence of the other string in this string, " +
                                        "or -1 if it is not present. If a start index is given, only the part of the string at or before that index is considered")
                                .executes((target, parameters) -> {
                                    int result;
                                    if (parameters.size() == 1) {
                                        result = getString(target).lastIndexOf(getString(parameters.get(0)));
                                    } else {
                                        result = getString(target).lastIndexOf(getString(parameters.get(0)), getInt(parameters.get(1)));
                                    }
                                    return new InternalIntegerArgument(result);
                                })
                                .withExamples(
                                        "do \"therein\".lastIndexOf(\"rein\") -> 3",
                                        "do \"therein\".lastIndexOf(\"in\") -> 5",
                                        "do \"therein\".lastIndexOf(\"e\") -> 4",
                                        "do \"therein\".lastIndexOf(\"e\", Integer.(\"3\")) -> 2",
                                        "do \"therein\".lastIndexOf(\"over\") -> -1",
                                        "do \"therein\".lastIndexOf(\"e\", Integer.(\"1\")) -> -1"
                                ),
                        new InstanceFunction("length")
                                .returns(InternalIntegerArgument.class, "The number of characters in this string")
                                .executes((target, parameters) -> {
                                    return new InternalIntegerArgument(getString(target).length());
                                })
                                .withExamples(
                                        "do \"Hello\".length() -> 5",
                                        "do \"therein\".length() -> 7",
                                        "do \"\".length() -> 0"
                                ),
                        new InstanceFunction("replace")
                                .withDescription("Replaces all instances of a string within this string with a new string")
                                .withParameters(
                                        new Parameter(InternalStringArgument.class, "pattern", "The string to look for"),
                                        new Parameter(InternalStringArgument.class, "replacement", "The string to insert")
                                )
                                .returns(InternalStringArgument.class, "A new string with every instance of the pattern string replaced with the replacement string")
                                .executes((target, parameters) -> {
                                    return new InternalStringArgument(getString(target).replace(getString(parameters.get(0)), getString(parameters.get(1))));
                                })
                                .withExamples(
                                        "do \"mississippi\".replace(\"is\", \"ab\") -> \"mabsabsippi\"",
                                        "do \"mississippi\".replace(\"ss\", \"\") -> \"miiippi\"",
                                        "do \"aaa\".replace(\"aa\", \"b\") -> \"ba\"",
                                        "do \"aaa\".replace(\"\", \"b\") -> \"ababab\""
                                ),
                        new InstanceFunction("startsWith")
                                .withDescription("Checks if this string starts with the given string")
                                .withParameters(new Parameter(InternalStringArgument.class, "other", "The string to look for"))
                                .returns(InternalBooleanArgument.class, "True if the other string can be found at the start of this string, and false otherwise")
                                .executes((target, parameters) -> {
                                    return new InternalBooleanArgument(getString(target).startsWith(getString(parameters.get(0))));
                                })
                                .withExamples(
                                        "do \"therein\".startsWith(\"the\") -> True",
                                        "do \"therein\".startsWith(\"there\") -> True",
                                        "do \"therein\".startsWith(\"rein\") -> False"
                                ),
                        new InstanceFunction("substring")
                                .withDescription("Creates a new string from part of this string")
                                .withParameters(new Parameter(InternalIntegerArgument.class, "start", "The index to start from"))
                                .withParameters(
                                        new Parameter(InternalIntegerArgument.class, "start", "The index to start from"),
                                        new Parameter(InternalIntegerArgument.class, "end", "The index to stop at")
                                )
                                .returns(InternalStringArgument.class, "A new string that contains all the characters in this string at or after the " +
                                        "start index and before but not at the end index. When the end index is not given, it defaults to <string>.length()")
                                .throwsException(
                                        "IndexOutOfBoundsException when start < 0, end > <string>.length(), or start > end"
                                )
                                .executes((target, parameters) -> {
                                    String result;
                                    try {
                                        if (parameters.size() == 1) {
                                            result = getString(target).substring(getInt(parameters.get(0)));
                                        } else {
                                            result = getString(target).substring(getInt(parameters.get(0)), getInt(parameters.get(1)));
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        throw new CommandRunException(e);
                                    }
                                    return new InternalStringArgument(result);
                                })
                                .withExamples(
                                        "do \"therein\".substring(Integer.(\"3\")) -> \"rein\"",
                                        "do \"therein\".substring(Integer.(\"1\"), Integer.(\"5\")) -> \"here\"",
                                        "do \"therein\".substring(Integer.(\"-1\"), Integer.(\"8\") -> IndexOutOfBounds because start (-1) < 0 and end (8) > <string>.length (7)",
                                        "do \"therein\".substring(Integer.(\"5\"), Integer.(\"1\")) -> IndexOutOfBounds because start (5) > end (1)"
                                ),
                        new InstanceFunction("toInt")
                                .withDescription("Turns this string into an Integer")
                                .returns(InternalIntegerArgument.class, "The number this string represents in base 10")
                                .throwsException("NumberFormatException when this string cannot be interpreted as an Integer")
                                .executes((target, parameters) -> {
                                    try {
                                        return new InternalIntegerArgument(Integer.parseInt(getString(target)));
                                    } catch (NumberFormatException e) {
                                        throw new CommandRunException(e);
                                    }
                                })
                                .withExamples(
                                        "\"10\".toInt() -> 10",
                                        "\"-5\".toInt() -> -5",
                                        "\"Hello\".toInt() -> NumberFormatException",
                                        "\"\".toInt() -> NumberFormatException"
                                )
                )
        );
    }
}
