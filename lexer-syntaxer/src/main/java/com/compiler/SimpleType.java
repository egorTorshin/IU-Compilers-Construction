package com.compiler;

/**
 * Represents a simple type in the AST.
 * A simple type is a basic type with just a name, like "int" or "String".
 */
public class SimpleType extends Type {
    /**
     * Constructs a new SimpleType with the given name.
     * @param name The name of the type
     */
    public SimpleType(String name) {
        super(name);
    }

    /**
     * Gets the name of this type.
     * @return The type name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of this type for AST visualization.
     * @return String in format "SimpleType(name)"
     */
    @Override
    public String toString() {
        return "SimpleType(" + name + ")";
    }
}
