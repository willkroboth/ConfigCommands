package me.willkroboth.configcommands.registeredcommands.expressions;

import com.mojang.brigadier.StringReader;
import me.willkroboth.configcommands.ConfigCommandsHandler;
import me.willkroboth.configcommands.exceptions.CommandRunException;
import me.willkroboth.configcommands.exceptions.ParseException;
import me.willkroboth.configcommands.functions.executions.InstanceExecution;
import me.willkroboth.configcommands.functions.executions.StaticExecution;
import me.willkroboth.configcommands.internalarguments.InternalArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents a string expression that compiles to something that transforms
 * {@link InternalArgument} variables to something else.
 */
public abstract class Expression<Return> {
    private final static Map<String, InternalArgument<?>> staticClassMap = new HashMap<>();

    /**
     * Adds an entry to the map that links names to {@link InternalArgument} objects,
     * used for finding static functions from a String.
     *
     * @param object The {@link InternalArgument} object to add to the static class map.
     *               {@link InternalArgument#getName()} is used as the key, and the object is the value.
     */
    public static void addToStaticClassMap(InternalArgument<?> object) {
        staticClassMap.put(object.getName(), object);
    }

    /**
     * @return The map from name to object used to find static functions from a String.
     */
    public static Map<String, InternalArgument<?>> getStaticClassMap() {
        return staticClassMap;
    }

    //////////////////////////////////////////////////////////////////////////////////
    // PARSE EXPRESSION                                                             //
    // Decides if the String starts with a StringConstant, Variable, or StaticClass //
    //////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a String into an {@link Expression} object.
     *
     * @param string          The String to parse.
     * @param argumentClasses A map from names to {@link InternalArgument} class objects that represents the variables
     *                        that can be accessed in the expression.
     * @param localDebug      True if debug messages should be logged, and false otherwise.
     * @return The {@link Expression} object parsed from the String.
     * @throws ParseException If the String is improperly formatted and cannot be parsed into a {@link Expression}.
     */
    public static Expression<?> parseExpression(String string, Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                boolean localDebug) throws ParseException {
        ConfigCommandsHandler.logDebug(localDebug, "Parsing expression: %s", string);
        ConfigCommandsHandler.increaseIndentation();

        try {
            StringReader stringReader = new StringReader(string);
            if (string.length() == 0) throw new ParseException(stringReader, "Cannot parse empty string!");

            if (stringReader.peek() == '"') return readStringConstant(stringReader, argumentClasses, localDebug);
            if (stringReader.peek() == '<') return readVariable(stringReader, argumentClasses, localDebug);
            return readStaticClass(stringReader, argumentClasses, localDebug);
        } finally {
            ConfigCommandsHandler.decreaseIndentation();
        }
    }

    /////////////////////////////////////////////////////
    // READING                                         //
    // Identifies start of an Expression String        //
    // May return directly, or set up a function chain //
    /////////////////////////////////////////////////////

