package com.compiler;

/**
 * Represents an array declaration statement in the source code.
 * Contains the array name and its type specification.
 */
public class ArrayDecl extends Statement implements VariableDeclaration {
    /** The name of the declared array */
    private String name;
    /** The type specification of the array */
    private ArrayType type;

    /**
     * Creates a new array declaration with the given name and type.
     * @param name The name of the array being declared
     * @param type The type specification of the array
     */
    public ArrayDecl(String name, ArrayType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of the declared array.
     * @return The array name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the type specification of the array.
     * @return The array type
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Returns a string representation of this array declaration in tree format.
     * @return A string showing the array name and type
     */
    @Override
    public String toString() {
        return "ArrayDecl\n"
                + "├── name: " + name + "\n"
                + "└── type: " + type;
    }
}