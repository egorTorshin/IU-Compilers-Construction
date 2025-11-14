package com.compiler;

/**
 * Represents a reference to a variable in the AST.
 * A variable reference consists of a name that refers to a previously declared variable
 * and can be evaluated to retrieve its current value from the execution context.
 */
public class VariableReference extends Expression {
    /** The name of the referenced variable */
    private String name;

    /**
     * Creates a new variable reference.
     * @param name The name of the variable being referenced
     */
    public VariableReference(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the referenced variable.
     * @return The variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Evaluates this variable reference by looking up its current value.
     * @return The current value of the referenced variable
     * @throws UnsupportedOperationException when symbol table lookup is not yet implemented
     */
    @Override
    public Object evaluate() {
        // TODO: Implement symbol table lookup
        throw new UnsupportedOperationException("Variable lookup not implemented yet");
    }

    /**
     * Returns a string representation of this variable reference in AST format.
     * @return AST string representation showing the variable name
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VariableReference\n");
        sb.append("└── name: '").append(name).append("'\n");
        sb.append("     └── type: <resolved at runtime>");
        return sb.toString();
    }
}