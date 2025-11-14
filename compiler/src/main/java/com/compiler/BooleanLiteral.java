package com.compiler;

/**
 * Represents a boolean literal value in the source code.
 * Can be either true or false.
 */
public class BooleanLiteral extends Expression {
    /** The boolean value of this literal */
    private boolean value;

    /**
     * Creates a new boolean literal with the specified value.
     * @param value The boolean value (true or false)
     */
    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    /**
     * Gets the boolean value of this literal.
     * @return The boolean value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Evaluates this boolean literal by returning its value.
     * @return The boolean value as an Object
     */
    @Override
    public Object evaluate() {
        return value;
    }

    /**
     * Returns a string representation of this boolean literal in tree format.
     * @return A string showing the boolean value
     */
    @Override
    public String toString() {
        return "BooleanLiteral\n└── value: " + value;
    }
}
