package com.compiler;

/**
 * Represents an array type in the source code.
 * Can represent both fixed-size and dynamic arrays.
 */
public class ArrayType extends Type {
    /** The type of elements stored in the array */
    private final Type elementType;
    /** The fixed size of the array, or null for dynamic arrays */
    private final Integer size;

    /**
     * Creates a new dynamic array type with the specified element type.
     * @param elementType The type of elements that will be stored in the array
     */
    public ArrayType(Type elementType) {
        super("array of " + elementType.toString());
        this.elementType = elementType;
        this.size = null;
    }

    /**
     * Creates a new fixed-size array type with the specified element type and size.
     * @param elementType The type of elements that will be stored in the array
     * @param size The fixed size of the array
     */
    public ArrayType(Type elementType, Integer size) {
        super("array[" + size + "] of " + elementType.toString());
        this.elementType = elementType;
        this.size = size;
    }

    /**
     * Gets the type of elements stored in this array.
     * @return The element type
     */
    public Type getElementType() {
        return elementType;
    }

    /**
     * Gets the fixed size of this array, or null if it's a dynamic array.
     * @return The array size, or null for dynamic arrays
     */
    public Integer getSize() {
        return size;
    }
}
