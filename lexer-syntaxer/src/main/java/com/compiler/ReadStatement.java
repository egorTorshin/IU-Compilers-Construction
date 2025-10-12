package com.compiler;

/**
 * Represents a read statement in the program that reads input into a variable.
 * This class extends the base Statement class and handles reading values from input
 * into the specified identifier/variable.
 */
public class ReadStatement extends Statement {
    /** The identifier/variable name that will store the read value */
    private String identifier;

    /**
     * Constructs a new ReadStatement with the specified identifier.
     * @param identifier The variable name that will store the read value
     */
    public ReadStatement(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the variable name associated with this read statement.
     * @return The identifier/variable name that stores the read value
     */
    public String getVariable() {
        return identifier;
    }

    /**
     * Returns a string representation of this ReadStatement in a tree-like format.
     * @return A formatted string showing the read operation and target variable
     */
    @Override
    public String toString() {
        return String.format("Read Statement%n" +
                           "├── Operation: READ%n" +
                           "└── Target: %s", identifier);
    }
}
