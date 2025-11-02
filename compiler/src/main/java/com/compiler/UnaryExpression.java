package com.compiler;

/**
 * Represents a unary expression in the AST.
 * A unary expression applies an operator to a single operand expression.
 * Supported operators are:
 * - "-" for numeric negation
 * - "not" for boolean negation
 */
public class UnaryExpression extends Expression {
    private String operator;
    private Expression expression;

    /**
     * Creates a new unary expression.
     *
     * @param operator The unary operator ("-" or "not")
     * @param expression The expression to apply the operator to
     */
    public UnaryExpression(String operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    /**
     * Gets the operand expression.
     *
     * @return The expression the operator is applied to
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Gets the unary operator.
     *
     * @return The operator string ("-" or "not")
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Evaluates this unary expression by applying the operator to the evaluated operand.
     *
     * @return The result of applying the unary operator
     * @throws RuntimeException if the operator is unknown or operand type is invalid
     */
    @Override
    public Object evaluate() {
        Object value = expression.evaluate();
        switch (operator) {
            case "-":
                return negate(value);
            case "not":
                return not(value);
            default:
                throw new RuntimeException("Unknown unary operator: " + operator);
        }
    }

    /**
     * Applies numeric negation to a value.
     *
     * @param value The value to negate
     * @return The negated value
     * @throws RuntimeException if the value is not an integer
     */
    private Object negate(Object value) {
        if (value instanceof Integer) {
            return -(Integer) value;
        }
        throw new RuntimeException("Invalid operand type for unary -");
    }

    /**
     * Applies boolean negation to a value.
     *
     * @param value The value to negate
     * @return The negated boolean value
     * @throws RuntimeException if the value is not a boolean
     */
    private Object not(Object value) {
        if (value instanceof Boolean) {
            return !(Boolean) value;
        }
        throw new RuntimeException("Invalid operand type for not");
    }

    /**
     * Returns a string representation of this unary expression in AST format.
     *
     * @return A formatted string showing the AST structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UnaryExpression\n");
        sb.append("├── Operator: ").append(operator).append("\n");
        String[] exprLines = expression.toString().split("\n");
        sb.append("└── Operand:\n");
        for (int i = 0; i < exprLines.length; i++) {
            if (i == exprLines.length - 1) {
                sb.append("    └── ").append(exprLines[i]).append("\n");
            } else {
                sb.append("    │   ").append(exprLines[i]).append("\n");
            }
        }
        return sb.toString();
    }
}
