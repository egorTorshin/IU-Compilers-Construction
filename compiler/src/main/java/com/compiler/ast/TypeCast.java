package com.compiler.ast;

/**
 * Represents a type cast expression in the abstract syntax tree.
 * This class handles the conversion of an expression from one type to another.
 */
public class TypeCast extends Expression {
    private Expression expr;
    private Type targetType;

    /**
     * Constructs a new TypeCast expression.
     *
     * @param expr The expression to be cast
     * @param targetType The type to cast the expression to
     */
    public TypeCast(Expression expr, Type targetType) {
        this.expr = expr;
        this.targetType = targetType;
    }

    /**
     * Gets the expression being cast.
     *
     * @return The expression to be cast
     */
    public Expression getExpression() {
        return expr;
    }

    /**
     * Gets the target type of the cast.
     *
     * @return The type to cast to
     */
    public Type getTargetType() {
        return targetType;
    }

    /**
     * Evaluates the type cast expression.
     *
     * @return The result of casting the evaluated expression to the target type
     * @throws UnsupportedOperationException when type casting is not yet implemented
     */
    @Override
    public Object evaluate() {
        Object value = expr.evaluate();
        // TODO: Implement type conversion
        throw new UnsupportedOperationException("Type casting not implemented yet");
    }

    /**
     * Returns a string representation of the type cast expression in AST format.
     *
     * @return A formatted string showing the AST structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TypeCast (").append(targetType).append(")\n");
        sb.append("├── Expression to cast:\n");
        String[] exprLines = expr.toString().split("\n");
        for (int i = 0; i < exprLines.length; i++) {
            if (i == exprLines.length - 1) {
                sb.append("└── ").append(exprLines[i]).append("\n");
            } else {
                sb.append("│   ").append(exprLines[i]).append("\n");
            }
        }
        return sb.toString();
    }
}
