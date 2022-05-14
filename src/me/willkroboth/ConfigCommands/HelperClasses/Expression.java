package me.willkroboth.ConfigCommands.HelperClasses;

import me.willkroboth.ConfigCommands.Exceptions.CommandRunException;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.ParseException;
import me.willkroboth.ConfigCommands.Exceptions.RegistrationExceptions.RegistrationException;
import me.willkroboth.ConfigCommands.Functions.NonGenericVarargs.ArgList;
import me.willkroboth.ConfigCommands.InternalArguments.InternalArgument;
import me.willkroboth.ConfigCommands.InternalArguments.InternalStringArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Expression {
    private final static Map<String, InternalArgument> staticClassMap = new HashMap<>();

    public static void addToClassMap(InternalArgument object){ staticClassMap.put(object.getName(), object); }

    public static Map<String, InternalArgument> getClassMap(){ return staticClassMap; }

    public static Expression parseExpression(String string, HashMap<String, Class<? extends InternalArgument>> argumentVariables,
                                             boolean debugMode, IndentedLogger logger) throws RegistrationException {
        if(string.charAt(0) == '"' && string.charAt(string.length()-1) == '"'){
            // basic string for basic initialization
            return new StringConstant(string.substring(1, string.length()-1));
        }
        boolean isStringConstant = string.charAt(0) == '"';

        // parsing variables
        StringBuilder wordBuild = new StringBuilder();
        int parseMode = isStringConstant ? -1:0;
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

        if(debugMode){ logger.info("Parsing expression: " + string); logger.increaseIndentation();}

        try {
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                switch (parseMode) {
                    case -1: // reading string constant
                        if(c == '"'){
                            if(i != 0){
                                //skip first "
                                if (debugMode) logger.info("String constant built as \"" + wordBuild + "\"");
                                targetExpression = new StringConstant(wordBuild.toString());

                                // know at least one char after "constant"_, needs to be .,  then another
                                if(string.charAt(i+1) != '.')
                                    throw new ParseException(string, "Invalid expression. Expected '.' after string constant, got '" + string.charAt(i+1) + "'");
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
                            if (debugMode) logger.info("Target built as \"" + word + "\"");

                            isStaticClass = staticClassMap.containsKey(word);
                            if(isStaticClass){
                                staticClass = staticClassMap.get(word);
                                if (debugMode) logger.info("Target identified as a static class");

                                if (i + 1 >= string.length())
                                    throw new ParseException(string, "Expression ended early. Expected function after \"class.\".");
                            } else {
                                if (!(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>'))
                                    throw new ParseException(string, "Target \"" + word + "\" Does not match constant class or variable format.");
                                if (!argumentVariables.containsKey(word))
                                    throw new ParseException(string, "Variable \"" + word + "\" dose not exist at this point. Must be declared in usage or earlier set command.");
                                if (debugMode) logger.info("Target identified as valid variable.");

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
                            if (debugMode) logger.info("Function name built as \"" + function + "\"");

                            parseMode = 2;
                            parenthesisDepth = 1;
                            if (debugMode) logger.info("parenthesisDepth is 1");

                            wordBuild = new StringBuilder();
                        } else {
                            wordBuild.append(c);
                        }
                        break;
                    case 2: // reading parameters
                        if (c == '(') {
                            parenthesisDepth += 1;
                            if (debugMode) logger.info("parenthesisDepth is " + parenthesisDepth);
                            wordBuild.append(c);
                        }
                        else if (c == ')') {
                            parenthesisDepth -= 1;
                            if (debugMode) logger.info("parenthesisDepth is " + parenthesisDepth);

                            if (parenthesisDepth != 0) {
                                wordBuild.append(c);
                            } else {
                                // Final parameter
                                String word = wordBuild.toString();
                                if(!word.isEmpty()) {
                                    if (debugMode) logger.info("Final parameter built as \"" + word + "\"");

                                    Expression parameter;
                                    try {
                                        parameter = parseExpression(word, argumentVariables, debugMode, logger);
                                    } catch (ParseException e) {
                                        throw new ParseException(string, "\n" + e.getMessage());
                                    }
                                    if (debugMode) logger.info("Parameter successfully parsed");

                                    parameterExpressions.add(parameter);
                                }
                                wordBuild = new StringBuilder();

                                // prepare and go to 1 to read a new function
                                if (debugMode) logger.info("Creating new FunctionCall expression");

                                if(!isStaticClass) {
                                    try {
                                        assert targetExpression != null;
                                        target = InternalArgument.getInternalArgument(targetExpression.getEvaluationType(argumentVariables));
                                    } catch (IllegalArgumentException e) {
                                        throw new ParseException(string, "Could not turn InternalArgument class returned by expression: \""
                                                + targetExpression + "\" (" + targetExpression.getEvaluationType(argumentVariables) + ") into an object. " +
                                                "This issue must be fixed in the plugin's code, so please contact the plugin's author.");
                                    }

                                    if (debugMode) {
                                        logger.info("target is type " + target.getClass().getSimpleName());
                                        logger.info("function is " + function);
                                    }
                                }

                                ArgList parameters = new ArgList();
                                for (Expression parameterExpression : parameterExpressions) {
                                    parameters.add(parameterExpression.getEvaluationType(argumentVariables));
                                }
                                if (debugMode){
                                    StringBuilder parametersString = new StringBuilder("[");
                                    if(parameters.size() != 0) {
                                        for (Class<? extends InternalArgument> parameter : parameters) {
                                            parametersString.append(parameter.getSimpleName());
                                            parametersString.append(", ");
                                        }
                                        parametersString.delete(parametersString.length() - 2, parametersString.length());
                                    }
                                    parametersString.append("]");
                                    logger.info("parameter types are " + parametersString);
                                }

                                if(isStaticClass) {
                                    if(!staticClass.hasStaticFunction(function, parameters))
                                        throw new ParseException(string, "Invalid function \"" + function + "\" on " + staticClass + " with parameters " + parameters + ". Function does not exist.");
                                } else {
                                    if (!target.hasFunction(function, parameters))
                                        throw new ParseException(string, "Invalid function \"" + function + "\" on " + target + " with parameters " + parameters + ". Function does not exist.");
                                }

                                if (debugMode) logger.info("target was found to have function");

                                if (isStaticClass) {
                                    targetExpression = new StaticFunctionCall(staticClass, function, parameterExpressions);
                                } else {
                                    targetExpression = new FunctionCall(targetExpression, function, parameterExpressions);
                                }

                                function = "";
                                parameterExpressions = new ArrayList<>();

                                i++; // skip '.'
                                if (i >= string.length()){
                                    if (debugMode) logger.info("End of expression. The function call will be returned.");
                                    return targetExpression;
                                }
                                if (string.charAt(i) != '.') throw new ParseException(string, "Expected '.' or nothing at all after closing a function. Found '" + string.charAt(i) + "' instead.");
                                parseMode = 1; // go back to reading a function, so they can be chained
                            }
                        }
                        else if (parenthesisDepth == 1 && c == ',') {
                            // new parameter
                            String word = wordBuild.toString();
                            if (debugMode) logger.info("New parameter built as \"" + word + "\"");

                            Expression parameter;
                            try {
                                parameter = parseExpression(word, argumentVariables, debugMode, logger);
                            } catch (ParseException e) {
                                throw new ParseException(string, "\n" + e.getMessage());
                            }
                            if (debugMode) logger.info("Parameter successfully parsed");

                            parameterExpressions.add(parameter);
                            if (string.charAt(i + 1) == ' ') {
                                i++; // skip ' ' if used to separate parameters
                            }
                            wordBuild = new StringBuilder();
                        }
                        else {
                            wordBuild.append(c);
                        }
                }
            }

            if (debugMode) logger.info("End of expression reached without returning anything.");
            switch (parseMode) {
                // reading string constant
                case -1 -> throw new ParseException(string, "Expression ended early. Expected statement staring with \" to be closed by \"");
                // reading variable
                case 0 -> {
                    String word = wordBuild.toString();
                    // expression is just a variable name
                    if (!(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>'))
                        throw new ParseException(string, "Target \"" + word + "\" Does not match variable format.");
                    if (!argumentVariables.containsKey(word))
                        throw new ParseException(string, "Variable \"" + word + "\" dose not exist at this point. Must be declared in usage or earlier set command.");
                    if (debugMode) logger.info("Expression found to be a valid variable reference");
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
            if (debugMode){ logger.decreaseIndentation();}
        }
    }

    public abstract String toString();

    public abstract Class<? extends InternalArgument> getEvaluationType(HashMap<String, Class<? extends InternalArgument>> argument_variables);

    public abstract InternalArgument evaluate(HashMap<String, InternalArgument> argument_variables,
                                              boolean debugMode, IndentedLogger logger) throws CommandRunException;
}

class FunctionCall extends Expression{
    private final Expression targetExpression;
    private final String function;
    private final List<? extends Expression> parameterExpressions;

    public FunctionCall(Expression targetExpression, String function, List<? extends Expression> parameterExpressions){
        this.targetExpression = targetExpression;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    public String toString() {
        return "(" + targetExpression.toString() + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    public Class<? extends InternalArgument> getEvaluationType(HashMap<String, Class<? extends InternalArgument>> argument_variables){
        InternalArgument target = InternalArgument.getInternalArgument(targetExpression.getEvaluationType(argument_variables));

        ArgList parameters = new ArgList();
        for(Expression parameterExpression: parameterExpressions){
            parameters.add(parameterExpression.getEvaluationType(argument_variables));
        }

        return target.getReturnTypeForFunction(function, parameters);
    }

    public InternalArgument evaluate(HashMap<String, InternalArgument> argument_variables, boolean debugMode, IndentedLogger logger) throws CommandRunException {
        if (debugMode) logger.info("Evaluating FunctionCall");

        if (debugMode) logger.info("Target expression is: " + targetExpression);
        logger.increaseIndentation();
        InternalArgument target = targetExpression.evaluate(argument_variables, debugMode, logger);
        logger.decreaseIndentation();

        if (debugMode) logger.info("Function is: " + function);

        List<InternalArgument> parameters = new ArrayList<>();
        for(Expression parameterExpression: parameterExpressions){
            if (debugMode) logger.info("Parameter expression is: " + parameterExpression);
            logger.increaseIndentation();
            parameters.add(parameterExpression.evaluate(argument_variables, debugMode, logger));
            logger.decreaseIndentation();
        }

        if (debugMode) logger.info("Running function");
        logger.increaseIndentation();
        InternalArgument result = target.runFunction(function, parameters);
        logger.decreaseIndentation();
        if (debugMode) logger.info("Result is " + result + " with value " + result.getValue());
        return result;
    }
}

class StaticFunctionCall extends Expression{
    private final InternalArgument targetClass;
    private final String function;
    private final List<? extends Expression> parameterExpressions;

    StaticFunctionCall(InternalArgument targetClass, String function, List<? extends Expression> parameterExpressions) {
        this.targetClass = targetClass;
        this.function = function;
        this.parameterExpressions = parameterExpressions;
    }

    public String toString() {
        return "(" + targetClass.toString() + ")." + function + "(" + parameterExpressions.toString() + ")";
    }

    public Class<? extends InternalArgument> getEvaluationType(HashMap<String, Class<? extends InternalArgument>> argument_variables){
        ArgList parameters = new ArgList();
        for(Expression parameterExpression: parameterExpressions){
            parameters.add(parameterExpression.getEvaluationType(argument_variables));
        }

        return targetClass.getReturnTypeForStaticFunction(function, parameters);
    }

    public InternalArgument evaluate(HashMap<String, InternalArgument> argument_variables, boolean debugMode, IndentedLogger logger) throws CommandRunException {
        if (debugMode) logger.info("Evaluating StaticFunctionCall");

        if (debugMode) logger.info("Target class is: " + targetClass);

        if (debugMode) logger.info("Function is: " + function);

        List<InternalArgument> parameters = new ArrayList<>();
        for(Expression parameterExpression: parameterExpressions){
            if (debugMode) logger.info("Parameter expression is: " + parameterExpression);
            logger.increaseIndentation();
            parameters.add(parameterExpression.evaluate(argument_variables, debugMode, logger));
            logger.decreaseIndentation();
        }

        if (debugMode) logger.info("Running function");
        logger.increaseIndentation();
        InternalArgument result = targetClass.runStaticFunction(function, parameters);
        logger.decreaseIndentation();
        if (debugMode) logger.info("Result is " + result + " with value " + result.getValue());
        return result;
    }
}

class Variable extends Expression{
    private final String name;

    public Variable(String name){ this.name = name; }

    public String toString() { return name; }

    public Class<? extends InternalArgument> getEvaluationType(HashMap<String, Class<? extends InternalArgument>> argument_variables) {
        return argument_variables.get(name);
    }

    public InternalArgument evaluate(HashMap<String, InternalArgument> argument_variables, boolean debugMode, IndentedLogger logger) throws CommandRunException {
        if (debugMode) {
            logger.info("Evaluating Variable");
            logger.info("Variable name is: " + name);
            logger.info("Class " + argument_variables.get(name).getClass().getSimpleName() + " with value " + argument_variables.get(name).forCommand());
        }
        return argument_variables.get(name);
    }
}

class StringConstant extends Expression{
    private final InternalStringArgument value;
    public StringConstant(String value){
        this.value = new InternalStringArgument(value);
    }

    public String toString() { return "\"" + value.getValue() + "\""; }

    public Class<? extends InternalArgument> getEvaluationType(HashMap<String, Class<? extends InternalArgument>> argument_variables) {
        return InternalStringArgument.class;
    }

    public InternalArgument evaluate(HashMap<String, InternalArgument> argument_variables, boolean debugMode, IndentedLogger logger) throws CommandRunException {
        if (debugMode) logger.info("Evaluating Constant");
        if (debugMode) logger.info("Constant is " + this);
        return value;
    }
}
