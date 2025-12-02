package com.compiler.ast;

// base class for all AST expression nodes
public abstract class Expression {
    public abstract Object evaluate();

    @Override
    public abstract String toString();
}