    private static Expression<?> readStringConstant(StringReader stringReader, Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                    boolean localDebug) throws ParseException {
        if (stringReader.read() != '"')
            throw new IllegalStateException("Expression#readStringConstant was called, but the stringReader did not start with '\"'");

        ConfigCommandsHandler.logDebug(localDebug, "Expression starts with '\"', reading StringConstant");
        StringBuilder stringConstant = new StringBuilder();

        boolean escaped = false;
        while (stringReader.canRead()) {
            char next = stringReader.read();

            if (escaped) {
                stringConstant.append(switch (next) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'b' -> '\b';
                    case 'r' -> '\r';
                    case 'f' -> '\f';
                    case '\'' -> '\'';
                    case '\"' -> '\"';
                    case '\\' -> '\\';
                    default -> throw new ParseException(stringReader, "A character that cannot be escaped (" + next + ") was found after an unescaped '\\'.");
                });
                ConfigCommandsHandler.logDebug(localDebug, "Added escaped character %s", next);
                escaped = false;
                continue;
            }
            if (next == '\\') {
                escaped = true;
                continue;
            }
            if (next == '\"') {
                Expression<?> expression = new StringConstant(stringConstant.toString());
                ConfigCommandsHandler.logDebug(localDebug, "StringConstant built as %s", expression);

                if (!stringReader.canRead()) {
                    ConfigCommandsHandler.logDebug(localDebug, "Expression ended. StringConstant returned.");
                    return expression;
                }

                if (stringReader.peek() == '.')
                    return parseInstanceFunctionCall(expression, stringReader, argumentClasses, localDebug);

                throw new ParseException(stringReader, "Found unexpected extra information after StringConstant. " +
                        "StringConstant should have ended the statement (no more characters) or lead to a function (indicated by '.'). " +
                        "Instead, found: " + stringReader.getRemaining());
            }
            stringConstant.append(next);
        }
        throw new ParseException(stringReader, "StringConstant was never closed. Expected unescaped '\"' to end the StringConstant.");
    }

    private static Expression<?> readVariable(StringReader stringReader, Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                              boolean localDebug) throws ParseException {
        if (stringReader.read() != '<')
            throw new IllegalStateException("Expression#readVariable was called, but the stringReader did not start with '<'");

        ConfigCommandsHandler.logDebug(localDebug, "Expression starts with '<', reading Variable");

        int start = stringReader.getCursor();
        while (stringReader.canRead()) {
            char next = stringReader.read();

            if (next == '>') {
                int end = stringReader.getCursor();
                // Make sure to include < > with these bounds.
                String variable = stringReader.getString().substring(start - 1, end);
                ConfigCommandsHandler.logDebug(localDebug, "Variable name built as \"%s\"", variable);

                if (!argumentClasses.containsKey(variable))
                    throw new ParseException(stringReader, "Variable (" + variable + ") dose not exist at this point. Must be declared as an argument or defined by an earlier set command.");
                ConfigCommandsHandler.logDebug(localDebug, "Name identified as valid variable.");

                Expression<?> expression = new Variable<>(variable);

                if (!stringReader.canRead()) {
                    ConfigCommandsHandler.logDebug(localDebug, "Expression ended. Variable returned.");
                    return expression;
                }

                if (stringReader.peek() == '.')
                    return parseInstanceFunctionCall(expression, stringReader, argumentClasses, localDebug);

                throw new ParseException(stringReader, "Found unexpected extra information after Variable. " +
                        "Variable should have ended the statement (no more characters) or lead to a function (indicated by '.'). " +
                        "Instead, found: " + stringReader.getRemaining());
            }
        }
        throw new ParseException(stringReader, "Variable was never closed. Expected '>' to end the Variable.");
    }

    private static Expression<?> readStaticClass(StringReader stringReader, Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                 boolean localDebug) throws ParseException {
        ConfigCommandsHandler.logDebug(localDebug, "Expression dose not start with '\"' (StringConstant) or '<' (Variable). Reading StaticClass.");

        int start = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != '.') {
            stringReader.skip();
        }

        int end = stringReader.getCursor();
        String staticClass = stringReader.getString().substring(start, end);

        ConfigCommandsHandler.logDebug(localDebug, "Static class built as \"%s\"", staticClass);

        if (!staticClassMap.containsKey(staticClass))
            throw new ParseException(stringReader, "Unknown StaticClass \"" + staticClass + "\"");

        InternalArgument<?> staticClassObject = staticClassMap.get(staticClass);
        if (!stringReader.canRead()) {
//            return new StaticClass(staticClassObject); // TODO: Figure this out
            throw new ParseException(stringReader, "Independent StaticClass objects are not yet implemented");
        } else {
            return parseStaticFunctionCall(staticClassObject, stringReader, argumentClasses, localDebug);
        }
    }

    /////////////////////////////////////////////////////////////////////
    // FUNCTION CALLS                                                  //
    // Read a name for the function, then the parameters between ( )   //
    // Parameter strings are recursively parsed as expressions         //
    // May end statement and return, or be chained to another function //
    /////////////////////////////////////////////////////////////////////

    private static String readFunctionName(StringReader stringReader, boolean localDebug) throws ParseException {
        if (stringReader.read() != '.')
            throw new IllegalStateException("Expression#readFunctionName was called, but the stringReader did not start with '.'");

        ConfigCommandsHandler.logDebug(localDebug, "Found '.', reading function name");

        int start = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != '(') {
            stringReader.skip();
        }

        if (!stringReader.canRead())
            throw new ParseException(stringReader, "Parameters never found. Expected function name to end with '('.");

        int end = stringReader.getCursor();

        String functionName = stringReader.getString().substring(start, end);
        ConfigCommandsHandler.logDebug(localDebug, "Function name built as \"%s\"", functionName);
        return functionName;
    }

    private static List<Expression<?>> readParameters(StringReader stringReader, Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                      boolean localDebug) throws ParseException {
        if (stringReader.read() != '(')
            throw new IllegalStateException("Expression#readParameters was called, but the stringReader did not start with '('");

        ConfigCommandsHandler.logDebug(localDebug, "Position %s, parenthesisDepth is 1", stringReader.getCursor() - 1);

        List<Expression<?>> parameters = new ArrayList<>();
        int parenthesisDepth = 1;
        int start = stringReader.getCursor();

        while (stringReader.canRead()) {
            char next = stringReader.peek();

            if (next == '"') {
                // Make sure to ignore ( ) inside StringConstants
                ConfigCommandsHandler.logDebug(localDebug, "Found String at position %s, ignoring parenthesis", stringReader.getCursor() + 1);
                skipOverString(stringReader);
                ConfigCommandsHandler.logDebug(localDebug, "String ended at position %s", stringReader.getCursor());
                continue;
            }

            if (next == '(') {
                parenthesisDepth++;
                ConfigCommandsHandler.logDebug(localDebug, "Position %s, parenthesisDepth is now %s", stringReader.getCursor(), parenthesisDepth);
            }
            if (next == ')') {
                parenthesisDepth--;
                ConfigCommandsHandler.logDebug(localDebug, "Position %s, parenthesisDepth is now %s", stringReader.getCursor(), parenthesisDepth);
            }
            stringReader.skip();

            if (parenthesisDepth == 0 || (parenthesisDepth == 1 && next == ',')) {
                // Ended a parameter
                int end = stringReader.getCursor();
                String parameter = stringReader.getString().substring(start, end - 1);

                if (parameter.length() == 0) {
                    if (parenthesisDepth != 0)
                        throw new ParseException(stringReader, "Found empty parameter, which cannot be parsed. (Were there 2 commas in a row?)");
                    else
                        ConfigCommandsHandler.logDebug(localDebug, "Found 0 parameters");
                } else {
                    ConfigCommandsHandler.logDebug(localDebug, "New parameter: %s", parameter);

                    Expression<?> expression;
                    try {
                        expression = parseExpression(parameter, argumentClasses, localDebug);
                    } catch (ParseException e) {
                        throw new ParseException(stringReader, "\n" + e.getMessage());
                    }
                    ConfigCommandsHandler.logDebug(localDebug, "Parameter successfully parsed");

                    parameters.add(expression);
                }

                if (parenthesisDepth == 0) {
                    // Closed final parenthesis
                    ConfigCommandsHandler.logDebug(localDebug, "Parameters closed");
                    return parameters;
                }

                // There can be whitespace between parameters
                stringReader.skipWhitespace();

                start = stringReader.getCursor();
            }
        }
        throw new ParseException(stringReader, "Parameters never closed. Expected " + parenthesisDepth + " more ')' to finish the statement.");
    }

    private static void skipOverString(StringReader stringReader) throws ParseException {
        if (stringReader.read() != '"')
            throw new IllegalStateException("Expression#skipOverString was called, but the stringReader did not start with '\"'");

        boolean escaped = false;
        while (stringReader.canRead()) {
            char next = stringReader.read();

            if (escaped) {
                // We don't need to bother checking if this is a valid escape character, since that will happen when
                //  reading it as a StringConstant. Here, we just need to make sure to properly consider how '"' may
                //  be escaped, affecting when the String will end.
                escaped = false;
                continue;
            }
            if (next == '\\') {
                escaped = true;
                continue;
            }
            if (next == '"') return;
        }
        throw new ParseException(stringReader, "String never closed. Expected unescaped '\"' to end the String.");
    }

    private static List<Class<? extends InternalArgument<?>>> getParameterTypes(List<Expression<?>> parameters,
                                                                                Map<String, Class<? extends InternalArgument<?>>> argumentClasses) {
        List<Class<? extends InternalArgument<?>>> parameterTypes = new ArrayList<>(parameters.size());
        for (Expression<?> expression : parameters) {
            parameterTypes.add(expression.getEvaluationType(argumentClasses));
        }
        return parameterTypes;
    }

    private static void logParametersString(List<Class<? extends InternalArgument<?>>> parameters) {
        StringBuilder parametersString = new StringBuilder("[");
        if (parameters.size() != 0) {
            for (Class<? extends InternalArgument<?>> parameter : parameters) {
                parametersString.append(parameter.getSimpleName());
                parametersString.append(", ");
            }
            parametersString.delete(parametersString.length() - 2, parametersString.length());
        }
        parametersString.append("]");
        ConfigCommandsHandler.logNormal("Parameter types: %s", parametersString);
    }

    // The cast is safe
    @SuppressWarnings("unchecked")
    private static <Target> Expression<?> parseInstanceFunctionCall(Expression<Target> instanceExpression, StringReader stringReader,
                                                           Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                           boolean localDebug) throws ParseException {
        Class<? extends InternalArgument<Target>> targetClass = instanceExpression.getEvaluationType(argumentClasses);
        ConfigCommandsHandler.logDebug(localDebug, "Target for the function call has class: %s", targetClass.getSimpleName());

        InternalArgument<Target> targetClassObject;
        try {
            targetClassObject = (InternalArgument<Target>) InternalArgument.getInternalArgument(targetClass);
        } catch (IllegalArgumentException e) {
            throw new ParseException(stringReader, "Could not turn target class for the function call ("
                    + targetClass.getSimpleName() + ") into an object. Please contact the author of the plugin that added " +
                    "the " + targetClass.getSimpleName() + ", as this is probably something they need to fix.", e);
        }

        String function = readFunctionName(stringReader, localDebug);
        List<Expression<?>> parameters = readParameters(stringReader, argumentClasses, localDebug);
        List<Class<? extends InternalArgument<?>>> parameterTypes = getParameterTypes(parameters, argumentClasses);

        if (localDebug) {
            ConfigCommandsHandler.logNormal("TargetClass: %s", targetClassObject.getName());
            ConfigCommandsHandler.logNormal("FunctionName: %s", function);
            logParametersString(parameterTypes);
        }

        InstanceExecution<Target, ?> instanceFunction = targetClassObject.getInstanceExecution(function, parameterTypes);


        if (instanceFunction == null)
            throw new ParseException(stringReader, "InstanceFunction on class " + targetClassObject.getName() +
                    " with name \"" + function + "\" and parameterTypes: " + parameterTypes + " could not be found.");


        ConfigCommandsHandler.logDebug(localDebug, "Found the defined function");


        Expression<?> expression = new InstanceFunctionCall<>(instanceExpression, instanceFunction, parameters);

        if (!stringReader.canRead()) {
            ConfigCommandsHandler.logDebug(localDebug, "Expression ended. InstanceFunctionCall returned.");
            return expression;
        }

        if (stringReader.peek() == '.')
            return parseInstanceFunctionCall(expression, stringReader, argumentClasses, localDebug);

        throw new ParseException(stringReader, "Found unexpected extra information after InstanceFunctionCall. " +
                "InstanceFunctionCall should have ended the statement (no more characters) or lead to another function (indicated by '.'). " +
                "Instead, found: " + stringReader.getRemaining());
    }

    private static Expression<?> parseStaticFunctionCall(InternalArgument<?> staticClass, StringReader stringReader,
                                                         Map<String, Class<? extends InternalArgument<?>>> argumentClasses,
                                                         boolean localDebug) throws ParseException {
        String function = readFunctionName(stringReader, localDebug);
        List<Expression<?>> parameters = readParameters(stringReader, argumentClasses, localDebug);
        List<Class<? extends InternalArgument<?>>> parameterTypes = getParameterTypes(parameters, argumentClasses);

        if (localDebug) {
            ConfigCommandsHandler.logNormal("StaticClass: %s", staticClass.getName());
            ConfigCommandsHandler.logDebug("FunctionName: %s", function);
            logParametersString(parameterTypes);
        }

        StaticExecution<?> staticFunction = staticClass.getStaticExecution(function, parameterTypes);

        if (staticFunction == null)
            throw new ParseException(stringReader, "Static function on class " + staticClass.getName() +
                    " with name \"" + function + "\" and parameterTypes: " + parameterTypes + " could not be found.");


        ConfigCommandsHandler.logDebug(localDebug, "Found the defined function");

        Expression<?> expression = new StaticFunctionCall<>(staticClass.getName(), staticFunction, parameters);

        if (!stringReader.canRead()) {
            ConfigCommandsHandler.logDebug(localDebug, "Expression ended. StaticFunctionCall returned.");
            return expression;
        }

        if (stringReader.peek() == '.')
            return parseInstanceFunctionCall(expression, stringReader, argumentClasses, localDebug);

        throw new ParseException(stringReader, "Found unexpected extra information after StaticFunctionCall. " +
                "StaticFunctionCall should have ended the statement (no more characters) or lead to another function (indicated by '.'). " +
                "Instead, found: " + stringReader.getRemaining());
    }

    /**
     * @return A String that represents the parsed expression.
     */
    public abstract String toString();

    /**
     * @param argumentClasses A map from name to {@link InternalArgument} class objects that represents the variables
     *                        available as the input to this {@link Expression}.
     * @return A {@link InternalArgument} class object that this {@link Expression} will return
     * given inputs with the input classes.
     */
    public abstract Class<? extends InternalArgument<Return>> getEvaluationType(Map<String, Class<? extends InternalArgument<?>>> argumentClasses);

    /**
     * Gives the result of running this {@link Expression} based on the values of some given variables.
     *
     * @param argumentVariables A map from name to {@link InternalArgument} object that represent each
     *                          of the available variables.
     * @param localDebug        True if debug messages should be logged, and false otherwise.
     * @return A {@link InternalArgument} object that was the result of evaluating the {@link Expression}.
     * @throws CommandRunException If evaluating this {@link Expression} causes an exception.
     */
    public abstract InternalArgument<Return> evaluate(Map<String, InternalArgument<?>> argumentVariables,
                                                      boolean localDebug) throws CommandRunException;
}