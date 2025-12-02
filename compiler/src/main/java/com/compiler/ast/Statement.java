package com.compiler.ast;

// base class for all AST statement nodes
public abstract class Statement {
    @Override
    public abstract String toString();

    public void execute() {
        throw new UnsupportedOperationException("Execute not implemented for " + getClass().getSimpleName());
    }
}
