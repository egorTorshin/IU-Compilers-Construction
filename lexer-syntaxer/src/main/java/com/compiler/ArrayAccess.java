package com.compiler;

/**
 * Represents an array access expression in the source code.
 * Handles accessing array elements using an index expression.
 */
public class ArrayAccess extends Expression {
    /** The name of the array being accessed */
    private String array;
    /** The index expression used to access the array element */
    private Expression index;

    /**
     * Creates a new ArrayAccess with the given array name and index expression.
     * @param array The name of the array to access
     * @param index The index expression to evaluate for accessing the array element
     */
    public ArrayAccess(String array, Expression index) {
        this.array = array;
        this.index = index;
    }

    /**
     * Evaluates this array access expression.
     * @return The value at the specified array index, or null if evaluation fails
     */
    @Override
    public Object evaluate() {
        return null;
    }

    /**
     * Gets the name of the array being accessed.
     * @return The array name
     */
    public String getArray() {
        return array;
    }

    /**
     * Gets the index expression used to access the array.
     * @return The index expression
     */
    public Expression getIndex() {
        return index;
    }

    /**
     * Returns a string representation of this array access in tree format.
     * @return A string showing the array name and index expression
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayAccess\n");
        sb.append("├── Array: ").append(array).append("\n");
        sb.append("└── Index: ").append(index);
        return sb.toString();
    }
}
