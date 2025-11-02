package com.compiler;

/**
 * Represents a real (floating-point) literal value in an expression.
 * This class extends Expression and holds a double value.
 */
public class RealLiteral extends Expression {
    /** The double value of this real literal */
    private double value;

    /**
     * Constructs a new RealLiteral with the specified value.
     * @param value The double value to store
     */
    public RealLiteral(double value) {
        this.value = value;
    }

    /**
     * Evaluates this real literal by returning its value.
     * @return The double value as an Object
     */
    @Override
    public Object evaluate() {
        return value;
    }

    /**
     * Gets the double value of this real literal.
     * @return The stored double value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns a string representation of this real literal in a tree format.
     * @return A string showing the type and value in tree structure
     */
    @Override
    public String toString() {
        return String.format("RealLiteral%n└── %.2f", value);
    }
}
