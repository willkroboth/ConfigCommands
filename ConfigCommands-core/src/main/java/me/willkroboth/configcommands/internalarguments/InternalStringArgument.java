package me.willkroboth.configcommands.internalarguments;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.IncorrectArgumentKey;
import me.willkroboth.configcommands.functions.InstanceFunctionList;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An {@link InternalArgument} that represents a Java {@link String}.
 */
public class InternalStringArgument extends InternalArgument<String> implements CommandArgument<String> {
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
    public Argument<String> createArgument(String name, @Nullable Object argumentInfo, boolean localDebug) throws IncorrectArgumentKey {
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
    public void setValue(String arg) {
        value = arg;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(InternalArgument<String> arg) {
        value = arg.getValue();
    }

    @Override
    public String forCommand() {
        return value;
    }

    @Override
    public InstanceFunctionList<String> getInstanceFunctions() {
        return merge(super.getInstanceFunctions(),
                functions(
                        instanceFunction("charAt")
                                .withDescription("Gets the character at the given index")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalIntegerArgument.class, "index", "The index of the character"))
                                        .returns(InternalStringArgument.class)
                                        .executes((string, index) -> {
                                            try {
                                                return new InternalStringArgument(String.valueOf(string.getValue().charAt(index.getValue())));
                                            } catch (IndexOutOfBoundsException e) {
                                                throw new CommandRunException(e);
                                            }
                                        })
                                )
                                .throwsException(
                                        "IndexOutOfBoundsException when index < 0 or index >= <string>.length()"
                                )
                                .withExamples(
                                        "do \"Hello\".charAt(Integer.(\"0\")) -> \"H\"",
                                        "do \"Hello\".charAt(Integer.(\"4\")) -> \"o\"",
                                        "do \"Hello\".charAt(Integer.(\"5\")) -> IndexOutOfBounds"
                                ),
                        instanceFunction("contains")
                                .withDescription("Checks if this string contains the given string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to look for"))
                                        .returns(InternalBooleanArgument.class, "True if the other string can be found inside this string, and false otherwise")
                                        .executes((string, other) -> new InternalBooleanArgument(string.getValue().contains(other.getValue())))
                                )
                                .withExamples(
                                        "do \"therein\".contains(\"the\") -> True",
                                        "do \"therein\".contains(\"here\") -> True",
                                        "do \"therein\".contains(\"in\") -> True",
                                        "do \"therein\".contains(\"over\") -> False"
                                ),
                        instanceFunction("endsWith")
                                .withDescription("Checks if this string ends with the given string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to look for"))
                                        .returns(InternalBooleanArgument.class, "True if the other string can be found at the end of this string, and false otherwise")
                                        .executes((string, other) -> new InternalBooleanArgument(string.getValue().endsWith(other.getValue())))
                                )
                                .withExamples(
                                        "do \"therein\".endsWith(\"in\") -> True",
                                        "do \"therein\".endsWith(\"rein\") -> True",
                                        "do \"therein\".endsWith(\"the\") -> False"
                                ),
                        instanceFunction("equals")
                                .withDescription("Checks if this string is equal to another string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to check against"))
                                        .returns(InternalBooleanArgument.class, "True if the other string is identical to this string, and false otherwise")
                                        .executes((string, other) -> new InternalBooleanArgument(string.getValue().equals(other.getValue())))
                                )
                                .withExamples(
                                        "do \"Hello\".equals(\"Hello\") -> True",
                                        "do \"Hello\".equals(\"Hi\") -> False",
                                        "do \"Hello\".equals(\"hElLo\") -> False"
                                ),
                        instanceFunction("equalsIgnoreCase")
                                .withDescription("Checks if this string is equal to another string, ignoring differences in case")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to check against"))
                                        .returns(InternalBooleanArgument.class, "True if the other string is identical to this string, ignoring case, and false otherwise")
                                        .executes((string, other) -> new InternalBooleanArgument(string.getValue().equalsIgnoreCase(other.getValue())))
                                )
                                .withExamples(
                                        "do \"Hello\".equals(\"Hello\") -> True",
                                        "do \"Hello\".equals(\"hElLo\") -> True",
                                        "do \"Hello\".equals(\"Hi\") -> False"
                                ),
                        instanceFunction("indexOf")
                                .withDescription("Gets the index of a another string within this string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to look for"))
                                        .returns(InternalIntegerArgument.class, "The index of the first occurrence of the other string in this string, or -1 if it is not present.")
                                        .executes((string, other) -> new InternalIntegerArgument(string.getValue().indexOf(other.getValue()))), InstanceExecution

                                        .withParameters(
                                                parameter(InternalStringArgument.class, "other", "The string to look for"),
                                                parameter(InternalIntegerArgument.class, "start index", "The index to start the search from")
                                        )
                                        .returns(InternalIntegerArgument.class, "The index of the first occurrence of the other string in this string, " +
                                                "or -1 if it is not present. Only the part of the string at or after the start index is considered")
                                        .executes((string, other, start) -> new InternalIntegerArgument(string.getValue().indexOf(other.getValue(), start.getValue())))
                                )
                                .withExamples(
                                        "do \"therein\".indexOf(\"rein\") -> 3",
                                        "do \"therein\".indexOf(\"in\") -> 5",
                                        "do \"therein\".indexOf(\"e\") -> 2",
                                        "do \"therein\".indexOf(\"e\", Integer.(\"3\")) -> 4",
                                        "do \"therein\".indexOf(\"over\") -> -1",
                                        "do \"therein\".indexOf(\"e\", Integer.(\"5\")) -> -1"
                                ),
                        instanceFunction("isEmpty")
                                .withDescription("Checks if this string has anything in it")
                                .withExecutions(InstanceExecution
                                        .returns(InternalBooleanArgument.class, "True if <string>.length() equals 0, and false otherwise")
                                        .executes(string -> new InternalBooleanArgument(string.getValue().isEmpty()))
                                ),
                        instanceFunction("join")
                                .withDescription("Adds another object onto the end of this string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalArgument.any(), "other", "The object to add. Automatically converted to a string using the forCommand method."))
                                        .returns(InternalStringArgument.class, "A new string that starts with this string and ends with the other object")
                                        .executes((string, other) -> new InternalStringArgument(string.getValue() + other.forCommand()))
                                )
                                .withExamples(
                                        "do \"Hello\".join(\"World\") -> \"HelloWorld\"",
                                        "do \"Count: \".join(Integer.()) -> \"Count: 0\""
                                ),
                        instanceFunction("lastIndexOf")
                                .withDescription("Gets the index of another string within this string, starting from the end")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to look for"))
                                        .returns(InternalIntegerArgument.class, "The index of the last occurrence of the other string in this string, or -1 if it is not present.")
                                        .executes((string, other) -> new InternalIntegerArgument(string.getValue().lastIndexOf(other.getValue()))), InstanceExecution

                                        .withParameters(
                                                parameter(InternalStringArgument.class, "other", "The string to look for"),
                                                parameter(InternalIntegerArgument.class, "start index", "The index to start the search from")
                                        )
                                        .returns(InternalIntegerArgument.class, "The index of the last occurrence of the other string in this string, " +
                                                "or -1 if it is not present. Only the part of the string at or before the start index is considered")
                                        .executes((string, other, start) -> new InternalIntegerArgument(string.getValue().lastIndexOf(other.getValue(), start.getValue())))
                                )
                                .withExamples(
                                        "do \"therein\".lastIndexOf(\"rein\") -> 3",
                                        "do \"therein\".lastIndexOf(\"in\") -> 5",
                                        "do \"therein\".lastIndexOf(\"e\") -> 4",
                                        "do \"therein\".lastIndexOf(\"e\", Integer.(\"3\")) -> 2",
                                        "do \"therein\".lastIndexOf(\"over\") -> -1",
                                        "do \"therein\".lastIndexOf(\"e\", Integer.(\"1\")) -> -1"
                                ),
                        instanceFunction("length")
                                .withExecutions(InstanceExecution
                                        .returns(InternalIntegerArgument.class, "The number of characters in this string")
                                        .executes(string -> new InternalIntegerArgument(string.getValue().length()))
                                )
                                .withExamples(
                                        "do \"Hello\".length() -> 5",
                                        "do \"therein\".length() -> 7",
                                        "do \"\".length() -> 0"
                                ),
                        instanceFunction("replace")
                                .withDescription("Replaces all instances of a string within this string with a new string")
                                .withExecutions(InstanceExecution
                                        .withParameters(
                                                parameter(InternalStringArgument.class, "pattern", "The string to look for"),
                                                parameter(InternalStringArgument.class, "replacement", "The string to insert")
                                        )
                                        .returns(InternalStringArgument.class, "A new string with every instance of the pattern string replaced with the replacement string")
                                        .executes((string, pattern, replacement) -> new InternalStringArgument(string.getValue().replace(pattern.getValue(), replacement.getValue())))
                                )
                                .withExamples(
                                        "do \"mississippi\".replace(\"is\", \"ab\") -> \"mabsabsippi\"",
                                        "do \"mississippi\".replace(\"ss\", \"\") -> \"miiippi\"",
                                        "do \"aaa\".replace(\"aa\", \"b\") -> \"ba\"",
                                        "do \"aaa\".replace(\"\", \"b\") -> \"ababab\""
                                ),
                        instanceFunction("startsWith")
                                .withDescription("Checks if this string starts with the given string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalStringArgument.class, "other", "The string to look for"))
                                        .returns(InternalBooleanArgument.class, "True if the other string can be found at the start of this string, and false otherwise")
                                        .executes((string, other) -> new InternalBooleanArgument(string.getValue().startsWith(other.getValue())))
                                )
                                .withExamples(
                                        "do \"therein\".startsWith(\"the\") -> True",
                                        "do \"therein\".startsWith(\"there\") -> True",
                                        "do \"therein\".startsWith(\"rein\") -> False"
                                ),
                        instanceFunction("substring")
                                .withDescription("Creates a new string from part of this string")
                                .withExecutions(InstanceExecution
                                        .withParameters(parameter(InternalIntegerArgument.class, "start", "The index to start from"))
                                        .returns(InternalStringArgument.class, "A new string that contains all of the characters in this string at or after the start index")
                                        .executes((string, start) -> {
                                            try {
                                                return new InternalStringArgument(string.getValue().substring(start.getValue()));
                                            } catch (IndexOutOfBoundsException e) {
                                                throw new CommandRunException(e);
                                            }
                                        }), InstanceExecution

                                        .withParameters(
                                                parameter(InternalIntegerArgument.class, "start", "The index to start from"),
                                                parameter(InternalIntegerArgument.class, "end", "The index to stop at")
                                        )
                                        .returns(InternalStringArgument.class, "A new string that contains all the characters in this string at or after the " +
                                                "start index and before but not at the end index.")
                                        .executes((string, start, end) -> {
                                            try {
                                                return new InternalStringArgument(string.getValue().substring(start.getValue(), end.getValue()));
                                            } catch (IndexOutOfBoundsException e) {
                                                throw new CommandRunException(e);
                                            }
                                        })
                                )
                                .throwsException(
                                        "IndexOutOfBoundsException when start < 0, end > <string>.length(), or start > end"
                                )
                                .withExamples(
                                        "do \"therein\".substring(Integer.(\"3\")) -> \"rein\"",
                                        "do \"therein\".substring(Integer.(\"1\"), Integer.(\"5\")) -> \"here\"",
                                        "do \"therein\".substring(Integer.(\"-1\"), Integer.(\"8\") -> IndexOutOfBounds because start (-1) < 0 and end (8) > <string>.length() (7)",
                                        "do \"therein\".substring(Integer.(\"5\"), Integer.(\"1\")) -> IndexOutOfBounds because start (5) > end (1)"
                                ),
                        instanceFunction("toInt")
                                .withDescription("Turns this string into an Integer")
                                .withExecutions(InstanceExecution
                                        .returns(InternalIntegerArgument.class, "The number this string represents in base 10")
                                        .executes(string -> {
                                            try {
                                                return new InternalIntegerArgument(Integer.parseInt(string.getValue()));
                                            } catch (NumberFormatException e) {
                                                throw new CommandRunException(e);
                                            }
                                        })
                                )
                                .throwsException("NumberFormatException when this string cannot be interpreted as an Integer")
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
