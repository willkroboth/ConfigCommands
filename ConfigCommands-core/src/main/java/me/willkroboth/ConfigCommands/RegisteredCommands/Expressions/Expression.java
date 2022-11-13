package me.willkroboth.ConfigCommands.RegisteredCommands.Expressions;

import me.willkroboth.ConfigCommands.ConfigCommandsHandler;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Expression {
    private final static Map<String, InternalArgument> staticClassMap = new HashMap<>();

    public static void addToClassMap(InternalArgument object) {
        staticClassMap.put(object.getName(), object);
    }

    public static Map<String, InternalArgument> getClassMap() {
        return staticClassMap;
    }

    public static Expression parseExpression(String string, Map<String, Class<? extends InternalArgument>> argumentClasses,
                                             boolean localDebug) throws ParseException {
        if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
            // basic string for basic initialization
            return new StringConstant(string.substring(1, string.length() - 1));
        }
        boolean isStringConstant = string.charAt(0) == '"';

        // parsing variables
        StringBuilder wordBuild = new StringBuilder();
        int parseMode = isStringConstant ? -1 : 0;
        // -1- reading string constant in function chain (until '"') - goes to 1
        // 0 - reading target variable/static class (until '.') - goes to 1
        // 1 - reading function (until '(') - goes to 2
        // 2 - reading parameters (between ( ), may have multiple layers, separated by ', ',
        //                          recursively calls to parse each section) - goes to 1
        int parenthesisDepth = 0;

        // output variables
        Expression targetExpression = null;
        InternalArgument target = null;
        String function = "";
        List<Expression> parameterExpressions = new ArrayList<>();

        InternalArgument staticClass = null;
        boolean isStaticClass = false;

        if (localDebug) {
            ConfigCommandsHandler.logNormal("Parsing expression: %s", string);
            ConfigCommandsHandler.increaseIndentation();
        }

        try {
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                switch (parseMode) {
                    case -1: // reading string constant
                        if (c == '"') {
                            if (i != 0) {
                                //skip first "
                                ConfigCommandsHandler.logDebug(localDebug, "String constant built as \"%s\"", wordBuild);
                                targetExpression = new StringConstant(wordBuild.toString());

                                // know at least one char after "constant"_, needs to be .,  then another
                                if (string.charAt(i + 1) != '.')
                                    throw new ParseException(string, "Invalid expression. Expected '.' after string constant, got '" + string.charAt(i + 1) + "'");
                                if (i + 2 >= string.length())
                                    throw new ParseException(string, "Expression ended early. Expected function after '\"constant\".'.");

                                i += 1;
                                parseMode = 1;
                                wordBuild = new StringBuilder();
                            }
                        } else {
                            wordBuild.append(c);
                        }
                        break;
                    case 0: // reading variable/static class
                        if (c == '.') {
                            String word = wordBuild.toString();
                            ConfigCommandsHandler.logDebug(localDebug, "Target built as \"%s\"", word);

                            isStaticClass = staticClassMap.containsKey(word);
                            if (isStaticClass) {
                                staticClass = staticClassMap.get(word);
                                ConfigCommandsHandler.logDebug(localDebug, "Target identified as a static class");

                                if (i + 1 >= string.length())
                                    throw new ParseException(string, "Expression ended early. Expected function after \"class.\".");
                            } else {
                                if (!(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>'))
                                    throw new ParseException(string, "Target \"" + word + "\" Does not match constant class or variable format.");
                                if (!argumentClasses.containsKey(word))
                                    throw new ParseException(string, "Variable \"" + word + "\" dose not exist at this point. Must be declared in usage or earlier set command.");
                                ConfigCommandsHandler.logDebug(localDebug, "Target identified as valid variable.");

                                targetExpression = new Variable(word);
                                if (i + 1 >= string.length())
                                    throw new ParseException(string, "Expression ended early. Expected function after \"variable.\".");
                            }
                            parseMode = 1;
                            wordBuild = new StringBuilder();
                        } else {
                            wordBuild.append(c);
                        }
                        break;
                    case 1: // reading function name
                        if (c == '(') {
                            function = wordBuild.toString();
                            ConfigCommandsHandler.logDebug(localDebug, "Function name built as \"%s\"", function);

                            parseMode = 2;
                            parenthesisDepth = 1;
                            ConfigCommandsHandler.logDebug(localDebug, "parenthesisDepth is 1");

                            wordBuild = new StringBuilder();
                        } else {
                            wordBuild.append(c);
                        }
                        break;
                    case 2: // reading parameters
                        if (c == '(') {
                            parenthesisDepth += 1;
                            ConfigCommandsHandler.logDebug(localDebug, "parenthesisDepth is %s", parenthesisDepth);
                            wordBuild.append(c);
                        } else if (c == ')') {
                            parenthesisDepth -= 1;
                            ConfigCommandsHandler.logDebug(localDebug, "parenthesisDepth is %s", parenthesisDepth);

                            if (parenthesisDepth != 0) {
                                wordBuild.append(c);
                            } else {
                                // Final parameter
                                String word = wordBuild.toString();
                                if (!word.isEmpty()) {
                                    ConfigCommandsHandler.logDebug(localDebug, "Final parameter built as \"%s\"", word);

                                    Expression parameter;
                                    try {
                                        parameter = parseExpression(word, argumentClasses, localDebug);
                                    } catch (ParseException e) {
                                        throw new ParseException(string, "\n" + e.getMessage());
                                    }
                                    ConfigCommandsHandler.logDebug(localDebug, "Parameter successfully parsed");

                                    parameterExpressions.add(parameter);
                                }
                                wordBuild = new StringBuilder();

                                // prepare and go to 1 to read a new function
                                ConfigCommandsHandler.logDebug(localDebug, "Creating new FunctionCall expression");

                                if (!isStaticClass) {
                                    try {
                                        assert targetExpression != null;
                                        target = InternalArgument.getInternalArgument(targetExpression.getEvaluationType(argumentClasses));
                                    } catch (IllegalArgumentException e) {
                                        throw new ParseException(string, "Could not turn InternalArgument class returned by expression: \""
                                                + targetExpression + "\" (" + targetExpression.getEvaluationType(argumentClasses) + ") into an object. " +
                                                "This issue must be fixed in the plugin's code, so please contact the plugin's author.");
                                    }

                                    if (localDebug) {
                                        ConfigCommandsHandler.logNormal("target is type " + target.getClass().getSimpleName());
                                        ConfigCommandsHandler.logNormal("function is " + function);
                                    }
                                }

                                List<Class<? extends InternalArgument>> parameters = new ArrayList<>();
                                for (Expression parameterExpression : parameterExpressions) {
                                    parameters.add(parameterExpression.getEvaluationType(argumentClasses));
                                }
                                if (localDebug) {
                                    StringBuilder parametersString = new StringBuilder("[");
                                    if (parameters.size() != 0) {
                                        for (Class<? extends InternalArgument> parameter : parameters) {
                                            parametersString.append(parameter.getSimpleName());
                                            parametersString.append(", ");
                                        }
                                        parametersString.delete(parametersString.length() - 2, parametersString.length());
                                    }
                                    parametersString.append("]");
                                    ConfigCommandsHandler.logNormal("parameter types are %s", parametersString);
                                }

                                if (isStaticClass) {
                                    if (!staticClass.hasStaticFunction(function, parameters))
                                        throw new ParseException(string, "Invalid static function \"" + function + "\" on " + staticClass + " with parameters " + parameters + ". Static function does not exist.");
                                } else {
                                    if (!target.hasFunction(function, parameters))
                                        throw new ParseException(string, "Invalid function \"" + function + "\" on " + target + " with parameters " + parameters + ". Function does not exist.");
                                }

                                ConfigCommandsHandler.logDebug(localDebug, "target was found to have function");

                                if (isStaticClass) {
                                    targetExpression = new StaticFunctionCall(staticClass, function, parameterExpressions);
                                    // if function is being chained, no longer using a static class name
                                    isStaticClass = false;
                                } else {
                                    targetExpression = new FunctionCall(targetExpression, function, parameterExpressions);
                                }

                                function = "";
                                parameterExpressions = new ArrayList<>();

                                i++; // skip '.'
                                if (i >= string.length()) {
                                    ConfigCommandsHandler.logDebug(localDebug, "End of expression. The function call will be returned.");
                                    return targetExpression;
                                }
                                if (string.charAt(i) != '.')
                                    throw new ParseException(string, "Expected '.' or nothing at all after closing a function. Found '" + string.charAt(i) + "' instead.");
                                parseMode = 1; // go back to reading a function, so they can be chained
                            }
                        } else if (parenthesisDepth == 1 && c == ',') {
                            // new parameter
                            String word = wordBuild.toString();
                            ConfigCommandsHandler.logDebug(localDebug, "New parameter built as \"%s\"", word);

                            Expression parameter;
                            try {
                                parameter = parseExpression(word, argumentClasses, localDebug);
                            } catch (ParseException e) {
                                throw new ParseException(string, "\n" + e.getMessage());
                            }
                            ConfigCommandsHandler.logDebug(localDebug, "Parameter successfully parsed");

                            parameterExpressions.add(parameter);
                            if (string.charAt(i + 1) == ' ') {
                                i++; // skip ' ' if used to separate parameters
                            }
                            wordBuild = new StringBuilder();
                        } else {
                            wordBuild.append(c);
                        }
                }
            }

            ConfigCommandsHandler.logDebug(localDebug, "End of expression reached without returning anything.");
            switch (parseMode) {
                // reading string constant
                case -1 -> throw new ParseException(string, "Expression ended early. Expected statement staring with \" to be closed by \"");
                // reading variable
                case 0 -> {
                    String word = wordBuild.toString();
                    // expression is just a variable name
                    if (!(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>'))
                        throw new ParseException(string, "Target \"" + word + "\" Does not match variable format.");
                    if (!argumentClasses.containsKey(word))
                        throw new ParseException(string, "Variable \"" + word + "\" dose not exist at this point. Must be declared in usage or earlier set command.");
                    ConfigCommandsHandler.logDebug(localDebug, "Expression found to be a valid variable reference");
                    targetExpression = new Variable(word);
                    return targetExpression;
                }
                // reading function name
                case 1 -> throw new ParseException(string, "Expression ended early. Expected ( after function name.");
                // reading parameters
                case 2 -> throw new ParseException(string, "Expression ended early. Expected all parenthesis to close.");
            }
            throw new ParseException(string, "Code reached invalid parseMode: " + parseMode);
        } finally {
            if (localDebug) ConfigCommandsHandler.decreaseIndentation();

        }
    }

    public abstract String toString();

    public abstract Class<? extends InternalArgument> getEvaluationType(Map<String, Class<? extends InternalArgument>> argumentClasses);

    public abstract InternalArgument evaluate(Map<String, InternalArgument> argumentVariables,
                                              boolean localDebug) throws CommandRunException;
}