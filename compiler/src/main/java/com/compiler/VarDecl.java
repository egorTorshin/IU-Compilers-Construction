package com.compiler;

/**
 * Represents a variable declaration statement in the AST.
 * A variable declaration binds a name to a type and optionally includes an initializer expression.
 */
public class VarDecl extends Statement implements VariableDeclaration {
    /** The name being bound in this variable declaration */
    private String name;
    
    /** The type of the declared variable */
    private Type type;
    
    /** Optional initializer expression for the variable */
    private Expression initializer;

    /**
     * Creates a new variable declaration.
     * @param name The name to bind
     * @param type The type of the variable
     * @param initializer Optional initializer expression, can be null
     */
    public VarDecl(String name, Type type, Expression initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    /**
     * Gets the name being bound in this variable declaration.
     * @return The variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the declared type of the variable.
     * @return The variable's type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the initializer expression if one exists.
     * @return The initializer expression, or null if none exists
     */
    public Expression getInitializer() {
        return initializer;
    }

    /**
     * Sets a new initializer expression for this variable.
     * @param initializer The new initializer expression
     */
    public void setInitializer(Expression initializer) {
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VarDecl\n");
        sb.append("├── name: ").append(name).append("\n");
        sb.append("├── type: ").append(type).append("\n");
        sb.append("│   └── ").append(type.getClass().getSimpleName()).append("\n");
        if (initializer != null) {
            String[] initLines = initializer.toString().split("\n");
            for (int i = 0; i < initLines.length; i++) {
                if (i == 0) {
                    sb.append("└── initializer: ").append(initLines[i]).append("\n");
                } else {
                    sb.append("    ").append(initLines[i]).append("\n");
                }
            }
        } else {
            sb.append("└── initializer: null\n");
        }
        return sb.toString();
    }
}
