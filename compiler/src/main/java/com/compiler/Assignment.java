package com.compiler;

/**
 * Represents an assignment statement in the source code.
 * Can handle both regular variable assignments and array element assignments.
 */
public class Assignment extends Statement {
    /** The target variable or array name being assigned to */
    private String target;
    /** The expression whose value will be assigned */
    private Expression value;
    /** The index expression for array assignments, null for regular assignments */
    private Expression index;

    /**
     * Creates a new regular assignment statement.
     * @param target The name of the variable being assigned to
     * @param value The expression whose value will be assigned
     */
    public Assignment(String target, Expression value) {
        this.target = target;
        this.value = value;
        this.index = null;
    }

    /**
     * Creates a new array element assignment statement.
     * @param target The name of the array being assigned to
     * @param index The index expression indicating which element to assign
     * @param value The expression whose value will be assigned
     */
    public Assignment(String target, Expression index, Expression value) {
        this.target = target;
        this.index = index;
        this.value = value;
    }

    /**
     * Gets the target variable or array name.
     * @return The assignment target name
     */
    public String getTarget() {
        return target;
    }

    /**
     * Gets the expression being assigned.
     * @return The value expression
     */
    public Expression getValue() {
        return value;
    }

    /**
     * Sets a new value expression for this assignment.
     * @param value The new value expression to assign
     */
    public void setValue(Expression value) {
        this.value = value;
    }

    /**
     * Gets the index expression for array assignments.
     * @return The index expression, or null for regular assignments
     */
    public Expression getIndex() {
        return index;
    }

    /**
     * Returns a string representation of this assignment.
     * For regular assignments: "target := value"
     * For array assignments: "target[index] := value"
     * @return The string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assignment\n");
        sb.append("├── Target: ").append(target).append("\n");
        if (index != null) {
            sb.append("├── Index: ").append(index).append("\n");
        }
        sb.append("└── Value: ").append(value);
        return sb.toString();
    }
}
