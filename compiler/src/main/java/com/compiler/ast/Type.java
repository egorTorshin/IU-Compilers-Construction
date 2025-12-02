package com.compiler.ast;

/**
 * Abstract base class representing types in the programming language.
 * This class serves as the foundation for the type system, providing common
 * functionality for both built-in types and user-defined types.
 */
public abstract class Type {
    /** The name of this type */
    protected final String name;

    /**
     * Constructs a new Type with the given name.
     *
     * @param name The name of the type
     */
    protected Type(String name) {
        this.name = name;
    }

    /**
     * Returns the string representation of this type.
     *
     * @return The name of the type
     */
    @Override
    public String toString() {
        return name;
    }

    /** Built-in integer type */
    public static final SimpleType INTEGER = new SimpleType("integer");
    /** Built-in boolean type */
    public static final SimpleType BOOLEAN = new SimpleType("boolean");
    /** Built-in string type */
    public static final SimpleType STRING = new SimpleType("string");
    /** Built-in void type for procedures that don't return a value */
    public static final SimpleType VOID = new SimpleType("void");

    /**
     * Creates a Type instance from its string representation.
     * This method handles the conversion of type names to their corresponding Type objects.
     *
     * @param typeName The name of the type to create
     * @return The corresponding Type object
     * @throws IllegalArgumentException if the type name is not recognized
     */
    public static Type fromString(String typeName) {
        switch (typeName) {
            case "integer": return INTEGER;
            case "boolean": return BOOLEAN;
            case "string": return STRING;
            case "void": return VOID;
            default: throw new IllegalArgumentException("Unknown type: " + typeName);
        }
    }
}