package com.compiler.semantic;

// semantic error: type mismatch, undefined variables, etc.
public class SemanticError {
    private String message;

    public SemanticError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SemanticError: " + message;
    }
}
