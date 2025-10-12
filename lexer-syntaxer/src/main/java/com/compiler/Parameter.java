package com.compiler;

/**
 * Represents a parameter in a function or method declaration.
 * A parameter consists of a name and its associated type.
 */
public class Parameter {
    /** The name of the parameter */
    private String name;
    
    /** The type of the parameter */
    private Type type;

    /**
     * Constructs a new Parameter with the specified name and type.
     * @param name The name of the parameter
     * @param type The type of the parameter
     */
    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of the parameter.
     * @return The parameter name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the parameter.
     * @return The parameter type
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Parameter[name=%s, type=%s]", name, type);
    }
}
