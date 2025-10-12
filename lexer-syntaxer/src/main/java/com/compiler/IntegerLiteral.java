package com.compiler;

/**
 * Represents an integer literal value in the abstract syntax tree.
 * This class handles integer constants that appear in the source code.
 */
public class IntegerLiteral extends Expression {
    /** The integer value of this literal */
    private int value;

    /**
     * Constructs a new integer literal with the specified value.
     *
     * @param value The integer value of this literal
     */
    public IntegerLiteral(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value of this literal.
     *
     * @return The integer value
     */
    public int getValue() {
        return value;
    }

    /**
     * Evaluates this expression by returning the integer value.
     *
     * @return The integer value as an Object
     */
    @Override
    public Object evaluate() {
        return value;
    }

    /**
     * Returns a string representation of this integer literal.
     * For negative values, includes the minus sign.
     * For positive values, the number is shown as-is.
     *
     * @return String representation of the integer value
     */
    @Override
    public String toString() {
        if (value < 0) {
            return "(" + value + ")";
        }
        return String.valueOf(value);
    }
}
