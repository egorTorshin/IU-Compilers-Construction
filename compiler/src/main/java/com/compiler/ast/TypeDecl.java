package com.compiler.ast;

/**
 * Represents a type declaration statement in the AST.
 * A type declaration binds a name to a type.
 */
public class TypeDecl extends Statement {
    /** The name being bound in this type declaration */
    private String name;
    
    /** The type being bound to the name */
    private Type type;

    /**
     * Creates a new type declaration.
     * @param name The name to bind
     * @param type The type to bind to the name
     */
    public TypeDecl(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name being bound in this type declaration.
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type being bound to the name.
     * @return The type
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TypeDecl\n");
        sb.append("├── name: ").append(name).append("\n");
        sb.append("└── type: ").append(type).append("\n");
        sb.append("     └── ").append(type.getClass().getSimpleName());
        return sb.toString();
    }
}
