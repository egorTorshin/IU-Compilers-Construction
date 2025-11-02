package com.compiler;

/**
 * Represents a return statement in the program.
 * A return statement can optionally contain an expression to return a value.
 */
public class ReturnStatement extends Statement {
    /** The expression to be returned, may be null for void returns */
    private Expression expression;

    /**
     * Constructs a new return statement with the given expression.
     * @param expression The expression to return, or null for void return
     */
    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    /**
     * Gets the expression associated with this return statement.
     * @return The expression to be returned, or null if void return
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Checks if this return statement has an expression.
     * @return true if there is a return expression, false for void return
     */
    public boolean hasExpression() {
        return expression != null;
    }

    /**
     * Returns a string representation of this return statement in a tree format.
     * @return A formatted string showing the return statement and its expression if present
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Return Statement\n");
        if (expression != null) {
            sb.append("└── Expression: ")
              .append(expression.toString().replace("\n", "\n    "));
        } else {
            sb.append("└── void return");
        }
        return sb.toString();
    }
}
