package com.compiler.ast;

public class IntegerLiteral extends Expression {
    private int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String toString() {
        if (value < 0) {
            return "(" + value + ")";
        }
        return String.valueOf(value);
    }
}
