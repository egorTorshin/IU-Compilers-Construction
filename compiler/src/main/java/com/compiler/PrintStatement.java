package com.compiler;

/**
 * Represents a print statement in the abstract syntax tree.
 * This class handles the printing of expressions to standard output.
 */
public class PrintStatement extends Statement {
    /** The expression to be printed */
    private Expression expression;

    /**
     * Constructs a new print statement with the specified expression.
     *
     * @param expression The expression whose value will be printed
     */
    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    /**
     * Gets the expression that this print statement will output.
     *
     * @return The expression to be printed
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Executes this print statement by evaluating the expression
     * and printing its value to standard output.
     */
    @Override
    public void execute() {
        Object value = expression.evaluate();
        System.out.println(value);
    }

    /**
     * Returns a string representation of this print statement using ASCII art tree structure.
     * The output includes the expression to be printed in a hierarchical format.
     *
     * @return A formatted string representation of the print statement
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrintStatement\n");
        String[] expressionLines = expression.toString().split("\n");
        for (int i = 0; i < expressionLines.length; i++) {
            if (i == 0) {
                sb.append("└── ");
            } else {
                sb.append("    ");
            }
            sb.append(expressionLines[i]);
            if (i < expressionLines.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
