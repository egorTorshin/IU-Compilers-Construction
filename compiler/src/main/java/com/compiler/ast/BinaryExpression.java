package com.compiler.ast;

public class BinaryExpression extends Expression {
    private Expression left;
    private String operator;
    private Expression right;

    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public Object evaluate() {
        Object leftVal = left.evaluate();
        Object rightVal = right.evaluate();

        switch (operator) {
            case "+":
                return add(leftVal, rightVal);
            case "-":
                return subtract(leftVal, rightVal);
            case "*":
                return multiply(leftVal, rightVal);
            case "/":
                return divide(leftVal, rightVal);
            case "%":
                return mod(leftVal, rightVal);
            case "and":
                return and(leftVal, rightVal);
            case "or":
                return or(leftVal, rightVal);
            case "xor":
                return xor(leftVal, rightVal);
            case "=":
                return equals(leftVal, rightVal);
            case "!=":
                return notEquals(leftVal, rightVal);
            case "<":
                return lessThan(leftVal, rightVal);
            case "<=":
                return lessOrEqual(leftVal, rightVal);
            case ">":
                return greaterThan(leftVal, rightVal);
            case ">=":
                return greaterOrEqual(leftVal, rightVal);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    /**
     * Performs addition of two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The sum of the operands
     * @throws RuntimeException if operands are not both integers
     */
    private Object add(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left + (Integer) right;
        }
        // Add other type combinations as needed
        throw new RuntimeException("Invalid operand types for +");
    }

    /**
     * Performs subtraction of two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The difference of the operands
     * @throws RuntimeException if operands are not both integers
     */
    private Object subtract(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left - (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for -");
    }

    /**
     * Performs multiplication of two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The product of the operands
     * @throws RuntimeException if operands are not both integers
     */
    private Object multiply(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left * (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for *");
    }

    /**
     * Performs division of two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The quotient of the operands
     * @throws RuntimeException if operands are not both integers
     */
    private Object divide(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left / (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for /");
    }

    /**
     * Performs modulo operation on two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The remainder after division
     * @throws RuntimeException if operands are not both integers
     */
    private Object mod(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left % (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for %");
    }

    /**
     * Performs logical AND operation on two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The result of the AND operation
     * @throws RuntimeException if operands are not both booleans
     */
    private Object and(Object left, Object right) {
        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left && (Boolean) right;
        }
        throw new RuntimeException("Invalid operand types for and");
    }

    /**
     * Performs logical OR operation on two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The result of the OR operation
     * @throws RuntimeException if operands are not both booleans
     */
    private Object or(Object left, Object right) {
        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left || (Boolean) right;
        }
        throw new RuntimeException("Invalid operand types for or");
    }

    /**
     * Performs logical XOR operation on two operands.
     * @param left The left operand
     * @param right The right operand
     * @return The result of the XOR operation
     * @throws RuntimeException if operands are not both booleans
     */
    private Object xor(Object left, Object right) {
        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left ^ (Boolean) right;
        }
        throw new RuntimeException("Invalid operand types for xor");
    }

    /**
     * Checks if two operands are not equal.
     * @param left The left operand
     * @param right The right operand
     * @return True if operands are not equal, false otherwise
     */
    private Object notEquals(Object left, Object right) {
        return !left.equals(right);
    }

    /**
     * Checks if left operand is less than right operand.
     * @param left The left operand
     * @param right The right operand
     * @return True if left is less than right, false otherwise
     * @throws RuntimeException if operands are not both integers
     */
    private Object lessThan(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left < (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for <");
    }

    /**
     * Checks if left operand is less than or equal to right operand.
     * @param left The left operand
     * @param right The right operand
     * @return True if left is less than or equal to right, false otherwise
     * @throws RuntimeException if operands are not both integers
     */
    private Object lessOrEqual(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left <= (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for <=");
    }

    /**
     * Checks if left operand is greater than right operand.
     * @param left The left operand
     * @param right The right operand
     * @return True if left is greater than right, false otherwise
     * @throws RuntimeException if operands are not both integers
     */
    private Object greaterThan(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left > (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for >");
    }

    /**
     * Checks if left operand is greater than or equal to right operand.
     * @param left The left operand
     * @param right The right operand
     * @return True if left is greater than or equal to right, false otherwise
     * @throws RuntimeException if operands are not both integers
     */
    private Object greaterOrEqual(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left >= (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for >=");
    }

    /**
     * Checks if two operands are equal.
     * @param left The left operand
     * @param right The right operand
     * @return True if operands are equal, false otherwise
     */
    private Object equals(Object left, Object right) {
        return left.equals(right);
    }

    /**
     * Returns a string representation of this binary expression in tree format.
     * @return A string showing the operator and both operands
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BinaryExpression\n");
        sb.append("├── Operator: ").append(operator).append("\n");
        sb.append("├── Left: ").append(left).append("\n");
        sb.append("└── Right: ").append(right);
        return sb.toString();
    }
}
