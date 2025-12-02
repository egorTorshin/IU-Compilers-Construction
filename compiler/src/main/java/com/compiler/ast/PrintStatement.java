package com.compiler.ast;

public class PrintStatement extends Statement {
    private Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void execute() {
        Object value = expression.evaluate();
        System.out.println(value);
    }

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
