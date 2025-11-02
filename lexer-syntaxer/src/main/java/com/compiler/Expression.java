package com.compiler;

/**
 * Base class for all expressions in the AST
 */
public abstract class Expression {
    /**
     * Evaluates the expression and returns its value
     */
    public abstract Object evaluate();

    /**
     * Returns a string representation of the expression
     */
    @Override
    public abstract String toString();
}
