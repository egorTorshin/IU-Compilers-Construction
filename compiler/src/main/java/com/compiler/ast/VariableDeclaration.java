package com.compiler.ast;

/**
 * Interface representing a variable declaration in the AST.
 * This interface is implemented by nodes that declare variables and bind names to types.
 */
public interface VariableDeclaration {
    /**
     * Gets the name being bound in this variable declaration.
     * @return The variable name
     */
    public String getName();

    /**
     * Gets the declared type of the variable.
     * @return The variable's type
     */
    public Type getType();
}
