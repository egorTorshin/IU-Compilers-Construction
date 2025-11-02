package com.compiler;

/**
 * Abstract base class for all statement nodes in the Abstract Syntax Tree (AST).
 * This class represents executable constructs in the program that perform actions
 * but do not necessarily produce values. Examples include assignments, loops,
 * conditional statements, and procedure calls.
 */
public abstract class Statement {
    /**
     * Returns a string representation of this statement.
     * Useful for debugging and error reporting.
     *
     * @return A string representation of the statement
     */
    @Override
    public abstract String toString();

    /**
     * Executes this statement, performing its intended action.
     * Each concrete statement type must implement this method to define its behavior.
     * 
     * @throws UnsupportedOperationException if the concrete class does not implement
     *         the execute method
     */
    public void execute() {
        // Default implementation
        throw new UnsupportedOperationException("Execute not implemented for " + getClass().getSimpleName());
    }
}
