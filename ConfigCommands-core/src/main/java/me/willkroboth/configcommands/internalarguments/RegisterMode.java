package me.willkroboth.configcommands.internalarguments;

/**
 * An enum used to select what to register when calling {@link InternalArgument#registerFromPackage(String, ClassLoader, RegisterMode, String)}.
 * There are three possible behaviors:
 * <ul>
 *     <li>{@link RegisterMode#All} - Both {@link InternalArgument} and {@link FunctionAdder} objects</li>
 *     <li>{@link RegisterMode#INTERNAL_ARGUMENTS} - Only {@link InternalArgument} objects</li>
 *     <li>{@link RegisterMode#FUNCTION_ADDERS} - Only {@link FunctionAdder} objects</li>
 * </ul>
 */
public enum RegisterMode {
    /**
     * Registers both {@link InternalArgument} and {@link FunctionAdder} objects.
     */
    All,
    /**
     * Registers only {@link InternalArgument} objects.
     */
    INTERNAL_ARGUMENTS,
    /**
     * Registers only {@link FunctionAdder} objects.
     */
    FUNCTION_ADDERS,
}
